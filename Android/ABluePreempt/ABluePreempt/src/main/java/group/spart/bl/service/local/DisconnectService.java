package group.spart.bl.service.local;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.view.Display;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import group.spart.abl.app.MainActivity;
import group.spart.bl.app.DisplayListDataBinding;
import group.spart.bl.service.SyncCallback;
import group.spart.bl.service.remote.RemoteDeviceInfo;

public class DisconnectService implements Runnable {
   public static final UUID DISCONNECT_SERVICE_UUID = UUID.fromString("e5dd63f3-c347-4bc8-8c55-5b31caf789f6");
   public static final String DISCONNECT_SERVICE_NAME = "BluePreempt Disconnect Service";
   private BluetoothAdapter fAdapter;
   private MainActivity fActivity;
   private BluetoothSocket fSocket;

   public DisconnectService(MainActivity activity) {
      fAdapter = BluetoothAdapter.getDefaultAdapter();
      fActivity = activity;
   }

   @Override
   public void run() {
      System.out.println("disconnection service started");

      // notify client
      SyncCallback notifyCallback = new SyncCallback() {
         @Override
         public void invoke(boolean success, Object returnValues) {
            try {
               OutputStream outputStream = fSocket.getOutputStream();
               outputStream.write("done".getBytes());
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      };

      while (true) {
         try {
            System.out.println("DisconnectService listening...");
            BluetoothServerSocket serverSocket = fAdapter.listenUsingRfcommWithServiceRecord(DISCONNECT_SERVICE_NAME, DISCONNECT_SERVICE_UUID);
            fSocket = serverSocket.accept();

            System.out.println("DisconnectService connection accepted");

            // disconnect local headset
            BluetoothDevice headset = fActivity.getMarkedHeadset();
            if (headset != null) {
               new HeadsetConnector().disconnect(headset, notifyCallback);
            }
            System.out.println("DisconnectService after disconnect()");
         } catch (IOException e) {
            System.out.println("error: DisconnectService: run() " + e.getMessage());
            e.printStackTrace();
         }
      }
   }
}
