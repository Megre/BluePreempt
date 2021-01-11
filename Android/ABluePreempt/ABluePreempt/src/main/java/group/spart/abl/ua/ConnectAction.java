package group.spart.abl.ua;

import group.spart.abl.app.MainActivity;
import group.spart.bl.service.local.HeadsetConnector;
import group.spart.bl.service.remote.RemoteDevice;
import group.spart.bl.service.remote.RemoteDeviceInfo;
import group.spart.bl.service.remote.RemoteDisconnector;

public class ConnectAction extends UserAction {
    private final MainActivity fActivity;

    public ConnectAction() { fActivity = MainActivity.instance(); }

    @Override
    public Object actionPerformed() {
        RemoteDeviceInfo deviceInfo = fActivity.getSelectedDevice();
        if(deviceInfo == null) {
            notifyUser("no headset selected");
            return null;
        }

        // disconnect headsets on remote devices
        new RemoteDisconnector().disconnect(fActivity.getDataBinding().getHeadsetDevices(), (success, returnValues) -> {
            // connect the headset on local device
            new HeadsetConnector()
                .connect(RemoteDevice.fromAddress(deviceInfo.getAddress()));
        });

        return null;
    }

}
