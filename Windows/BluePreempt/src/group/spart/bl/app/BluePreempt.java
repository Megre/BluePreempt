package group.spart.bl.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.bluetooth.RemoteDevice;

import group.spart.bl.cmd.CommandExecutorCallback;
import group.spart.bl.service.InfoObserver;
import group.spart.bl.service.SyncCallback;
import group.spart.bl.service.local.DeviceDiscovery;
import group.spart.bl.service.local.DeviceType;
import group.spart.bl.service.local.DisconnectService;
import group.spart.bl.service.local.HeadsetConnectionDetector;
import group.spart.bl.service.local.HeadsetConnector;
import group.spart.bl.service.local.VolumeSetter;
import group.spart.bl.service.remote.DisconnectServiceDiscovery;
import group.spart.bl.service.remote.DisconnectionState;
import group.spart.bl.service.remote.RemoteDisconnector;
import group.spart.bl.service.remote.RemoteDeviceInfo;
import group.spart.bl.util.Utils;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn,
 * @version created on: Dec 27, 2020 12:02:56 AM 
 */
public class BluePreempt implements InfoObserver {
	private List<RemoteDevice> fHeadsets = new ArrayList<>();
	private List<RemoteDevice> fMasters = new ArrayList<>();
	private List<RemoteDeviceInfo> fDeviceInfos = new ArrayList<>();
	private ConcurrentHashMap<RemoteDevice, Boolean> fHeadsetConnectionState;
	
	private CountDownLatch fLatch;
	private DisplayListDataBinding fDataBinding;
	
	public BluePreempt(DisplayListDataBinding dataBinding) {
		fDataBinding = dataBinding;
		DisconnectService.instance(this).startService();
	}
	
	public RemoteDevice getMarkedHeadset() {
		if(fDataBinding.getMarkedDeviceInfo() == null) return null;
		
		return fDataBinding.getMarkedDeviceInfo().getfDevice();
	}
	
	public void searchDevices(SyncCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DeviceDiscovery discovery = new DeviceDiscovery();
				discovery.discover();
				fHeadsets = discovery.foundHeadsets();
				fMasters = discovery.foundMasters();
				callback.invoke(true, makeRemodeDeviceInfo(fHeadsets, fMasters));
			}
		}).start();
	}
	
	public ConcurrentHashMap<RemoteDevice, Boolean> detectConnectionState() {
		fLatch = new CountDownLatch(fHeadsets.size());
		ConcurrentHashMap<RemoteDevice, Boolean> stateMap = new ConcurrentHashMap<>();
		for(RemoteDevice device: fHeadsets) {
			CommandExecutorCallback callback = new HSConnectionDetectorCallback(device, stateMap);
			HeadsetConnectionDetector detector = new HeadsetConnectionDetector(callback);
			detector.detect(device.getBluetoothAddress());
		}
		try {
			fLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		
		fHeadsetConnectionState = stateMap;
		return stateMap;
	}
	
	public ConcurrentHashMap<RemoteDevice, Boolean> getHeadsetConnectionState() {
		return fHeadsetConnectionState;
	}
	
	public void connectHeadset(final RemoteDevice selectedHeadset, SyncCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// save current volume
				int volume = new VolumeSetter().getVolume();
				
				// disconnect the headset on remote device
				GUI.instance().notifyUser("disconnecting headsets on remote devices...");
				DisconnectionState state = new RemoteDisconnector().disconnect(fMasters);
				GUI.instance().notifyUser(state.toString());
				
				// try to connect the headset on local device
//				boolean success = HeadsetConnector.connectHandsFreeService(selectedHeadset);
				
				boolean success = (state == DisconnectionState.Disconnected);
				if(success) {
					GUI.instance().notifyUser("connecting headset...");
					success = HeadsetConnector.connect(selectedHeadset.getBluetoothAddress());
				}
				
				callback.invoke(success, volume);
			}
			
		}).start();

	}
	
	private List<RemoteDeviceInfo> makeRemodeDeviceInfo(List<RemoteDevice> headsets, List<RemoteDevice> masters) {
		fDeviceInfos.clear();
		for(RemoteDevice device: headsets) {
			fDeviceInfos.add(new RemoteDeviceInfo(Utils.deviceName(device), device.getBluetoothAddress(), DeviceType.HEADSET));
		}
		for(RemoteDevice device: masters) {
			fDeviceInfos.add(new RemoteDeviceInfo(Utils.deviceName(device), device.getBluetoothAddress(), DeviceType.MASTER));
		}
		return fDeviceInfos;
	}
	
	private void searchDisconnectServices() {
		DisconnectServiceDiscovery disServices = DisconnectServiceDiscovery.instance(fHeadsets);
		disServices.discover();
	}
	
	private class HSConnectionDetectorCallback implements CommandExecutorCallback {
		private RemoteDevice fDevice;
		private ConcurrentHashMap<RemoteDevice, Boolean> fStateMap;
		
		public HSConnectionDetectorCallback(RemoteDevice device, ConcurrentHashMap<RemoteDevice, Boolean> stateMap) {
			fDevice = device;
			fStateMap = stateMap;
		}

		/**
		 * @see group.spart.bl.cmd.CommandExecutorCallback#invoke(boolean, java.util.List, java.util.List)
		 */
		@Override
		public void invoke(boolean success, List<String> stdOutput, List<String> errorOutput) {
			String deviceName = null;
			try {
				deviceName = fDevice.getFriendlyName(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			synchronized (BluePreempt.this) {
				if(!success) { 
					System.out.println(deviceName + ": get connection state failed.");
				}
				else if(stdOutput.size() > 0) {
					System.out.println(deviceName + ": " + (stdOutput.get(0).contains(" connected")?"connected":"disconnected"));
				}
				
				fStateMap.put(fDevice, success && stdOutput.size()>0 && stdOutput.get(0).contains(" connected"));
				fLatch.countDown();
			}

		}
		
	};
	
	/**
	 * @see group.spart.bl.service.InfoObserver#updateInfo()
	 */
	@Override
	public void updateInfo() {
		fHeadsets.clear();
		for(RemoteDeviceInfo deviceInfo: fDataBinding.getHedsetSource()) {
			fHeadsets.add(deviceInfo.getfDevice());
		}
		fMasters.clear();
		for(RemoteDeviceInfo deviceInfo: fDataBinding.getMasterSource()) {
			fMasters.add(deviceInfo.getfDevice());
		}
	}
	
	public static void main(String[] args) throws IOException {
		BluePreempt instance = new BluePreempt(null);
		instance.searchDevices(new SyncCallback() {
			@Override
			public void invoke(boolean success, Object returnValues) {
				instance.detectConnectionState();		
				instance.searchDisconnectServices();
			}
		});
	}
}
