package group.spart.bl.app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.bluetooth.RemoteDevice;
import java.awt.List;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import group.spart.bl.cfg.UserConfig;
import group.spart.bl.service.SyncCallback;
import group.spart.bl.service.remote.RemoteDeviceInfo;
import group.spart.bl.util.Utils;

import java.awt.SystemColor;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 28, 2020 12:57:51 PM 
 */
public class GUI {
	public static GUIOutput out;
	
	private BluePreempt fBluePreempt;
	private WorkingState fWorkingState = new WorkingState();
	private UserConfig fUserConfig = new UserConfig(Envioronment.CfgFilePath);
	
	// component
	private JFrame frmBluepreempt;
	private List fHeadsetList;
	private List fOutputList;
	private List fMasterList;
	private JLabel fCurrentDevice;

	private DisplayListDataBinding fListDataBinding;
	
	private final String fTaskIdConnectOrRefresh = "connect or refresh";
	
	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.setup();
					window.frmBluepreempt.setVisible(true);
					window.process();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
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
	}
	
	private void process() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				java.util.List<RemoteDeviceInfo> savedDevices = fUserConfig.getSavedDevices();
				if(savedDevices.size() == 0) searchDevices();
				else {
					out.println(savedDevices.size() + " cached devices loaded");
					updateDeviceInfo(savedDevices, fUserConfig.getMarkedDeviceIdx());
				}
			}
		});
	}
	
	private void searchDevices() {
		if(fWorkingState.isBusy(fTaskIdConnectOrRefresh)) {
			out.println("please wait...");
			return;
		}
		
		out.println("searching bluetooth devices...");
		new Thread(new Runnable() {
			public void run() {
				fBluePreempt.searchDevices(new SyncCallback() {
					@Override
					public void invoke(boolean success, Object returnValues) {
						@SuppressWarnings("unchecked")
						java.util.List<RemoteDeviceInfo> infos = (java.util.List<RemoteDeviceInfo>) returnValues;
						updateDeviceInfo(infos, -1);
						out.println(infos.size() + " device(s) found");
						
						fWorkingState.finish(fTaskIdConnectOrRefresh);
					}
				});
			}
		}).start();
	}
	
	private void updateDeviceInfo(java.util.List<RemoteDeviceInfo> infos, int markedIdx) {
		fListDataBinding.update(infos);
		fListDataBinding.markDevice(markedIdx);
	}
	
	@SuppressWarnings("unused")
 	private void detectConnectionState(java.util.List<RemoteDevice> headsets) {
		out.println("detecting connection state...");
		ConcurrentHashMap<RemoteDevice, Boolean> stateMap = fBluePreempt.detectConnectionState();
		fHeadsetList.removeAll();
		for(int idx=0; idx<headsets.size(); ++idx) {
			fHeadsetList.add((stateMap.get(headsets.get(idx))?"+ ":"- ")
				+ Utils.deviceName(headsets.get(idx)));
		}
	}
	
	private RemoteDevice getSelectedDevice() {
		return Optional.ofNullable(fListDataBinding.getHedsetSource())
				.filter(infos -> fHeadsetList.getSelectedIndex() >= 0)
				.map(infos -> infos.get(fHeadsetList.getSelectedIndex()).getfDevice())
				.orElse(null);
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
		
		fHeadsetList = new List();
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
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String taskId = fTaskIdConnectOrRefresh;
				if(fWorkingState.isBusy(taskId)) {
					out.println("please wait...");
					return;
				}
				
				if(getSelectedDevice() == null) {
					out.println("no headset selected");
					return;
				}
				
				out.println("connecting headset...");
				fBluePreempt.connectHeadset(getSelectedDevice(), new SyncCallback() {
					@Override
					public void invoke(boolean success, Object returnValues) {
						out.println("headset connection " 
								+ (success?"success":"failed") 
								+ (returnValues.toString().isEmpty()?"":(": " + returnValues.toString())));
						fWorkingState.finish(taskId);
					}
				});
			}
		});
		btnConnect.setBounds(325, 21, 93, 23);
		panel_1.add(btnConnect);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchDevices();
			}
		});
		btnRefresh.setToolTipText("Detect Bluetooth Devices Around");
		btnRefresh.setBounds(325, 87, 93, 23);
		panel_1.add(btnRefresh);
		
		JButton btnNewButton = new JButton("Mark");
		btnNewButton.setToolTipText("Mark the Headset that Allows Remote Mobile Phones or Computers to Preempt");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fListDataBinding.markDevice(fHeadsetList.getSelectedIndex());
			}
		});
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
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				java.net.URI uri;
				try {
					uri = new java.net.URI("https://github.com/megre/BluePreempt");
					java.awt.Desktop.getDesktop().browse(uri);
				} 
				catch (URISyntaxException e1) { } 
				catch (IOException e1) { }
			}
		});
		lblNewLabel.setBounds(385, 10, 32, 32);
		lblNewLabel.setIcon(new ImageIcon(Utils.jarPath() + "/cfg/help.png"));
		frmBluepreempt.getContentPane().add(lblNewLabel);

	}
}
