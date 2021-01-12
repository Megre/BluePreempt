package group.spart.bl.service.remote;

import java.util.Enumeration;
import java.util.List;
import static java.lang.System.out;

import javax.bluetooth.DataElement;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

import group.spart.bl.service.AssignedUUIDs;
import group.spart.bl.service.Discoverable;
import group.spart.bl.service.ServiceDiscovery;
import group.spart.bl.service.ServiceState;
import group.spart.bl.service.local.DeviceDiscovery;
import group.spart.bl.service.local.DisconnectService;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 26, 2020 11:53:58 PM 
 */
public class DisconnectServiceDiscovery implements Discoverable {
	private static ServiceDiscovery fServiceDiscovery;
	private static DisconnectServiceDiscovery fInstance;
	
	static {
		synchronized (DisconnectServiceDiscovery.class) {
			if(fInstance == null) {
				fInstance = new DisconnectServiceDiscovery();
			}
		}
	}
	
	public static DisconnectServiceDiscovery instance(List<RemoteDevice> devices) {
		fServiceDiscovery = ServiceDiscovery.instance(devices, 
				new int[] {  AssignedUUIDs.PrimaryLanguageOffsetBase, AssignedUUIDs.ServiceID,
						AssignedUUIDs.ServiceAvailability, AssignedUUIDs.ServiceClassIDList},
				new UUID[] { new UUID(AssignedUUIDs.RFCOMM) });
		fServiceDiscovery.setFilter(fInstance.new DSFilter());
		return fInstance;
	}
	
	public boolean discover() {
		return fServiceDiscovery.discover();
	}
	
	public List<ServiceRecord> servicesFound() {
		return fServiceDiscovery.servicesFound();
	}

	private class DSFilter implements ServiceFilter {

		@Override
		public boolean filter(ServiceRecord serviceRecord) {
			String url = serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			if (url == null) return false;

			DataElement serviceName = serviceRecord.getAttributeValue(AssignedUUIDs.PrimaryLanguageOffsetBase);
			String strServiceName = (serviceName == null?"(unknown)":serviceName.getValue().toString());
			out.println("  > service " + strServiceName + " @ " + url);
			out.println("    service state " + (isServiceAvailable(serviceRecord)?"available":"unavailable"));
			
			DataElement serviceID = serviceRecord.getAttributeValue(AssignedUUIDs.ServiceID);
			out.println("    service id: " + (serviceID == null?"(unknown)":serviceID.getValue()));
			
			DataElement serviceClassIDList = serviceRecord.getAttributeValue(AssignedUUIDs.ServiceClassIDList);
			if(serviceClassIDList != null && serviceClassIDList.getDataType() == DataElement.DATSEQ) {
				Enumeration<?> ids = (Enumeration<?>) serviceClassIDList.getValue();
				while(ids.hasMoreElements()) {
					UUID uuid = (UUID) ((DataElement) ids.nextElement()).getValue();
					out.println("    service class id: " + uuid.toString());
					
					if(uuid.equals(DisconnectService.DISCONNECT_SERVICE_UUID)) {
						out.println("    ↑ matched service");
						return true;
					}
				}
			}
			
//			if (strServiceName.equals(DisconnectService.DISCONNECT_SERVICE_NAME)) {
//				out.println("    matched service");
//				return true;
//			} 
			return false;
		}
		
		private boolean isServiceAvailable(ServiceRecord serviceRecord) {
			DataElement canUse = serviceRecord.getAttributeValue(AssignedUUIDs.ServiceAvailability);
			return canUse != null && canUse.getLong() == ServiceState.Available;
		}
	}
	
	public static void main(String[] args) {
		DeviceDiscovery discovery = new DeviceDiscovery();
		discovery.discover();
		List<RemoteDevice> devices = discovery.foundDevices();
		DisconnectServiceDiscovery.instance(devices).discover();
	}
}
