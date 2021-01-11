package group.spart.abl.ua;

import group.spart.abl.app.MainActivity;

public class MarkAction extends UserAction {

    public MarkAction() {
        super(true);
    }

    @Override
    public Object actionPerformed() {
        int selectedIdx = MainActivity.instance().getSelectedDeviceIndex();
        if(selectedIdx < 0) {
            notifyUser("no headset selected");
            return null;
        }

        MainActivity.instance().getDataBinding().markDevice(selectedIdx);
        return null;
    }
}
