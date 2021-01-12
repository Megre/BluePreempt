package group.spart.bl.service.local;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.bluetooth.BluetoothConnectionException;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import group.spart.bl.cmd.CommandExecutor;
import group.spart.bl.service.AssignedUUIDs;
import group.spart.bl.service.remote.HandsfreeServiceDiscovery;
import group.spart.bl.util.Utils;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 24, 2020 11:02:26 AM 
 */
public class HeadsetConnector {
	private static final String BTCOM_PATH = "btcom.exe";
	
	private static StreamConnection fConnection;
	private static Thread fConnThread;
	
	public static boolean connectHandsFreeService(RemoteDevice device) {
		resetConnection();
		
		HandsfreeServiceDiscovery discovery = HandsfreeServiceDiscovery.instance(device);
		boolean success = discovery.discover();
		
		if(!success) return false;
		
		List<ServiceRecord> services = discovery.servicesFound();
		if(services.size() == 0) return false;
		
		fConnThread = new Thread(new Runnable() {
			public void run() {
				ServiceRecord record = services.get(0);
				try {
					fConnection = (StreamConnection) Connector
							.open(record.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
					if(fConnection == null) return;
					
					byte[] bytes = new byte[1024];
					InputStream stream = fConnection.openInputStream();
					while(stream.read(bytes) > 0) {
						System.out.println(String.valueOf(bytes));
						Thread.sleep(100);
					}
				} catch (BluetoothConnectionException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		});
		fConnThread.start();
		
		return success;
	}
	
	public static boolean connect(String bluetoothAddress) {
		return disconnect(bluetoothAddress)
				&& connect(bluetoothAddress, AssignedUUIDs.Handsfree, true)
				&& connect(bluetoothAddress, AssignedUUIDs.A2DPAudioSink, true);
	}
	
	public static boolean disconnect(String bluetoothAddress) {
		return connect(bluetoothAddress, AssignedUUIDs.Handsfree, false)
				&& connect(bluetoothAddress, AssignedUUIDs.A2DPAudioSink, false);
	}
	
	private static void resetConnection() {
		if(fConnThread == null) return;
		
		try {
			if(fConnection != null) {
				fConnection.close();
				fConnection = null;
			}
			if(fConnThread.isAlive()) fConnThread.interrupt();
				
			while(fConnThread.isAlive()) {
				Thread.sleep(100);
			}
			fConnThread = null;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean connect(String bluetoothAddress, int service, boolean connect) {
		return runAndWait(command(bluetoothAddress, service, connect));
	}
	
	private static boolean runAndWait(List<String> command) {
		CommandExecutor runner = new CommandExecutor(command);
		runner.run();
		
		return runner.success();
	}	
	
	private static List<String> command(String bluetoothAddress, int service, boolean connect) {
		String[] cmd = new String[]{
				BTCOM_PATH,
				"-b\"" + Utils.friendlyAddress(bluetoothAddress) + "\"", 
				connect?"-c":"-r", 
				"-s" + String.format("%04x", service)
			}; 
		return Arrays.asList(cmd);
	}
	
	public static void main(String[] args) {
		System.out.println(connect("48D84507E49E"));
	}
	
}
