package group.spart.abl.app;

import android.content.Context;

import java.io.File;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 1, 2021 10:53:24 PM 
 */
public class AEnvioronment {
	private final String fCfgFilePath;
	private final Context fContext;
	
	public AEnvioronment() {
		fContext = MainActivity.instance();
		fCfgFilePath = getConfigDir() + "/user.cfg";
	}
	
	public String getConfigFilePath() {
		return fCfgFilePath;
	}
	
	private String getConfigDir() {
		File data = fContext.getFilesDir();
		File cfgDir = new File(data.getAbsolutePath() + "/cfg");
		if(cfgDir.exists() || cfgDir.mkdirs()) {
			return cfgDir.getAbsolutePath();
		}
		return null;
	}
}
