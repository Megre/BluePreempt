package group.spart.bl.app;

import java.awt.Color;
import java.awt.Graphics;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 28, 2020 2:28:19 PM 
 */
public class OutputList extends ReadonlyList {
	private static final long serialVersionUID = 1874750046979962238L;

	public OutputList() {
		super();
	}
	
	@Override
	public void paint(Graphics g) { 
		setForeground(Color.BLUE);
		super.paint(g);
	}
}
