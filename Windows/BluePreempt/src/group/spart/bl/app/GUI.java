package group.spart.bl.app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.bluetooth.RemoteDevice;
import java.awt.List;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import group.spart.bl.cfg.UserConfig;
import group.spart.bl.service.remote.RemoteDeviceInfo;
import group.spart.bl.ua.ConnectAction;
import group.spart.bl.ua.HelpAction;
import group.spart.bl.ua.MarkAction;
import group.spart.bl.ua.ScanAction;
import group.spart.bl.ui.ListView;
import group.spart.bl.ui.OutputList;
import group.spart.bl.ui.ReadonlyList;
import group.spart.bl.util.Utils;

import java.awt.SystemColor;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 28, 2020 12:57:51 PM 
 */
public class GUI {
	public static GUIOutput out;
	public static GUI fInstance;
	
	private BluePreempt fBluePreempt;
	private UserConfig fUserConfig;
	
	// component
	private JFrame frmBluepreempt;
	private ListView fHeadsetList;
	private ListView fOutputList;
	private ListView fMasterList;
	private JLabel fCurrentDevice;

	private DisplayListDataBinding fListDataBinding;
	private final ExecutorService fExecutor = Executors.newCachedThreadPool();
	
	/**
	 * Create the application.
	 */
	public GUI() {
		fInstance = this;
		fUserConfig = new UserConfig(Envioronment.CfgFilePath);
		
		initialize();
	}
	
	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(() -> {
			try {
				GUI window = new GUI();
				window.setup();
				window.frmBluepreempt.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public List getHeadsetList() {
		return fHeadsetList;
	}
	
	public RemoteDevice getMarkedHeadset() {
		return Optional.ofNullable(fListDataBinding.getMarkedDeviceInfo())
				.map(RemoteDeviceInfo::getfDevice)
				.orElse(null);
	}
	
	private void setup() {
		fCurrentDevice.setText("This Device: " + Utils.deviceName());
		out = new GUIOutput(fOutputList);
		
		fListDataBinding = new DisplayListDataBinding(fHeadsetList, fMasterList);
		fBluePreempt = new BluePreempt(fListDataBinding);
		fListDataBinding.attachObserver(fBluePreempt);
		
		loadSavedDevices();
	}
	
	private void loadSavedDevices() {
		java.util.List<RemoteDeviceInfo> savedDevices = fUserConfig.getSavedDevices();
		if(savedDevices.size() == 0) notifyUser("click Scan button to search bluetooth devices");
		else {
			notifyUser(savedDevices.size() + " cached devices loaded");
			updateDeviceInfo(savedDevices, fUserConfig.getMarkedDeviceIdx());
		}
	}
	
	public void updateDeviceInfo(java.util.List<RemoteDeviceInfo> infos, int markedIdx) {
		fListDataBinding.update(infos);
		fListDataBinding.markDevice(markedIdx);
	}
	
	@SuppressWarnings("unused")
 	private void detectConnectionState(java.util.List<RemoteDevice> headsets) {
		notifyUser("detecting connection state...");
		ConcurrentHashMap<RemoteDevice, Boolean> stateMap = fBluePreempt.detectConnectionState();
		fHeadsetList.removeAll();
		for(int idx=0; idx<headsets.size(); ++idx) {
			fHeadsetList.add((stateMap.get(headsets.get(idx))?"+ ":"- ")
				+ Utils.deviceName(headsets.get(idx)));
		}
	}
	
	public RemoteDevice getSelectedDevice() {
		return Optional.ofNullable(fListDataBinding.getHedsetSource())
				.filter(infos -> getSelectedIndex() >= 0)
				.map(infos -> infos.get(getSelectedIndex()).getfDevice())
				.orElse(null);
	}
	
	public int getSelectedIndex() {
		return fHeadsetList.getSelectedIndex();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmBluepreempt = new JFrame();
		frmBluepreempt.setIconImage(Toolkit.getDefaultToolkit().getImage(Utils.jarPath() + "/cfg/logo.png"));
		frmBluepreempt.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				fUserConfig.saveConfig(fListDataBinding);
				System.exit(0);
			}
		});
		frmBluepreempt.setTitle("BluePreempt");
		frmBluepreempt.getContentPane().setBackground(SystemColor.control);
		frmBluepreempt.setResizable(false);
		frmBluepreempt.setBounds(100, 100, 445, 432);
		frmBluepreempt.getContentPane().setLayout(null);
		frmBluepreempt.setLocationRelativeTo(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Headsets", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(6, 41, 428, 128);
		frmBluepreempt.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		fHeadsetList = new ListView();
		fHeadsetList.setBounds(10, 21, 294, 90);
		panel_1.add(fHeadsetList);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Outputs", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(6, 290, 428, 104);
		frmBluepreempt.getContentPane().add(panel);
		panel.setLayout(null);
		
		fOutputList = new OutputList();
		fOutputList.setBounds(10, 20, 408, 74);
		panel.add(fOutputList);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setToolTipText("Connect to Selected Headset");
		btnConnect.addActionListener(new ConnectAction());
		btnConnect.setBounds(325, 21, 93, 23);
		panel_1.add(btnConnect);
		
		JButton btnScan = new JButton("Scan");
		btnScan.addActionListener(new ScanAction());
		btnScan.setToolTipText("Detect Bluetooth Devices Around");
		btnScan.setBounds(325, 87, 93, 23);
		panel_1.add(btnScan);
		
		JButton btnNewButton = new JButton("Mark");
		btnNewButton.setToolTipText("Mark the Headset that Allows Remote Mobile Phones or Computers to Preempt");
		btnNewButton.addActionListener(new MarkAction());
		btnNewButton.setBounds(325, 54, 93, 23);
		panel_1.add(btnNewButton);
		
		panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Phones & Computers", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(6, 179, 428, 101);
		frmBluepreempt.getContentPane().add(panel);
		panel.setLayout(null);
		
		fMasterList = new ReadonlyList();
		fMasterList.setBounds(10, 20, 408, 71);
		panel.add(fMasterList);
		
		fCurrentDevice = new JLabel("(Current Device)");
		fCurrentDevice.setHorizontalAlignment(SwingConstants.LEFT);
		fCurrentDevice.setBounds(10, 10, 354, 29);
		frmBluepreempt.getContentPane().add(fCurrentDevice);
		fCurrentDevice.setToolTipText("Current Device");
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.addMouseListener(new HelpAction());
		lblNewLabel.setBounds(385, 10, 32, 32);
		lblNewLabel.setIcon(new ImageIcon(Utils.jarPath() + "/cfg/help.png"));
		frmBluepreempt.getContentPane().add(lblNewLabel);

	}
	
	public DisplayListDataBinding getDataBinding() {
		return fListDataBinding;
	}
	
	public void notifyUser(String message) {
		out.println(message);
	}
	
	public void execute(Runnable runnable) {
		EventQueue.invokeLater(() -> {
			fExecutor.execute(runnable);
		});
	}
	
	public BluePreempt getAdapter() {
		return fBluePreempt;
	}
	public static GUI instance() {
		return fInstance;
	}
}
