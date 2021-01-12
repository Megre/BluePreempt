package group.spart.bl.app;

import java.awt.AWTEvent;
import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 28, 2020 2:28:19 PM 
 */
public class ReadonlyList extends List {
	private static final long serialVersionUID = 1874750046979962238L;

	public ReadonlyList() {
		super();
		
		enableEvents(AWTEvent.ITEM_EVENT_MASK 
				| AWTEvent.PAINT_EVENT_MASK
				| AWTEvent.MOUSE_EVENT_MASK);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		e.consume();
	}
	
	@Override
	protected void processItemEvent(ItemEvent e) { 
		
	}
	
	
}
