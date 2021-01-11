package group.spart.bl.app;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

import group.spart.abl.app.ABluetoothManager;
import group.spart.abl.ui.DisplayList;
import group.spart.bl.service.local.DeviceType;
import group.spart.bl.service.remote.RemoteDeviceInfo;


/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 30, 2020 5:21:57 PM 
 */
public class DisplayListDataBinding {

	private java.util.List<RemoteDeviceInfo> fHeadsetInfos, fMasterInfos;
	private final DisplayList fHeadsetList, fMasterList;

	public DisplayListDataBinding(DisplayList headsetList, DisplayList masterList) {
		fHeadsetList = headsetList;
		fMasterList = masterList;
	}
	
	public void markDevice(int markedIdx) {
		if(markedIdx < 0 || markedIdx >= fHeadsetList.getItemCount()) return;

		synchronized (this) {
			fHeadsetList.setMark(markedIdx);
		}
	}

	public void update(java.util.List<RemoteDeviceInfo> deviceInfos) {
		if(deviceInfos == null || deviceInfos.size() == 0) return;
		
		synchronized(this) {
			RemoteDeviceInfo markedDeviceInfo = getMarkedDeviceInfo();
			
			getHeadsetSource().clear();
			getMasterSource().clear();
			fHeadsetList.removeAll();
			fMasterList.removeAll();

			int markedIdx = -1, headsetIdx = 0;
			for(RemoteDeviceInfo info: deviceInfos) {
				if(info.getType() == DeviceType.HEADSET) {
					if(info.equals(markedDeviceInfo)) {
						markedIdx = headsetIdx;
					}
					fHeadsetList.add(info.getName());
					fHeadsetInfos.add(info);
					++headsetIdx;
				}
				else if(info.getType() == DeviceType.MASTER) {
					fMasterList.add(info.getName());
					fMasterInfos.add(info);
				}
			}
			markDevice(markedIdx);
		}
	}
	
	public RemoteDeviceInfo getMarkedDeviceInfo() {
		if(fHeadsetInfos == null) return null;
		if(fHeadsetList.getMark() < 0 || fHeadsetList.getMark() >= fHeadsetInfos.size()) return null;
		
		return fHeadsetInfos.get(fHeadsetList.getMark());
	}
	
	public int getMarkedDeviceIdx() {
		return fHeadsetList.getMark();
	}

	public java.util.List<RemoteDeviceInfo> getHeadsetSource() {
		if(fHeadsetInfos == null) {
			fHeadsetInfos = new ArrayList<>();
		}
		
		return fHeadsetInfos;
	}
	
	public java.util.List<RemoteDeviceInfo> getMasterSource() {
		if(fMasterInfos == null) {
			fMasterInfos = new ArrayList<>();
		}
		
		return fMasterInfos;
	}

	public BluetoothDevice[] getHeadsetDevices() {
		java.util.List<RemoteDeviceInfo> masters = getMasterSource();
		BluetoothDevice[] devices = new BluetoothDevice[masters.size()];
		for(int c=0; c<devices.length; ++c) {
			devices[c] = ABluetoothManager.adapter().getRemoteDevice(masters.get(c).getAddress());
		}
		return devices;
	}

}
