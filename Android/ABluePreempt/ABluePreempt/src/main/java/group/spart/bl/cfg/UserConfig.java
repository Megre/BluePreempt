package group.spart.bl.cfg;

import java.util.ArrayList;
import java.util.List;
import group.spart.bl.app.DisplayListDataBinding;
import group.spart.bl.service.local.DeviceType;
import group.spart.bl.service.remote.RemoteDeviceInfo;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 29, 2020 2:49:54 PM 
 */
public class UserConfig {

	private final String DEVICE_ITEM_NAME = "device";
	private final String MARK_ITEM_NAME = "mark";
	private final String STATE_ITEM_NAME = "state";

	private final String fCfgPath;
	private final List<ConfigItem> fConfigItems;
	private boolean fFirstRun = true;
	private int fMarkedInx = -1;

	public UserConfig(String cfgPath) {
		fCfgPath = cfgPath;
		fConfigItems = ConfigItemFactory.parse(fCfgPath);
	}
	
	public List<RemoteDeviceInfo> getSavedDevices() {
		List<RemoteDeviceInfo> deviceInfos = new ArrayList<>();
		for(int idx=0; idx<fConfigItems.size(); ++idx) {
			ConfigItem item = fConfigItems.get(idx);
			SimpleConfigItem simpleItem = (SimpleConfigItem) item;
			if(DEVICE_ITEM_NAME.equals(simpleItem.getItemName())) {
				deviceInfos.add(new RemoteDeviceInfo(getDeviceName(simpleItem), 
						getDeviceAddress(simpleItem),
						getDeviceType(simpleItem)));
			}
			else if(MARK_ITEM_NAME.equals(simpleItem.getItemName())) {
				try {
					fMarkedInx = Integer.parseInt(item.getValue("index"));
				}
				catch(NumberFormatException e) { e.printStackTrace(); }
			}
			else if(STATE_ITEM_NAME.equals(simpleItem.getItemName())) {
				fFirstRun = ("true".equals(item.getValue("firstRun")));
				System.out.println("first run: " + fFirstRun);
			}
		}
		return deviceInfos;
	}
	
	public int getMarkedDeviceIdx() {
		return fMarkedInx;
	}
	
	public DeviceType getDeviceType(ConfigItem item) {
		return DeviceType.fromString(item.getValue("type"));
	}
	
	public String getDeviceName(ConfigItem item) {
		return item.getValue("name");
	}
	
	public String getDeviceAddress(ConfigItem item) {
		return item.getValue("address");
	}

	public boolean isFirstRun() {
		return fFirstRun;
	}
	
	public void saveConfig(DisplayListDataBinding dataBinding) {
		// devices
		List<RemoteDeviceInfo> headsetInfos = dataBinding.getHeadsetSource();
		List<RemoteDeviceInfo> masterInfos = dataBinding.getMasterSource();
		
		List<ConfigItem> configItems = new ArrayList<>();
		for(RemoteDeviceInfo deviceInfo: headsetInfos) {
			configItems.add(newCongitem(deviceInfo));
		}
		for(RemoteDeviceInfo deviceInfo: masterInfos) {
			configItems.add(newCongitem(deviceInfo));
		}

		// mark
		ConfigItem markItem = new SimpleConfigItem(MARK_ITEM_NAME);
		markItem.addItem("index", new SingleValue(String.valueOf(dataBinding.getMarkedDeviceIdx())));
		configItems.add(markItem);

		// state
		ConfigItem stateItem = new SimpleConfigItem(STATE_ITEM_NAME);
		stateItem.addItem("firstRun", new SingleValue("false"));
		configItems.add(stateItem);

		new ConfigItemsWriter(configItems, fCfgPath).write();
	}
	
	private ConfigItem newCongitem(RemoteDeviceInfo deviceInfo) {
		ConfigItem item = new SimpleConfigItem(DEVICE_ITEM_NAME);
		item.addItem("name", new SingleValue(deviceInfo.getName()));
		item.addItem("address", new SingleValue(deviceInfo.getAddress()));
		item.addItem("type", new SingleValue(deviceInfo.getType().toString()));
		return item;
	}
	
}
