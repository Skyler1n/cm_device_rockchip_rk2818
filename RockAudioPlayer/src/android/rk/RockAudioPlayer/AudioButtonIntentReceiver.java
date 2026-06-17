package android.rk.RockAudioPlayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

/* loaded from: classes.dex */
public class AudioButtonIntentReceiver extends BroadcastReceiver {
    private static final int LONG_PRESS_DELAY = 1000;
    private static final int MSG_LONGPRESS_TIMEOUT = 1;
    private static final String TAG = "AudioButtonIntentReceiver";
    private static long mLastClickTime = 0;
    private static boolean mDown = false;
    private static boolean mLaunched = false;
    private static Handler mHandler = new Handler() { // from class: android.rk.RockAudioPlayer.AudioButtonIntentReceiver.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int i = msg.what;
        }
    };

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        KeyEvent event;
        String intentAction = intent.getAction();
        if ("android.media.AUDIO_BECOMING_NOISY".equals(intentAction)) {
            Intent i = new Intent(context, AudioPlaybackService.class);
            i.setAction(AudioPlaybackService.SERVICECMD);
            i.putExtra(AudioPlaybackService.CMDNAME, AudioPlaybackService.CMDPAUSE);
            context.startService(i);
        } else if ("android.intent.action.MEDIA_BUTTON".equals(intentAction) && (event = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT")) != null) {
            int keycode = event.getKeyCode();
            int action = event.getAction();
            String command = null;
            switch (keycode) {
                case 79:
                case 85:
                    command = AudioPlaybackService.CMDTOGGLEPAUSE;
                    break;
                case 86:
                    command = AudioPlaybackService.CMDSTOP;
                    break;
                case 87:
                    command = AudioPlaybackService.CMDNEXT;
                    break;
                case 88:
                    command = AudioPlaybackService.CMDPREVIOUS;
                    break;
            }
            if (command != null) {
                if (action != 0) {
                    mHandler.removeMessages(1);
                    mDown = false;
                } else if (!mDown) {
                    Intent i2 = new Intent(context, AudioPlaybackService.class);
                    i2.setAction(AudioPlaybackService.SERVICECMD);
                    i2.putExtra(AudioPlaybackService.CMDNAME, command);
                    context.startService(i2);
                    mDown = true;
                }
                abortBroadcast();
            }
        }
    }
}
