package group.spart.bl.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import group.spart.error.Assert;

/** 
* @author megre
* @email renhao.x@seu.edu.cn
* @version created on: Aug 18, 2020 6:50:31 PM 
*/
public class ConfigItemFactory {
	
	private static final Pattern fItemBeginPtn = Pattern.compile("^ *\\[(.+)\\] *$");
	
	private static class Pair {
		private String fKey, fValue;
		public Pair(String key, String value) { 
			fKey = key; 
			fValue = value; 
		}
		public String getKey() { return fKey; }
		public String getValue() { return fValue; } 
	}
	
	public static List<ConfigItem> parse(String configFile) {
		List<ConfigItem> items = new ArrayList<ConfigItem>();
		
		File inputFile = new File(configFile);
		if(!inputFile.exists() || !inputFile.isFile()) return items;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
			String line = null;
			while((line = br.readLine()) != null) {
				if(!isItemBegin(line)) continue;
				
				ConfigItem configItem = newConfigItem(line);
				List<String> lines = itemLines(br, 0);
				for(String item: lines) {
					Pair keyValue = getKeyValue(item);
					configItem.addItem(keyValue.getKey(), newConfigItemValue(keyValue.getValue()));
				}
				items.add(configItem);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	private static ConfigItem newConfigItem(String line) {
		return new SimpleConfigItem(itemName(line));
	}
	
	private static ConfigItemValue newConfigItemValue(String value) {
		if(isListValue(value)) return new ListValue(value);
		else if(isMultipleListValue(value)) return new MultipleListValue(value);
		else return new SingleValue(value);
	}
	
	private static boolean isListValue(String value) {
		return !isMultipleListValue(value) && value.contains(ListValue.SEPERATOR);
	}
	
	private static boolean isMultipleListValue(String value) {
		return value.contains(MultipleListValue.SEPERATOR);
	}
	
	private static String trimSpaceAndComment(String line) {
		final int idx = line.indexOf('#');
		if(idx == -1) return line.trim();
		return line.substring(0, idx).trim();
	}
	
	private static List<String> itemLines(BufferedReader br, int startLine) throws IOException {
		List<String> lines = new ArrayList<String>();
		String line = null;
		while((line = br.readLine()) != null) {
			if(isComment(line)) continue;
			if(line.trim().isEmpty()) break;
			lines.add(trimSpaceAndComment(line));
		}
		Assert.ensure(lines.size() > 0, "The content of configuration item is empty after line " + startLine); 
		return lines;
	}
	
	private static Pair getKeyValue(String line) { 
		final String[] keyValue = line.split("=");
		Assert.ensure(keyValue.length == 2, "Error content line: " + line);
		
		return new Pair(keyValue[0].trim(), keyValue[1].trim());
	}
	
	private static String itemName(String line) {
		Matcher matcher = fItemBeginPtn.matcher(line);
		return matcher.matches()?matcher.group(1):"";
	}
	
	private static boolean isItemBegin(String line) {
		return fItemBeginPtn.matcher(line).matches();
	}
	
	private static boolean isComment(String line) {
		return line.trim().startsWith("#");
	}
	
}
