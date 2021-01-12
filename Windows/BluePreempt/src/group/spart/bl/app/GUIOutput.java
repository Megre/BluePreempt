package group.spart.bl.app;

import java.awt.List;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 28, 2020 1:28:02 PM 
 */
public class GUIOutput {
	
	private List fOutput;
	
	public GUIOutput(List outputList) {
		fOutput = outputList;
	}
	
	public void println(String format, Object... args) {
		synchronized(this) {
			fOutput.add(String.format(format, args));
			makeVisible();
		}
	}
	
	public void println(String line) {
		synchronized(this) {
			fOutput.add(line);
			makeVisible();
		}
	}
	
	private void makeVisible() {
		fOutput.makeVisible(fOutput.getItemCount() - 1);
	}
}
