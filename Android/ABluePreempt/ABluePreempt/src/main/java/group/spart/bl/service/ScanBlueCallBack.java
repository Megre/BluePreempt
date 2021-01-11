package group.spart.bl.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public interface ScanBlueCallBack {
    void onScanStarted();
    void onScanFinished();
    void onScanning(BluetoothDevice device);
}
