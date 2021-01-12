package group.spart.bl.ua;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import group.spart.bl.app.GUI;
import group.spart.bl.service.SyncCallback;
import group.spart.bl.service.remote.RemoteDeviceInfo;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 12, 2021 4:08:28 PM 
 */
public class ScanAction extends UserAction implements ActionListener {

	/**
	 * @see group.spart.bl.ua.Performable#actionPerformed()
	 */
	@Override
	public Object actionPerformed() {
		notifyUser("searching bluetooth devices...");
		GUI.instance().getAdapter().searchDevices(new SyncCallback() {
			@Override
			public void invoke(boolean success, Object returnValues) {
				@SuppressWarnings("unchecked")
				java.util.List<RemoteDeviceInfo> infos = (java.util.List<RemoteDeviceInfo>) returnValues;
				GUI.instance().updateDeviceInfo(infos, -1);
				notifyUser(infos.size() + " device(s) found");
				finish();
			}
		});
		
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
