package group.spart.bl.ua;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import group.spart.bl.app.GUI;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 12, 2021 4:06:32 PM 
 */
public class MarkAction extends UserAction implements ActionListener {

	public MarkAction() {
		super(true);
	}
	
	/**
	 * @see group.spart.bl.ua.Performable#actionPerformed()
	 */
	@Override
	public Object actionPerformed() {
		int idx = GUI.instance().getSelectedIndex();
		if(idx < 0) {
			notifyUser("no headset selected");
			return null;
		}
		GUI.instance().getDataBinding().markDevice(idx);
		return null;
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		act();
	}

}
