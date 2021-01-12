package group.spart.bl.service.local;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 29, 2020 9:44:41 PM 
 */
public enum DeviceType {
	
	HEADSET("HEADSET"),
	MASTER("MASTER");
	
	private String fTypeName;
	
	private DeviceType(String name) {
		fTypeName = name;
	}
	
	@Override
	public String toString() {
		return fTypeName;
	}
	
	public static DeviceType fromString(String type) {
		return DeviceType.valueOf(type.toUpperCase());
	}
	
	public static final int MajorDeviceClassOfAudioOrVideo = 0x0400;
	public static final int MinorDeviceClassOfWearableHeadset = 0x0004;
	
	public static final int MajorDeviceClassOfPhone = 0x0200;
	public static final int MinorDeviceClassOfSmartPhone = 0x000c;
	
	public static final int MajorDeviceClassOfPeripheral = 0x0500;
	public static final int MinorDeviceClassOfKeyboard = 0x0040;
	
	public static final int MajorDeviceClassOfComputer = 0x0100;
	
	public static boolean isAudioOrVideo(int majorDeviceClass) {
		return majorDeviceClass == MajorDeviceClassOfAudioOrVideo;
	}
	
	public static boolean isMaster(int majorDeviceClass) {
		return majorDeviceClass == MajorDeviceClassOfPhone
				|| majorDeviceClass == MajorDeviceClassOfComputer;
	}
	
	public static boolean isAudioOrVideo(DeviceType type) {
		return type == HEADSET;
	}
	
	public static boolean isMaster(DeviceType type) {
		return type == MASTER;
	}
}
