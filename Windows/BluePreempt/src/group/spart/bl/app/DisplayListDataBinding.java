package group.spart.bl.app;

import java.awt.List;
import java.util.ArrayList;
import java.util.Optional;

import group.spart.bl.service.InfoObserver;
import group.spart.bl.service.InfoSubject;
import group.spart.bl.service.local.DeviceType;
import group.spart.bl.service.remote.RemoteDeviceInfo;


/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 30, 2020 5:21:57 PM 
 */
public class DisplayListDataBinding implements InfoSubject {

	private java.util.List<RemoteDeviceInfo> fDeviceInfos,
		fHeadsetInfos, fMasterInfos;
	private List fHeadsetList, fMasterList;
	private int fMarkedIdx = -1;
	
	private java.util.List<InfoObserver> fInfoObservers = new ArrayList<>();
	
	public DisplayListDataBinding(List headsetList, List masterList) {
		fHeadsetList = headsetList;
		fMasterList = masterList;
	}
	
	public void markDevice(int markedIdx) {
		markListItem(fHeadsetList, markedIdx);
	}
	
	public void update(java.util.List<RemoteDeviceInfo> deviceInfos) {
		if(deviceInfos == null || deviceInfos.size() == 0) return;
		
		synchronized(this) {
			RemoteDeviceInfo markedDeviceInfo = Optional.ofNullable(getMarkedDeviceInfo()).orElse(null);
			
			fDeviceInfos = deviceInfos;
			
			fHeadsetList.removeAll();
			fMasterList.removeAll();
			getHedsetSource().clear();
			getMasterSource().clear();
			
			int headsetIdx = 0;
			for(RemoteDeviceInfo info: deviceInfos) {
				if(info.getfType() == DeviceType.HEADSET) {
					if(info.equals(markedDeviceInfo)) {
						fMarkedIdx = headsetIdx;
					}
					fHeadsetList.add(info.getfName());
					fHeadsetInfos.add(info);
					++headsetIdx;
				}
				else if(info.getfType() == DeviceType.MASTER) {
					fMasterList.add(info.getfName());
					fMasterInfos.add(info);
				}
			}
			markDevice(fMarkedIdx);
			notifyObservers();
		}
	}
	
	public RemoteDeviceInfo getMarkedDeviceInfo() {
		return Optional.ofNullable(fHeadsetInfos)
			.filter(infos -> fMarkedIdx >= 0 && fMarkedIdx < infos.size())
			.map(infos -> infos.get(fMarkedIdx))
			.orElse(null);
	}
	
	public int getMarkedDeviceIdx() {
		return fMarkedIdx;
	}
	
	public java.util.List<RemoteDeviceInfo> getSource() {
		if(fDeviceInfos == null) {
			fDeviceInfos = new ArrayList<>();
		}
		return fDeviceInfos;
	}
	
	public java.util.List<RemoteDeviceInfo> getHedsetSource() {
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
	
	private void markListItem(List list, int index) {
		if(index < 0 || index >= list.getItemCount()) return;
		
		synchronized (this) {
			if(fMarkedIdx >= 0 && fMarkedIdx < list.getItemCount()) {
				list.remove(fMarkedIdx);
				list.add(getMarkedDeviceInfo().getfName(), fMarkedIdx);
			}
			
			fMarkedIdx = index;
			list.remove(index);
			list.add("* " + getMarkedDeviceInfo().getfName(), index);
		}
	}

	/**
	 * @see group.spart.bl.service.InfoSubject#attachObserver(group.spart.bl.service.InfoObserver)
	 */
	@Override
	public void attachObserver(InfoObserver observer) {
		fInfoObservers.add(observer);
	}

	/**
	 * @see group.spart.bl.service.InfoSubject#notifyObservers()
	 */
	@Override
	public void notifyObservers() {
		for(InfoObserver observer: fInfoObservers) {
			observer.updateInfo();
		}
	}
	
}
