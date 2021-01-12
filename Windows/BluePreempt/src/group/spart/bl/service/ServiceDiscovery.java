package group.spart.bl.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import group.spart.bl.service.local.DeviceDiscovery;
import group.spart.bl.service.remote.ServiceFilter;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 26, 2020 11:53:58 PM 
 */
public class ServiceDiscovery implements Discoverable {
	private static ServiceDiscovery fInstance;
	private static final Object fServiceSearchCompleted = new Object();
	
	private int[] fAttrIDs = new int[] {  AssignedUUIDs.PrimaryLanguageOffsetBase };
	private UUID[] fServiceClassUUIDs = new UUID[] { new UUID(AssignedUUIDs.RFCOMM) };
	private ServiceFilter fServiceFilter;
	
	private List<RemoteDevice> fDevices;
	private List<ServiceRecord> fServicesFound = new ArrayList<>();
	
	 static {
		 synchronized (ServiceDiscovery.class) {
			 if(fInstance == null) {
				 fInstance = new ServiceDiscovery();
			 }
		}
	}
	
	private ServiceDiscovery() { }
	
	public static ServiceDiscovery instance(RemoteDevice device, int[] attrIDs, UUID[] serviceClassUUIDs) {
		return instance(Arrays.asList(new RemoteDevice[] { device }), attrIDs, serviceClassUUIDs);
	}
	
	public static ServiceDiscovery instance(List<RemoteDevice> devices, int[] attrIDs, UUID[] serviceClassUUIDs) {
		fInstance.servicesFound().clear();
		fInstance.setFilter(null);
		fInstance.fAttrIDs = attrIDs;
		fInstance.fServiceClassUUIDs = serviceClassUUIDs;
		return fInstance.setDevices(devices);
	}
	
	public List<ServiceRecord> servicesFound() {
		return fServicesFound;
	}
	
	public ServiceDiscovery setFilter(ServiceFilter filter) {
		fServiceFilter = filter;
		return fInstance;
	}

	/**
	 * @see group.spart.bl.service.Discoverable#discover()
	 */
	@Override
	public boolean discover() {

		try {
			for (RemoteDevice btDevice : fDevices) {
				synchronized (fServiceSearchCompleted) {
					System.out.println("Search services on " 
							+ btDevice.getFriendlyName(false) + " @ "
							+ btDevice.getBluetoothAddress());

					DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
					agent.searchServices(fAttrIDs, fServiceClassUUIDs, btDevice, new DSDListener());
					fServiceSearchCompleted.wait();
				}
			}
			return true;
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private class DSDListener implements DiscoveryListener {
		@Override
		public void serviceSearchCompleted(int transID, int respCode) {
			System.out.println("  > service search completed!");
			synchronized (fServiceSearchCompleted) {
				fServiceSearchCompleted.notifyAll();
			}
		}
	
		@Override
		public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
			for (int idx = 0; idx < servRecord.length; idx++) {
				ServiceRecord record = servRecord[idx];
				if(fServiceFilter == null) {
					fServicesFound.add(record);
					continue;
				}
				
				if(fServiceFilter.filter(record)) {
					fServicesFound.add(record);
				}
			}
		}
	
		@Override
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) { }
	
		@Override
		public void inquiryCompleted(int discType) { }		
	}

	private ServiceDiscovery setDevices(List<RemoteDevice> devices) {
		fDevices = devices;
		return fInstance;
	}
	
	public static void main(String[] args) {
		DeviceDiscovery discovery = new DeviceDiscovery();
		discovery.discover();
		List<RemoteDevice> devices = discovery.foundDevices();
		ServiceDiscovery.instance(devices,  
				new int[] {  AssignedUUIDs.PrimaryLanguageOffsetBase, AssignedUUIDs.ServiceAvailability },
				new UUID[] { new UUID(AssignedUUIDs.RFCOMM) }).discover();
	}
}
