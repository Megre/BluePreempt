package group.spart.bl.service.remote;

import java.io.IOException;

import javax.bluetooth.RemoteDevice;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 29, 2020 11:06:56 PM 
 */
public class SavedRemoteDevice extends RemoteDevice {

	/**
	 * @param address
	 */
	public SavedRemoteDevice(String address) {
		super(address);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(new SavedRemoteDevice("DCF09037F89C").getFriendlyName(false));
	}

}
