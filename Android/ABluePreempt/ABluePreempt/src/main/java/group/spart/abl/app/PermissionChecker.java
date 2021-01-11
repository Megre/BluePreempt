package group.spart.abl.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PermissionChecker {
    public static final int REQUEST_CODE_LOCATION = 1;

    private final String[] fPermsNeeded = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };
    private final MainActivity fActivity;

    public PermissionChecker() {
        fActivity = MainActivity.instance();
    }

    public void checkPermissions() {
        // ignore battery optimizations
        if(!isIgnoringBatteryOptimizations()) {
            requestIgnoreBatteryOptimizations();
        }

        // location permissions
        int[] grantResults = new int[fPermsNeeded.length];
        for (int idx=0; idx<fPermsNeeded.length; ++idx) {
            grantResults[idx] = ContextCompat.checkSelfPermission(fActivity, fPermsNeeded[idx]);
        }

        dealWithGrantedPerms(fPermsNeeded, grantResults);
        requestDeniedPerms(fPermsNeeded, grantResults);
    }

    public void dealWithGrantedPerms(String[] permissions, int[] grantResults) {
        for(int idx=0; idx<permissions.length; ++idx) {
            if(grantResults[idx] != PackageManager.PERMISSION_GRANTED) continue;

            if (permissions[idx].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (isGPSOpen()) continue;

                navigateToGPSSettings();
            }
        }
    }

    private void requestDeniedPerms(String[] permissions, int[] grantResults) {
        java.util.List<String> permsDenied = new ArrayList<>();
        for(int idx=0; idx<permissions.length; ++idx) {
            if(grantResults[idx] != PackageManager.PERMISSION_GRANTED) {
                permsDenied.add(permissions[idx]);
            }
        }

        if (permsDenied.isEmpty()) return;

        ActivityCompat.requestPermissions(fActivity,
                permsDenied.toArray(new String[0]),
                REQUEST_CODE_LOCATION);
    }

    private boolean isGPSOpen() {
        LocationManager locationManager = (LocationManager) fActivity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null
            && locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    private void navigateToGPSSettings() {
        new AlertDialog.Builder(fActivity)
                .setTitle("Please Open GPS")
                .setMessage("Location service is needed to scan bluetooth devices")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Settings",
                        (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            fActivity.startActivity(intent);
                        })
                .setCancelable(false)
                .show();
    }

    private boolean isIgnoringBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) fActivity.getSystemService(Context.POWER_SERVICE);
        return powerManager != null
                && powerManager.isIgnoringBatteryOptimizations(fActivity.getPackageName());
    }

    private void requestIgnoreBatteryOptimizations() {
        try {
            @SuppressLint("BatteryLife") Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + fActivity.getPackageName()));
            fActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
