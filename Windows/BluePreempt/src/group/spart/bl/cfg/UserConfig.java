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

	private String fCfgPath;
	private final String fDeviceItemName = "device";
	private List<ConfigItem> fConfigItems;
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
			if("device".equals(simpleItem.getItemName()) && fDeviceItemName.equals(simpleItem.getItemName())) {
				deviceInfos.add(new RemoteDeviceInfo(getDeviceName(simpleItem), 
						getDeviceAddress(simpleItem),
						getDeviceType(simpleItem)));
			}
			else if("mark".equals(simpleItem.getItemName())) {
				try {
					fMarkedInx = Integer.valueOf(item.getValue("index"));
				}
				catch(NumberFormatException e) { }
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
	
	public void saveConfig(DisplayListDataBinding dataBinding) {
		List<RemoteDeviceInfo> headsetInfos = dataBinding.getHedsetSource();
		List<RemoteDeviceInfo> masterInfos = dataBinding.getMasterSource();
		
		List<ConfigItem> configItems = new ArrayList<>();
		for(RemoteDeviceInfo deviceInfo: headsetInfos) {
			configItems.add(newCongitem(deviceInfo));
		}
		for(RemoteDeviceInfo deviceInfo: masterInfos) {
			configItems.add(newCongitem(deviceInfo));
		}
		
		ConfigItem markItem = new SimpleConfigItem("mark");
		markItem.addItem("index", new SingleValue(String.valueOf(dataBinding.getMarkedDeviceIdx())));
		configItems.add(markItem);
		new ConfigItemsWriter(configItems, fCfgPath).write();
	}
	
	private ConfigItem newCongitem(RemoteDeviceInfo deviceInfo) {
		ConfigItem item = new SimpleConfigItem(fDeviceItemName);
		item.addItem("name", new SingleValue(deviceInfo.getfName()));
		item.addItem("address", new SingleValue(deviceInfo.getfAddress()));
		item.addItem("type", new SingleValue(deviceInfo.getfType().toString()));
		return item;
	}
	
}
