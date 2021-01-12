package group.spart.bl.app;

import group.spart.bl.util.Utils;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 1, 2021 10:48:35 PM 
 */
public class Envioronment {
	public static final String CfgPath = Utils.jarPath() + "/cfg";
	public static final String CfgFilePath = CfgPath + "/user.cfg";
	public static final String ToolPath = Utils.jarPath() + "/tools";
}
