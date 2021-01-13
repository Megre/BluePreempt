package group.spart.bl.service.local;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import group.spart.abl.app.ABluetoothManager;
import group.spart.abl.app.MainActivity;
import group.spart.bl.service.SyncCallback;

public class DisconnectionService extends Service implements Runnable {
    public static final UUID DISCONNECT_SERVICE_UUID = UUID.fromString("e5dd63f3-c347-4bc8-8c55-5b31caf789f6");
    public static final String DISCONNECT_SERVICE_NAME = "BluePreempt Disconnect Service";

    private final BluetoothAdapter fAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket fSocket;
    private final MainActivity fActivity = MainActivity.instance();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MainActivity.instance().execute(this);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();

        MainActivity.instance().notifyUser("disconnection service stopped");
    }

    @Override
    public void run() {
        System.out.println("disconnection service started");

        // notify client
        SyncCallback notifyCallback = (success, returnValues) -> {
            try {
                OutputStream outputStream = fSocket.getOutputStream();
                outputStream.write("done".getBytes());
                outputStream.flush();
                outputStream.close();
                fSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        while (true) {
            try {
                if(!ABluetoothManager.isBluetoothEnabled()) {
                    Thread.sleep(200);
                    continue;
                }

                System.out.println("disconnection service is listening...");
                BluetoothServerSocket serverSocket = fAdapter.listenUsingRfcommWithServiceRecord(DISCONNECT_SERVICE_NAME, DISCONNECT_SERVICE_UUID);
                fSocket = serverSocket.accept();

                fActivity.notifyUser("headset disconnection request accepted");

                // disconnect local headset
                BluetoothDevice headset = fActivity.getMarkedHeadset();
                if(headset == null) {
                    notifyCallback.invoke(false, null);
                    fActivity.notifyUser("no headset marked. Please mark a headset that allows remote devices to disconnect");
                    continue;
                }

                new HeadsetConnector().disconnect(headset, notifyCallback);
                fActivity.notifyUser("headset disconnected");

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
