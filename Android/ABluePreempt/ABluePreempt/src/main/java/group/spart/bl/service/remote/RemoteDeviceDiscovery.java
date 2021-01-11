package group.spart.bl.service.remote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import group.spart.abl.app.ABluetoothManager;
import group.spart.abl.app.MainActivity;
import group.spart.bl.service.Discoverable;
import group.spart.bl.service.ScanBlueCallBack;
import group.spart.bl.service.ScanBlueReceiver;
import group.spart.bl.service.SyncCallback;
import group.spart.bl.service.local.DeviceType;

public class RemoteDeviceDiscovery implements Discoverable {
    private final MainActivity fActivity;
    private final BluetoothAdapter fAdapter;
    private ScanBlueReceiver fReceiver;
    private final java.util.List<RemoteDeviceInfo> fDeviceList = new ArrayList<>();
    private final SyncCallback fCallback;

    public  RemoteDeviceDiscovery(SyncCallback callback) {
        fAdapter = ABluetoothManager.adapter();
        fActivity = MainActivity.instance();
        fCallback = callback;
    }

    @Override
    public boolean discover() {
        if (fAdapter.isDiscovering()){
            fAdapter.cancelDiscovery();
        }

        if(!fAdapter.startDiscovery()) return false;

        fReceiver = new ScanBlueReceiver(new ScanBlueCallBack() {
            private final Set<String> found = new HashSet<>();

            @Override
            public void onScanStarted() {
                System.out.println("scanning bluetooth devices...");
                for(BluetoothDevice device: getBoundedDevices()) {
                    BluetoothClass bluetoothClass = device.getBluetoothClass();
                    System.out.println("bounded: " + device.getName() + " @ " + device.getAddress());
                    System.out.println(String.format("major class: 0x%x, device class: 0x%x", bluetoothClass.getMajorDeviceClass(),
                            bluetoothClass.getDeviceClass()));

                    if(DeviceType.UNSUPPORTED != DeviceType.getType(bluetoothClass.getMajorDeviceClass())) {
                        found.add(device.getAddress());
                        fDeviceList.add(makeRemoteDeviceInfo(device));
                    }
                }
            }

            @Override
            public void onScanFinished() {
                System.out.println(fDeviceList.size() + " devices found");
                MainActivity.instance().unregisterReceiver(fReceiver);
                fCallback.invoke(true, fDeviceList);
            }

            @Override
            public void onScanning(BluetoothDevice device) {
                if(found.contains(device.getAddress())) return;

                BluetoothClass bluetoothClass = device.getBluetoothClass();

                System.out.println("found: " + device.getName() + " @ " + device.getAddress());
                System.out.println(String.format("major class: 0x%x, device class: 0x%x", bluetoothClass.getMajorDeviceClass(),
                        bluetoothClass.getDeviceClass()));

                if(DeviceType.UNSUPPORTED != DeviceType.getType(bluetoothClass.getMajorDeviceClass())) {
                    fDeviceList.add(makeRemoteDeviceInfo(device));
                }
            }
        });

        IntentFilter filterStarted = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filterFinished = new IntentFilter(android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        fActivity.registerReceiver(fReceiver, filterStarted);
        fActivity.registerReceiver(fReceiver, filterFinished);
        fActivity.registerReceiver(fReceiver, filterFound);

        return false;
    }

    private RemoteDeviceInfo makeRemoteDeviceInfo(BluetoothDevice device) {
        String name = (device.getName()==null? device.getAddress() :device.getName());
        return new RemoteDeviceInfo(name, device.getAddress(), DeviceType.getType(device.getBluetoothClass().getMajorDeviceClass()));
    }

    public Set<BluetoothDevice> getBoundedDevices() {
        return fAdapter.getBondedDevices();
    }


}
