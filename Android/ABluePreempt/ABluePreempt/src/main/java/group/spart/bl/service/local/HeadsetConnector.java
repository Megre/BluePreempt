package group.spart.bl.service.local;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import group.spart.abl.app.MainActivity;
import group.spart.bl.service.SyncCallback;

public class HeadsetConnector {
    private MainActivity fActivity;
    private BluetoothAdapter fBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothProfile.ServiceListener fA2dpListener, fHeadsetListener;
    private BluetoothA2dp mA2dp;
    private BluetoothHeadset mHeadset;
    private boolean fConnect;
    private BluetoothDevice fHeadsetDevice;
    private SyncCallback fCallback;

    public HeadsetConnector() {
        fActivity = MainActivity.instance();

        fA2dpListener = new ProfileServiceListener(BluetoothProfile.A2DP, new SyncCallback() {
            @Override
            public void invoke(boolean success, Object returnValues) {
                System.out.println("a2dp listener");
                connect(BluetoothProfile.A2DP, (BluetoothProfile) returnValues);

                if(fCallback != null) {
                    fCallback.invoke(true, null);
                }
            }
        });

        fHeadsetListener = new ProfileServiceListener(BluetoothProfile.HEADSET, new SyncCallback() {
            @Override
            public void invoke(boolean success, Object returnValues) {
                System.out.println("headset listener");
                connect(BluetoothProfile.HEADSET, (BluetoothProfile) returnValues);

                fBluetoothAdapter.getProfileProxy(fActivity, fA2dpListener, BluetoothProfile.A2DP);
            }
        });
    }

    public void connect(BluetoothDevice headsetDevice) {
        MainActivity.instance().notifyUser("connecting " + headsetDevice.getName());
        connectHeadset(true, headsetDevice);
    }

    public void disconnect(BluetoothDevice headsetDevice, SyncCallback callback) {
        fCallback = callback;
        connectHeadset(false, headsetDevice);
    }

    private void connectHeadset(boolean connect, BluetoothDevice headsetDevice) {
        fConnect = connect;
        fHeadsetDevice = headsetDevice;
        fBluetoothAdapter.getProfileProxy(fActivity, fHeadsetListener, BluetoothProfile.HEADSET);
    }

    private void connect(int profileType, BluetoothProfile profile) {
        String strMethod = (fConnect?"connect":"disconnect");
        try {
            Class<?> clazz = (profileType == BluetoothProfile.A2DP? BluetoothA2dp.class : BluetoothHeadset.class);
            Method method = clazz.getMethod(strMethod, BluetoothDevice.class);
            method.setAccessible(true);
            method.invoke(profileType == BluetoothProfile.A2DP? (BluetoothA2dp) profile : (BluetoothHeadset) profile,
                    fHeadsetDevice);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            fBluetoothAdapter.closeProfileProxy(profileType, profile);
        }
    }

    private class ProfileServiceListener implements BluetoothProfile.ServiceListener {
        private SyncCallback fCallback;
        private BluetoothProfile fProfile;
        private int fProfileType;
        public ProfileServiceListener(int profile, SyncCallback callback) {
            fCallback = callback;
            fProfileType = profile;
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if(profile == fProfileType) {
                fBluetoothAdapter.closeProfileProxy(fProfileType, fProfile);
            }
        }
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if(profile == fProfileType){
                fProfile = proxy;
                fCallback.invoke(true, fProfile);
            }
        }
    }
}
