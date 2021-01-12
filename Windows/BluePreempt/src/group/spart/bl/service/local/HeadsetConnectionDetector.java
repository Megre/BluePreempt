package group.spart.bl.service.local;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group.spart.bl.cmd.CommandExecutor;
import group.spart.bl.cmd.CommandExecutorCallback;
import group.spart.bl.service.InfoObserver;
import group.spart.bl.service.InfoSubject;
import group.spart.bl.util.Utils;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 26, 2020 10:38:50 PM 
 */
public class HeadsetConnectionDetector implements InfoSubject {
	private final String fBtStateComponent = Utils.jarPath() + "/tools/btstate.exe";

	private List<InfoObserver> fObservers = new ArrayList<>();
	private CommandExecutorCallback fCallback;
	
	public HeadsetConnectionDetector(CommandExecutorCallback callback) {
		fCallback = callback;
	}
	
	public void detect(String bluetoothAddress) {
		final String[] cmd = {fBtStateComponent, Utils.friendlyAddress(bluetoothAddress)};
		CommandExecutor executor = new CommandExecutor(Arrays.asList(cmd));
		executor.setCallback(new CommandExecutorCallback() {
			
			@Override
			public void invoke(boolean success, List<String> stdOutput, List<String> errorOutput) {
				fCallback.invoke(success, stdOutput, errorOutput);
				
				// notify attached observers
				synchronized (this) {
					notifyObservers();
				}
			}
		});
		Thread execThread = new Thread(executor);
		execThread.start();
	}


	/**
	 * @see group.spart.bl.service.InfoSubject#attachObserver(group.spart.bl.service.InfoObserver)
	 */
	@Override
	public void attachObserver(InfoObserver observer) {
		fObservers.add(observer);
	}

	/**
	 * @see group.spart.bl.service.InfoSubject#notifyObservers()
	 */
	@Override
	public void notifyObservers() {
		for(InfoObserver observer: fObservers) {
			observer.updateInfo();
		}
	}
	
	public static void main(String[] args) {
		new HeadsetConnectionDetector(null).detect("48D84507E49E");
	}
}
