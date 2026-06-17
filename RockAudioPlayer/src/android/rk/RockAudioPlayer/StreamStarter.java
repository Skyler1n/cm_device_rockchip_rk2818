package android.rk.RockAudioPlayer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URLDecoder;

/* loaded from: classes.dex */
public class StreamStarter extends Activity {
    private String mPath;
    private final String TAG = "StreamStarter.java";
    private BroadcastReceiver mStatusListener = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.StreamStarter.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            StreamStarter.this.LOG("mStatusListener.onReceive() : action = " + action);
            if (action.equals(AudioPlaybackService.MEDIAPLAY_ERROR)) {
                Log.e("StreamStarter.java", "MEDIAPLAY_ERROR@@@@@@");
                StreamStarter.this.MediaPlayerErrorShow();
            } else if (action.equals(AudioPlaybackService.ASYNC_OPEN_COMPLETE)) {
                try {
                    MusicUtils.sService.play();
                } catch (RemoteException e) {
                    Log.e("StreamStarter.java", "mStatusListener.onReceive() : caught a RemoteException : ", e);
                }
                Intent intent2 = new Intent("android.rk.RockAudioPlayer.PLAYBACK_VIEWER");
                intent2.putExtra("checkLayout", 1);
                StreamStarter.this.startActivity(intent2);
            }
            StreamStarter.this.finish();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void LOG(String msg) {
        if (RockAudioPlayer.DEBUG) {
            Log.d("StreamStarter.java", msg);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setVolumeControlStream(3);
        requestWindowFeature(1);
        setContentView(R.layout.streamstarter);
        TextView tv = (TextView) findViewById(R.id.streamloading);
        Uri uri = getIntent().getData();
        LOG("uri.getHost() = " + uri.getHost());
        String msg = getString(R.string.streamloadingtext);
        LOG("msg = " + msg);
        tv.setText(msg + " " + uri.getHost());
        this.mPath = getIntent().getData().toString();
        LOG("onCreate() : mPath = " + this.mPath);
        if (!this.mPath.toLowerCase().startsWith("http://") && !this.mPath.startsWith("content://")) {
            this.mPath = URLDecoder.decode(getIntent().getData().toString());
            int start = this.mPath.indexOf("://");
            this.mPath = this.mPath.substring(start + 3, this.mPath.length());
            LOG("mPath = " + this.mPath);
        }
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        MusicUtils.bindToService(this, new ServiceConnection() { // from class: android.rk.RockAudioPlayer.StreamStarter.1
            @Override // android.content.ServiceConnection
            public void onServiceConnected(ComponentName classname, IBinder obj) {
                try {
                    IntentFilter f = new IntentFilter();
                    f.addAction(AudioPlaybackService.ASYNC_OPEN_COMPLETE);
                    f.addAction(AudioPlaybackService.MEDIAPLAY_ERROR);
                    StreamStarter.this.registerReceiver(StreamStarter.this.mStatusListener, new IntentFilter(f));
                    StreamStarter.this.LOG("onServiceConnected() : To ask AudioPlaybackService to play '" + StreamStarter.this.mPath + "'.");
                    MusicUtils.sService.openfileAsync(StreamStarter.this.mPath);
                } catch (RemoteException e) {
                    Log.e("StreamStarter.java", "init is fail~~~~~~~~~~~~~~");
                }
            }

            @Override // android.content.ServiceConnection
            public void onServiceDisconnected(ComponentName classname) {
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void MediaPlayerErrorShow() {
        Toast.makeText(this, (int) R.string.no_support, 4000).show();
    }

    @Override // android.app.Activity
    public void onPause() {
        if (MusicUtils.sService != null) {
            try {
                if (!MusicUtils.sService.isPlaying()) {
                    MusicUtils.sService.stop();
                }
            } catch (RemoteException e) {
            }
        }
        unregisterReceiver(this.mStatusListener);
        MusicUtils.unbindFromService(this);
        super.onPause();
    }
}
