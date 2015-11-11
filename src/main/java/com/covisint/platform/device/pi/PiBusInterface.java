package com.covisint.platform.device.pi;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name = "com.covisint.platform.devices.public", announced = "true")
public interface PiBusInterface extends BusObject {

	@BusProperty
	public double getSpeed() throws BusException;

	@BusProperty
	public int getState() throws BusException;

	@BusProperty
	public int getLedState() throws BusException;

	@BusMethod
	public String ping(String text) throws BusException;

	@BusMethod
	public void setTargetSpeed(double targetSpeed) throws BusException;

	@BusSignal
	public void targetSpeedReached() throws BusException;

	@BusMethod
	public void turnOnLight() throws BusException;

	@BusMethod
	public void turnOffLight() throws BusException;

	@BusSignal
	public void lightTurnedOn() throws BusException;

	@BusSignal
	public void lightTurnedOff() throws BusException;
	
}