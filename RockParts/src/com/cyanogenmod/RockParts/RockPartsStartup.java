package com.cyanogenmod.RockParts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RockPartsStartup extends BroadcastReceiver
{
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
   public void onReceive(final Context context, final Intent bootintent) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

       // USB Host Mode
       if(prefs.getBoolean("usb_host_mode", false))
        writeValue("/sys/bus/platform/drivers/dwc_otg/force_usb_mode", 1);
       else
        writeValue("/sys/bus/platform/drivers/dwc_otg/force_usb_mode", 0);

       // Touch Vibration
       if(prefs.getBoolean("touchscreen_vibration", false))
           writeValue("/sys/bus/spi/drivers/xpt2046_ts/MOTOenable", 1);
       else
           writeValue("/sys/bus/spi/drivers/xpt2046_ts/MOTOenable", 0);
   }
}
