package com.rockchip.tscalibration;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.IBinder;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import java.io.File;

/* loaded from: classes.dex */
public class AutoCalibration extends Service {
    private static final String TAG = "cali-service";
    private AppToDriver mATD = new AppToDriver();
    private File saveToPreference = new File("/data/data/com.rockchip.tscalibration/shared_prefs/TouchCheck.xml");

    public boolean LoadCalibrateFormPrefFile() {
        Log.d(TAG, "LoadCalibrateFormPrefFile....");
        Point[] calibrateAD = new Point[5];
        if (!this.saveToPreference.exists() || this.saveToPreference == null) {
            Log.v(TAG, "file lose:" + this.saveToPreference);
            return false;
        }
        SharedPreferences prefCalibrate = getSharedPreferences(AppToDriver.TOUCH_CHECK_VALUE, 0);
        int i = 0;
        while (true) {
            this.mATD.getClass();
            if (i < 5) {
                calibrateAD[i] = new Point(-1, -1);
                calibrateAD[i].x = prefCalibrate.getInt(this.mATD.strTouch[(i * 2) + 0], -1);
                calibrateAD[i].y = prefCalibrate.getInt(this.mATD.strTouch[(i * 2) + 1], -1);
                if (calibrateAD[i].x <= 0 || calibrateAD[i].y <= 0) {
                    break;
                }
                i++;
            } else {
                this.mATD.setCalibrateAD(calibrateAD);
                return this.mATD.doCheckCalibration();
            }
        }
        return false;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        boolean ret = LoadCalibrateFormPrefFile();
        int prop = Integer.parseInt(SystemProperties.get("ro.secure.tscalibration", "0"));
        int firstBoot = Settings.Secure.getInt(getContentResolver(), "device_provisioned", 0);
        if (!ret && prop == 1 && firstBoot != 0) {
            PackageManager pm = getPackageManager();
            ComponentName name = new ComponentName(this, TSCalibration.class);
            if (pm.getComponentEnabledSetting(name) == 2) {
                pm.setComponentEnabledSetting(name, 1, 1);
                Intent tscheck = new Intent(this, TSCalibration.class);
                tscheck.putExtra("enabletouchcheck", "enabletouchcheck");
                tscheck.addFlags(270532608);
                startActivity(tscheck);
            }
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }
}
