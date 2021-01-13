package group.spart.bl.service.local;

import java.util.Arrays;
import java.util.List;

import group.spart.bl.cmd.CommandExecutor;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 24, 2020 11:52:18 PM 
 */
public class VolumeSetter {
	public static final int STATE_MUTE = -1;
	public static final int STATE_MAINTAIN = 0;
	public static final int STATE_SPEAK = 1;

	private final String SETVOLUME_PATH = "tools/setvolume.exe";
	
	public int getVolume() {
		String result = execute(new String[] { SETVOLUME_PATH, "get" });
		if(result != null) {
			try {
				return Integer.parseInt(result.split(",")[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return -1;
	}
	
	public void setVolume(int volume, int state) {
		execute(new String[] { SETVOLUME_PATH, "set", String.valueOf(volume), String.valueOf(state) });
	}
	
	public void setVolume(int volume) {
		setVolume(volume, STATE_MAINTAIN);
	}
	
	private String execute(String[] cmd) {
		CommandExecutor executor = new CommandExecutor(Arrays.asList(cmd));
		executor.run();
		if(executor.success()) {
			List<String> outputs = executor.getStdOutput();
			if(outputs.size() > 0) {
				return outputs.get(0);
			}
		}
		
		return null;
	}
	
}
