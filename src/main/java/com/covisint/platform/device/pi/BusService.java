package com.covisint.platform.device.pi;

import java.util.Timer;

import org.alljoyn.bus.BusException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("bus")
public class BusService implements PiBusInterface, InitializingBean {

	@Autowired
	private InternalState state;

	@Override
	public void afterPropertiesSet() throws Exception {

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
	}

}