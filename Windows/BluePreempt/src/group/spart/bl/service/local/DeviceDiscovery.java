package group.spart.bl.service.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

import group.spart.bl.service.Discoverable;

/**
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 25, 2020 11:50:04 PM
 */
public class DeviceDiscovery implements Discoverable {
	private List<RemoteDevice> fAudios = new ArrayList<>();
	private List<RemoteDevice> fMasters = new ArrayList<>();
	
	private final Object fInquiryCompleted = new Object();
	
	public boolean discover() {
		synchronized (fInquiryCompleted) {
			try {
				DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
				boolean started = agent.startInquiry(DiscoveryAgent.GIAC, new DDListener());
				if (started) {
					System.out.println("Scanning devices...");
					fInquiryCompleted.wait();
					System.out.println("  > " + fAudios.size() + " audio/video device(s) found, " 
							+ fMasters.size() + " phone/computer device(s) found");
					return true;
				}
			} catch (BluetoothStateException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			
			return false;
		}
	}
	
	public HashMap<String, RemoteDevice> loadCachedDevices() {
		synchronized (fInquiryCompleted) {
			HashMap<String, RemoteDevice> cachedDevices = new HashMap<>();
			try {
				loadCachedDevices(cachedDevices);
				System.out.println(cachedDevices.size() + " cached device(s) retrived");
			} catch (BluetoothStateException e) {
				e.printStackTrace();
			}
			
			return cachedDevices;
		}
	}

	public List<RemoteDevice> foundHeadsets() {
		return fAudios;
	}
	
	public List<RemoteDevice> foundMasters() {
		return fMasters;
	}
	
	public List<RemoteDevice> foundDevices() {
		List<RemoteDevice> allDevices = new ArrayList<>();
		allDevices.addAll(fAudios);
		allDevices.addAll(fMasters);
		return allDevices;
	}
	
	private void loadCachedDevices(HashMap<String, RemoteDevice> cachedDevices) throws BluetoothStateException {
		DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
		RemoteDevice[] devices = agent.retrieveDevices(DiscoveryAgent.PREKNOWN);
		if(devices != null) {
			for(RemoteDevice device: devices) {
				if(!cachedDevices.containsKey(device.getBluetoothAddress())) cachedDevices.put(device.getBluetoothAddress(), device);
			}
		}
		
		devices = agent.retrieveDevices(DiscoveryAgent.CACHED);
		if(devices != null) {
			for(RemoteDevice device: devices) {
				if(!cachedDevices.containsKey(device.getBluetoothAddress())) cachedDevices.put(device.getBluetoothAddress(), device);
			}
		}
	}
	
	private void addDevice(RemoteDevice device, DeviceClass classOfDevice) {
		if(DeviceType.isAudioOrVideo(classOfDevice.getMajorDeviceClass())) {
			fAudios.add(device);
		}
		else if(DeviceType.isMaster(classOfDevice.getMajorDeviceClass())){
			fMasters.add(device);
		}
	}
	
	private class DDListener implements DiscoveryListener {
		@Override
		public void deviceDiscovered(RemoteDevice device, DeviceClass classOfDevice) {
			try {
				synchronized (fInquiryCompleted) {
					System.out.println("  > " + device.getFriendlyName(false) + " @ " + device.getBluetoothAddress());
					System.out.println(String.format("    service classes: 0x%x, major: 0x%x, minor: 0x%x", 
							classOfDevice.getServiceClasses(),
							classOfDevice.getMajorDeviceClass(),
							classOfDevice.getMinorDeviceClass()));
					
					addDevice(device, classOfDevice);
				}
			} catch (IOException cantGetDeviceName) {
				
			}
		}

		@Override
		public void inquiryCompleted(int discType) {
			synchronized (fInquiryCompleted) { fInquiryCompleted.notifyAll(); }
		}

		@Override
		public void serviceSearchCompleted(int transID, int respCode) { }

		@Override
		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) { }
	}
	
	public static void main(String[] args) {
		new DeviceDiscovery().discover();
	}
}
