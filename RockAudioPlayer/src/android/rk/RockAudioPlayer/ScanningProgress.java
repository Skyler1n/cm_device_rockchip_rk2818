package android.rk.RockAudioPlayer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

/* loaded from: classes.dex */
public class ScanningProgress extends Activity {
    private static final int CHECK = 0;
    private Handler mHandler = new Handler() { // from class: android.rk.RockAudioPlayer.ScanningProgress.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                String status = Environment.getExternalStorageState();
                if (!status.equals("mounted")) {
                    ScanningProgress.this.finish();
                    return;
                }
                Cursor c = MusicUtils.query(ScanningProgress.this, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null, null, null);
                if (c != null) {
                    c.close();
                    ScanningProgress.this.setResult(-1);
                    ScanningProgress.this.finish();
                    return;
                }
                Message next = obtainMessage(0);
                sendMessageDelayed(next, 3000L);
            }
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setVolumeControlStream(3);
        requestWindowFeature(1);
        setContentView(R.layout.scanning);
        getWindow().setLayout(-2, -2);
        setResult(0);
        Message msg = this.mHandler.obtainMessage(0);
        this.mHandler.sendMessageDelayed(msg, 1000L);
    }

    @Override // android.app.Activity
    public void onDestroy() {
        this.mHandler.removeMessages(0);
        super.onDestroy();
    }
}
