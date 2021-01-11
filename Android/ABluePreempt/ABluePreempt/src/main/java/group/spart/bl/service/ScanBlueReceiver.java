package group.spart.bl.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScanBlueReceiver extends BroadcastReceiver {
    private static final String TAG = ScanBlueReceiver.class.getName();
    private ScanBlueCallBack callBack;

    public ScanBlueReceiver(ScanBlueCallBack callBack){
        this.callBack = callBack;
    }

    //广播接收器，当远程蓝牙设备被发现时，回调函数onReceiver()会被执行
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (action == BluetoothAdapter.ACTION_DISCOVERY_STARTED) {
            callBack.onScanStarted();
        }
        else if(action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
            callBack.onScanFinished();
        }
        else if(action == BluetoothDevice.ACTION_FOUND) {
            callBack.onScanning(device);
        }
    }
}