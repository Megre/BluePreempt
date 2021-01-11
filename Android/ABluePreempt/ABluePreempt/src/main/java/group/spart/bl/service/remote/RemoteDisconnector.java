package group.spart.bl.service.remote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import group.spart.abl.app.MainActivity;
import group.spart.bl.service.SyncCallback;
import group.spart.bl.service.local.DisconnectService;

public class RemoteDisconnector {
    private CountDownLatch fLatch;
    private boolean fSuccess = false;

    public RemoteDisconnector() {

    }

    public void disconnect(BluetoothDevice[] bluetoothDevices, SyncCallback callback) {
        MainActivity.instance().notifyUser("disconnecting headsets on remote devices...");
        fLatch = new CountDownLatch(bluetoothDevices.length);
        for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
            MainActivity.instance().execute(new DisconnectRunnable(bluetoothDevice));
        }
        try {
            fLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            MainActivity.instance().notifyUser("disconnection " + (fSuccess?"succeeded":"failed"));
            if (callback != null) callback.invoke(fSuccess, null);
        }
    }

    private void countDownToZero() {
        for(int c=0; c<fLatch.getCount(); ++c) fLatch.countDown();
    }

    private synchronized void countDown() {
        if(fSuccess) countDownToZero();
        else if(fLatch.getCount() > 0) fLatch.countDown();
    }

    private class DisconnectRunnable implements Runnable {
        private final BluetoothDevice fDevice;

        public DisconnectRunnable(BluetoothDevice device) {
            fDevice = device;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            try{
                UUID serviceUUID = DisconnectService.DISCONNECT_SERVICE_UUID;
                System.out.println("connecting socket uuid: " + serviceUUID); // 这里应该是服务的uuid
                socket = fDevice.createRfcommSocketToServiceRecord(serviceUUID);
                if (!socket.isConnected()){
                    socket.connect();
                    InputStream inputStream = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    final int readLength = inputStream.read(bytes);
                    System.out.println("read: " + new String(bytes) + ", " + readLength);
                    synchronized (RemoteDisconnector.this) {
                        if(!fSuccess) fSuccess = true;
                    }
                }
            }catch (IOException e){
                System.err.println("socket connection failed");
                try {
                    if(socket != null) socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            finally {
                countDown();
            }
        }
    }
}