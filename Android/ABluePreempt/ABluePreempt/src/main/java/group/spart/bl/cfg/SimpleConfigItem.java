package group.spart.bl.cfg;

import java.util.Map;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 29, 2020 2:16:37 PM 
 */
public class SimpleConfigItem extends ConfigItem {

	private String fItemName;
	
	public SimpleConfigItem(String itemName) {
		fItemName = itemName;
	}
	
	public String getItemName() {
		return fItemName;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("\n[" + fItemName + "]\n");
		for(Map.Entry<String, ConfigItemValue> entry: getKeyValueMap().entrySet()) {
			buffer.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
		}
		
		return buffer.toString();
	}
}
