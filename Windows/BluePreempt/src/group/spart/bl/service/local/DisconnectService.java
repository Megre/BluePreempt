package group.spart.bl.service.local;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.ServiceRegistrationException;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import group.spart.bl.app.BluePreempt;
import group.spart.bl.app.GUI;
import group.spart.bl.service.AssignedUUIDs;
import group.spart.bl.service.ServiceState;
import group.spart.bl.service.remote.DisconnectionState;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 26, 2020 11:54:57 PM 
 */
public class DisconnectService implements Runnable {
	public static final String DISCONNECT_SERVICE_NAME = "BluePreempt Disconnect Service";
	private static final String DISCONNECT_SERVICE_UUID_STR = "e5dd63f3-c347-4bc8-8c55-5b31caf789f6";
	public static final UUID DISCONNECT_SERVICE_UUID = new UUID(DISCONNECT_SERVICE_UUID_STR.replace("-", ""), false);
	public static final int MaxMessageLength = 1024;

	private BluePreempt fBluePreempt;
	
	private String fUrl; 
	private StreamConnectionNotifier fNotifier;
	private LocalDevice fLocalDevice;
	
	private Thread fDisconnectThread = new Thread(this);
	private Thread fServieAvailabilityThread;
	private static DisconnectService fInstance;
	
	public static DisconnectService instance(BluePreempt bluePreempt) {
		if(fInstance == null) {
			fInstance = new DisconnectService(bluePreempt);
		}
		return fInstance;
	}
	
	private DisconnectService(BluePreempt bluePreempt) {
		fBluePreempt = bluePreempt;
		
		try {
			fLocalDevice = LocalDevice.getLocalDevice();
			fUrl = "btspp://localhost:" + DISCONNECT_SERVICE_UUID + ";name=" + DISCONNECT_SERVICE_NAME;
			fNotifier = (StreamConnectionNotifier) Connector.open(fUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startService() {
		if(!fDisconnectThread.isAlive()) {
			fDisconnectThread.start();
		}
		
//		detectHeadsetConnectionState();
	}
	
	@Override
	public synchronized void run() {
		System.out.println("Disconnection service started. Wait for client...");
		StreamConnection streamConnection;
		
		while(true) {
			try {
				streamConnection = fNotifier.acceptAndOpen(); // block and wait
				
				GUI.instance().notifyUser("headset disconnection request accepted");
				
				// get marked headset
				if(fBluePreempt.getMarkedHeadset() == null) {
					GUI.instance().notifyUser("no headset marked. Please mark a headset that allows remote devices to disconnect");
					sendNotify(streamConnection, false);
					continue;
				}
				
				// disconnect local headset and notify remote service
				RemoteDevice connectedHeadset = fBluePreempt.getMarkedHeadset(); 
				boolean disconnected = HeadsetConnector.disconnect(connectedHeadset.getBluetoothAddress());
				sendNotify(streamConnection, disconnected);
				
				GUI.instance().notifyUser("headset disconnected");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendNotify(StreamConnection streamConnection, boolean disconnected) throws IOException {
		OutputStream outputStream = streamConnection.openOutputStream();
		outputStream.write((disconnected?DisconnectionState.Disconnected:DisconnectionState.Failed).toString().getBytes());
		outputStream.close();
		streamConnection.close();
	}
	
	@SuppressWarnings("unused")
	private void detectHeadsetConnectionState() {
		if(fServieAvailabilityThread == null) {
			fServieAvailabilityThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(true) {
						setAvailable(getConnectedHeadset() != null);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
			fServieAvailabilityThread.start();
		}
	}
	
	private RemoteDevice getConnectedHeadset() {
		ConcurrentHashMap<RemoteDevice, Boolean> stateMap = fBluePreempt.getHeadsetConnectionState();
		if(stateMap == null) return null;
		
		RemoteDevice connectedHeadset = null;
		for(java.util.Map.Entry<RemoteDevice, Boolean> entry : stateMap.entrySet()) {
			if(entry.getValue()) {
				connectedHeadset = entry.getKey();
				break;
			}
		}
		return connectedHeadset;
	}
	
	private void setAvailable(boolean available) {
		ServiceRecord serviceRecord = fLocalDevice.getRecord(fNotifier);
		DataElement canUse = serviceRecord.getAttributeValue(AssignedUUIDs.ServiceAvailability);
		if(canUse != null) {
			if(!((canUse.getLong() == ServiceState.Available) ^ available)) {
				return;
			}
		}
		
		serviceRecord.setAttributeValue(AssignedUUIDs.ServiceAvailability,
				new DataElement(DataElement.U_INT_1, available?ServiceState.Available:ServiceState.Busy));
		try {
			fLocalDevice.updateRecord(serviceRecord);
			System.out.println("disconnect service " + (available?"enabled":"disabled"));
		} catch (ServiceRegistrationException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		System.out.println(java.util.UUID.randomUUID().toString());

	}
	
	
}
