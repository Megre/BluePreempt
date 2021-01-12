package group.spart.bl.ua;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.bluetooth.RemoteDevice;

import group.spart.bl.app.GUI;
import group.spart.bl.service.SyncCallback;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 12, 2021 3:16:54 PM 
 */
public class ConnectAction extends UserAction implements ActionListener  {

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		act();
	}

	/**
	 * @see group.spart.bl.ua.Performable#actionPerformed()
	 */
	@Override
	public Object actionPerformed() {
		RemoteDevice headset = GUI.instance().getSelectedDevice();
		if(headset == null) {
			notifyUser("no headset selected");
			return null;
		}
		
		GUI.instance().getAdapter().connectHeadset(headset, new SyncCallback() {
			@Override
			public void invoke(boolean success, Object returnValues) {
				notifyUser("headset connection " + (success?"success":"failed"));
				finish();
			}
		});
		return null;
	}
	
}
