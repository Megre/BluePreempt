package group.spart.bl.ua;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 12, 2021 4:08:59 PM 
 */
public class HelpAction extends UserAction implements MouseListener {

	/**
	 * @see group.spart.bl.ua.Performable#actionPerformed()
	 */
	@Override
	public Object actionPerformed() {
		java.net.URI uri;
		try {
			uri = new java.net.URI("https://github.com/megre/BluePreempt");
			java.awt.Desktop.getDesktop().browse(uri);
		} 
		catch (URISyntaxException e1) { } 
		catch (IOException e1) { }
		return null;
	}

	/**
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		act();
		
	}

	/**
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	/**
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	/**
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	/**
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		
	}

}
