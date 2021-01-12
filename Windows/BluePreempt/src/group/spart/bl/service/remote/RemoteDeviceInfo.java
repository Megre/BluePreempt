package group.spart.bl.service.remote;

import java.util.Objects;

import javax.bluetooth.RemoteDevice;

import group.spart.bl.service.local.DeviceType;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 30, 2020 12:24:17 AM 
 */
public class RemoteDeviceInfo {

	private String fName, fAddress;
	DeviceType fType;
	
	private RemoteDevice fDevice;
	
	public RemoteDeviceInfo(String name, String address, DeviceType type) {
		fName = name;
		fAddress = address;
		fType = type;
		
		fDevice = new SavedRemoteDevice(address);
	}
	
	@Override
	public boolean equals(Object object) {
		if(!(object instanceof RemoteDeviceInfo)) return false;
		
		return Objects.equals(fAddress, ((RemoteDeviceInfo) object).fAddress);
	}
	
	@Override
	public String toString() {
		return "{" + fName + ", " + fAddress + ", " + fType + "}";
	}
	
	/**
	 * @return the fDevice
	 */
	public RemoteDevice getfDevice() {
		return fDevice;
	}

	/**
	 * @return the fName
	 */
	public String getfName() {
		return fName;
	}

	/**
	 * @return the fAddress
	 */
	public String getfAddress() {
		return fAddress;
	}

	/**
	 * @return the fType
	 */
	public DeviceType getfType() {
		return fType;
	}
	
	
}
