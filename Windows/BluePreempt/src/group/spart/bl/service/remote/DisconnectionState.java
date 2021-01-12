package group.spart.bl.service.remote;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 28, 2020 10:42:22 PM 
 */
public enum DisconnectionState {
	DisconnectionServiceNotFound("remote disconnection service not found"),
	Disconnected("disconnected"),
	Failed("failed");
	
	private String fName;
	
	private DisconnectionState(String name) {
		fName = name;
	}
	
	@Override
	public String toString() {
		return fName;
	}
}
