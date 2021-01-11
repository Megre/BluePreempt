package group.spart.bl.service.remote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 2, 2021 12:30:16 AM 
 */
public class RemoteDevice {

	private BluetoothDevice fDevice;

	public RemoteDevice(String address) {
		fDevice = fromAddress(address);
	}

	public BluetoothDevice getDevice() {
		return fDevice;
	}

	public static BluetoothDevice fromAddress(String address) {
		return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
	}

	@Override
	public boolean equals(Object object) {
		if(object instanceof BluetoothDevice) {
			return fDevice.equals(object);
		}

		if(object instanceof RemoteDevice) {
			return fDevice.equals(((RemoteDevice) object).fDevice);
		}

		return false;
	}

}
