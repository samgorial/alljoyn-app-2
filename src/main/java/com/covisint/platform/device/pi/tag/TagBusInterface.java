package com.covisint.platform.device.pi.tag;

import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusMethod;
import org.alljoyn.bus.annotation.BusProperty;
import org.alljoyn.bus.annotation.BusSignal;

@BusInterface(name = "com.covisint.platform.devices.pi.tag", announced = "true")
public interface TagBusInterface extends BusObject {

	@BusProperty
	public int getIt() throws BusException;

	@BusMethod
	public void tag() throws BusException;

	@BusSignal
	public void wasTagged() throws BusException;

}