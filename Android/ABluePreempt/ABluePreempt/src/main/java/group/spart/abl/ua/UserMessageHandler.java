package group.spart.abl.ua;

import android.os.Handler;
import android.os.Message;

import java.util.List;

import group.spart.abl.app.MainActivity;
import group.spart.bl.service.remote.RemoteDeviceInfo;

public class UserMessageHandler extends Handler {
    public final static int NOTIFY_USER = 1;
    public final static int UPDATE_DISPLAY = 2;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case NOTIFY_USER:
                MainActivity.instance().log(msg.obj.toString());
                break;
            case UPDATE_DISPLAY:
                MainActivity.instance().getDataBinding().update((List<RemoteDeviceInfo>) msg.obj);
                break;
        }
    }

    public void notifyUser(String message) {
        Message msg = new Message();
        msg.what = NOTIFY_USER;
        msg.obj = message;
        sendMessage(msg);
    }

    public void updateDisplay(List<RemoteDeviceInfo> infoList) {
        Message msg = new Message();
        msg.what = UPDATE_DISPLAY;
        msg.obj = infoList;
        sendMessage(msg);
    }
}
