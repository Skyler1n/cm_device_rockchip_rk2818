package com.rockchip.tscalibration;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

/* loaded from: classes.dex */
public class TouchScreenMonitor extends Activity {
    private final String TAG = "TSMonitor";

    @Override // android.app.Activity
    public void onCreate(Bundle service) {
        super.onCreate(service);
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
