package group.spart.bl.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import group.spart.bl.util.Utils;

/** 
 * 
 * @author megre
 * @email renhao.x@seu.edu.cn
 * @version created on: Dec 24, 2020 11:29:18 PM 
 */
public class CommandExecutor implements Runnable {
	private final long fTimeout;
	private List<String> fCommand = new ArrayList<>();
	
	private boolean fSuccess = false;
	private LineReader fStdReader, fErrorReader;
	
	private CommandExecutorCallback fCallback;
	
	public CommandExecutor(List<String> command, long timeoutSeconds) {
		fCommand = command;
		fTimeout = timeoutSeconds;
	}
	
	public CommandExecutor(List<String> command) {
		this(command, 0);
	}
	
	public void setCallback(CommandExecutorCallback callback) {
		fCallback = callback;
	}
	
	public boolean success() {
		return fSuccess;
	}
	
	public List<String> getStdOutput() {
		return fStdReader.fOutputLines;
	}
	
	public List<String> getErrorOutput() {
		return fErrorReader.fOutputLines;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ProcessBuilder processBuilder = new ProcessBuilder(fCommand);
		System.out.println(Utils.hostName() + ": execute command: " + processBuilder.command());
		
		try {
			Process process = processBuilder.start();
			fErrorReader = new LineReader(process.getErrorStream());
			fStdReader = new LineReader(process.getInputStream());
			fErrorReader.start();
			fStdReader.start();
			
			if(fTimeout <= 0) {
				process.waitFor();
			}
			else {
				process.waitFor(fTimeout, TimeUnit.SECONDS);
			
				if(process.isAlive()) {
					process.destroyForcibly();
					invokeCallback();
					return;
				}
			}
			
			fSuccess = (process.exitValue() == 0 && !fErrorReader.hasErrors());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		invokeCallback();
	}
	
	private void invokeCallback() {
		if(fCallback != null) {
			fCallback.invoke(fSuccess, getStdOutput(), getErrorOutput());
		}
	}
	
	private class LineReader {
		private InputStream fInputStream;
		private Thread fThread;
		private List<String> fOutputLines = new ArrayList<>();
		
		public LineReader(InputStream inputStream) {
			fInputStream = inputStream;
		}
		
		public void start() {
			fThread = new Thread(new LineReaderRunner());
			fThread.start();
		}
		
		public boolean hasErrors() {
			return fOutputLines.size() > 0;
		}
	
		private class LineReaderRunner implements Runnable {
			@Override
			public void run() {
				try {
					BufferedReader bfStream = new BufferedReader(new InputStreamReader(fInputStream, System.getProperty("sun.jnu.encoding")));
					String line;
					while((line = bfStream.readLine()) != null) {
						synchronized (CommandExecutor.class) {
							System.out.println(line);
						}
						fOutputLines.add(line);
						Thread.sleep(100);
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
}
