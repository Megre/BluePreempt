package group.spart.bl.service.remote;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import group.spart.bl.service.SyncCallback;
import group.spart.bl.service.local.DisconnectService;


/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 28, 2020 9:49:59 AM 
 */
public class RemoteDisconnector {
	private CountDownLatch fLatch;
	
	public RemoteDisconnector() {
		
	}
	
	public DisconnectionState disconnect(List<RemoteDevice> devices) {
		final List<DisconnectionState> resultCollector = new ArrayList<>();
		
		// discover remote disconnection service
		DisconnectServiceDiscovery svrDiscovery = DisconnectServiceDiscovery.instance(devices);
		svrDiscovery.discover();
		List<ServiceRecord> services = svrDiscovery.servicesFound();
		
		// disconnect remote headset
		fLatch = new CountDownLatch(services.size());
		for(ServiceRecord svrRecord: services) {
			String connUrl = svrRecord.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
			disconnectRemoteHeadset(connUrl, new SyncCallback() {
				@Override
				public void invoke(boolean success, Object returnValues) {
					DisconnectionState state = (DisconnectionState) returnValues;
					synchronized (RemoteDisconnector.this) {
						resultCollector.add(state);
					}
					fLatch.countDown();
				}
			});
		}
		
		// wait all remote services to finish
		try {
			fLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for(DisconnectionState state: resultCollector) {
			System.out.println("disconnection result: " + state);
			if(state == DisconnectionState.Disconnected) return DisconnectionState.Disconnected;
		}
		
		return DisconnectionState.Failed;
	}
	
	/**
	 * Connect to discover servcie and read notification
	 * @param connUrl
	 * @return
	 */
	private void disconnectRemoteHeadset(String connUrl, SyncCallback callback) {
		try {
			System.out.println("Connecting to " + connUrl + "...");
			StreamConnection connection = (StreamConnection) Connector.open(connUrl);
			DataInputStream inputStream = connection.openDataInputStream();
			while(true) {
				if(inputStream.available() <= 0) {
					Thread.sleep(500);
					continue;
				}
				
				byte[] bytes = new byte[DisconnectService.MaxMessageLength];
				int length = inputStream.read(bytes);
				System.out.println("received notification: " + new String(bytes, 0, length));
				
				inputStream.close();
				break;
			}
			
		} catch (BluetoothConnectionException e) {
			System.out.println("bluetooth connection exception, error code " + e.getStatus());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		finally {
			callback.invoke(true, DisconnectionState.Disconnected);
		}
	}
	
	
	public static void main(String[] args) {
		
	}
}
