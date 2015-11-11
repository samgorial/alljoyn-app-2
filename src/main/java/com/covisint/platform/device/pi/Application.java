package com.covisint.platform.device.pi;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Status;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements InitializingBean {

	static {
		System.loadLibrary("alljoyn_java");
	}

	public static final String APP_NAME = "Smart_Fan_AJ_Application";

	public static final String NAMESPACE = "com.covisint.platform.device.smartfan";

	public static final short CONTACT_PORT = 42;

	private static Executor executor = Executors.newFixedThreadPool(2);

	@Autowired
	@Qualifier("about")
	private BusService aboutBus;

	@Autowired
	@Qualifier("bus")
	private BusService appBus;

	static boolean aboutSessionEstablished = false;
	static int aboutSessionId;

	static boolean busSessionEstablished = false;
	static int busSessionId;

	public void afterPropertiesSet() throws Exception {

		executor.execute(new Runnable() {

			@Override
			public void run() {
				doAboutAnnouncement();
			}

		});

		executor.execute(new Runnable() {

			@Override
			public void run() {
				setupBusInterface();
			}

		});
		
	}

	private void doAboutAnnouncement() {

		BusAttachment bus = new BusAttachment(APP_NAME, BusAttachment.RemoteMessage.Receive);

		Status status = bus.registerBusObject(aboutBus, "/about");

		if (status != Status.OK) {
			System.err.println("Could not register bus object: " + status.toString());
			return;
		}

		status = bus.connect();
		
		if (status != Status.OK) {
			System.err.println("Could not connect to bus: " + status.toString());
			return;
		}

		System.out.println("Bus connection successful on " + System.getProperty("org.alljoyn.bus.address"));

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

		SessionOpts sessionOpts = new SessionOpts();
		sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
		sessionOpts.isMultipoint = false;
		sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
		sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

		status = bus.bindSessionPort(contactPort, sessionOpts, new SessionPortListener() {

			public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
				System.out.println("SessionPortListener.acceptSessionJoiner called");
				if (sessionPort == CONTACT_PORT) {
					return true;
				} else {
					return false;
				}
			}

			public void sessionJoined(short sessionPort, int id, String joiner) {
				System.out.println(
						String.format("SessionPortListener.sessionJoined(%d, %d, %s)", sessionPort, id, joiner));
				aboutSessionId = id;
				aboutSessionEstablished = true;
				System.out.println("BusAttachment session established: about data");
			}
		});

		if (status != Status.OK) {
			System.err.println("Could not bind session: " + status.toString());
			return;
		}

		AboutObj aboutObj = new AboutObj(bus);
		status = aboutObj.announce(contactPort.value, new AboutData());

		if (status != Status.OK) {
			System.out.println("Announce failed " + status.toString());
			return;
		}

        while (!aboutSessionEstablished) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Thread Exception caught");
                e.printStackTrace();
            }
        }
        
	}

	private void setupBusInterface() {

		BusAttachment bus = new BusAttachment(APP_NAME, BusAttachment.RemoteMessage.Receive);

		Status status = bus.registerBusObject(appBus, "/");
		
		if (status != Status.OK) {
			System.err.println("Could not register bus object: " + status.toString());
			return;
		}

		status = bus.connect();

		if (status != Status.OK) {
			System.err.println("Could not connect to bus: " + status.toString());
			return;
		}

		System.out.println("Connection successful on " + System.getProperty("org.alljoyn.bus.address"));

		Mutable.ShortValue contactPort = new Mutable.ShortValue(CONTACT_PORT);

		SessionOpts sessionOpts = new SessionOpts();
		sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
		sessionOpts.isMultipoint = false;
		sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
		sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

		status = bus.bindSessionPort(contactPort, sessionOpts, new SessionPortListener() {
			public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
				System.out.println("SessionPortListener.acceptSessionJoiner called");
				if (sessionPort == CONTACT_PORT) {
					return true;
				} else {
					return false;
				}
			}

			public void sessionJoined(short sessionPort, int id, String joiner) {
				System.out.println(
						String.format("SessionPortListener.sessionJoined(%d, %d, %s)", sessionPort, id, joiner));
				busSessionId = id;
				busSessionEstablished = true;

				System.out.println("BusAttachment session established: properties");

			}
		});

		if (status != Status.OK) {
			System.err.println("Could not bind session: " + status.toString());
			return;
		}

		int flags = 0; // do not use any request name flags
		status = bus.requestName(NAMESPACE, flags);
		
		if (status != Status.OK) {
			System.err.println("Requested name " + NAMESPACE + " was not available.");
			return;
		}

		status = bus.advertiseName(NAMESPACE, SessionOpts.TRANSPORT_ANY);
		
		if (status != Status.OK) {
			System.err.println("Could not advertise name: " + status);
			bus.releaseName(NAMESPACE);
			return;
		}
		
        while (!busSessionEstablished) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println("Thread Exception caught");
                e.printStackTrace();
            }
        }
        
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
