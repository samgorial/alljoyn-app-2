package com.covisint.platform.device.pi.tag;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.covisint.platform.device.pi.Application;
import com.covisint.platform.device.pi.PiBusInterface;

//@Component
public class TagService implements InitializingBean {

	private static Executor executor = Executors.newSingleThreadExecutor();

	private static BusAttachment mBus;
	private static ProxyBusObject mProxyObj;
	private static PiBusInterface mRemoteInterface;
	private static boolean isJoined = false;

	static class MyBusListener extends BusListener {
		public void foundAdvertisedName(String name, short transport, String namePrefix) {
			System.out
					.println(String.format("BusListener.foundAdvertisedName(%s, %d, %s)", name, transport, namePrefix));
			short contactPort = Application.CONTACT_PORT;
			SessionOpts sessionOpts = new SessionOpts();
			sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
			sessionOpts.isMultipoint = false;
			sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
			sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

			Mutable.IntegerValue sessionId = new Mutable.IntegerValue();

			mBus.enableConcurrentCallbacks();

			Status status = mBus.joinSession(name, contactPort, sessionId, sessionOpts, new SessionListener());
			if (status != Status.OK) {
				return;
			}
			System.out.println(String.format("BusAttachement.joinSession successful sessionId = %d", sessionId.value));

			mProxyObj = mBus.getProxyBusObject("com.covisint.platform.device.nest", "/", sessionId.value,
					new Class<?>[] { PiBusInterface.class });

			mRemoteInterface = mProxyObj.getInterface(PiBusInterface.class);
			isJoined = true;

		}

		public void nameOwnerChanged(String busName, String previousOwner, String newOwner) {
			if ("com.covisint.platform.device.nest".equals(busName)) {
				System.out
						.println("BusAttachement.nameOwnerChagned(" + busName + ", " + previousOwner + ", " + newOwner);
			}
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
//		init();
	}

	private void init() {

		executor.execute(new Runnable() {

			@Override
			public void run() {

				mBus = new BusAttachment(Application.APP_NAME, BusAttachment.RemoteMessage.Receive);

				BusListener listener = new MyBusListener();
				mBus.registerBusListener(listener);

				Status status = mBus.connect();
				if (status != Status.OK) {
					return;
				}

				System.out.println(
						"BusAttachment.connect successful on " + System.getProperty("org.alljoyn.bus.address"));

				status = mBus.findAdvertisedName("com.covisint.platform.device.nest");
				if (status != Status.OK) {
					return;
				}
				System.out.println(
						"BusAttachment.findAdvertisedName successful " + "com.covisint.platform.device.nest");

				while (!isJoined) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						System.out.println("Program interupted");
					}
				}

				System.out.println("Joined game!");
				
			}

		});

	}

//	private void tagSomeoneElse() {
//		Random rand = new Random(System.currentTimeMillis());
//
//		try {
//			Thread.sleep(rand.nextInt(4000) + 1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		// Tag someone else!
//		try {
//			mRemoteInterface.turnOnLight();
//		} catch (BusException e) {
//			e.printStackTrace();
//		}
//	}

}