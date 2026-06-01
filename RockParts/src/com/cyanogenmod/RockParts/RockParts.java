package com.cyanogenmod.RockParts;

import com.cyanogenmod.RockParts.R;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RockParts extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rockparts);

        // RKTSCalibration
        Preference calibrationPref = findPreference("touchscreen_calibration");
        if (calibrationPref != null) {
            if (isPackageInstalled("com.rockchip.tscalibration")) {
                calibrationPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent();
                        intent.setClassName("com.rockchip.tscalibration", 
                                            "com.rockchip.tscalibration.TouchScreenMonitor");
                        startActivity(intent);
                        return true;
                    }
                });
            } else {
                getPreferenceScreen().removePreference(calibrationPref);
            }
        }
    }

    /**
     * Check if RKTSCalibration is installed
     */
    private boolean isPackageInstalled(String packageName) {
        try {
            getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void writeValue(String parameter, int value) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(parameter));
            fos.write(String.valueOf(value).getBytes());
            fos.flush();
            fos.getFD().sync();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // USB Host Mode
        if (prefs.getBoolean("usb_host_mode", false))
            writeValue("/sys/bus/platform/drivers/dwc_otg/force_usb_mode", 1);
        else
            writeValue("/sys/bus/platform/drivers/dwc_otg/force_usb_mode", 0);

        // Touch Vibration
        if (prefs.getBoolean("touchscreen_vibration", false))
            writeValue("/sys/bus/spi/drivers/xpt2046_ts/MOTOenable", 1);
        else
            writeValue("/sys/bus/spi/drivers/xpt2046_ts/MOTOenable", 0);
    }
}
