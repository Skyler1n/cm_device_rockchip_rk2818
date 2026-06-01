package com.rockchip.tscalibration;

import android.graphics.Point;
import java.io.File;

/* loaded from: classes.dex */
public interface Operation {
    boolean doCheckCalibration();

    Point getPointAdc();

    String getStringFormFile(File file);

    void setCalibrateAD(Point[] pointArr);

    boolean writeStringToFile(File file, String str);
}
