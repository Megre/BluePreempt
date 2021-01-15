package group.spart.bl.ua;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import group.spart.bl.app.GUI;
import group.spart.bl.util.Utils;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Jan 15, 2021 5:31:47 PM 
 */
public class FrameAction extends WindowAdapter {
	
	private SystemTray fSystemTray;
	private JFrame fMainFrame;
	private TrayIcon trayIcon;
	
	public FrameAction() {
		fSystemTray = SystemTray.getSystemTray();
		fMainFrame = GUI.instance().getMainFrame();
		
		ImageIcon icon = new ImageIcon(Utils.jarPath() + "/cfg/logo.png");
		trayIcon = new TrayIcon(icon.getImage(), fMainFrame.getTitle(), null);
	}

	@Override
	public void windowClosing(WindowEvent e) {
		GUI.instance().saveUserConfig();
		System.exit(0);
	}
	
	public void windowIconified(WindowEvent e) {
		minimizeToTray();
	}
	
	private void minimizeToTray() {
		// hide main frame
		fMainFrame.setVisible(false);
		
		// add to tray
		trayIcon.setImageAutoSize(true);
		try {
			fSystemTray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		// click tray to show main frame
		trayIcon.addMouseListener(new MouseAdapter() {
 
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					fSystemTray.remove(trayIcon);
					fMainFrame.setVisible(true);
					fMainFrame.setExtendedState(JFrame.NORMAL);
					fMainFrame.toFront();
				}
			}
		});
	}
}
