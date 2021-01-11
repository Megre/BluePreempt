package group.spart.bl.service;

import group.spart.bl.cmd.ExecutionCallback;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 28, 2020 10:02:53 PM 
 */
public interface SyncCallback extends ExecutionCallback {
	
	public void invoke(boolean success, Object returnValues);
}
