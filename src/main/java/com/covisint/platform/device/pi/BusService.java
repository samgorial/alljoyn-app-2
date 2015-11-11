package com.covisint.platform.device.pi;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("bus")
public class BusService implements PiBusInterface, InitializingBean {

	@Autowired
	private InternalState state;

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

	protected boolean playTag() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (playTag()) {
			initGame();
		}
	}

	private void initGame() {

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
				System.out
						.println("BusAttachment.findAdvertisedName successful " + "com.covisint.platform.device.nest");

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

	private void tagSomeoneElse() {
		// Tag someone else!
		try {
			System.out.println("About to call remote#turnOnLight");
			mRemoteInterface.turnOnLight();
			System.out.println("Successfully called remote#turnOnLight");
			turnOffLight();
		} catch (BusException e) {
			e.printStackTrace();
		}
	}

	@Override
	public double getSpeed() throws BusException {
		System.out.println("Speed queried: " + state.speed);
		return state.speed;
	}

	@Override
	public int getState() throws BusException {
		return state.status;
	}

	@Override
	public void lightTurnedOn() throws BusException {
		System.out.println("Light turned on");
	}

	@Override
	public void lightTurnedOff() throws BusException {
		System.out.println("Light turned off");
	}

	@Override
	public String ping(String text) throws BusException {
		System.out.println("PING! " + text);
		return text;
	}

	@Override
	public void setTargetSpeed(double targetSpeed) throws BusException {
		System.out.println("Setting target speed to " + targetSpeed);
		state.speed = targetSpeed;
		targetSpeedReached();
	}

	@Override
	public void targetSpeedReached() throws BusException {
		System.out.println("Target speed " + state.speed + " reached!");
	}

	@Override
	public int getLedState() throws BusException {
		System.out.println("LED state queried: " + state.ledLit);
		return state.ledLit ? 1 : 0;
	}

	@Override
	public void turnOffLight() throws BusException {
		System.out.println("Turning off light.");
		state.ledLit = false;
		lightTurnedOff();
	}

	Timer timer = new Timer();

	@Override
	public void turnOnLight() throws BusException {
		System.out.println("Turning on light.");
		state.ledLit = true;
		lightTurnedOn();

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				tagSomeoneElse();
			}
		}, new Random(System.currentTimeMillis()).nextInt(4000) + 1000);

	}

}