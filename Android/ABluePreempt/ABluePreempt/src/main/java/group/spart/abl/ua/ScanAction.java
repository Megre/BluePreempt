package group.spart.abl.ua;

import java.util.List;

import group.spart.abl.app.MainActivity;
import group.spart.bl.service.remote.RemoteDeviceDiscovery;
import group.spart.bl.service.remote.RemoteDeviceInfo;

public class ScanAction extends UserAction {

    @Override
    public Object actionPerformed() {
        MainActivity.instance().notifyUser("scanning bluetooth devices...");
        RemoteDeviceDiscovery deviceDiscovery = new RemoteDeviceDiscovery(this::invoke);
        deviceDiscovery.discover();

        return null;
    }

    private void invoke(boolean success, Object returnValues) {
        List<RemoteDeviceInfo> deviceList = (List<RemoteDeviceInfo>) returnValues;
        MainActivity.instance().updateDisplay(deviceList);
        MainActivity.instance().notifyUser(deviceList.size() + " devices found");
        finish();
    }
}
