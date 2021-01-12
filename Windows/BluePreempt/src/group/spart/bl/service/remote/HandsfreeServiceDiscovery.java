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

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 26, 2020 11:53:58 PM 
 */
public class HandsfreeServiceDiscovery implements Discoverable {
	private static ServiceDiscovery fServiceDiscovery;
	private static HandsfreeServiceDiscovery fInstance;
	
	static {
		synchronized (HandsfreeServiceDiscovery.class) {
			if(fInstance == null) {
				fInstance = new HandsfreeServiceDiscovery();
			}
		}
	}
	
	public static HandsfreeServiceDiscovery instance(RemoteDevice device) {
		fServiceDiscovery = ServiceDiscovery.instance(device, 
				new int[] { AssignedUUIDs.ServiceClassIDList },
				new UUID[] { new UUID(AssignedUUIDs.RFCOMM) });
		fServiceDiscovery.setFilter(fInstance.new HSFilter());
		return fInstance;
	}
	
	public boolean discover() {
		return fServiceDiscovery.discover();
	}
	
	public List<ServiceRecord> servicesFound() {
		return fServiceDiscovery.servicesFound();
	}

	private class HSFilter implements ServiceFilter {

		@Override
		public boolean filter(ServiceRecord serviceRecord) {
			String url = serviceRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			if (url == null) return false;
			
			DataElement serviceClass = serviceRecord.getAttributeValue(AssignedUUIDs.ServiceClassIDList);
			if(serviceClass != null && serviceClass.getDataType() == DataElement.DATSEQ) {
				@SuppressWarnings("unchecked")
				Enumeration<DataElement> sc = (Enumeration<DataElement>) serviceClass.getValue();
				while(sc.hasMoreElements()) {
					if(sc.nextElement().getValue().equals(new UUID(AssignedUUIDs.Handsfree))) {
						out.println("  > matched handsfree service: " + url);
						return true;
					}
				}
			}
			return false;
		}
		
	}
	
	public static void main(String[] args) {
		HandsfreeServiceDiscovery.instance(new SavedRemoteDevice("48D84507E49E")).discover();
	}
}
