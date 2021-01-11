package group.spart.bl.service;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 24, 2020 12:30:12 PM 
 */
public class AssignedUUIDs {
	// protocal
	public static final int SDP = 0x0001;
	public static final int RFCOMM = 0x0003;
	public static final int OBEX = 0x0008;
	public static final int HTTP = 0x000C;
	public static final int L2CAP = 0x0100;
	public static final int BNEP = 0x000F;
	
	// service class
	public static final int SerialPort = 0x1101;
	public static final int ServiceDiscoveryServerServiceClassID = 0x1000;
	public static final int BrowseGroupDescriptorServiceClassID = 0x1001;
	public static final int OBEXObjectPush = 0x1105;
	public static final int OBEXFileTransfer = 0x1106;
	public static final int PersonalAreaNetworkingUser = 0x1115;
	public static final int NetworkAccessPoint = 0x1116;
	public static final int GroupNetwork = 0x1117;
	public static final int A2DPAudioSink = 0x110b;
	public static final int Handsfree = 0x111e;
	
	// browse group list
	public static final int PublicBrowseGroup = 0x1002;
	
	// universal attributes
	public static final int ServiceClassIDList = 0x0001;
	public static final int ServiceRecordState = 0x0002;
	public static final int LanguageBaseAttributeIDList = 0x0006;
	public static final int ServiceAvailability = 0x0008;
	public static final int ClientExecutableURL = 0x000B;
	
	// offsets for strings
	public static final int PrimaryLanguageOffsetBase = 0x0100;
}
