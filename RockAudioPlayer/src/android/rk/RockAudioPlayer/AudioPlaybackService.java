package android.rk.RockAudioPlayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.rk.RockAudioPlayer.IAudioPlaybackService;
import android.rk.RockAudioPlayer.MusicUtils;
import android.util.Log;
import android.widget.RemoteViews;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

/* loaded from: classes.dex */
public class AudioPlaybackService extends Service implements MusicUtils.Defs {
    public static final String ASYNC_OPEN_COMPLETE = "android.rk.RockAudioPlayer.asyncopencomplete";
    private static final int AUDIOTRYTIME = 10000;
    private static final int BOOKMARKCOLIDX = 10;
    public static final String CMDNAME = "command";
    public static final String CMDNEXT = "next";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDSTOP = "stop";
    public static final String CMDTOGGLEPAUSE = "togglepause";
    private static final int FADEIN = 4;
    private static final int IDCOLIDX = 0;
    private static final int IDLE_DELAY = 60000;
    public static final int LAST = 3;
    private static final int MAX_HISTORY_SIZE = 10;
    public static final String MEDIAPLAY_ERROR = "android.rk.RockAudioPlayer.mediaplayerror";
    public static final String META_CHANGED = "android.rk.RockAudioPlayer.metachanged";
    public static final int NEXT = 2;
    public static final String NEXT_ACTION = "android.rk.RockAudioPlayer.musicservicecommand.next";
    public static final int NOW = 1;
    public static final String PAUSE_ACTION = "android.rk.RockAudioPlayer.musicservicecommand.pause";
    private static final int PHONE_CHANGED = 1;
    public static final int PLAYBACKSERVICE_STATUS = 1;
    public static final String PLAYBACK_COMPLETE = "android.rk.RockAudioPlayer.playbackcomplete";
    public static final String PLAYSTATE_CHANGED = "android.rk.RockAudioPlayer.playstatechanged";
    private static final int PODCASTCOLIDX = 9;
    public static final String PREVIOUS_ACTION = "android.rk.RockAudioPlayer.musicservicecommand.previous";
    public static final String QUEUE_CHANGED = "android.rk.RockAudioPlayer.queuechanged";
    private static final int REFRESH = 1;
    private static final int RELEASE_WAKELOCK = 2;
    public static final int REPEAT_ALLONCE = 2;
    public static final int REPEAT_ALLREPEAT = 3;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ONCE = 0;
    public static final int REPEAT_TRY = 4;
    private static final int SERVER_DIED = 3;
    public static final String SERVICECMD = "android.rk.RockAudioPlayer.musicservicecommand";
    public static final int SHUFFLE_AUTO = 2;
    public static final int SHUFFLE_CIRCLE = 4;
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_ONE = 3;
    public static final String STOP_FOR_SD_CARD_REMOVED = "android.rk.RockAudioPlayer.stopforsdcardremoved";
    private static final String TAG = "AudioPlaybackService";
    public static final String TOGGLEPAUSE_ACTION = "android.rk.RockAudioPlayer.musicservicecommand.togglepause";
    private static final int TRACK_ENDED = 1;
    private static int mRepeatMode = 3;
    private BroadcastReceiver mAudioButtonReceiver;
    private int mCardId;
    private Cursor mCursor;
    private String mFilePath;
    private String mFileToPlay;
    private boolean mOneShot;
    private MultiPlayer mPlayer;
    private SharedPreferences mPreferences;
    private PowerManager.WakeLock mWakeLock;
    private AudioPlayerAppWidgetProvider mAppWidgetProvider = AudioPlayerAppWidgetProvider.getInstance();
    private int mShuffleMode = 0;
    private int mMediaMountedCount = 0;
    private int[] mAutoShuffleList = null;
    private boolean Eject = false;
    private int[] mPlayList = null;
    private int mPlayListLen = 0;
    private int mPlayPos = -1;
    private Vector<Integer> mHistory = new Vector<Integer>(10);
    private final Shuffler mRand = new Shuffler();
    private int mOpenFailedCounter = 0;
    private int mNosongidCount = 0;
    String[] mCursorCols = {"audio._id AS _id", "artist", "album", "title", "_data", "_display_name", "mime_type", "album_id", "artist_id", "duration", "is_podcast", "bookmark"};
    private BroadcastReceiver mUnmountReceiver = null;
    private int mServiceStartId = -1;
    private boolean mServiceInUse = false;
    private boolean mResumeAfterCall = false;
    private boolean mWasPlaying = false;
    private boolean mQuietMode = false;
    private String mTracknameBack = null;
    private String mDisplayname = null;
    private Handler mPhoneHandler = new Handler() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            AudioPlaybackService.this.LOG("mPhoneHandler.handleMessage() : msg.what = " + msg.what);
            switch (msg.what) {
                case 1:
                default:
                    return;
            }
        }
    };
    private final Handler mRefreshHandler = new Handler() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.2
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    long cur_time = AudioPlaybackService.this.position();
                    if (4 == AudioPlaybackService.this.getRepeatMode() && 10000 <= cur_time) {
                        if (!AudioPlaybackService.this.mOneShot) {
                            AudioPlaybackService.this.next(true);
                            AudioPlaybackService.this.opennext();
                            AudioPlaybackService.this.notifyChange(AudioPlaybackService.META_CHANGED);
                        } else {
                            AudioPlaybackService.this.seek(0L);
                            AudioPlaybackService.this.play();
                        }
                    }
                    if (2 == MusicUtils.getABRequire() && MusicUtils.getABEnd() <= cur_time) {
                        MusicUtils.setABRequire(1);
                        AudioPlaybackService.this.AudioSetAB();
                    }
                    AudioPlaybackService.this.NextRefresh(1000L);
                    return;
                default:
                    return;
            }
        }
    };
    private Handler mMediaplayerHandler = new Handler() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.3
        float mCurrentVolume = 1.0f;

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            AudioPlaybackService.this.LOG("mMediaplayerHandler.handleMessage() : msg.what = " + msg.what);
            switch (msg.what) {
                case 1:
                    if (AudioPlaybackService.mRepeatMode == 1) {
                        AudioPlaybackService.this.seek(0L);
                        AudioPlaybackService.this.play();
                        return;
                    } else if (AudioPlaybackService.mRepeatMode == 0 || (AudioPlaybackService.mRepeatMode == 2 && AudioPlaybackService.this.mPlayPos >= AudioPlaybackService.this.mPlayListLen - 1)) {
                        AudioPlaybackService.this.seek(0L);
                        AudioPlaybackService.this.pause();
                        return;
                    } else if (!AudioPlaybackService.this.mOneShot) {
                        AudioPlaybackService.this.next(false);
                        AudioPlaybackService.this.opennext();
                        AudioPlaybackService.this.notifyChange(AudioPlaybackService.META_CHANGED);
                        return;
                    } else {
                        AudioPlaybackService.this.notifyChange(AudioPlaybackService.PLAYBACK_COMPLETE);
                        return;
                    }
                case 2:
                    AudioPlaybackService.this.mWakeLock.release();
                    return;
                case 3:
                    if (AudioPlaybackService.this.mWasPlaying) {
                        AudioPlaybackService.this.next(true);
                        AudioPlaybackService.this.opennext();
                        AudioPlaybackService.this.notifyChange(AudioPlaybackService.META_CHANGED);
                        return;
                    }
                    AudioPlaybackService.this.openCurrent();
                    return;
                case 4:
                    if (!AudioPlaybackService.this.isPlaying()) {
                        this.mCurrentVolume = 0.0f;
                        AudioPlaybackService.this.mPlayer.setVolume(this.mCurrentVolume);
                        AudioPlaybackService.this.play();
                        AudioPlaybackService.this.mMediaplayerHandler.sendEmptyMessageDelayed(4, 10L);
                        return;
                    }
                    this.mCurrentVolume += 0.01f;
                    if (this.mCurrentVolume < 1.0f) {
                        AudioPlaybackService.this.mMediaplayerHandler.sendEmptyMessageDelayed(4, 10L);
                    } else {
                        this.mCurrentVolume = 1.0f;
                    }
                    AudioPlaybackService.this.mPlayer.setVolume(this.mCurrentVolume);
                    return;
                default:
                    return;
            }
        }
    };
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra(AudioPlaybackService.CMDNAME);
            AudioPlaybackService.this.LOG("mIntentReceiver.onReceive() : action = " + action + ",cmd = " + cmd);
            if (AudioPlaybackService.CMDNEXT.equals(cmd) || AudioPlaybackService.NEXT_ACTION.equals(action)) {
                AudioPlaybackService.this.next(true);
                AudioPlaybackService.this.opennext();
                AudioPlaybackService.this.notifyChange(AudioPlaybackService.META_CHANGED);
            } else if (AudioPlaybackService.CMDPREVIOUS.equals(cmd) || AudioPlaybackService.PREVIOUS_ACTION.equals(action)) {
                AudioPlaybackService.this.prev();
                AudioPlaybackService.this.opennext();
                AudioPlaybackService.this.notifyChange(AudioPlaybackService.META_CHANGED);
            } else if (AudioPlaybackService.CMDTOGGLEPAUSE.equals(cmd) || AudioPlaybackService.TOGGLEPAUSE_ACTION.equals(action)) {
                if (AudioPlaybackService.this.isPlaying()) {
                    AudioPlaybackService.this.pause();
                } else {
                    AudioPlaybackService.this.play();
                }
            } else if (AudioPlaybackService.CMDPAUSE.equals(cmd) || AudioPlaybackService.PAUSE_ACTION.equals(action)) {
                AudioPlaybackService.this.pause();
            } else if (AudioPlaybackService.CMDSTOP.equals(cmd)) {
                AudioPlaybackService.this.pause();
                AudioPlaybackService.this.seek(0L);
            } else if (AudioPlayerAppWidgetProvider.CMDAPPWIDGETUPDATE.equals(cmd)) {
                int[] appWidgetIds = intent.getIntArrayExtra("appWidgetIds");
                AudioPlaybackService.this.mAppWidgetProvider.performUpdate(AudioPlaybackService.this, appWidgetIds);
            }
        }
    };
    private final char[] hexdigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private Handler mDelayedStopHandler = new Handler() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.5
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            AudioPlaybackService.this.LOG("mDelayedStopHandler.handleMessage() : msg.what = " + msg.what);
            if (!AudioPlaybackService.this.isPlaying() && !AudioPlaybackService.this.mResumeAfterCall && !AudioPlaybackService.this.mServiceInUse && !AudioPlaybackService.this.mMediaplayerHandler.hasMessages(1)) {
                AudioPlaybackService.this.saveQueue(true);
                Log.i(AudioPlaybackService.TAG, "mDelayedStopHandler.handleMessage() : Idle time out, but we do NOT stop AudioPlaybackService");
            }
        }
    };
    private final IAudioPlaybackService.Stub mBinder = new IAudioPlaybackService.Stub() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.7
        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void openfileAsync(String path) {
            AudioPlaybackService.this.openAsync(path);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void openfile(String path) {
            AudioPlaybackService.this.open(path, true);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void open(int[] list, int position) {
            AudioPlaybackService.this.open(list, position);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getQueuePosition() {
            return AudioPlaybackService.this.getQueuePosition();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void setQueuePosition(int index) {
            AudioPlaybackService.this.setQueuePosition(index);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public boolean isPlaying() {
            return AudioPlaybackService.this.isPlaying();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void stop() {
            AudioPlaybackService.this.stop();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void pause() {
            AudioPlaybackService.this.LOG("Stub.pause() : Entered.");
            AudioPlaybackService.this.pause();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void play() {
            AudioPlaybackService.this.LOG("MultiPlayer.play() : Entered.");
            AudioPlaybackService.this.play();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void prev() {
            AudioPlaybackService.this.prev();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void next() {
            AudioPlaybackService.this.next(true);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void opennext() {
            AudioPlaybackService.this.opennext();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getTrackName() {
            return AudioPlaybackService.this.getTrackName();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getAlbumName() {
            return AudioPlaybackService.this.getAlbumName();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getAlbumId() {
            return AudioPlaybackService.this.getAlbumId();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getArtistName() {
            return AudioPlaybackService.this.getArtistName();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getArtistId() {
            return AudioPlaybackService.this.getArtistId();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getbitrate() {
            return AudioPlaybackService.this.getbitrate();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getGenrelName(int songid) {
            return AudioPlaybackService.this.getGenrelName(songid);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getGenrelId() {
            return AudioPlaybackService.this.getGenrelId();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getDisplayName() {
            return AudioPlaybackService.this.getDisplayName();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public long getDuration() {
            return AudioPlaybackService.this.getDuration();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void enqueue(int[] list, int action) {
            AudioPlaybackService.this.enqueue(list, action);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int[] getQueue() {
            return AudioPlaybackService.this.getQueue();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void moveQueueItem(int from, int to) {
            AudioPlaybackService.this.moveQueueItem(from, to);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getPath() {
            return AudioPlaybackService.this.getPath();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getDirPath() {
            return AudioPlaybackService.this.getDirPath();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getAudioId() {
            return AudioPlaybackService.this.getAudioId();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public long position() {
            return AudioPlaybackService.this.position();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public long duration() {
            return AudioPlaybackService.this.duration();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public long seek(long pos) {
            return AudioPlaybackService.this.seek(pos);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void setShuffleMode(int shufflemode) {
            AudioPlaybackService.this.setShuffleMode(shufflemode);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getShuffleMode() {
            return AudioPlaybackService.this.getShuffleMode();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int removeTracks(int first, int last) {
            return AudioPlaybackService.this.removeTracks(first, last);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int removeTrack(int id) {
            return AudioPlaybackService.this.removeTrack(id);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void setRepeatMode(int repeatmode) {
            AudioPlaybackService.this.setRepeatMode(repeatmode);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getRepeatMode() {
            return AudioPlaybackService.this.getRepeatMode();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public int getMediaMountedCount() {
            return AudioPlaybackService.this.getMediaMountedCount();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void setTracknameBack(String str) {
            AudioPlaybackService.this.setTracknameBack(str);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getTracknameBack() {
            return AudioPlaybackService.this.getTracknameBack();
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public void setDisplayname(String str) {
            AudioPlaybackService.this.setDisplayname(str);
        }

        @Override // android.rk.RockAudioPlayer.IAudioPlaybackService
        public String getDisplayname() {
            return AudioPlaybackService.this.getDisplayname();
        }
    };

    static /* synthetic */ int access$2008(AudioPlaybackService x0) {
        int i = x0.mMediaMountedCount;
        x0.mMediaMountedCount = i + 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void LOG(String msg) {
        if (RockAudioPlayer.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void LOG(String msg, Exception e) {
        if (RockAudioPlayer.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public void setTracknameBack(String str) {
        Log.d("TAG", "audiopalyBackService the str is " + str);
        this.mTracknameBack = str;
    }

    public String getTracknameBack() {
        Log.d("TAG", "audiopalyBackService the mTracknameBack is " + this.mTracknameBack);
        return this.mTracknameBack;
    }

    public void setDisplayname(String str) {
        Log.d("TAG", "audiopalyBackService the str is " + str);
        this.mDisplayname = str;
    }

    public String getDisplayname() {
        Log.d("TAG", "audiopalyBackService the getDisplayname is " + this.mDisplayname);
        return this.mDisplayname;
    }

    private void startAndFadeIn() {
        this.mMediaplayerHandler.sendEmptyMessageDelayed(4, 10L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void AudioSetAB() {
        if (MusicUtils.getABRequire() == 1) {
            MusicUtils.setABRequire(2);
            seek(MusicUtils.getABStart());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void NextRefresh(long delay) {
        Message msg = this.mRefreshHandler.obtainMessage(1);
        this.mRefreshHandler.removeMessages(1);
        this.mRefreshHandler.sendMessageDelayed(msg, delay);
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        LOG("onCreate() : Entered.");
        this.mPreferences = getSharedPreferences("Music", 3);
        this.mCardId = FileUtils.getFatVolumeId(Environment.getExternalStorageDirectory().getPath());
        registerExternalStorageListener();
        this.mPlayer = new MultiPlayer();
        this.mPlayer.setHandler(this.mMediaplayerHandler);
        NotificationManager nm = (NotificationManager) getSystemService("notification");
        nm.cancel(1);
        reloadQueue();
        IntentFilter commandFilter = new IntentFilter();
        commandFilter.addAction(SERVICECMD);
        commandFilter.addAction(TOGGLEPAUSE_ACTION);
        commandFilter.addAction(PAUSE_ACTION);
        commandFilter.addAction(NEXT_ACTION);
        commandFilter.addAction(PREVIOUS_ACTION);
        registerReceiver(this.mIntentReceiver, commandFilter);
        this.mAudioButtonReceiver = new AudioButtonIntentReceiver();
        IntentFilter audioButtonIntentFilter = new IntentFilter("android.intent.action.MEDIA_BUTTON");
        audioButtonIntentFilter.setPriority(1);
        registerReceiver(this.mAudioButtonReceiver, audioButtonIntentFilter);
        PowerManager pm = (PowerManager) getSystemService("power");
        this.mWakeLock = pm.newWakeLock(1, getClass().getName());
        this.mWakeLock.setReferenceCounted(false);
        Message msg = this.mDelayedStopHandler.obtainMessage();
        this.mDelayedStopHandler.sendMessageDelayed(msg, 60000L);
        LOG("Quit onCreate()");
        NextRefresh(1000L);
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (isPlaying()) {
            Log.e(TAG, "Service being destroyed while still playing.");
        }
        this.mPlayer.stop();
        if (this.mCursor != null) {
            this.mCursor.close();
            this.mCursor = null;
        }
        unregisterReceiver(this.mIntentReceiver);
        unregisterReceiver(this.mAudioButtonReceiver);
        if (this.mUnmountReceiver != null) {
            unregisterReceiver(this.mUnmountReceiver);
            this.mUnmountReceiver = null;
        }
        this.mWakeLock.release();
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveQueue(boolean full) {
        if (!this.mOneShot) {
            SharedPreferences.Editor ed = this.mPreferences.edit();
            if (full) {
                StringBuilder q = new StringBuilder();
                int len = this.mPlayListLen;
                for (int i = 0; i < len; i++) {
                    int n = this.mPlayList[i];
                    if (n == 0) {
                        q.append("0;");
                    } else {
                        while (n != 0) {
                            int digit = n & 15;
                            n >>= 4;
                            q.append(this.hexdigits[digit]);
                        }
                        q.append(";");
                    }
                }
                ed.putString("queue", q.toString());
                ed.putInt("cardid", this.mCardId);
            }
            ed.putInt("curpos", this.mPlayPos);
            if (this.mPlayer.isInitialized()) {
                ed.putLong("seekpos", this.mPlayer.position());
            }
            ed.putInt("repeatmode", mRepeatMode);
            ed.putInt("shufflemode", this.mShuffleMode);
            ed.commit();
        }
    }

    private void reloadQueue() {
        int i;
        String q = null;
        LOG("ENTER reloadQueue");
        boolean newstyle = false;
        int id = this.mCardId;
        LOG(" mPreferences = " + this.mPreferences);
        if (this.mPreferences.contains("cardid")) {
            newstyle = true;
            id = this.mPreferences.getInt("cardid", this.mCardId ^ (-1));
        }
        if (id == this.mCardId) {
            q = this.mPreferences.getString("queue", "");
        }
        if (q != null && q.length() > 1) {
            String[] entries = q.split(";");
            int len = entries.length;
            ensurePlayListCapacity(len);
            for (int i2 = 0; i2 < len; i2++) {
                if (newstyle) {
                    String revhex = entries[i2];
                    int n = 0;
                    for (int j = revhex.length() - 1; j >= 0; j--) {
                        n <<= 4;
                        char c = revhex.charAt(j);
                        if (c >= '0' && c <= '9') {
                            i = c - '0';
                        } else if (c < 'a' || c > 'f') {
                            len = 0;
                            break;
                        } else {
                            i = (c + '\n') - 97;
                        }
                        n += i;
                    }
                    this.mPlayList[i2] = n;
                } else {
                    this.mPlayList[i2] = Integer.parseInt(entries[i2]);
                }
            }
            this.mPlayListLen = len;
            LOG("reloadQueue() : mPlayListLen = len : " + len);
            int pos = this.mPreferences.getInt("curpos", 0);
            if (pos < 0 || pos >= len) {
                this.mPlayListLen = 0;
                LOG("reloadQueue() : pos = " + pos + ", mPlayListLen : " + this.mPlayListLen);
                return;
            }
            this.mPlayPos = pos;
            Cursor c2 = MusicUtils.query(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_id=" + this.mPlayList[this.mPlayPos], null, null);
            if (c2 == null || c2.getCount() == 0) {
                SystemClock.sleep(3000L);
                c2 = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, "_id=" + this.mPlayList[this.mPlayPos], null, null);
            }
            if (c2 != null) {
                c2.close();
            }
            this.mOpenFailedCounter = 20;
            this.mQuietMode = true;
            this.mQuietMode = false;
            if (!this.mPlayer.isInitialized()) {
                this.mPlayListLen = 0;
                LOG("reloadQueue() : mPlayer is not initialized. mPlayListLen : " + this.mPlayListLen);
                return;
            }
            long seekpos = this.mPreferences.getLong("seekpos", 0L);
            seek((seekpos < 0 || seekpos >= duration()) ? 0L : seekpos);
            int repmode = this.mPreferences.getInt("repeatmode", 0);
            if (repmode > 4 || repmode < 0) {
                repmode = 0;
            }
            mRepeatMode = repmode;
            int shufmode = this.mPreferences.getInt("shufflemode", 0);
            if (!(shufmode == 2 || shufmode == 1)) {
                shufmode = 0;
            }
            if (shufmode == 2 && !makeAutoShuffleList()) {
                shufmode = 0;
            }
            this.mShuffleMode = shufmode;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        this.mDelayedStopHandler.removeCallbacksAndMessages(null);
        this.mServiceInUse = true;
        return this.mBinder;
    }

    @Override // android.app.Service
    public void onRebind(Intent intent) {
        this.mDelayedStopHandler.removeCallbacksAndMessages(null);
        this.mServiceInUse = true;
    }

    @Override // android.app.Service
    public void onStart(Intent intent, int startId) {
        this.mServiceStartId = startId;
        this.mDelayedStopHandler.removeCallbacksAndMessages(null);
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        String cmd = intent.getStringExtra(CMDNAME);
        LOG("onStart() : action = " + action + ", cmd = " + cmd + ", this = " + this);
        if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
            next(true);
            opennext();
            notifyChange(META_CHANGED);
        } else if (CMDPREVIOUS.equals(cmd) || PREVIOUS_ACTION.equals(action)) {
            prev();
            opennext();
            notifyChange(META_CHANGED);
        } else if (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action)) {
            if (isPlaying()) {
                pause();
            } else {
                play();
            }
        } else if (CMDPAUSE.equals(cmd) || PAUSE_ACTION.equals(action)) {
            pause();
        } else if (CMDSTOP.equals(cmd)) {
            pause();
            seek(0L);
        }
        this.mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = this.mDelayedStopHandler.obtainMessage();
        this.mDelayedStopHandler.sendMessageDelayed(msg, 60000L);
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        LOG("onUnbind() : Entered.");
        this.mServiceInUse = false;
        saveQueue(true);
        if (isPlaying() || this.mResumeAfterCall) {
            return true;
        }
        if (this.mPlayListLen > 0 || this.mMediaplayerHandler.hasMessages(1)) {
            Message msg = this.mDelayedStopHandler.obtainMessage();
            this.mDelayedStopHandler.sendMessageDelayed(msg, 60000L);
            return true;
        }
        LOG("onUnbind() : To stop Service itself. mServiceStartId = " + this.mServiceStartId);
        stopSelf(this.mServiceStartId);
        return true;
    }

    public void closeExternalStorageFiles(String storagePath) {
        stop(true);
        notifyChange(QUEUE_CHANGED);
        notifyChange(META_CHANGED);
        notifyChange(STOP_FOR_SD_CARD_REMOVED);
    }

    public void registerExternalStorageListener() {
        if (this.mUnmountReceiver == null) {
            this.mUnmountReceiver = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.6
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    AudioPlaybackService.this.LOG("mUnmountReceiver.onReceive() : action = " + action);
                    if (action.equals("android.intent.action.MEDIA_EJECT") || action.equals("android.intent.action.MEDIA_BAD_REMOVAL") || action.equals("android.intent.action.MEDIA_UNMOUNTED")) {
                        String path = intent.getData().getPath();
                        AudioPlaybackService.this.LOG("mUnmountReceiver.onReceive() : path = " + path + ", mFilePath = " + AudioPlaybackService.this.mFilePath);
                        if (AudioPlaybackService.this.mFilePath != null && AudioPlaybackService.this.mFilePath.startsWith(path)) {
                            Log.w(AudioPlaybackService.TAG, "mUnmountReceiver.onReceive() : Source volume was EJECTED or BAD_REMOVED, to stop.");
                            AudioPlaybackService.this.saveQueue(true);
                            AudioPlaybackService.this.Eject = true;
                            AudioPlaybackService.this.mOneShot = true;
                            AudioPlaybackService.this.closeExternalStorageFiles(path);
                        }
                    } else if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                        AudioPlaybackService.access$2008(AudioPlaybackService.this);
                        AudioPlaybackService.this.mCardId = FileUtils.getFatVolumeId(intent.getData().getPath());
                        AudioPlaybackService.this.notifyChange(AudioPlaybackService.QUEUE_CHANGED);
                        AudioPlaybackService.this.notifyChange(AudioPlaybackService.META_CHANGED);
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction("android.intent.action.MEDIA_EJECT");
            iFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
            iFilter.addAction("android.intent.action.MEDIA_MOUNTED");
            iFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
            iFilter.addDataScheme("file");
            registerReceiver(this.mUnmountReceiver, iFilter);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyChange(String what) {
        LOG("notifyChange() : Entered : what = " + what);
        Intent i = new Intent(what);
        i.putExtra("id", Integer.valueOf(getAudioId()));
        i.putExtra("artist", getArtistName());
        i.putExtra("album", getAlbumName());
        i.putExtra("track", getTrackName());
        if (this.Eject) {
            i.putExtra("eject", true);
        }
        sendBroadcast(i);
        if (what.equals(QUEUE_CHANGED)) {
            saveQueue(true);
        } else {
            saveQueue(false);
        }
        this.mAppWidgetProvider.notifyChange(this, what);
    }

    private void ensurePlayListCapacity(int size) {
        if (this.mPlayList == null || size > this.mPlayList.length) {
            int[] newlist = new int[size * 2];
            int len = this.mPlayListLen;
            for (int i = 0; i < len; i++) {
                newlist[i] = this.mPlayList[i];
            }
            this.mPlayList = newlist;
        }
    }

    private void addToPlayList(int[] list, int position) {
        int addlen = list.length;
        if (position < 0) {
            this.mPlayListLen = 0;
            position = 0;
        }
        LOG("addToPlayList() : mPlayListLen : " + this.mPlayListLen + ", position : " + position);
        ensurePlayListCapacity(this.mPlayListLen + addlen);
        if (position > this.mPlayListLen) {
            position = this.mPlayListLen;
        }
        int tailsize = this.mPlayListLen - position;
        for (int i = tailsize; i > 0; i--) {
            this.mPlayList[position + i] = this.mPlayList[(position + i) - addlen];
        }
        for (int i2 = 0; i2 < addlen; i2++) {
            this.mPlayList[position + i2] = list[i2];
        }
        this.mPlayListLen += addlen;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0020 A[Catch: all -> 0x0051, TryCatch #0 {, blocks: (B:6:0x0008, B:8:0x0010, B:9:0x001c, B:11:0x0020, B:12:0x002e, B:14:0x0030, B:16:0x003e, B:17:0x004f), top: B:22:0x0008 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void enqueue(int[] r3, int r4) {
        /*
            r2 = this;
            java.lang.String r0 = "android.rk.RockAudioPlayer.queuechanged"
            java.lang.String r0 = "android.rk.RockAudioPlayer.metachanged"
            monitor-enter(r2)
            r0 = 2
            if (r4 != r0) goto L_0x0030
            int r0 = r2.mPlayPos     // Catch: all -> 0x0051
            int r0 = r0 + 1
            int r1 = r2.mPlayListLen     // Catch: all -> 0x0051
            if (r0 >= r1) goto L_0x0030
            int r0 = r2.mPlayPos     // Catch: all -> 0x0051
            int r0 = r0 + 1
            r2.addToPlayList(r3, r0)     // Catch: all -> 0x0051
            java.lang.String r0 = "android.rk.RockAudioPlayer.queuechanged"
            r2.notifyChange(r0)     // Catch: all -> 0x0051
        L_0x001c:
            int r0 = r2.mPlayPos     // Catch: all -> 0x0051
            if (r0 >= 0) goto L_0x002e
            r0 = 0
            r2.mPlayPos = r0     // Catch: all -> 0x0051
            r2.openCurrent()     // Catch: all -> 0x0051
            r2.play()     // Catch: all -> 0x0051
            java.lang.String r0 = "android.rk.RockAudioPlayer.metachanged"
            r2.notifyChange(r0)     // Catch: all -> 0x0051
        L_0x002e:
            monitor-exit(r2)     // Catch: all -> 0x0051
        L_0x002f:
            return
        L_0x0030:
            r0 = 2147483647(0x7fffffff, float:NaN)
            r2.addToPlayList(r3, r0)     // Catch: all -> 0x0051
            java.lang.String r0 = "android.rk.RockAudioPlayer.queuechanged"
            r2.notifyChange(r0)     // Catch: all -> 0x0051
            r0 = 1
            if (r4 != r0) goto L_0x001c
            int r0 = r2.mPlayListLen     // Catch: all -> 0x0051
            int r1 = r3.length     // Catch: all -> 0x0051
            int r0 = r0 - r1
            r2.mPlayPos = r0     // Catch: all -> 0x0051
            r2.openCurrent()     // Catch: all -> 0x0051
            r2.play()     // Catch: all -> 0x0051
            java.lang.String r0 = "android.rk.RockAudioPlayer.metachanged"
            r2.notifyChange(r0)     // Catch: all -> 0x0051
            monitor-exit(r2)     // Catch: all -> 0x0051
            goto L_0x002f
        L_0x0051:
            r0 = move-exception
            monitor-exit(r2)     // Catch: all -> 0x0051
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.rk.RockAudioPlayer.AudioPlaybackService.enqueue(int[], int):void");
    }

    public void open(int[] list, int position) {
        LOG("open(int[], int) : Entered");
        synchronized (this) {
            if (this.mShuffleMode == 2) {
                this.mShuffleMode = 1;
            }
            int oldId = getAudioId();
            int listlength = list.length;
            boolean newlist = true;
            if (this.mPlayListLen == listlength) {
                newlist = false;
                int i = 0;
                while (true) {
                    if (i >= listlength) {
                        break;
                    } else if (list[i] != this.mPlayList[i]) {
                        newlist = true;
                        break;
                    } else {
                        i++;
                    }
                }
            }
            if (newlist) {
                addToPlayList(list, -1);
                notifyChange(QUEUE_CHANGED);
            }
            int i2 = this.mPlayPos;
            if (position >= 0) {
                this.mPlayPos = position;
            } else {
                this.mPlayPos = this.mRand.nextInt(this.mPlayListLen);
            }
            this.mHistory.clear();
            saveBookmarkIfNeeded();
            openCurrent();
            if (oldId != getAudioId()) {
                notifyChange(META_CHANGED);
            }
        }
    }

    public void moveQueueItem(int index1, int index2) {
        synchronized (this) {
            if (index1 >= this.mPlayListLen) {
                index1 = this.mPlayListLen - 1;
            }
            if (index2 >= this.mPlayListLen) {
                index2 = this.mPlayListLen - 1;
            }
            if (index1 < index2) {
                int tmp = this.mPlayList[index1];
                for (int i = index1; i < index2; i++) {
                    this.mPlayList[i] = this.mPlayList[i + 1];
                }
                this.mPlayList[index2] = tmp;
                if (this.mPlayPos == index1) {
                    this.mPlayPos = index2;
                } else if (this.mPlayPos >= index1 && this.mPlayPos <= index2) {
                    this.mPlayPos--;
                }
            } else if (index2 < index1) {
                int tmp2 = this.mPlayList[index1];
                for (int i2 = index1; i2 > index2; i2--) {
                    this.mPlayList[i2] = this.mPlayList[i2 - 1];
                }
                this.mPlayList[index2] = tmp2;
                if (this.mPlayPos == index1) {
                    this.mPlayPos = index2;
                } else if (this.mPlayPos >= index2 && this.mPlayPos <= index1) {
                    this.mPlayPos++;
                }
            }
            notifyChange(QUEUE_CHANGED);
        }
    }

    public int[] getQueue() {
        int[] list;
        synchronized (this) {
            int len = this.mPlayListLen;
            list = new int[len];
            for (int i = 0; i < len; i++) {
                list[i] = this.mPlayList[i];
            }
        }
        return list;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openCurrent() {
        synchronized (this) {
            if (this.mCursor != null) {
                this.mCursor.close();
                this.mCursor = null;
            }
            if (this.mPlayListLen != 0) {
                stop(false);
                String id = String.valueOf(this.mPlayList[this.mPlayPos]);
                this.mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, "_id=" + id, null, null);
                if (this.mCursor != null) {
                    this.mCursor.moveToFirst();
                    open(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id, false);
                    if (isPodcast()) {
                        long bookmark = getBookmark();
                        seek(bookmark - 5000);
                    }
                }
            }
        }
    }

    public void openAsync(String path) {
        synchronized (this) {
            if (path != null) {
                LOG("openAsync() path = " + path);
                LOG("openAsync() : To set mFilePath to '" + path + "'.");
                mRepeatMode = 1;
                ensurePlayListCapacity(1);
                this.mPlayListLen = 1;
                LOG("openAsync() : mPlayListLen : " + this.mPlayListLen);
                this.mPlayPos = 1;
                this.mPlayList = new int[2];
                this.mPlayList[0] = this.mPlayPos;
                this.mFileToPlay = path;
                this.mCursor = null;
                this.mPlayer.setDataSourceAsync(this.mFileToPlay);
                this.mOneShot = true;
            }
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:30:0x00c0 -> B:52:0x0084). Please submit an issue!!! */
    public void open(String path, boolean oneshot) {
        String[] selectionArgs;
        String where;
        Uri uri;
        LOG("open(String, boolean) : Entered : path = " + path + ", oneshot = " + oneshot);
        synchronized (this) {
            if (path != null) {
                if (oneshot) {
                    mRepeatMode = 1;
                    ensurePlayListCapacity(1);
                    this.mPlayListLen = 1;
                    this.mPlayPos = -1;
                }
                LOG("open(String, boolean) : mCursor = " + this.mCursor);
                if (this.mCursor == null) {
                    ContentResolver resolver = getContentResolver();
                    if (path.startsWith("content://media/")) {
                        uri = Uri.parse(path);
                        where = null;
                        selectionArgs = null;
                    } else {
                        uri = MediaStore.Audio.Media.getContentUriForPath(path);
                        where = "_data=?";
                        selectionArgs = new String[]{path};
                    }
                    try {
                        this.mCursor = resolver.query(uri, this.mCursorCols, where, selectionArgs, null);
                        if (this.mCursor != null) {
                            if (this.mCursor.getCount() == 0) {
                                this.mCursor.close();
                                this.mCursor = null;
                            } else {
                                this.mCursor.moveToNext();
                                ensurePlayListCapacity(1);
                                this.mPlayListLen = 1;
                                this.mPlayList[0] = this.mCursor.getInt(0);
                                this.mPlayPos = 0;
                            }
                        }
                    } catch (UnsupportedOperationException e) {
                    }
                }
                if (this.mCursor.getColumnIndex("_data") < 0) {
                    stop(true);
                    return;
                }
                try {
                    String filestr = this.mCursor.getString(this.mCursor.getColumnIndexOrThrow("_data"));
                    File file = new File(filestr);
                    if (!file.exists()) {
                        Log.e(TAG, "open(String, boolean) : Target file '" + filestr + "'  does NOT exist.");
                        stop(true);
                        notifyChange(MEDIAPLAY_ERROR);
                        return;
                    }
                    LOG("open(String, boolean) : To set mFilePath to '" + filestr + "'.");
                    this.mFilePath = filestr;
                    this.mFileToPlay = path;
                    this.mPlayer.setDataSource(this.mFileToPlay);
                    this.mOneShot = oneshot;
                    LOG("open(String, boolean) : mPlayer.isInitialized() = " + this.mPlayer.isInitialized());
                    if (!this.mPlayer.isInitialized()) {
                        Log.w(TAG, "open(String, boolean) : Failed to init mPlayer.");
                        stop(true);
                        if (this.mPlayListLen > 1) {
                            notifyChange(MEDIAPLAY_ERROR);
                        }
                    } else {
                        this.mOpenFailedCounter = 0;
                    }
                } catch (CursorIndexOutOfBoundsException e2) {
                    Log.e(TAG, "open(String, boolean) : Failed to get target file path : " + e2);
                    stop(true);
                    notifyChange(MEDIAPLAY_ERROR);
                }
            }
        }
    }

    public void play() {
        LOG("play() : Entered.", new Exception());
        LOG("play() : mPlayListLen = " + this.mPlayListLen + ", mPlayPos = " + this.mPlayPos + ", mPlayer.isInitialized() = " + this.mPlayer.isInitialized());
        if (this.mPlayer.isInitialized()) {
            long duration = this.mPlayer.duration();
            long currentPos = this.mPlayer.position();
            if (mRepeatMode != 1 && duration > 2000 && currentPos >= duration - 2000) {
                Log.i(TAG, "play() : mRepeatMode = " + mRepeatMode + ",duration = " + duration + ", currentPos = " + currentPos + ". : To play next track!");
                next(true);
                opennext();
                notifyChange(META_CHANGED);
            }
            this.mPlayer.start();
            setForeground(true);
            NotificationManager nm = (NotificationManager) getSystemService("notification");
            RemoteViews views = new RemoteViews(getPackageName(), (int) R.layout.statusbars);
            views.setImageViewResource(R.id.icon, R.drawable.play_play_iconbk);
            if (getAudioId() < 0) {
                views.setTextViewText(R.id.trackname, getPath());
                views.setTextViewText(R.id.artistalbum, null);
            } else {
                String artist = getArtistName();
                views.setTextViewText(R.id.trackname, getTrackName());
                if (artist == null || artist.equals("<unknown>")) {
                    artist = getString(R.string.unknown_artist_name);
                }
                String album = getAlbumName();
                if (album == null || album.equals("<unknown>")) {
                    album = getString(R.string.unknown_album_name);
                }
                views.setTextViewText(R.id.artistalbum, getString(R.string.notification_artist_album, new Object[]{artist, album}));
            }
            Intent statusintent = new Intent(this, RockAudioPlayer.class);
            statusintent.putExtra("checkLayout", 1);
            if (this.mOneShot) {
                statusintent.putExtra("oneshot", true);
            }
            statusintent.setFlags(335544320);
            Notification status = new Notification();
            status.contentView = views;
            status.flags |= 2;
            status.icon = R.drawable.play_play_iconbk;
            status.contentIntent = PendingIntent.getActivity(this, 0, statusintent, 0);
            nm.notify(1, status);
            notifyChange(PLAYSTATE_CHANGED);
            this.mWasPlaying = true;
            return;
        }
        if (this.mPlayListLen <= 0) {
        }
    }

    private void stop(boolean remove_status_icon) {
        LOG("stop(boolean) : Eterned : remove_status_icon=  " + remove_status_icon);
        if (this.mPlayer.isInitialized()) {
            this.mPlayer.stop();
        }
        MusicUtils.setABRequire(0);
        this.mFileToPlay = null;
        LOG("stop(boolean) : Eterned : To set mFilePath to null.");
        this.mFilePath = null;
        if (this.mCursor != null) {
            this.mCursor.close();
            this.mCursor = null;
        }
        if (remove_status_icon) {
            gotoIdleState();
            this.mWasPlaying = false;
            return;
        }
        stopForeground(false);
    }

    public void stop() {
        stop(true);
    }

    public void pause() {
        synchronized (this) {
            if (isPlaying()) {
                this.mPlayer.pause();
                gotoIdleState();
                this.mWasPlaying = false;
                notifyChange(PLAYSTATE_CHANGED);
                saveBookmarkIfNeeded();
            }
        }
    }

    public boolean isPlaying() {
        if (this.mPlayer.isInitialized()) {
            return this.mPlayer.isPlaying();
        }
        return false;
    }

    public boolean isOneShot() {
        return this.mOneShot;
    }

    public boolean hasPlayListInited() {
        return (this.mPlayList == null || this.mPlayListLen == 0 || -1 == this.mPlayPos) ? false : true;
    }

    public void prev() {
        Cursor c;
        LOG("prev() : mPlayList = " + this.mPlayList + ", mPlayListLen = " + this.mPlayListLen + ", mPlayPos = " + this.mPlayPos);
        if (this.mPlayList != null && -1 != this.mPlayPos && this.mPlayListLen != 0) {
            synchronized (this) {
                if (this.mOneShot) {
                    seek(0L);
                    play();
                    return;
                }
                LOG("prev() : mShuffleMode = " + this.mShuffleMode);
                if (this.mShuffleMode == 1) {
                    int histsize = this.mHistory.size();
                    if (histsize != 0) {
                        Integer pos = this.mHistory.remove(histsize - 1);
                        this.mPlayPos = pos.intValue();
                    } else {
                        return;
                    }
                } else {
                    this.mPlayListLen = MusicUtils.gettotalsongnums();
                    LOG("prev() : mPlayListLen = " + this.mPlayListLen);
                    if (this.mPlayPos > 0) {
                        this.mPlayPos--;
                    } else {
                        this.mPlayPos = this.mPlayListLen - 1;
                    }
                }
                do {
                    LOG("prev() : this = " + this + ", mPlayList.length = " + this.mPlayList.length + ", mPlayPos = " + this.mPlayPos);
                    c = MusicUtils.query(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_id=" + this.mPlayList[this.mPlayPos], null, null);
                    if (c == null || c.getCount() == 0) {
                        this.mPlayPos--;
                        if (this.mPlayPos < 0) {
                            this.mPlayPos = this.mPlayListLen - 1;
                        }
                        Log.e(TAG, "No this audio file!!!");
                        continue;
                    }
                } while (c == null);
                c.close();
                saveBookmarkIfNeeded();
                stop(false);
                MusicUtils.setcurrentsongnums(this.mPlayPos + 1);
                LOG("the current num is" + MusicUtils.getcurrentsongnums());
                String id = String.valueOf(this.mPlayList[this.mPlayPos]);
                this.mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, "_id=" + id, null, null);
                if (this.mCursor != null) {
                    this.mCursor.moveToFirst();
                    this.mFileToPlay = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id;
                }
            }
        }
    }

    public void next(boolean force) {
        Cursor c;
        LOG("next() : mPlayList = " + this.mPlayList + ", mPlayListLen = " + this.mPlayListLen + ", mPlayPos = " + this.mPlayPos + ", force = " + force);
        if (this.mPlayList != null && this.mPlayListLen != 0 && -1 != this.mPlayPos) {
            synchronized (this) {
                if (this.mOneShot) {
                    seek(0L);
                    play();
                    return;
                }
                if (this.mPlayPos >= 0) {
                    this.mHistory.add(Integer.valueOf(this.mPlayPos));
                }
                if (this.mHistory.size() > 10) {
                    this.mHistory.removeElementAt(0);
                }
                this.mPlayListLen = MusicUtils.gettotalsongnums();
                this.mPlayPos = MusicUtils.getcurrentsongnums() - 1;
                LOG("next() : mPlayListLen = " + this.mPlayListLen + ", mPlayPos = " + this.mPlayPos);
                switch (mRepeatMode) {
                    case 0:
                        if (force) {
                            this.mPlayPos++;
                            if (this.mPlayPos > this.mPlayListLen - 1) {
                                this.mPlayPos = 0;
                                break;
                            }
                        } else {
                            seek(0L);
                            pause();
                            return;
                        }
                        break;
                    case 1:
                        if (force) {
                            this.mPlayPos++;
                            if (this.mPlayPos > this.mPlayListLen - 1) {
                                this.mPlayPos = 0;
                                break;
                            }
                        }
                        break;
                    case 2:
                    case 4:
                        this.mPlayPos++;
                        if (this.mPlayPos <= this.mPlayListLen - 1 || force) {
                            if (this.mPlayPos >= this.mPlayListLen) {
                                this.mPlayPos = 0;
                                break;
                            }
                        } else {
                            this.mPlayPos = 0;
                            seek(0L);
                            pause();
                            MusicUtils.setcurrentsongnums(this.mPlayPos + 1);
                            return;
                        }
                        break;
                    case 3:
                        this.mPlayPos++;
                        LOG("mPlayPos============" + this.mPlayPos);
                        if (this.mPlayPos > this.mPlayListLen - 1) {
                            this.mPlayPos = 0;
                            break;
                        }
                        break;
                }
                do {
                    c = MusicUtils.query(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_id=" + this.mPlayList[this.mPlayPos], null, null);
                    if (c == null || c.getCount() == 0) {
                        this.mPlayPos++;
                        if (this.mPlayPos > this.mPlayListLen - 1) {
                            this.mPlayPos = 0;
                        }
                        Log.e(TAG, "No this audio file!!!");
                        continue;
                    }
                } while (c == null);
                c.close();
                MusicUtils.setcurrentsongnums(this.mPlayPos + 1);
                saveBookmarkIfNeeded();
                stop(false);
                String id = String.valueOf(this.mPlayList[this.mPlayPos]);
                this.mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, "_id=" + id, null, null);
                if (this.mCursor != null) {
                    this.mCursor.moveToFirst();
                    this.mFileToPlay = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id;
                }
            }
        }
    }

    public void opennext() {
        if (!this.mOneShot) {
            this.mNosongidCount = 0;
            openCurrent();
            play();
        }
    }

    private void gotoIdleState() {
        LOG("gotoIdleState() : Entered.");
        NotificationManager nm = (NotificationManager) getSystemService("notification");
        nm.cancel(1);
        this.mDelayedStopHandler.removeCallbacksAndMessages(null);
        Message msg = this.mDelayedStopHandler.obtainMessage();
        this.mDelayedStopHandler.sendMessageDelayed(msg, 60000L);
    }

    private void saveBookmarkIfNeeded() {
        try {
            if (isPodcast()) {
                long pos = position();
                long bookmark = getBookmark();
                long duration = duration();
                if (pos < bookmark && pos + 10000 > bookmark) {
                    return;
                }
                if (pos <= bookmark || pos - 10000 >= bookmark) {
                    if (pos < 15000 || pos + 10000 > duration) {
                        pos = 0;
                    }
                    ContentValues values = new ContentValues();
                    values.put("bookmark", Long.valueOf(pos));
                    Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursor.getLong(0));
                    getContentResolver().update(uri, values, null, null);
                }
            }
        } catch (SQLiteException e) {
        }
    }

    private void doAutoShuffleUpdate() {
        boolean notify = false;
        if (this.mPlayPos > 10) {
            removeTracks(0, this.mPlayPos - 9);
            notify = true;
        }
        int to_add = 7 - (this.mPlayListLen - (this.mPlayPos < 0 ? -1 : this.mPlayPos));
        for (int i = 0; i < to_add; i++) {
            int idx = this.mRand.nextInt(this.mAutoShuffleList.length);
            Integer which = Integer.valueOf(this.mAutoShuffleList[idx]);
            ensurePlayListCapacity(this.mPlayListLen + 1);
            int[] iArr = this.mPlayList;
            int i2 = this.mPlayListLen;
            this.mPlayListLen = i2 + 1;
            iArr[i2] = which.intValue();
            notify = true;
        }
        if (notify) {
            notifyChange(QUEUE_CHANGED);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class Shuffler {
        private int mPrevious;
        private Random mRandom;

        private Shuffler() {
            this.mRandom = new Random();
        }

        public int nextInt(int interval) {
            int ret;
            do {
                ret = this.mRandom.nextInt(interval);
                if (ret != this.mPrevious) {
                    break;
                }
            } while (interval > 1);
            this.mPrevious = ret;
            return ret;
        }
    }

    private boolean makeAutoShuffleList() {
        ContentResolver res = getContentResolver();
        Cursor c = null;
        try {
            c = res.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "is_music=1", null, null);
            if (c == null || c.getCount() == 0) {
                if (c != null) {
                    c.close();
                }
                return false;
            }
            int len = c.getCount();
            int[] list = new int[len];
            for (int i = 0; i < len; i++) {
                c.moveToNext();
                list[i] = c.getInt(0);
            }
            this.mAutoShuffleList = list;
            if (c != null) {
                c.close();
            }
            return true;
        } catch (RuntimeException e) {
            if (c != null) {
                c.close();
            }
            return false;
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            throw new RuntimeException(th);
        }
    }

    public int removeTracks(int first, int last) {
        int numremoved = removeTracksInternal(first, last);
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    private int removeTracksInternal(int first, int last) {
        int i = 0;
        synchronized (this) {
            if (last >= first) {
                if (first < 0) {
                    first = 0;
                }
                if (last >= this.mPlayListLen) {
                    last = this.mPlayListLen - 1;
                }
                boolean gotonext = false;
                if (first <= this.mPlayPos && this.mPlayPos <= last) {
                    this.mPlayPos = first;
                    gotonext = true;
                } else if (this.mPlayPos > last) {
                    this.mPlayPos -= (last - first) + 1;
                }
                int num = (this.mPlayListLen - last) - 1;
                for (int i2 = 0; i2 < num; i2++) {
                    this.mPlayList[first + i2] = this.mPlayList[last + 1 + i2];
                }
                this.mPlayListLen -= (last - first) + 1;
                if (gotonext) {
                    if (this.mPlayListLen == 0) {
                        stop(true);
                        this.mPlayPos = -1;
                    } else {
                        if (this.mPlayPos >= this.mPlayListLen) {
                            this.mPlayPos = 0;
                        }
                        boolean wasPlaying = isPlaying();
                        stop(false);
                        openCurrent();
                        if (wasPlaying) {
                            play();
                        }
                    }
                }
                i = (last - first) + 1;
            }
        }
        return i;
    }

    public int removeTrack(int id) {
        int numremoved = 0;
        synchronized (this) {
            int i = 0;
            while (i < this.mPlayListLen) {
                if (this.mPlayList[i] == id) {
                    numremoved += removeTracksInternal(i, i);
                    i--;
                }
                i++;
            }
        }
        if (numremoved > 0) {
            notifyChange(QUEUE_CHANGED);
        }
        return numremoved;
    }

    public void setShuffleMode(int shufflemode) {
        LOG("setShuffleMode() : shufflemode = " + shufflemode);
        synchronized (this) {
            if (this.mShuffleMode != shufflemode || this.mPlayListLen <= 0) {
                this.mShuffleMode = shufflemode;
                if (this.mShuffleMode == 2) {
                    if (makeAutoShuffleList()) {
                        this.mPlayListLen = 0;
                        doAutoShuffleUpdate();
                        this.mPlayPos = 0;
                        openCurrent();
                        play();
                        notifyChange(META_CHANGED);
                        return;
                    }
                    this.mShuffleMode = 0;
                }
                saveQueue(false);
            }
        }
    }

    public int getShuffleMode() {
        return this.mShuffleMode;
    }

    public void setRepeatMode(int repeatmode) {
        synchronized (this) {
            mRepeatMode = repeatmode;
            LOG("mRepeatMode = " + mRepeatMode);
            saveQueue(false);
        }
    }

    public int getRepeatMode() {
        if (mRepeatMode > 4 || mRepeatMode < 0) {
            mRepeatMode = 0;
        }
        return mRepeatMode;
    }

    public int getMediaMountedCount() {
        return this.mMediaMountedCount;
    }

    public String getPath() {
        return this.mFileToPlay;
    }

    public String getDirPath() {
        synchronized (this) {
            if (this.mCursor == null) {
                return null;
            }
            try {
                return this.mCursor.getString(this.mCursor.getColumnIndex("_data"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getDirPath() : Failed to get dir path.");
                return null;
            }
        }
    }

    public int getAudioId() {
        synchronized (this) {
            if (this.mPlayPos < 0 || !this.mPlayer.isInitialized()) {
                return -1;
            }
            return this.mPlayList[this.mPlayPos];
        }
    }

    public int getQueuePosition() {
        int i;
        synchronized (this) {
            i = this.mPlayPos;
        }
        return i;
    }

    public void setQueuePosition(int pos) {
        synchronized (this) {
            stop(false);
            this.mPlayPos = pos;
            openCurrent();
            play();
            notifyChange(META_CHANGED);
        }
    }

    public String getArtistName() {
        synchronized (this) {
            if (this.mCursor == null) {
                return null;
            }
            try {
                return this.mCursor.getString(this.mCursor.getColumnIndex("artist"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getArtistName() : Failed to get artist name : ", e);
                return null;
            }
        }
    }

    public String getGenrelName(int songid) {
        String str;
        synchronized (this) {
            ContentResolver resolver = getContentResolver();
            Uri uri1 = MediaStore.Audio.Genres.Members.getContentUri("external", 0L);
            Uri uri = MediaStore.Audio.Genres.getContentUri("external");
            StringBuilder where1 = new StringBuilder();
            where1.append("audio_id='" + songid + "'");
            Cursor cur = resolver.query(uri1, new String[]{"genre_id"}, where1.toString(), null, null);
            int genreid = cur.getInt(cur.getColumnIndex("genre_id"));
            StringBuilder where2 = new StringBuilder();
            where2.append("genre_id='" + genreid + "'");
            resolver.query(uri, new String[]{"name"}, where2.toString(), null, null);
            try {
                str = getString(cur.getColumnIndexOrThrow("name"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getGenrelName() : Failed to get genrel name : ", e);
                str = null;
            }
        }
        return str;
    }

    public int getGenrelId() {
        synchronized (this) {
            if (this.mCursor == null) {
                return -1;
            }
            try {
                return this.mCursor.getInt(this.mCursor.getColumnIndex("_id"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getGenrelId() : Failed to get genrel id : ", e);
                return -1;
            }
        }
    }

    public String getDisplayName() {
        synchronized (this) {
            if (this.mCursor == null) {
                return null;
            }
            try {
                return this.mCursor.getString(this.mCursor.getColumnIndex("_display_name"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getDisplayName() : Failed to get display name : ", e);
                return null;
            }
        }
    }

    public int getArtistId() {
        synchronized (this) {
            if (this.mCursor == null) {
                return -1;
            }
            try {
                return this.mCursor.getInt(this.mCursor.getColumnIndex("artist_id"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getArtistId() : Failed to get artist id : ", e);
                return -1;
            }
        }
    }

    public int getbitrate() {
        synchronized (this) {
            if (this.mCursor == null) {
                return -1;
            }
            try {
                return this.mCursor.getInt(this.mCursor.getColumnIndex("artist_id"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getbitrate() : Failed to get bit rate : ", e);
                return -1;
            }
        }
    }

    public String getAlbumName() {
        synchronized (this) {
            if (this.mCursor == null) {
                return null;
            }
            try {
                return this.mCursor.getString(this.mCursor.getColumnIndex("album"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getAlbumName() : Failed to get album name : ", e);
                return null;
            }
        }
    }

    public int getAlbumId() {
        synchronized (this) {
            if (this.mCursor == null) {
                return -1;
            }
            try {
                return this.mCursor.getInt(this.mCursor.getColumnIndex("album_id"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getAlbumId() : Failed to get album id : ", e);
                return -1;
            }
        }
    }

    public String getTrackName() {
        synchronized (this) {
            if (this.mCursor == null) {
                Log.d(TAG, "the mCursor is null !!!!");
                return null;
            }
            try {
                Log.d(TAG, "the mCursor is not null !!!!");
                return this.mCursor.getString(this.mCursor.getColumnIndex("title"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getTrackName() : Failed to get track name : ", e);
                return null;
            }
        }
    }

    public long getDuration() {
        synchronized (this) {
            if (this.mCursor == null) {
                return -1L;
            }
            try {
                return this.mCursor.getInt(this.mCursor.getColumnIndex("duration"));
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "getDuration() : Failed to get duration : ", e);
                return -1L;
            }
        }
    }

    private boolean isPodcast() {
        synchronized (this) {
            if (this.mCursor == null) {
                return false;
            }
            try {
                return this.mCursor.getInt(9) > 0;
            } catch (CursorIndexOutOfBoundsException e) {
                Log.e(TAG, "isPodcast() : Failed to get whether podcast : ", e);
                return false;
            }
        }
    }

    private long getBookmark() {
        long j;
        synchronized (this) {
            j = this.mCursor == null ? 0L : this.mCursor.getLong(10);
        }
        return j;
    }

    public long duration() {
        if (this.mPlayer.isInitialized()) {
            return this.mPlayer.duration();
        }
        return -1L;
    }

    public long position() {
        if (this.mPlayer.isInitialized()) {
            return this.mPlayer.position();
        }
        return -1L;
    }

    public long seek(long pos) {
        if (!this.mPlayer.isInitialized()) {
            return -1L;
        }
        if (pos < 0) {
            pos = 0;
        }
        if (pos > this.mPlayer.duration()) {
            pos = this.mPlayer.duration();
            if (pos == -1) {
                stop();
                return -1L;
            }
        }
        return this.mPlayer.seek(pos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MultiPlayer {
        private Handler mHandler;
        private MediaPlayer mMediaPlayer = new MediaPlayer();
        private boolean mIsInitialized = false;
        private boolean mIsPlaying = false;
        MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.MultiPlayer.1
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mp) {
                AudioPlaybackService.this.LOG("listener.onCompletion() : Entered.");
                AudioPlaybackService.this.mWakeLock.acquire(30000L);
                MultiPlayer.this.mHandler.sendEmptyMessage(1);
                MultiPlayer.this.mHandler.sendEmptyMessage(2);
                MultiPlayer.this.mIsPlaying = false;
                AudioPlaybackService.this.LOG("listener.onCompletion() : To set mFilePath to null.");
                AudioPlaybackService.this.mFilePath = null;
                AudioPlaybackService.this.notifyChange(AudioPlaybackService.PLAYSTATE_CHANGED);
            }
        };
        MediaPlayer.OnPreparedListener preparedlistener = new MediaPlayer.OnPreparedListener() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.MultiPlayer.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mp) {
                AudioPlaybackService.this.notifyChange(AudioPlaybackService.ASYNC_OPEN_COMPLETE);
            }
        };
        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() { // from class: android.rk.RockAudioPlayer.AudioPlaybackService.MultiPlayer.3
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mp, int what, int extra) {
                AudioPlaybackService.this.LOG("errorListener.onError() : Entered, mp = " + mp + ", what = " + what + ", extra = " + extra + ", To set mFilePath to null.");
                AudioPlaybackService.this.mFilePath = null;
                switch (what) {
                    case 100:
                        MultiPlayer.this.mIsPlaying = false;
                        MultiPlayer.this.mIsInitialized = false;
                        MultiPlayer.this.mMediaPlayer.release();
                        MultiPlayer.this.mMediaPlayer = new MediaPlayer();
                        return true;
                    default:
                        MultiPlayer.this.mIsPlaying = false;
                        MultiPlayer.this.mIsInitialized = false;
                        MultiPlayer.this.mMediaPlayer.reset();
                        MultiPlayer.this.mMediaPlayer.release();
                        MultiPlayer.this.mMediaPlayer = new MediaPlayer();
                        AudioPlaybackService.this.notifyChange(AudioPlaybackService.MEDIAPLAY_ERROR);
                        return false;
                }
            }
        };

        public MultiPlayer() {
            this.mMediaPlayer.setWakeMode(AudioPlaybackService.this, 1);
        }

        public void setDataSourceAsync(String path) {
            AudioPlaybackService.this.LOG("MultiPlayer.setDataSourceAsync() : Entered : path = " + path);
            try {
                this.mMediaPlayer.reset();
                this.mMediaPlayer.setDataSource(path);
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.setOnPreparedListener(this.preparedlistener);
                this.mMediaPlayer.prepareAsync();
                this.mMediaPlayer.setOnCompletionListener(this.listener);
                this.mMediaPlayer.setOnErrorListener(this.errorListener);
                this.mIsInitialized = true;
                AudioPlaybackService.this.LOG("MultiPlayer.setDataSourceAsync() : Initialized successfully.");
            } catch (IOException ex) {
                this.mIsInitialized = false;
                Log.e(AudioPlaybackService.TAG, "setDataSourceAsync() :", ex);
            } catch (IllegalArgumentException ex2) {
                this.mIsInitialized = false;
                Log.e(AudioPlaybackService.TAG, "setDataSourceAsync() :", ex2);
            }
        }

        public void setDataSource(String path) {
            AudioPlaybackService.this.LOG("MultiPlayer.setDataSource() : Entered : path = " + path);
            try {
                this.mMediaPlayer.reset();
                this.mMediaPlayer.setOnPreparedListener(null);
                if (path.startsWith("content://")) {
                    this.mMediaPlayer.setDataSource(AudioPlaybackService.this, Uri.parse(path));
                } else {
                    this.mMediaPlayer.setDataSource(path);
                }
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.setOnCompletionListener(this.listener);
                this.mMediaPlayer.setOnErrorListener(this.errorListener);
                this.mIsInitialized = true;
                AudioPlaybackService.this.LOG("MultiPlayer.setDataSource() : Initialized successfully.");
            } catch (IOException ex) {
                this.mIsInitialized = false;
                Log.e(AudioPlaybackService.TAG, "setDataSource() : ", ex);
            } catch (IllegalArgumentException ex2) {
                this.mIsInitialized = false;
                Log.e(AudioPlaybackService.TAG, "setDataSource() : ", ex2);
            }
        }

        public boolean isInitialized() {
            return this.mIsInitialized;
        }

        public void start() {
            AudioPlaybackService.this.LOG("MultiPlayer.start() : Entered.");
            AudioPlaybackService.this.requestToStopFmPlay();
            this.mMediaPlayer.start();
            AudioPlaybackService.this.LOG("MultiPlayer.start() : isPlaying : " + this.mMediaPlayer.isPlaying());
            this.mIsPlaying = true;
        }

        public void stop() {
            AudioPlaybackService.this.LOG("MultiPlayer.stop() : Entered.", new Exception());
            this.mMediaPlayer.reset();
            this.mIsPlaying = false;
            this.mIsInitialized = false;
        }

        public void pause() {
            AudioPlaybackService.this.LOG("MultiPlayer.pause() : Entered.");
            this.mMediaPlayer.pause();
            AudioPlaybackService.this.LOG("MultiPlayer.pause() : isPlaying : " + this.mMediaPlayer.isPlaying());
            this.mIsPlaying = false;
        }

        public boolean isPlaying() {
            return this.mIsPlaying;
        }

        public void setHandler(Handler handler) {
            this.mHandler = handler;
        }

        public long duration() {
            return this.mMediaPlayer.getDuration();
        }

        public long position() {
            return this.mMediaPlayer.getCurrentPosition();
        }

        public long seek(long whereto) {
            AudioPlaybackService.this.LOG("MultiPlayer.seek() : whereto = " + whereto);
            this.mMediaPlayer.seekTo((int) whereto);
            return whereto;
        }

        public void setVolume(float vol) {
            AudioPlaybackService.this.LOG("MultiPlayer.setVolume() : vol = " + vol);
            this.mMediaPlayer.setVolume(vol, vol);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestToStopFmPlay() {
        Intent j = new Intent("com.rk.FmRadio.fmservicecommand");
        j.putExtra(CMDNAME, CMDSTOP);
        sendBroadcast(j);
    }
}
