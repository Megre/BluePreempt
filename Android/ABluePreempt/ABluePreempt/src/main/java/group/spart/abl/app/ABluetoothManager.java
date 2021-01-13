package group.spart.abl.app;

import android.bluetooth.BluetoothAdapter;

public class ABluetoothManager {
    private static final BluetoothAdapter fAdapter = BluetoothAdapter.getDefaultAdapter();

    public static boolean isBluetoothEnabled() {
        if(fAdapter == null) return false;

        return fAdapter.isEnabled();
    }

    public static BluetoothAdapter adapter() { return fAdapter; }

}
