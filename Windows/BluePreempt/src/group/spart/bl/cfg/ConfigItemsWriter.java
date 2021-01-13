package group.spart.bl.cfg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 29, 2020 2:29:43 PM 
 */
public class ConfigItemsWriter {

	private List<ConfigItem> fConfigItems;
	private String fSavePath;
	
	public ConfigItemsWriter(List<ConfigItem> configItems, String savePath) {
		fConfigItems = configItems;
		fSavePath = savePath;
	}
	
	public void write() {
		if(fConfigItems.size() == 0) return;
		
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(makeFile(fSavePath)));
			for(ConfigItem item: fConfigItems) {
				bufferedWriter.write(item.toString());
			}
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private File makeFile(String filePath) throws IOException {
		File cfgFile = new File(fSavePath);
		if(cfgFile.exists()) return cfgFile;
		
		if(cfgFile.getParentFile() != null) {
			cfgFile.getParentFile().mkdirs();
		}
		cfgFile.createNewFile();
		return cfgFile;
	}
}
