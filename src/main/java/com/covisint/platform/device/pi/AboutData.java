package com.covisint.platform.device.pi;

import static com.covisint.platform.device.pi.Constants.DESCRIPTION;
import static com.covisint.platform.device.pi.Constants.DEVICE_ID;
import static com.covisint.platform.device.pi.Constants.DEVICE_NAME;
import static com.covisint.platform.device.pi.Constants.FIRMWARE_VERSION;
import static com.covisint.platform.device.pi.Constants.MANUFACTURER;
import static com.covisint.platform.device.pi.Constants.MODEL_NUMBER;
import static com.covisint.platform.device.pi.Constants.SERIAL_NUMBER;

import java.util.HashMap;
import java.util.Map;

import org.alljoyn.bus.AboutDataListener;
import org.alljoyn.bus.ErrorReplyBusException;
import org.alljoyn.bus.Variant;
import org.alljoyn.bus.Version;

public class AboutData implements AboutDataListener {

	@Override
	public Map<String, Variant> getAboutData(String language) throws ErrorReplyBusException {
		System.out.println("MyAboutData.getAboutData was called for `" + language + "` language.");
		Map<String, Variant> aboutData = new HashMap<String, Variant>();
		aboutData.put("AppId", new Variant(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }));
		aboutData.put("AppName", new Variant(DEVICE_NAME));
		aboutData.put("DefaultLanguage", new Variant("en"));
		aboutData.put("DeviceId", new Variant(DEVICE_ID));
		aboutData.put("DeviceName", new Variant(DEVICE_NAME));
		aboutData.put("SerialNumber", new Variant(SERIAL_NUMBER));
		aboutData.put("ModelNumber", new Variant(MODEL_NUMBER));
		aboutData.put("SupportedLanguages", new Variant(new String[] { "en" }));
		aboutData.put("FirmwareVersion", new Variant(FIRMWARE_VERSION));
		aboutData.put("AJSoftwareVersion", new Variant(Version.get()));
		aboutData.put("SoftwareVersion", new Variant(FIRMWARE_VERSION));
		aboutData.put("Manufacturer", new Variant(MANUFACTURER));
		aboutData.put("Description", new Variant(DESCRIPTION));
		return aboutData;
	}

	@Override
	public Map<String, Variant> getAnnouncedAboutData() throws ErrorReplyBusException {
		System.out.println("MyAboutData.getAnnouncedAboutData was called.");
		Map<String, Variant> aboutData = new HashMap<String, Variant>();
		aboutData.put("AppId", new Variant(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 }));
		aboutData.put("AppName", new Variant(DEVICE_NAME));
		aboutData.put("DefaultLanguage", new Variant("en"));
		aboutData.put("DeviceId", new Variant(DEVICE_ID));
		aboutData.put("DeviceName", new Variant(DEVICE_NAME));
		aboutData.put("SerialNumber", new Variant(SERIAL_NUMBER));
		aboutData.put("ModelNumber", new Variant(MODEL_NUMBER));
		aboutData.put("SupportedLanguages", new Variant(new String[] { "en" }));
		aboutData.put("FirmwareVersion", new Variant(FIRMWARE_VERSION));
		aboutData.put("AJSoftwareVersion", new Variant(Version.get()));
		aboutData.put("SoftwareVersion", new Variant(FIRMWARE_VERSION));
		aboutData.put("Manufacturer", new Variant(MANUFACTURER));
		aboutData.put("Description", new Variant(DESCRIPTION));
		return aboutData;
	}

}