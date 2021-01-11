package group.spart.abl.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import group.spart.abl.ua.ConnectAction;
import group.spart.abl.ua.HelpAction;
import group.spart.abl.ua.MarkAction;
import group.spart.abl.ua.ScanAction;
import group.spart.abl.ua.UserMessageHandler;
import group.spart.abl.ui.DisplayList;
import group.spart.abl.ui.TextViewAdapterFactory;
import group.spart.bl.app.DisplayListDataBinding;
import group.spart.bl.cfg.UserConfig;
import group.spart.bl.service.local.DisconnectService;
import group.spart.bl.service.remote.RemoteDevice;
import group.spart.bl.service.remote.RemoteDeviceInfo;

public class MainActivity extends AppCompatActivity {
    private static MainActivity fInstance;

    private final UserMessageHandler fMessageHandler;
    private DisplayListDataBinding fDataBinding;
    private final PermissionChecker fChecker;
    private DisplayList fHeadsetList;
    private DisplayList fOutputList;
    private final ExecutorService fExecutor;
    private UserConfig fUserConfig;

    public MainActivity() {
        fInstance = this;
        fMessageHandler = new UserMessageHandler();
        fChecker = new PermissionChecker();
        fExecutor = Executors.newCachedThreadPool();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // no title bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // initialize
        initialize();

        // check permissions
        if(!ABluetoothManager.isBluetoothEnabled()) {
            notifyUser("bluetooth device is not available");
        }
        fChecker.checkPermissions();
    }

    @Override
    public final void onRequestPermissionsResult(int requestCode,  @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PermissionChecker.REQUEST_CODE_LOCATION) {
            fChecker.dealWithGrantedPerms(permissions, grantResults);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            fUserConfig.saveConfig(fDataBinding);
            return moveTaskToBack(true);
        }
        return super.dispatchKeyEvent(event);
    }

    private void initialize() {
        // initialize display list
        ListView lvHeadsets = findViewById(R.id.lvHeadsets);
        ListView lvMasters = findViewById(R.id.lvMasters);
        ListView lvOutput = findViewById(R.id.lvOutputs);
        fHeadsetList = new DisplayList(lvHeadsets);
        DisplayList fMasterList = new DisplayList(lvMasters, TextViewAdapterFactory.STYLE_READONLY);
        fOutputList = new DisplayList(lvOutput, TextViewAdapterFactory.STYLE_OUTPUT);

        // read user.cfg
        fUserConfig = new UserConfig(new AEnvioronment().getConfigFilePath());

        // display devices
        fDataBinding = new DisplayListDataBinding(fHeadsetList, fMasterList);
        fDataBinding.update(fUserConfig.getSavedDevices());
        fDataBinding.markDevice(fUserConfig.getMarkedDeviceIdx());
        ((TextView) findViewById(R.id.tvCurrentDevice)).setText(BluetoothAdapter.getDefaultAdapter().getName());

        // start disconnection service
        execute(new DisconnectService(this));

        // user action
        findViewById(R.id.btnConnect).setOnClickListener(new ConnectAction());
        findViewById(R.id.btnRefresh).setOnClickListener(new ScanAction());
        findViewById(R.id.btnMark).setOnClickListener(new MarkAction());
        findViewById(R.id.ivHelp).setOnClickListener(new HelpAction());
    }

    public RemoteDeviceInfo getSelectedDevice() {
        if(getSelectedDeviceIndex() < 0) return null;

        return  fDataBinding.getHeadsetSource().get(getSelectedDeviceIndex());
    }

    public int getSelectedDeviceIndex() {
        return fHeadsetList.getSelection();
    }

    public DisplayListDataBinding getDataBinding() {
        return fDataBinding;
    }

    public void log(String message) {
        synchronized (this) {
            fOutputList.add(message);
        }
    }

    public void notifyUser(String message) {
        fMessageHandler.notifyUser(message);
    }

    public void updateDisplay(java.util.List<RemoteDeviceInfo> infoList) {
        fMessageHandler.updateDisplay(infoList);
    }

    public BluetoothDevice getMarkedHeadset() {
        RemoteDeviceInfo info = fDataBinding.getMarkedDeviceInfo();
        if(info == null) return null;

        return RemoteDevice.fromAddress(info.getAddress());
    }

    public void execute(Runnable runnable) {
        fExecutor.execute(runnable);
    }

    public static MainActivity instance() { return fInstance; }

}