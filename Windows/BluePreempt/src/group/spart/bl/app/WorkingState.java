package group.spart.bl.app;

import java.util.HashSet;
import java.util.Set;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 29, 2020 10:36:03 PM 
 */
public class WorkingState {
	private Set<String> fWorkingState = new HashSet<>();
	
	public synchronized boolean isBusy(String taskId) {
		if(!fWorkingState.contains(taskId)) {
			fWorkingState.add(taskId);
			return false;
		}
		
		return true;
	}
	
	public synchronized void finish(String taskId) {
		fWorkingState.remove(taskId);
	}
}
