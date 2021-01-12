package group.spart.bl.cmd;

import java.util.List;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 26, 2020 10:53:21 PM 
 */
public interface CommandExecutorCallback extends ExecutionCallback {

	void invoke(boolean success, List<String> stdOutput, List<String> errorOutput);
}
