package group.spart.bl.util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 27, 2020 12:04:45 AM 
 */
public class Utils {

	public static String friendlyAddress(String bluetoothAddress) {
		StringBuffer buffer = new StringBuffer();
		for(int idx=0; idx<bluetoothAddress.length(); ++idx) {
			if(idx > 0 && idx%2 == 0) buffer.append(":");
			
			buffer.append(bluetoothAddress.charAt(idx));
		}
		return buffer.toString();
	}
	
	public static String deviceName(RemoteDevice device) {
		try {
			if(device != null)
				return device.getFriendlyName(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Unknown device";
	}
	
	public static String deviceName() {
		try {
			return LocalDevice.getLocalDevice().getFriendlyName();
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		}
		
		return "Unknown device";
	}
	
	public static String hostName() {
		String hostName = "Unknown host";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			hostName = addr.getHostName().toString();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}  
		return hostName;
	}
	
	public static String jarPath() {
		File file = new File("");
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(Utils.friendlyAddress("48D84507E49E"));
		System.out.println(jarPath());
		
	}
}
