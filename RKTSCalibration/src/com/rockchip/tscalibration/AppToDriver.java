package com.rockchip.tscalibration;

import android.graphics.Point;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Locale;

/* loaded from: classes.dex */
public class AppToDriver implements Operation {
    public static final String TOUCH_CHECK_VALUE = "TouchCheck";
    private final String TAG = "ATD";
    public File fileAdValue = new File("/sys/class/touchpanel/touchadc");
    public File fileAdCalibrate = new File("/sys/class/touchpanel/touchcheck");
    public File caliStatus = new File("/sys/class/touchpanel/calistatus");
    public String[] strTouch = {"CENTER_X", "CENTER_Y", "TOP_LEFT_X", "TOP_LEFT_Y", "TOP_RIGHT_X", "TOP_RIGHT_Y", "BOTTOM_LEFT_X", "BOTTOM_LEFT_Y", "BOTTOM_RIGHT_X", "BOTTOM_RIGHT_Y"};
    public final int TOUCH_TOP_LEFT = 0;
    public final int TOUCH_TOP_RIGHT = 1;
    public final int TOUCH_BOTTOM_LEFT = 2;
    public final int TOUCH_BOTTOM_RIGHT = 3;
    public final int TOUCH_CENTER = 4;
    public final int TOUCH_CHECK_END = 5;

    @Override // com.rockchip.tscalibration.Operation
    public String getStringFormFile(File file) {
        if (!file.exists()) {
            Log.d("ATD", "File not exist");
            return null;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            return raf.readLine();
        } catch (IOException e) {
            Log.w("ATD", "Exception opening file: " + file.getAbsolutePath(), e);
            return null;
        }
    }

    @Override // com.rockchip.tscalibration.Operation
    public boolean doCheckCalibration() {
        String status = getStringFormFile(this.caliStatus);
        if (status != null && status.length() > 0) {
            Log.v("ATD", "status" + status);
            if (status.equals("successful")) {
                return true;
            }
        }
        return false;
    }

    @Override // com.rockchip.tscalibration.Operation
    public boolean writeStringToFile(File file, String str) {
        if (!file.exists()) {
            Log.d("ATD", "File not exist");
            return false;
        }
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            char[] toChar = str.toCharArray();
            byte[] buffer = new byte[toChar.length];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = (byte) toChar[i];
            }
            raf.write(buffer);
            return true;
        } catch (IOException e) {
            Log.w("ATD", "Exception opening file: " + file.getAbsolutePath(), e);
            return false;
        }
    }

    @Override // com.rockchip.tscalibration.Operation
    public Point getPointAdc() {
        Log.d("ATD", "READY NEW POINT");
        Point adc = new Point(-1, -1);
        Log.d("ATD", "NEW POINT -1,-1");
        String strAdc = getStringFormFile(this.fileAdValue);
        Log.d("ATD", "Read String: " + strAdc);
        if (strAdc != null && strAdc.length() > 0) {
            int num = 0;
            char[] adcChars = strAdc.toCharArray();
            for (int i = 0; i < adcChars.length; i++) {
                if (adcChars[i] < '0' || adcChars[i] > '9') {
                    if (adcChars[i] != ',') {
                        break;
                    }
                    adc.x = num;
                    num = 0;
                } else {
                    num = (num * 10) + (adcChars[i] - '0');
                }
            }
            adc.y = num;
        }
        Log.d("ATD", "ADC: " + adc.toString());
        return adc;
    }

    @Override // com.rockchip.tscalibration.Operation
    public void setCalibrateAD(Point[] calibrateAD) {
        StringBuffer sb = new StringBuffer(100);
        for (int i = 0; i < 5; i++) {
            String substr = String.format(Locale.US, "%04d,%04d", Integer.valueOf(calibrateAD[i].x), Integer.valueOf(calibrateAD[i].y));
            Log.d("ATD", substr);
            if (i < 4) {
                substr = substr + ",";
            }
            sb.append(substr);
        }
        Log.d("ATD", "Write string: " + sb.toString());
        writeStringToFile(this.fileAdCalibrate, sb.toString());
    }
}
