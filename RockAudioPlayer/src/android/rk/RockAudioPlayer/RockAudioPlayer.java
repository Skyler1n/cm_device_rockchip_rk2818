package android.rk.RockAudioPlayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.rk.RockAudioPlayer.MusicUtils;
import android.rk.RockAudioPlayer.RepeatingImageButton;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public class RockAudioPlayer extends ListActivity implements View.OnClickListener, ServiceConnection, View.OnTouchListener, View.OnLongClickListener, MusicUtils.Defs {
    private static final int ABOUT = 6;
    private static final int ALBUM_ART_DECODED = 4;
    private static final int AUDIOTRYTIME = 10;
    private static long AudioABEnd = 0;
    private static long AudioABStart = 0;
    private static final int DIALOG_WAIT = 0;
    private static final int DisplayList = 5;
    private static final int GET_ALBUM_ART = 3;
    private static final int LISTREFRESHTIME = 100;
    private static final int Lrc = 4;
    private static final int OPEN_PLAYFILE = 5;
    private static final int PlayFx = 3;
    private static final int QUIT = 2;
    private static final int REFRESH = 1;
    private static final int RESUME__SHOW_INTRINSIC_UI = 10;
    private static final int SearchNet = 2;
    private static final int VOLUME_DISPLAY_TIME = 1500;
    private ImageView AB_mode;
    private ImageView AlbumImage;
    private ImageView ArtistImage;
    private BitmapDrawable DefaultAlbumIcon;
    private ImageButton DiskPlayImageButton;
    private int NowTrackNum;
    private int OnclickCounter;
    private int PlaylistNum;
    int SelectPlaylistId;
    private ImageView SuffleModeImage;
    private ArrayList<TextView> TrackList;
    int TrackNum;
    private ArrayList<TrackInfo> TrackSet;
    View Volume_view;
    View addplaylist;
    private TextView albumstring;
    private TextView artiststring;
    private AudioManager audioMa;
    View b;
    private TextView bitrate;
    Cursor cur;
    private boolean fir_start_display_ser;
    private ArrayList<TextView> list_textview;
    View lyric_layout;
    private LyricManager lyricmanager;
    public Cursor mAlbumCursor;
    private ListView mAlbumList;
    public Cursor mArtistCursor;
    private ListView mArtistList;
    private boolean mCreateShortcut;
    private String mCurrentAlbumName;
    private String mCurrentArtistNameForAlbum;
    private String mCurrentPlaylistName;
    private String mCurrentSend;
    private TextView mCurrentTime;
    private String mCurrentTrackName;
    private ProgressDialog mDialogPleaseWait;
    private long mDuration;
    public Cursor mGenreCursor;
    private ListView mGenreList;
    private ArrayList<ImageView> mImageViewList;
    private long mLastSeekEventTime;
    public int[] mList;
    private ImageView mLrcImageButton;
    private RepeatingImageButton mNextButton;
    private ImageView mPauseButton;
    public Cursor mPlaylistCursor;
    private ListView mPlaylistList;
    public int mPosition;
    private RepeatingImageButton mPrevButton;
    private ImageView mQueueButton;
    private boolean mRelaunchAfterConfigChange;
    private ImageView mRepeatButton;
    private ImageView mShuffleButton;
    private String mSortOrder;
    private int mState;
    private TextView mTotalTime;
    private int mTouchSlop;
    private TrackAdapterForPlaylist mTrackAdapterForPlaylist;
    public Cursor mTrackCursor;
    private ListView mTrackList;
    private ListView mTrackListView;
    private ArrayList<View> mViewList;
    private RepeatingImageButton mVol1Button;
    private RepeatingImageButton mVol2Button;
    public int mbacktoFolder;
    public boolean misFolderManage;
    private ProgressBar myProgress;
    public String now_path;
    private int oldImageView;
    private int oldView;
    private boolean paused;
    View play_display_fir;
    View play_display_sec;
    AnimationDrawable rocketAnimation;
    private TextView samplingrate;
    ArrayList<FileInfo> savearray;
    private SeekBar seekBar;
    private View song_info;
    private TextView songnums;
    private TextView songtitlestring;
    TextView textview_lyric;
    static boolean DEBUG = true;
    private static int i = 0;
    private final int DELAY_TIME = 10;
    private final String TAG = "RockAudioPlayer.java";
    private String mAlbumId = null;
    private String mArtistId = null;
    private String mPlaylistId = null;
    private String mGenreId = null;
    private int mSelectedId = -1;
    private TrackListAdapter mCurrentAdapter = null;
    private TrackListAdapter mTrackListAdapter = null;
    private TrackListAdapter mPlaylistTrackListAdapter = null;
    private AlbumListAdapter mAlbumListAdapter = null;
    private ArtistListAdapter mArtistListAdapter = null;
    private GenreListAdapter mGenreListAdapter = null;
    private PlaylistListAdapter mPlaylistListAdapter = null;
    private final int TrackMore = 1;
    private final int TrackLetter = 2;
    private boolean ShowAboutFlag = true;
    private boolean mEject = false;
    private boolean manimation = false;
    boolean misbuttonFoler = false;
    boolean mNetSearchFlag = false;
    private int volume = 0;
    View.OnClickListener PlaylistListener = new View.OnClickListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.playlist_operate_delete /* 2131427443 */:
                    View vp = (View) v.getParent();
                    ListView vl = (ListView) vp.getParent();
                    int position = vl.getPositionForView(vp) + 1;
                    RockAudioPlayer.this.LOG(" postion = " + position);
                    ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, position);
                    Resources res1 = RockAudioPlayer.this.getResources();
                    String LastPlayedName1 = res1.getString(R.string.recentlyadded);
                    int i2 = MusicUtils.getPlaylistId(RockAudioPlayer.this, LastPlayedName1);
                    RockAudioPlayer.this.LOG("i = " + i2);
                    int j = MusicUtils.getPlaylistId(RockAudioPlayer.this, position);
                    RockAudioPlayer.this.LOG("j = " + j);
                    if (i2 >= 0 && j >= 0) {
                        if (j == i2) {
                            Toast.makeText(RockAudioPlayer.this, (int) R.string.cannot_delete_playlist, 0).show();
                            return;
                        }
                        Cursor c = RockAudioPlayer.this.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, new String[]{"_id", "name"}, null, null, "name");
                        c.move(position);
                        long deletePosition = c.getLong(c.getColumnIndex("_id"));
                        StringBuilder where = new StringBuilder();
                        where.append("_id = " + deletePosition);
                        String filter = where.toString();
                        RockAudioPlayer.this.getContentResolver().delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, filter, null);
                        MusicUtils.DeleteSongsInPlaylist(RockAudioPlayer.this, deletePosition);
                        RockAudioPlayer.access$910(RockAudioPlayer.this);
                        if (RockAudioPlayer.this.PlaylistNum < 0) {
                            RockAudioPlayer.this.PlaylistNum = 1;
                        }
                        RockAudioPlayer.this.LOG("PlaylistNum = " + RockAudioPlayer.this.PlaylistNum);
                        Toast.makeText(RockAudioPlayer.this, (int) R.string.playlist_deleted_message, 0).show();
                        RockAudioPlayer.this.PlaylistDisplay();
                        c.close();
                        return;
                    }
                    return;
                case R.id.playlist_operate_add /* 2131427444 */:
                    Toast.makeText(RockAudioPlayer.this, (int) R.string.playlist_operate_add, 0).show();
                    RockAudioPlayer.this.mAdapter = 11;
                    RockAudioPlayer.this.mState = 4;
                    View vp1 = (View) v.getParent();
                    ListView vl1 = (ListView) vp1.getParent();
                    int position1 = vl1.getPositionForView(vp1) + 1;
                    RockAudioPlayer.this.LOG(" postion = " + position1);
                    RockAudioPlayer.this.SelectPlaylistId = MusicUtils.getPlaylistId(RockAudioPlayer.this, position1);
                    RockAudioPlayer.this.LOG("SelectPlaylistId = " + RockAudioPlayer.this.SelectPlaylistId);
                    Resources res = RockAudioPlayer.this.getResources();
                    res.getString(R.string.recentlyadded);
                    RockAudioPlayer.this.mArtistId = null;
                    RockAudioPlayer.this.mAlbumId = null;
                    RockAudioPlayer.this.mPlaylistId = null;
                    ArrayList<TrackInfo> TrackNullSet = new ArrayList<TrackInfo>();
                    RockAudioPlayer.this.cur = RockAudioPlayer.this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data", "_display_name"}, "is_music=1", null, "title_key");
                    RockAudioPlayer.this.cur.moveToFirst();
                    RockAudioPlayer.this.LOG("cur = " + RockAudioPlayer.this.cur);
                    RockAudioPlayer.this.TrackNum = RockAudioPlayer.this.cur.getCount();
                    RockAudioPlayer.this.LOG("TrackNum = " + RockAudioPlayer.this.TrackNum);
                    RockAudioPlayer.this.mTrackAdapterForPlaylist = new TrackAdapterForPlaylist(RockAudioPlayer.this, TrackNullSet);
                    RockAudioPlayer.this.setListAdapter(RockAudioPlayer.this.mTrackAdapterForPlaylist);
                    RockAudioPlayer.this.CheckAddPlaylistVisible();
                    RockAudioPlayer.this.NowTrackNum = 0;
                    RockAudioPlayer.this.mTracklistHandler.post(RockAudioPlayer.this.mTrackListForPlaylist);
                    return;
                default:
                    return;
            }
        }
    };
    public int mAdapter = 0;
    public int mAdapterold = this.mAdapter;
    private String[] mCursorCols = {"_id", "title", "title_key", "_display_name", "_data", "album", "artist", "artist_id", "duration"};
    private String[] mPlaylistMemberCols = {"_id", "title", "_display_name", "title_key", "_data", "album", "artist", "artist_id", "duration", "play_order", "audio_id"};
    String[] mOneCursorCols = {"audio._id AS _id", "artist", "album", "title", "_data", "_display_name", "mime_type", "album_id", "artist_id", "duration"};
    private String mArtistName = null;
    private String mTrackname = null;
    private String mTracknameBack = null;
    private String mDisplayname = null;
    private String mDisplaynameBack = null;
    private final Handler mHandler = new Handler() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.3
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    long next = RockAudioPlayer.this.refreshNow();
                    RockAudioPlayer.this.queueNextRefresh(next);
                    return;
                case 2:
                    RockAudioPlayer.this.HintToastShow(R.string.playback_failed);
                    try {
                        RockAudioPlayer.this.mDisplayService.next();
                        RockAudioPlayer.this.updateTrackInfo();
                        RockAudioPlayer.this.queueNextRefresh(1L);
                        Message msg1 = RockAudioPlayer.this.mHandler.obtainMessage(5);
                        msg1.arg1 = RockAudioPlayer.this.OnclickCounter;
                        RockAudioPlayer.this.mHandler.sendMessageDelayed(msg1, 800L);
                        return;
                    } catch (RemoteException e) {
                        return;
                    }
                case 3:
                case 6:
                case 7:
                case 8:
                case 9:
                default:
                    return;
                case 4:
                    RockAudioPlayer.this.DiskPlayImageButton.setImageDrawable((Drawable) msg.obj);
                    return;
                case 5:
                    try {
                        RockAudioPlayer.this.LOG("msg.arg1 = " + msg.arg1);
                        RockAudioPlayer.this.LOG("OnclickCounter = " + RockAudioPlayer.this.OnclickCounter);
                        if (msg.arg1 >= RockAudioPlayer.this.OnclickCounter) {
                            RockAudioPlayer.this.LOG("OPEN_PLAYFILE,opennext()");
                            RockAudioPlayer.this.OnclickCounter = 0;
                            RockAudioPlayer.this.mDisplayService.opennext();
                            RockAudioPlayer.this.setPauseButtonImage();
                            return;
                        }
                        return;
                    } catch (RemoteException e2) {
                        return;
                    }
                case 10:
                    RockAudioPlayer.this.showIntrinsicUi((Bundle) msg.obj);
                    return;
            }
        }
    };
    private BroadcastReceiver mScanListener = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            RockAudioPlayer.this.LOG("mScanListener BroadcastReceiver action" + action);
            if (action.equals("android.intent.action.MEDIA_EJECT") || action.equals("android.intent.action.MEDIA_UNMOUNTED")) {
                RockAudioPlayer.this.LOG("ACTION_MEDIA_UNMOUNTED OR ACTION_MEDIA_EJECT");
                if (RockAudioPlayer.this.mDisplayService != null) {
                    try {
                        RockAudioPlayer.this.mDisplayService.stop();
                    } catch (RemoteException e) {
                    }
                }
                RockAudioPlayer.this.finish();
            } else if ("android.intent.action.MEDIA_SCANNER_STARTED".equals(action) || "android.intent.action.MEDIA_SCANNER_FINISHED".equals(action)) {
                RockAudioPlayer.this.LOG(" RockAudioPlayer mScanListener the media scan finish");
                MusicUtils.setSpinnerState(RockAudioPlayer.this);
                RockAudioPlayer.this.mReScanHandler.sendEmptyMessage(0);
            }
        }
    };
    private Handler mReScanHandler = new Handler() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.5
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            RockAudioPlayer.this.LOG("mReScanHandler  mTrackCursor = " + RockAudioPlayer.this.mTrackCursor);
            RockAudioPlayer.this.getCursor(RockAudioPlayer.this.mTrackListAdapter.getQueryHandler(), null, 0);
            MusicUtils.setSpinnerState(RockAudioPlayer.this);
        }
    };
    private Handler mTracklistHandler = new Handler();
    private final int LISTDISPLAYNUM = 6;
    private Runnable mTrackListForPlaylist = new Runnable() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.6
        @Override // java.lang.Runnable
        public void run() {
            RockAudioPlayer.this.LOG("mTrackListForPlaylist.run() : Entered.");
            Resources resources = RockAudioPlayer.this.getResources();
            if (!RockAudioPlayer.this.cur.isAfterLast() && RockAudioPlayer.this.TrackNum > 0) {
                RockAudioPlayer.this.LOG("TrackNum1 =  " + RockAudioPlayer.this.TrackNum);
                if (RockAudioPlayer.this.TrackNum <= 0 || RockAudioPlayer.this.TrackNum >= 6) {
                    for (int i2 = 0; i2 < 6; i2++) {
                        new String();
                        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", RockAudioPlayer.this.SelectPlaylistId);
                        int id = RockAudioPlayer.this.cur.getInt(RockAudioPlayer.this.cur.getColumnIndex("_id"));
                        String name = RockAudioPlayer.this.cur.getString(RockAudioPlayer.this.cur.getColumnIndex("_display_name"));
                        TrackInfo song = new TrackInfo();
                        new TextView(RockAudioPlayer.this);
                        StringBuilder where = new StringBuilder();
                        where.append("audio_id=" + id + " AND ");
                        where.append("playlist_id=" + RockAudioPlayer.this.SelectPlaylistId);
                        Cursor songcur = RockAudioPlayer.this.getContentResolver().query(uri, new String[]{"audio_id"}, where.toString(), null, null);
                        where.setLength(0);
                        if (songcur == null || !songcur.moveToFirst()) {
                            song.id = id;
                            song.name = new String(name);
                            song.icon = resources.getDrawable(R.drawable.playlist_trackchoice_notclick);
                            RockAudioPlayer.this.mTrackAdapterForPlaylist.add(song);
                        } else {
                            song.id = id;
                            song.name = new String(name);
                            song.icon = resources.getDrawable(R.drawable.playlist_trackchoice_click);
                            RockAudioPlayer.this.mTrackAdapterForPlaylist.add(song);
                            songcur.close();
                        }
                        if (songcur != null) {
                            songcur.close();
                        }
                        RockAudioPlayer.this.cur.moveToNext();
                    }
                } else {
                    for (int i3 = 0; i3 < RockAudioPlayer.this.TrackNum; i3++) {
                        new String();
                        Uri uri2 = MediaStore.Audio.Playlists.Members.getContentUri("external", RockAudioPlayer.this.SelectPlaylistId);
                        int id2 = RockAudioPlayer.this.cur.getInt(RockAudioPlayer.this.cur.getColumnIndex("_id"));
                        String name2 = RockAudioPlayer.this.cur.getString(RockAudioPlayer.this.cur.getColumnIndex("_display_name"));
                        TrackInfo song2 = new TrackInfo();
                        new TextView(RockAudioPlayer.this);
                        StringBuilder where2 = new StringBuilder();
                        where2.append("audio_id=" + id2 + " AND ");
                        where2.append("playlist_id=" + RockAudioPlayer.this.SelectPlaylistId);
                        Cursor songcur2 = RockAudioPlayer.this.getContentResolver().query(uri2, new String[]{"audio_id"}, where2.toString(), null, null);
                        where2.setLength(0);
                        if (songcur2 == null || !songcur2.moveToFirst()) {
                            song2.id = id2;
                            song2.name = new String(name2);
                            song2.icon = resources.getDrawable(R.drawable.playlist_trackchoice_notclick);
                            RockAudioPlayer.this.mTrackAdapterForPlaylist.add(song2);
                        } else {
                            song2.id = id2;
                            song2.name = new String(name2);
                            song2.icon = resources.getDrawable(R.drawable.playlist_trackchoice_click);
                            RockAudioPlayer.this.mTrackAdapterForPlaylist.add(song2);
                            songcur2.close();
                        }
                        if (songcur2 != null) {
                            songcur2.close();
                        }
                        RockAudioPlayer.this.cur.moveToNext();
                    }
                    RockAudioPlayer.this.TrackNum = 0;
                    RockAudioPlayer.this.mTracklistHandler.removeCallbacks(RockAudioPlayer.this.mTrackListForPlaylist);
                    RockAudioPlayer.this.cur.close();
                }
                RockAudioPlayer.this.mTracklistHandler.postDelayed(RockAudioPlayer.this.mTrackListForPlaylist, 100L);
                RockAudioPlayer.this.TrackNum -= 6;
                if (RockAudioPlayer.this.TrackNum <= 0) {
                    RockAudioPlayer.this.cur.close();
                }
                RockAudioPlayer.this.LOG("TrackNum2 =  " + RockAudioPlayer.this.TrackNum);
            }
        }
    };
    String[] mCols = {"_id", "name"};
    private final String DEFAULT_SORT_ORDER = "_id";
    private BroadcastReceiver mTrackListListener = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.7
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            RockAudioPlayer.this.getListView().invalidateViews();
        }
    };
    private BroadcastReceiver mNowPlayingListener = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.8
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AudioPlaybackService.META_CHANGED)) {
                RockAudioPlayer.this.getListView().invalidateViews();
            } else if (intent.getAction().equals(AudioPlaybackService.QUEUE_CHANGED)) {
                RockAudioPlayer.this.setPauseButtonImage();
                Cursor c = new NowPlayingCursor(MusicUtils.sService, RockAudioPlayer.this.mCursorCols);
                if (c == null && c.getCount() == 0) {
                    RockAudioPlayer.this.finish();
                    return;
                }
                RockAudioPlayer.this.mTrackListAdapter.changeCursor(c);
                c.close();
            }
        }
    };
    private BroadcastReceiver mStatusListener = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.9
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AudioPlaybackService.META_CHANGED)) {
                RockAudioPlayer.this.mEject = intent.getBooleanExtra("eject", false);
                RockAudioPlayer.this.LOG("mEject = " + RockAudioPlayer.this.mEject);
                RockAudioPlayer.this.LOG("BroadcastReceiver&&&&&&&*********");
                RockAudioPlayer.this.updateTrackInfo();
                RockAudioPlayer.this.setPauseButtonImage();
                RockAudioPlayer.this.queueNextRefresh(1L);
            } else if (action.equals(AudioPlaybackService.PLAYBACK_COMPLETE)) {
                if (RockAudioPlayer.this.mOneShot) {
                    try {
                        RockAudioPlayer.this.mDisplayService.seek(0L);
                        RockAudioPlayer.this.mDisplayService.play();
                    } catch (RemoteException e) {
                    }
                } else {
                    RockAudioPlayer.this.setPauseButtonImage();
                }
            } else if (action.equals(AudioPlaybackService.PLAYSTATE_CHANGED)) {
                RockAudioPlayer.this.setPauseButtonImage();
            } else if (action.equals(AudioPlaybackService.MEDIAPLAY_ERROR)) {
                RockAudioPlayer.this.LOG("MediaPlayer is wrong!!");
                Message msg = RockAudioPlayer.this.mHandler.obtainMessage(2);
                RockAudioPlayer.this.mHandler.removeMessages(2);
                RockAudioPlayer.this.mHandler.sendMessageDelayed(msg, 1000L);
            }
        }
    };
    private String backupid = null;
    private IAudioPlaybackService mDisplayService = null;
    private long mStartSeekPos = 0;
    private boolean mSeeking = false;
    private boolean mFromTouch = false;
    private boolean diskflag = true;
    private long mPosOverride = -1;
    private boolean mOneShot = false;
    private Handler mBindHandler = new Handler();
    private Handler mLyricHandler = new Handler();
    private int position_now = 0;
    private Runnable mBindRun = new Runnable() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.12
        @Override // java.lang.Runnable
        public void run() {
            if (!RockAudioPlayer.this.misFolderManage) {
                RockAudioPlayer.this.LOG("position_now = " + RockAudioPlayer.this.position_now);
                MusicUtils.playAll(RockAudioPlayer.this, RockAudioPlayer.this.mTrackCursor, RockAudioPlayer.this.position_now);
            } else {
                RockAudioPlayer.this.LOG("mPosition = " + RockAudioPlayer.this.mPosition);
                RockAudioPlayer.this.misFolderManage = false;
                RockAudioPlayer.this.LOG("mList.length = " + RockAudioPlayer.this.mList.length);
                MusicUtils.playAll(RockAudioPlayer.this, RockAudioPlayer.this.mList, RockAudioPlayer.this.mPosition);
            }
            RockAudioPlayer.this.songnums.setText(Integer.toString(MusicUtils.getcurrentsongnums()) + "/" + Integer.toString(MusicUtils.gettotalsongnums()));
        }
    };
    private Handler ShowAboutHandler = new Handler();
    private Runnable ShowAboutRunnable = new Runnable() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.13
        @Override // java.lang.Runnable
        public void run() {
            RockAudioPlayer.this.ShowAboutFlag = true;
        }
    };
    private Handler ControlVolumeDisplayHandler = new Handler();
    private Runnable ControlVolumeDisplayRunnable = new Runnable() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.14
        @Override // java.lang.Runnable
        public void run() {
            RockAudioPlayer.this.Volume_view.setVisibility(4);
        }
    };
    private long mPosnum = 0;
    int mInitialX = -1;
    int mLastX = -1;
    int mTextWidth = 0;
    int mViewWidth = 0;
    boolean mDraggingLabel = false;
    Handler mLabelScroller = new Handler() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.15
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            TextView tv = (TextView) msg.obj;
            int x = (tv.getScrollX() * 3) / 4;
            tv.scrollTo(x, 0);
            if (x == 0) {
                tv.setEllipsize(TextUtils.TruncateAt.END);
                return;
            }
            Message newmsg = obtainMessage(0, tv);
            RockAudioPlayer.this.mLabelScroller.sendMessageDelayed(newmsg, 15L);
        }
    };
    private RepeatingImageButton.RepeatListener mRewListener = new RepeatingImageButton.RepeatListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.16
        @Override // android.rk.RockAudioPlayer.RepeatingImageButton.RepeatListener
        public void onRepeat(View v, long howlong, int repcnt) {
            if (MusicUtils.getABRequire() != 0) {
                RockAudioPlayer.this.AudioABStop();
                RockAudioPlayer.this.AB_mode.setImageResource(R.drawable.audioab00 + MusicUtils.getABRequire());
            }
            RockAudioPlayer.this.scanBackward(repcnt, howlong);
        }
    };
    private RepeatingImageButton.RepeatListener mVol1Listener = new RepeatingImageButton.RepeatListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.17
        @Override // android.rk.RockAudioPlayer.RepeatingImageButton.RepeatListener
        public void onRepeat(View v, long howlong, int repcnt) {
            RockAudioPlayer.this.ControlVolumeDisplayHandler.removeCallbacks(RockAudioPlayer.this.ControlVolumeDisplayRunnable);
            RockAudioPlayer.this.AudioSetVolume(true);
            RockAudioPlayer.this.ControlVolumeDisplayHandler.postDelayed(RockAudioPlayer.this.ControlVolumeDisplayRunnable, 1500L);
        }
    };
    private RepeatingImageButton.RepeatListener mVol2Listener = new RepeatingImageButton.RepeatListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.18
        @Override // android.rk.RockAudioPlayer.RepeatingImageButton.RepeatListener
        public void onRepeat(View v, long howlong, int repcnt) {
            RockAudioPlayer.this.ControlVolumeDisplayHandler.removeCallbacks(RockAudioPlayer.this.ControlVolumeDisplayRunnable);
            RockAudioPlayer.this.AudioSetVolume(false);
            RockAudioPlayer.this.ControlVolumeDisplayHandler.postDelayed(RockAudioPlayer.this.ControlVolumeDisplayRunnable, 1500L);
        }
    };
    private RepeatingImageButton.RepeatListener mFfwdListener = new RepeatingImageButton.RepeatListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.19
        @Override // android.rk.RockAudioPlayer.RepeatingImageButton.RepeatListener
        public void onRepeat(View v, long howlong, int repcnt) {
            if (MusicUtils.getABRequire() != 0) {
                RockAudioPlayer.this.AudioABStop();
                RockAudioPlayer.this.AB_mode.setImageResource(R.drawable.audioab00 + MusicUtils.getABRequire());
            }
            RockAudioPlayer.this.scanForward(repcnt, howlong);
        }
    };
    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.20
        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStartTrackingTouch(SeekBar bar) {
            RockAudioPlayer.this.mLastSeekEventTime = 0L;
            RockAudioPlayer.this.mFromTouch = true;
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (fromuser && RockAudioPlayer.this.mDisplayService != null) {
                long now = SystemClock.elapsedRealtime();
                if (MusicUtils.getABRequire() != 0) {
                    RockAudioPlayer.this.AudioABStop();
                    RockAudioPlayer.this.AB_mode.setImageResource(R.drawable.audioab00 + MusicUtils.getABRequire());
                }
                if (now - RockAudioPlayer.this.mLastSeekEventTime > 250) {
                    RockAudioPlayer.this.mLastSeekEventTime = now;
                    RockAudioPlayer.this.mPosOverride = (RockAudioPlayer.this.mDuration * progress) / 1000;
                    try {
                        if (RockAudioPlayer.this.mPosOverride < 0) {
                            RockAudioPlayer.this.mDisplayService.position();
                        } else {
                            long j = RockAudioPlayer.this.mPosOverride;
                        }
                        RockAudioPlayer.this.mDisplayService.seek(RockAudioPlayer.this.mPosOverride);
                    } catch (RemoteException e) {
                    }
                    if (!RockAudioPlayer.this.mFromTouch) {
                        RockAudioPlayer.this.refreshNow();
                        RockAudioPlayer.this.mPosOverride = -1L;
                    }
                }
            }
        }

        @Override // android.widget.SeekBar.OnSeekBarChangeListener
        public void onStopTrackingTouch(SeekBar bar) {
            RockAudioPlayer.this.LOG("onStopTrackingTouch");
            RockAudioPlayer.this.mPosOverride = -1L;
            RockAudioPlayer.this.mFromTouch = false;
        }
    };
    private Handler mRecentListHander = new Handler();
    private Runnable mRecentListRunnable = new Runnable() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.21
        @Override // java.lang.Runnable
        public void run() {
            RockAudioPlayer.this.AudioRecentAdd();
        }
    };

    /* loaded from: classes.dex */
    public interface Def {
        public static final int ADDTOPLAYLIST = 11;
        public static final int ALBUM = 2;
        public static final int ALLMUSIC = 0;
        public static final int ARTIST = 1;
        public static final int DEFAULT = -1;
        public static final int DIRECTORY = 7;
        public static final int GENRE = 3;
        public static final int ISADDTOPLAYLIST = 12;
        public static final int JUSTPLAYED = 8;
        public static final int NOTADDTOPLAYLIST = 13;
        public static final int PLAY = 10;
        public static final int PLAYLIST = 4;
        public static final int SEARCH = 6;
        public static final int SEARCHRESULT = 9;
        public static final int TRACK = 5;
    }

    static /* synthetic */ int access$910(RockAudioPlayer x0) {
        int i2 = x0.PlaylistNum;
        x0.PlaylistNum = i2 - 1;
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void LOG(String msg) {
        if (DEBUG) {
            Log.d("RockAudioPlayer.java", msg);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        LOG("onCreate() : Entered.");
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        String dialogTile = getResources().getString(R.string.load_title);
        String dialogMsg = getResources().getString(R.string.wait);
        this.mDialogPleaseWait = ProgressDialog.show(this, dialogTile, dialogMsg, true, false);
        this.audioMa = (AudioManager) getSystemService("audio");
        setVolumeControlStream(3);
        setDefaultKeyMode(3);
        this.mHandler.sendMessageDelayed(Message.obtain(this.mHandler, 10, savedInstanceState), 250L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showIntrinsicUi(Bundle savedInstanceState) {
        LOG("showIntrinsicUi() : Entered.");
        setContentView(R.layout.main_display_land);
        CheckAddPlaylistVisible();
        initImageViewList();
        setButton();
        getListView();
        if (this.mTrackListAdapter != null) {
            this.mTrackListAdapter.setActivity(this);
            setListAdapter(this.mTrackListAdapter);
        }
        playonCreat(savedInstanceState);
        MusicUtils.bindToService(this, this);
        this.play_display_fir = findViewById(R.id.main_display_layout_id);
        this.play_display_sec = findViewById(R.id.play_main_layout);
        this.mbacktoFolder = 0;
        this.misFolderManage = false;
    }

    public void CheckAddPlaylistVisible() {
        this.addplaylist = findViewById(R.id.addplaylist);
        if (this.mAdapter == 4) {
            this.addplaylist.setVisibility(0);
        } else {
            this.addplaylist.setVisibility(8);
        }
    }

    public void AddPlaylistVisible() {
        this.addplaylist = findViewById(R.id.addplaylist);
        this.addplaylist.setVisibility(0);
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        LOG("Enter the onServiceConnected()");
        IntentFilter f = new IntentFilter();
        f.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        f.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        f.addAction("android.intent.action.MEDIA_UNMOUNTED");
        f.addAction("android.intent.action.MEDIA_EJECT");
        f.addDataScheme("file");
        registerReceiver(this.mScanListener, f);
        TrackDisplay();
        Resources res = getResources();
        this.PlaylistNum = MusicUtils.getPlaylistNum(this);
        String LastPlayedName = res.getString(R.string.recentlyadded);
        LOG("PlaylistNum = " + this.PlaylistNum);
        if (!MusicUtils.IsPlaylistExist(this, LastPlayedName)) {
            LOG("is not exist!!!!");
            new newPlaylist(this);
        }
        Bundle extras_main = getIntent().getExtras();
        if (extras_main == null) {
            this.mOneShot = getIntent().getBooleanExtra("oneshot", false);
        } else if (extras_main.getInt("checkLayout") == 1 || isPickIntent()) {
            this.manimation = true;
            LOG("   is started with the notification~~~~~");
            if (extras_main.getBoolean("searchnetflag")) {
                this.mNetSearchFlag = true;
            }
            View audioInfo_tmp = findViewById(R.id.audio_info);
            this.play_display_fir.setVisibility(4);
            this.play_display_sec.setVisibility(0);
            bindService();
            if (MusicUtils.getisLyric()) {
                this.lyric_layout.setVisibility(0);
                audioInfo_tmp.setVisibility(4);
                this.lyricmanager.pause(false);
            }
            this.mRelaunchAfterConfigChange = extras_main.getBoolean("configchange");
            if (!this.mOneShot) {
                this.mOneShot = extras_main.getBoolean("oneshot");
            }
        }
        LOG("onServiceConnected mOneShot = " + this.mOneShot);
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override // android.app.ListActivity, android.app.Activity
    public void onDestroy() {
        MusicUtils.unbindFromService(this);
        LOG("Enter onDestroy()");
        try {
            if ("nowplaying".equals(this.mPlaylistId)) {
                unregisterReceiverSafe(this.mNowPlayingListener);
            } else {
                unregisterReceiverSafe(this.mTrackListListener);
            }
        } catch (IllegalArgumentException e) {
        }
        if (this.play_display_fir != null && this.play_display_fir.getVisibility() == 4) {
            displayonStop();
        }
        unregisterReceiverSafe(this.mScanListener);
        this.mReScanHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private boolean isPickIntent() {
        String action = getIntent().getAction();
        return "android.intent.action.VIEW".equals(action) || "android.intent.action.GET_CONTENT".equals(action);
    }

    private void unregisterReceiverSafe(BroadcastReceiver receiver) {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
        }
    }

    @Override // android.app.Activity
    public void onRestart() {
        super.onRestart();
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        updateTrackInfo();
        if (MusicUtils.getisLyric() && this.lyricmanager != null && this.lyricmanager.isPaused()) {
            this.lyricmanager.pause(false);
        }
        LOG("onResume");
    }

    @Override // android.app.Activity
    public void onPause() {
        this.mReScanHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
    }

    public void bindService() {
        this.paused = false;
        LOG("Enter bindService()");
        this.mDisplayService = MusicUtils.sService;
        try {
            startPlayback();
            if (this.mDisplayService.getAudioId() >= 0 || this.mDisplayService.isPlaying() || this.mDisplayService.getPath() != null) {
                setPauseButtonImage();
            }
            int mode = this.mDisplayService.getRepeatMode();
            LOG("mode = " + mode);
            this.SuffleModeImage = (ImageView) findViewById(R.id.suffle_mode);
            this.SuffleModeImage.setImageResource(R.drawable.shuffle_01 + mode);
        } catch (RemoteException e) {
        }
        IntentFilter f = new IntentFilter();
        f.addAction(AudioPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(AudioPlaybackService.META_CHANGED);
        f.addAction(AudioPlaybackService.PLAYBACK_COMPLETE);
        f.addAction(AudioPlaybackService.MEDIAPLAY_ERROR);
        registerReceiver(this.mStatusListener, new IntentFilter(f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTrackInfo() {
        if (this.mDisplayService != null) {
            try {
                String path = this.mDisplayService.getPath();
                LOG("path = " + path);
                if (path != null) {
                    LOG("updateTrackInfo()");
                    this.AB_mode.setImageResource(R.drawable.audioab00 + MusicUtils.getABRequire());
                    if (path.toLowerCase().startsWith("/flash") || path.toLowerCase().startsWith("/sdcard") || path.toLowerCase().startsWith("/usb1")) {
                        LOG("path.toLowerCase().startsWith(/flash");
                        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mOneCursorCols, "_data=?", new String[]{path}, null);
                        if (c == null || c.getCount() <= 0) {
                            if ("<unknown>".equals(this.mArtistName) || this.mArtistName == null) {
                                this.mArtistName = getString(R.string.unknown_artist_name);
                            }
                            this.artiststring.setText(this.mArtistName);
                            this.mTrackname = this.mDisplayService.getTracknameBack();
                            this.songtitlestring.setText(this.mTrackname);
                            this.mDisplayname = this.mDisplayService.getDisplayname();
                            Log.d("TAG", "the mTracknameBack is" + this.mTracknameBack + " 987");
                            String albumName = this.mDisplayService.getAlbumName();
                            LOG("albumName = " + albumName);
                            int albumid = this.mDisplayService.getAlbumId();
                            if ("<unknown>".equals(albumName) || albumName == null) {
                                albumName = getString(R.string.unknown_album_name);
                            }
                            this.albumstring.setText(albumName);
                            Log.d("RockAudioPlayer.java", "albumid = " + albumid);
                            if (albumid != -1) {
                                AlbumThread(albumid);
                            }
                            if (MusicUtils.getisLyric()) {
                                Log.d("RockAudioPlayer.java", "MusicUtils.getisLyric() is true ");
                                getLrc();
                            }
                            this.songnums.setText("");
                        } else {
                            c.moveToFirst();
                            this.mArtistName = c.getString(c.getColumnIndex("artist"));
                            LOG("artistName = " + this.mArtistName);
                            if ("<unknown>".equals(this.mArtistName) || this.mArtistName == null) {
                                this.mArtistName = getString(R.string.unknown_artist_name);
                            }
                            this.artiststring.setText(this.mArtistName);
                            this.mTrackname = c.getString(c.getColumnIndex("title"));
                            LOG("the num is " + c.getColumnIndex("_display_name"));
                            this.mDisplayname = c.getString(c.getColumnIndex("_display_name"));
                            this.mDisplayService.setDisplayname(this.mDisplayname);
                            if (this.mTrackname != null) {
                                this.songtitlestring.setText(this.mTrackname);
                                this.mDisplayService.setTracknameBack(this.mTrackname);
                            } else {
                                this.songtitlestring.setText(path);
                                this.mDisplayService.setTracknameBack(path);
                            }
                            Log.d("TAG", "the mTracknameBack is" + this.mTracknameBack + " 123");
                            int albumid2 = c.getInt(c.getColumnIndex("album_id"));
                            String albumName2 = c.getString(c.getColumnIndex("album"));
                            if ("<unknown>".equals(albumName2) || albumName2 == null) {
                                albumName2 = getString(R.string.unknown_album_name);
                            }
                            this.albumstring.setText(albumName2);
                            if (albumid2 != -1) {
                                Drawable d = MusicUtils.getAlbumWork(this, albumid2, this.DefaultAlbumIcon);
                                this.DiskPlayImageButton.setImageDrawable(d);
                            }
                            if (MusicUtils.getisLyric()) {
                                getLrc();
                            }
                            this.songnums.setText("");
                            c.close();
                        }
                    } else {
                        LOG("test 2010.12.31");
                        this.ArtistImage.setVisibility(0);
                        this.AlbumImage.setVisibility(0);
                        this.songtitlestring.setVisibility(0);
                        this.artiststring.setVisibility(0);
                        this.albumstring.setVisibility(0);
                        this.mArtistName = this.mDisplayService.getArtistName();
                        if ("<unknown>".equals(this.mArtistName) || this.mArtistName == null) {
                            this.mArtistName = getString(R.string.unknown_artist_name);
                        }
                        this.artiststring.setText(this.mArtistName);
                        this.mTrackname = this.mDisplayService.getTrackName();
                        this.mDisplayname = this.mDisplayService.getDisplayName();
                        if (this.mTrackname != null) {
                            this.songtitlestring.setText(this.mTrackname);
                        } else {
                            this.songtitlestring.setText(path);
                        }
                        String albumName3 = this.mDisplayService.getAlbumName();
                        LOG("albumName = " + albumName3);
                        int albumid3 = this.mDisplayService.getAlbumId();
                        if ("<unknown>".equals(albumName3) || albumName3 == null) {
                            albumName3 = getString(R.string.unknown_album_name);
                        }
                        this.albumstring.setText(albumName3);
                        Log.d("RockAudioPlayer.java", "albumid = " + albumid3);
                        if (albumid3 != -1) {
                            AlbumThread(albumid3);
                        }
                        if (MusicUtils.getisLyric()) {
                            getLrc();
                        }
                        if (this.mTrackname != null) {
                            this.songnums.setText(Integer.toString(MusicUtils.getcurrentsongnums()) + "/" + Integer.toString(MusicUtils.gettotalsongnums()));
                        }
                    }
                    this.mDuration = this.mDisplayService.getDuration();
                    if (this.mDuration <= 0) {
                        this.mDuration = this.mDisplayService.duration();
                    }
                    this.mTotalTime.setText(MusicUtils.makeTimeString(this, this.mDuration / 1000));
                } else if (this.mEject) {
                    if (this.mDisplayService != null) {
                        this.mDisplayService.stop();
                    }
                    finish();
                }
            } catch (RemoteException ex) {
                LOG("error" + ex);
                finish();
            }
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.rk.RockAudioPlayer.RockAudioPlayer$2] */
    private void AlbumThread(final int albumid) {
        new Thread() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Drawable d = MusicUtils.getArtwork(RockAudioPlayer.this, albumid, RockAudioPlayer.this.DefaultAlbumIcon);
                Message msg1 = RockAudioPlayer.this.mHandler.obtainMessage(4);
                msg1.obj = d;
                RockAudioPlayer.this.mHandler.sendMessageDelayed(msg1, 800L);
            }
        }.start();
    }

    private void getLrc() {
        try {
            String path = this.mDisplayService.getPath();
            LOG("path = " + path);
            if (path != null) {
                if (this.mDisplayService.getAudioId() >= 0 || !path.toLowerCase().startsWith("http://")) {
                    lyriccontrol(this.mArtistName, this.mTrackname, this.mDisplayname, null);
                } else {
                    View audioInfo_tmp = findViewById(R.id.audio_info);
                    ((View) this.artiststring.getParent()).setVisibility(4);
                    ((View) this.albumstring.getParent()).setVisibility(4);
                    this.ArtistImage.setVisibility(8);
                    this.AlbumImage.setVisibility(8);
                    this.lyric_layout.setVisibility(4);
                    audioInfo_tmp.setVisibility(0);
                    this.artiststring.setText(path);
                }
            }
        } catch (RemoteException e) {
        }
    }

    public void HintToastShow(int Rsid) {
        Toast.makeText(this, Rsid, 4000).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueNextRefresh(long delay) {
        if (!this.paused && this.mDisplayService != null) {
            Message msg = this.mHandler.obtainMessage(1);
            this.mHandler.removeMessages(1);
            this.mHandler.sendMessageDelayed(msg, delay);
        }
    }

    private void startPlayback() {
        String filename;
        if (this.mDisplayService != null) {
            Intent intent = getIntent();
            Uri uri = intent.getData();
            LOG("uri = " + uri);
            if (uri != null && uri.toString().length() > 0) {
                String scheme = uri.getScheme();
                if ("file".equals(scheme)) {
                    filename = uri.getPath();
                } else {
                    filename = uri.toString();
                }
                try {
                    this.mOneShot = true;
                    if (!this.mRelaunchAfterConfigChange) {
                        this.mDisplayService.stop();
                        this.mDisplayService.openfile(filename);
                        this.mDisplayService.play();
                    }
                } catch (Exception ex) {
                    LOG("couldn't start playback: " + ex);
                }
            }
            LOG("startPlayback()");
            updateTrackInfo();
            long next = refreshNow();
            queueNextRefresh(next);
        }
    }

    @Override // android.app.Activity
    public void onSaveInstanceState(Bundle outcicle) {
        outcicle.putIntArray("list", this.mList);
        outcicle.putInt("position", this.mPosition);
        super.onSaveInstanceState(outcicle);
    }

    public void initImageViewList() {
        this.oldImageView = 0;
        this.mImageViewList = new ArrayList<ImageView>();
        ImageView tmp = (ImageView) findViewById(R.id.all_music_button_bd);
        this.mImageViewList.add(tmp);
        ImageView tmp2 = (ImageView) findViewById(R.id.playlist_button_bc);
        this.mImageViewList.add(tmp2);
        ImageView tmp3 = (ImageView) findViewById(R.id.mainsearch_button_bc);
        this.mImageViewList.add(tmp3);
        ImageView tmp4 = (ImageView) findViewById(R.id.directory_button_bc);
        this.mImageViewList.add(tmp4);
        ImageView tmp5 = (ImageView) findViewById(R.id.justplayed_button_bc);
        this.mImageViewList.add(tmp5);
        ImageView tmp6 = (ImageView) findViewById(R.id.artist_button_bc);
        this.mImageViewList.add(tmp6);
        ImageView tmp7 = (ImageView) findViewById(R.id.album_button_bc);
        this.mImageViewList.add(tmp7);
        ImageView tmp8 = (ImageView) findViewById(R.id.genre_button_bc);
        this.mImageViewList.add(tmp8);
        ImageView tmp9 = (ImageView) findViewById(R.id.return_button);
        this.mImageViewList.add(tmp9);
    }

    public void setBk(int op) {
        ImageView tmp = this.mImageViewList.get(op);
        tmp.setVisibility(0);
        if (op != this.oldImageView) {
            ImageView tmp2 = this.mImageViewList.get(this.oldImageView);
            tmp2.setVisibility(4);
        }
        this.oldImageView = op;
    }

    void setButton() {
        LOG("Enter the seButton");
        this.b = findViewById(R.id.all_music_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.playlist_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.mainsearch_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.SearchButton);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.directory_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.justplayed_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.artist_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.album_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.genre_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.addplaylist_addview);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.return_home_button);
        this.b.setOnClickListener(this);
        this.b.setOnLongClickListener(this);
        this.b = findViewById(R.id.abrepeat_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.shuffle_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.pre_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.play_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.next_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.addtoplaylist_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.settings_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.return_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.vol1_button);
        this.b.setOnClickListener(this);
        this.b = findViewById(R.id.vol2_button);
        this.b.setOnClickListener(this);
        LOG("setButton quit");
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addplaylist_addview /* 2131427334 */:
                RemoveCallback();
                this.PlaylistNum = MusicUtils.getPlaylistNum(this);
                LOG("PlaylistNum =" + this.PlaylistNum);
                if (this.PlaylistNum >= 10) {
                    Toast.makeText(this, "Can't add more playlist", 0).show();
                    return;
                }
                newPlaylist ret = new newPlaylist(this);
                LOG("newplaylist ret = " + ret);
                PlaylistDisplay();
                this.PlaylistNum++;
                return;
            case R.id.all_music_button /* 2131427378 */:
                RemoveCallback();
                this.mAdapter = 0;
                CheckAddPlaylistVisible();
                setBk(0);
                hidesearchview();
                this.mArtistId = null;
                this.mAlbumId = null;
                this.mPlaylistId = null;
                this.mGenreId = null;
                TrackDisplay();
                return;
            case R.id.mainsearch_button /* 2131427381 */:
                RemoveCallback();
                setBk(2);
                View searchView = findViewById(R.id.search_layout);
                EditText searchtext = (EditText) findViewById(R.id.SearchText);
                if (searchView.getVisibility() == 4) {
                    InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
                    imm.showSoftInput(searchtext, 0);
                    searchView.setVisibility(0);
                    searchtext.setFocusable(true);
                    searchtext.setInputType(1);
                    this.mAdapterold = this.mAdapter;
                    this.mAdapter = 6;
                    CheckAddPlaylistVisible();
                    return;
                }
                searchView.setVisibility(4);
                this.mAdapter = this.mAdapterold;
                CheckAddPlaylistVisible();
                return;
            case R.id.artist_button /* 2131427384 */:
                RemoveCallback();
                this.mAdapter = 1;
                CheckAddPlaylistVisible();
                setBk(5);
                hidesearchview();
                ArtistDisplay();
                return;
            case R.id.album_button /* 2131427387 */:
                RemoveCallback();
                this.mAdapter = 2;
                CheckAddPlaylistVisible();
                setBk(6);
                hidesearchview();
                AlbumDisplay();
                return;
            case R.id.genre_button /* 2131427390 */:
                RemoveCallback();
                this.mAdapter = 3;
                CheckAddPlaylistVisible();
                setBk(7);
                hidesearchview();
                GenreDisplay();
                return;
            case R.id.directory_button /* 2131427393 */:
                this.misbuttonFoler = true;
                RemoveCallback();
                CheckAddPlaylistVisible();
                setBk(3);
                hidesearchview();
                DirectoryDisplay("/");
                return;
            case R.id.playlist_button /* 2131427395 */:
                RemoveCallback();
                this.mAdapter = 4;
                CheckAddPlaylistVisible();
                setBk(1);
                hidesearchview();
                PlaylistDisplay();
                return;
            case R.id.justplayed_button /* 2131427398 */:
                RemoveCallback();
                this.mAdapter = 0;
                setBk(4);
                hidesearchview();
                try {
                    if (this.mTrackCursor != null) {
                        if (MusicUtils.sService.isPlaying()) {
                            View audioInfo_tmp = findViewById(R.id.audio_info);
                            this.play_display_fir.setVisibility(4);
                            this.play_display_sec.setVisibility(0);
                            bindService();
                            if (MusicUtils.getisLyric()) {
                                this.lyric_layout.setVisibility(0);
                                audioInfo_tmp.setVisibility(4);
                                this.lyricmanager.pause(false);
                            }
                        } else {
                            this.position_now = 0;
                            this.mState = 0;
                            if (this.mTrackCursor.getCount() == 0) {
                                LOG("--- in onListItemClick  return");
                                Toast.makeText(this, (int) R.string.No_Music, 4000).show();
                            } else {
                                changedisplay(false);
                                this.mAdapter = 10;
                                this.mBindHandler.postDelayed(this.mBindRun, 10L);
                            }
                        }
                    }
                    return;
                } catch (RemoteException e) {
                    return;
                }
            case R.id.return_home_button /* 2131427401 */:
            case R.id.return_button /* 2131427432 */:
                LOG("return");
                if (this.mbacktoFolder == 1) {
                    DirectoryDisplay(this.now_path);
                    this.mbacktoFolder = 0;
                    return;
                } else if (this.play_display_fir.getVisibility() != 4) {
                    switch (this.mState) {
                        case 1:
                            LOG("EXIT ARTIST");
                            this.mState = -1;
                            this.mAdapter = 1;
                            setListAdapter(this.mArtistListAdapter);
                            return;
                        case 2:
                            this.mState = -1;
                            this.mAdapter = 2;
                            setListAdapter(this.mAlbumListAdapter);
                            return;
                        case 3:
                            this.mState = -1;
                            this.mAdapter = 3;
                            setListAdapter(this.mGenreListAdapter);
                            return;
                        case 4:
                            this.mState = -1;
                            this.mAdapter = 4;
                            AddPlaylistVisible();
                            setListAdapter(this.mPlaylistListAdapter);
                            return;
                        default:
                            LOG("default");
                            RemoveCallback();
                            finish();
                            return;
                    }
                } else if (this.mOneShot && !this.mNetSearchFlag) {
                    RemoveCallback();
                    finish();
                    return;
                } else if (this.mNetSearchFlag) {
                    this.mNetSearchFlag = false;
                    finish();
                    removeDialog(5);
                    Intent intentsearch = new Intent();
                    intentsearch.setClass(this, SearchNetActivity.class);
                    startActivity(intentsearch);
                    return;
                } else {
                    changedisplay(true);
                    removeDialog(5);
                    int DisplaySongsPosition = MusicUtils.getcurrentsongnums() - 1;
                    LOG("DisplaySongsPosition = " + DisplaySongsPosition);
                    this.mAdapter = 10;
                    switch (this.mState) {
                        case 1:
                            this.mState = 1;
                            setListAdapter(this.mArtistListAdapter);
                            this.mArtistId = this.backupid;
                            TrackDisplay();
                            getListView().setSelection(DisplaySongsPosition);
                            this.mArtistId = null;
                            return;
                        case 2:
                            this.mState = 2;
                            setListAdapter(this.mAlbumListAdapter);
                            this.mAlbumId = this.backupid;
                            TrackDisplay();
                            getListView().setSelection(DisplaySongsPosition);
                            this.mAlbumId = null;
                            return;
                        case 3:
                            this.mState = 3;
                            setListAdapter(this.mGenreListAdapter);
                            this.mGenreId = this.backupid;
                            TrackDisplay();
                            getListView().setSelection(DisplaySongsPosition);
                            this.mGenreId = null;
                            return;
                        case 4:
                            this.mState = 4;
                            AddPlaylistVisible();
                            setListAdapter(this.mPlaylistListAdapter);
                            this.mPlaylistId = this.backupid;
                            TrackDisplay();
                            getListView().setSelection(DisplaySongsPosition);
                            this.mPlaylistId = null;
                            return;
                        default:
                            TrackDisplay();
                            getListView().setSelection(DisplaySongsPosition);
                            return;
                    }
                }
            case R.id.SearchButton /* 2131427404 */:
                RemoveCallback();
                EditText searchtextread = (EditText) findViewById(R.id.SearchText);
                View searchViewread = findViewById(R.id.search_layout);
                CharSequence searchcontent = searchtextread.getText();
                String strsearchcontent = new String(searchcontent.toString());
                if (strsearchcontent.equals("")) {
                    Toast.makeText(this, (int) R.string.search_noinput, 0).show();
                    return;
                }
                searchtextread.setText("");
                if (searchfile(strsearchcontent)) {
                    Animation myAnimation_out = new ScaleAnimation(1.1f, 0.0f, 1.1f, 0.0f, 1, 0.5f, 1, 0.5f);
                    searchViewread.setAnimation(myAnimation_out);
                    myAnimation_out.setDuration(300L);
                    searchViewread.setVisibility(4);
                    Toast.makeText(this, (int) R.string.search_true, 1).show();
                    return;
                }
                Toast.makeText(this, (int) R.string.search_false, 1).show();
                return;
            case R.id.pre_button /* 2131427422 */:
                if (this.mDisplayService != null) {
                    try {
                        LOG("mOneShot = " + this.mOneShot);
                        if (this.mOneShot) {
                            this.mDisplayService.seek(0L);
                            this.mDisplayService.play();
                        } else {
                            this.OnclickCounter++;
                            this.mDisplayService.prev();
                            updateTrackInfo();
                            queueNextRefresh(1L);
                            Message msg = this.mHandler.obtainMessage(5);
                            msg.arg1 = this.OnclickCounter;
                            this.mHandler.sendMessageDelayed(msg, 800L);
                        }
                        return;
                    } catch (RemoteException e2) {
                        return;
                    }
                } else {
                    return;
                }
            case R.id.play_button /* 2131427423 */:
                doPauseResume();
                return;
            case R.id.next_button /* 2131427424 */:
                if (this.mDisplayService != null) {
                    try {
                        if (this.mOneShot) {
                            this.mDisplayService.seek(0L);
                            this.mDisplayService.play();
                        } else {
                            this.OnclickCounter++;
                            this.mDisplayService.next();
                            updateTrackInfo();
                            queueNextRefresh(1L);
                            Message msg2 = this.mHandler.obtainMessage(5);
                            msg2.arg1 = this.OnclickCounter;
                            this.mHandler.sendMessageDelayed(msg2, 800L);
                        }
                        return;
                    } catch (RemoteException e3) {
                        return;
                    }
                } else {
                    return;
                }
            case R.id.abrepeat_button /* 2131427426 */:
                SetAudioABImageButton();
                return;
            case R.id.shuffle_button /* 2131427427 */:
                SetAudioRepeatModeImageButton();
                return;
            case R.id.addtoplaylist_button /* 2131427428 */:
                if (!this.mOneShot) {
                    showDialog(5);
                    return;
                } else {
                    Toast.makeText(this, (int) R.string.HintAddPlayList, 0).show();
                    return;
                }
            case R.id.settings_button /* 2131427429 */:
                if (this.mOneShot) {
                    Toast.makeText(this, (int) R.string.HintLrc, 0).show();
                    return;
                } else {
                    WhetherDisplayLrc();
                    return;
                }
            case R.id.vol2_button /* 2131427430 */:
                this.ControlVolumeDisplayHandler.removeCallbacks(this.ControlVolumeDisplayRunnable);
                AudioSetVolume(false);
                this.ControlVolumeDisplayHandler.postDelayed(this.ControlVolumeDisplayRunnable, 1500L);
                return;
            case R.id.vol1_button /* 2131427431 */:
                this.ControlVolumeDisplayHandler.removeCallbacks(this.ControlVolumeDisplayRunnable);
                AudioSetVolume(true);
                this.ControlVolumeDisplayHandler.postDelayed(this.ControlVolumeDisplayRunnable, 1500L);
                return;
            default:
                return;
        }
    }

    private void RemoveCallback() {
        if (this.mTrackListForPlaylist != null) {
            this.mTracklistHandler.removeCallbacks(this.mTrackListForPlaylist);
        }
    }

    private void TrackDisplay() {
        CheckAddPlaylistVisible();
        LOG("Enter TrackDisplay()");
        this.mTrackList = getListView();
        if (this.mTrackListAdapter == null) {
            this.mTrackListAdapter = new TrackListAdapter(getApplication(), this, R.layout.track_list_item, null, new String[0], new int[0], "nowplaying".equals(this.mPlaylistId), -1);
            LOG("mTrackListAdapter = " + this.mTrackListAdapter);
            this.mTrackListAdapter.setActivity(this);
            setListAdapter(this.mTrackListAdapter);
            getCursor(this.mTrackListAdapter.getQueryHandler(), null, 0);
            return;
        }
        this.mTrackListAdapter.setActivity(this);
        getTrackCursor(this.mTrackListAdapter.getQueryHandler(), null);
        LOG("mTrackCursor2 = " + this.mTrackCursor);
        setListAdapter(this.mTrackListAdapter);
    }

    /* loaded from: classes.dex */
    public class newPlaylist {
        private String mPlaylistName;
        private Toast mToast;
        private int playlistNum = 1;

        public newPlaylist(Context context) {
            RockAudioPlayer.this.LOG("Enter newPlaylist()");
            RockAudioPlayer.this.LOG("PalylistNum = " + RockAudioPlayer.this.PlaylistNum);
            AddtoPlaylistSet(context);
        }

        private int idForplaylist(String name, Context context) {
            RockAudioPlayer.this.LOG("Enter idForplaylist()");
            Cursor c = MusicUtils.query(context, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "name=?", new String[]{name}, "_id");
            if (c == null) {
                return 1000;
            }
            int id = -1;
            if (c != null) {
                c.moveToFirst();
                if (!c.isAfterLast()) {
                    id = c.getInt(0);
                }
                c.close();
            }
            return id;
        }

        public void showToast(Context context, String string) {
            if (this.mToast == null) {
                this.mToast = Toast.makeText(context, "", 0);
            }
            this.mToast.setText(string);
            this.mToast.show();
        }

        private String makePlaylistName(Context context) {
            String suggestedname;
            RockAudioPlayer.this.LOG("Enter makePlaylistName()");
            Resources res = RockAudioPlayer.this.getResources();
            String playlistName = res.getString(R.string.recentlyadded);
            if (!MusicUtils.IsPlaylistExist(RockAudioPlayer.this, playlistName)) {
                suggestedname = RockAudioPlayer.this.getString(R.string.recentlyadded);
            } else {
                String template = RockAudioPlayer.this.getString(R.string.new_playlist_name_template2);
                String[] cols = {"name"};
                ContentResolver resolver = RockAudioPlayer.this.getContentResolver();
                Cursor c = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, cols, "name != ''", null, "_id");
                if (c == null) {
                    return null;
                }
                int i = this.playlistNum;
                this.playlistNum = i + 1;
                suggestedname = String.format(template, Integer.valueOf(i));
                RockAudioPlayer.this.LOG("playlistNum = " + this.playlistNum);
                boolean done = false;
                while (!done) {
                    done = true;
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        String playlistname = c.getString(0);
                        if (playlistname.compareToIgnoreCase(suggestedname) == 0) {
                            int i2 = this.playlistNum;
                            this.playlistNum = i2 + 1;
                            suggestedname = String.format(template, Integer.valueOf(i2 - 1));
                            done = false;
                        }
                        c.moveToNext();
                    }
                }
                c.close();
            }
            return suggestedname;
        }

        private void AddtoPlaylistSet(Context context) {
            this.mPlaylistName = makePlaylistName(context);
            if (this.mPlaylistName != null && this.mPlaylistName.length() > 0) {
                ContentResolver resolver = RockAudioPlayer.this.getContentResolver();
                int id = idForplaylist(this.mPlaylistName, context);
                if (id != 1000) {
                    if (id >= 0) {
                        ContentUris.withAppendedId(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, id);
                        MusicUtils.clearPlaylist(RockAudioPlayer.this, id);
                    } else {
                        ContentValues values = new ContentValues(1);
                        RockAudioPlayer.this.LOG("mPlaylistName==" + this.mPlaylistName);
                        values.put("name", this.mPlaylistName);
                        resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                    }
                } else {
                    return;
                }
            }
            RockAudioPlayer.this.LOG("newplaylist success");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void PlaylistDisplay() {
        LOG("Enter PlaylistDisplay()");
        this.mPlaylistList = getListView();
        if (this.mPlaylistListAdapter == null) {
            this.mPlaylistListAdapter = new PlaylistListAdapter(getApplication(), this, R.layout.playlist_list_item, this.mPlaylistCursor, new String[]{"_id"}, new int[]{16908308}, this.PlaylistListener);
            setListAdapter(this.mPlaylistListAdapter);
            getPlaylistCursor(this.mPlaylistListAdapter.getQueryHandler(), null);
            return;
        }
        this.mPlaylistListAdapter.setActivity(this);
        setListAdapter(this.mPlaylistListAdapter);
        this.mPlaylistCursor = this.mPlaylistListAdapter.getCursor();
        if (this.mPlaylistCursor != null) {
            initPlaylistCursor(this.mPlaylistCursor);
        } else {
            getPlaylistCursor(this.mPlaylistListAdapter.getQueryHandler(), null);
        }
    }

    void DirectoryDisplay(String start_path) {
        this.misbuttonFoler = true;
        Intent intent = new Intent(this, Folder_Manage_1_6.class);
        intent.putExtra("start_path", start_path);
        startActivityForResult(intent, 3);
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 3:
                LOG("RockAudioPlayer--!!!!!@@@@###-onActivityResult()");
                if (this.misbuttonFoler) {
                    this.misbuttonFoler = false;
                    if (data != null) {
                        Bundle extras = data.getExtras();
                        int isgeneralfinish = extras.getInt("isgeneralfinish");
                        if (isgeneralfinish == 1) {
                            this.play_display_sec.setVisibility(4);
                            this.play_display_fir.setVisibility(0);
                            return;
                        }
                        int isselectFile = extras.getInt("isselectFile");
                        if (isselectFile == 1) {
                            this.mList = extras.getIntArray("cur_list");
                            this.mPosition = extras.getInt("cur_position");
                            this.now_path = extras.getString("now_path");
                            this.mbacktoFolder = 1;
                            this.misFolderManage = true;
                            LOG("    mPosition = " + this.mPosition);
                            changedisplay(false);
                            this.mBindHandler.postDelayed(this.mBindRun, 700L);
                            return;
                        }
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    private void JustplayedDisplay() {
        this.mTrackList = getListView();
        Resources res = getResources();
        String LastPlayedName = res.getString(R.string.recentlyadded);
        this.mPlaylistId = String.valueOf(MusicUtils.getPlaylistId(this, LastPlayedName));
        this.mAdapter = 10;
        TrackDisplay();
        this.mPlaylistId = null;
    }

    void ArtistDisplay() {
        this.mArtistList = getListView();
        if (this.mArtistListAdapter == null) {
            this.mArtistListAdapter = new ArtistListAdapter(getApplication(), this, R.layout.artist_list_item, this.mArtistCursor, new String[0], new int[0]);
            setListAdapter(this.mArtistListAdapter);
            setTitle(R.string.artist);
            getArtistCursor(this.mArtistListAdapter.getQueryHandler(), null);
        } else {
            this.mArtistListAdapter.setActivity(this);
            this.mArtistCursor = this.mArtistListAdapter.getCursor();
            LOG("mArtistCursor1 = " + this.mArtistCursor);
            if (this.mArtistCursor != null) {
                initArtistCursor(this.mArtistCursor);
            } else {
                getArtistCursor(this.mArtistListAdapter.getQueryHandler(), null);
            }
            LOG("mArtistCursor2 = " + this.mArtistCursor);
            setListAdapter(this.mArtistListAdapter);
        }
        LOG("mArtistListAdapter = " + this.mAdapter);
    }

    void AlbumDisplay() {
        this.mAlbumList = getListView();
        LOG("The last mAdapter = " + this.mAdapter);
        if (this.mAlbumListAdapter == null) {
            this.mAlbumListAdapter = new AlbumListAdapter(getApplication(), this, R.layout.album_list_item, this.mAlbumCursor, new String[0], new int[0]);
            setListAdapter(this.mAlbumListAdapter);
            getAlbumCursor(this.mAlbumListAdapter.getQueryHandler(), null);
            return;
        }
        this.mAlbumListAdapter.setActivity(this);
        this.mAlbumCursor = this.mAlbumListAdapter.getCursor();
        LOG("mAlbumCursor1 = " + this.mAlbumCursor);
        if (this.mAlbumCursor != null) {
            initAlbumCursor(this.mAlbumCursor);
        } else {
            getAlbumCursor(this.mAlbumListAdapter.getQueryHandler(), null);
        }
        LOG("mAlbumCursor2 = " + this.mAlbumCursor);
        setListAdapter(this.mAlbumListAdapter);
    }

    void GenreDisplay() {
        this.mGenreList = getListView();
        if (this.mGenreListAdapter == null) {
            this.mGenreListAdapter = new GenreListAdapter(getApplication(), this, R.layout.genre_list_item, this.mGenreCursor, new String[0], new int[0]);
            setListAdapter(this.mGenreListAdapter);
            getGenreCursor(this.mGenreListAdapter.getQueryHandler(), null);
        } else {
            this.mGenreListAdapter.setActivity(this);
            this.mGenreCursor = this.mGenreListAdapter.getCursor();
            if (this.mGenreCursor != null) {
                initGenreCursor(this.mGenreCursor);
            } else {
                getGenreCursor(this.mGenreListAdapter.getQueryHandler(), null);
            }
            setListAdapter(this.mGenreListAdapter);
        }
        LOG("mAdapter = " + this.mAdapter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Cursor getCursor(AsyncQueryHandler async, String filter, int kind) {
        LOG("Enter the getCursor()");
        switch (kind) {
            case 0:
                getTrackCursor(async, filter);
                break;
            case 1:
                getArtistCursor(async, filter);
                break;
            case 2:
                getAlbumCursor(async, filter);
                break;
            case 3:
                getGenreCursor(async, filter);
                break;
            case 4:
                getPlaylistCursor(async, filter);
                break;
        }
        return null;
    }

    public Cursor getArtistCursor(AsyncQueryHandler async, String filter) {
        StringBuilder where = new StringBuilder();
        where.append("artist != ''");
        String[] keywords = null;
        if (filter != null) {
            String[] searchWords = filter.split(" ");
            keywords = new String[searchWords.length];
            Collator col = Collator.getInstance();
            col.setStrength(0);
            for (int i2 = 0; i2 < searchWords.length; i2++) {
                keywords[i2] = '%' + MediaStore.Audio.keyFor(searchWords[i2]) + '%';
            }
            for (int i3 = 0; i3 < searchWords.length; i3++) {
                where.append(" AND ");
                where.append("artist_key LIKE ?");
            }
        }
        String whereclause = where.toString();
        String[] cols = {"_id", "artist", "number_of_albums", "number_of_tracks"};
        if (async != null) {
            async.startQuery(0, null, MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, cols, whereclause, keywords, "artist_key");
            return null;
        }
        Cursor ret = MusicUtils.query(this, MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, cols, whereclause, keywords, "artist_key");
        return ret;
    }

    public Cursor getAlbumCursor(AsyncQueryHandler async, String filter) {
        StringBuilder where = new StringBuilder();
        where.append("album != ''");
        String[] keywords = null;
        if (filter != null) {
            String[] searchWords = filter.split(" ");
            keywords = new String[searchWords.length];
            Collator col = Collator.getInstance();
            col.setStrength(0);
            for (int i2 = 0; i2 < searchWords.length; i2++) {
                keywords[i2] = '%' + MediaStore.Audio.keyFor(searchWords[i2]) + '%';
            }
            for (int i3 = 0; i3 < searchWords.length; i3++) {
                where.append(" AND ");
                where.append("artist_key||");
                where.append("album_key LIKE ?");
            }
        }
        String whereclause = where.toString();
        String[] cols = {"_id", "album", "album_key", "artist", "numsongs", "album_art"};
        if (this.mArtistId != null) {
            if (async != null) {
                async.startQuery(0, null, MediaStore.Audio.Artists.Albums.getContentUri("external", Long.valueOf(this.mArtistId).longValue()), cols, whereclause, keywords, "album_key");
                return null;
            }
            Cursor ret = MusicUtils.query(this, MediaStore.Audio.Artists.Albums.getContentUri("external", Long.valueOf(this.mArtistId).longValue()), cols, whereclause, keywords, "album_key");
            return ret;
        } else if (async != null) {
            async.startQuery(0, null, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, cols, whereclause, keywords, "album_key");
            return null;
        } else {
            Cursor ret2 = MusicUtils.query(this, MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, cols, whereclause, keywords, "album_key");
            return ret2;
        }
    }

    public Cursor getGenreCursor(AsyncQueryHandler async, String filter) {
        StringBuilder where = new StringBuilder();
        where.append("_id != ''");
        String[] keywords = null;
        if (filter != null) {
            String[] searchWords = filter.split(" ");
            keywords = new String[searchWords.length];
            Collator col = Collator.getInstance();
            col.setStrength(0);
            for (int i2 = 0; i2 < searchWords.length; i2++) {
                keywords[i2] = '%' + MediaStore.Audio.keyFor(searchWords[i2]) + '%';
            }
            for (int i3 = 0; i3 < searchWords.length; i3++) {
                where.append(" AND ");
                where.append("artist_key LIKE ?");
            }
        }
        String whereclause = where.toString();
        String[] cols = {"_id", "name"};
        if (async != null) {
            async.startQuery(0, null, MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, cols, whereclause, keywords, "name");
            return null;
        }
        Cursor ret = MusicUtils.query(this, MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, cols, whereclause, keywords, "name");
        return ret;
    }

    public Cursor getPlaylistCursor(AsyncQueryHandler async, String filter) {
        StringBuilder where = new StringBuilder();
        where.append("name != ''");
        String[] keywords = null;
        if (filter != null) {
            String[] searchWords = filter.split(" ");
            keywords = new String[searchWords.length];
            Collator col = Collator.getInstance();
            col.setStrength(0);
            for (int i2 = 0; i2 < searchWords.length; i2++) {
                keywords[i2] = '%' + searchWords[i2] + '%';
            }
            for (int i3 = 0; i3 < searchWords.length; i3++) {
                where.append(" AND ");
                where.append("name LIKE ?");
            }
        }
        String whereclause = where.toString();
        if (async != null) {
            async.startQuery(0, null, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, this.mCols, whereclause, keywords, "name");
            return null;
        }
        Cursor c = MusicUtils.query(this, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, this.mCols, whereclause, keywords, "name");
        return c;
    }

    public Cursor getTrackCursor(AsyncQueryHandler async, String filter) {
        LOG("Enter getTrakCursor() and async= " + async + "filter = " + filter);
        Cursor ret = null;
        this.mSortOrder = "title_key";
        StringBuilder where = new StringBuilder();
        where.append("title != ''");
        String[] keywords = null;
        if (filter != null) {
            String[] searchWords = filter.split(" ");
            keywords = new String[searchWords.length];
            Collator col = Collator.getInstance();
            col.setStrength(0);
            for (int i2 = 0; i2 < searchWords.length; i2++) {
                keywords[i2] = '%' + MediaStore.Audio.keyFor(searchWords[i2]) + '%';
            }
            for (int i3 = 0; i3 < searchWords.length; i3++) {
                where.append(" AND ");
                where.append("artist_key||");
                where.append("album_key||");
                where.append("title_key LIKE ?");
            }
        }
        if (this.mGenreId != null) {
            LOG("mGenre = " + this.mGenreId);
            this.mSortOrder = "title_key";
            if (async != null) {
                async.startQuery(0, null, MediaStore.Audio.Genres.Members.getContentUri("external", Integer.valueOf(this.mGenreId).intValue()), this.mCursorCols, where.toString(), keywords, this.mSortOrder);
                ret = null;
            } else {
                ret = MusicUtils.query(this, MediaStore.Audio.Genres.Members.getContentUri("external", Integer.valueOf(this.mGenreId).intValue()), this.mCursorCols, where.toString(), keywords, this.mSortOrder);
            }
        } else if (this.mPlaylistId != null) {
            LOG("mPlaylist = " + this.mPlaylistId);
            if (this.mPlaylistId.equals("nowplaying")) {
                if (MusicUtils.sService != null) {
                    ret = new NowPlayingCursor(MusicUtils.sService, this.mCursorCols);
                    if (ret.getCount() == 0) {
                        finish();
                    }
                }
            } else if (this.mPlaylistId.equals("podcasts")) {
                LOG("mPlaylist = podcasts");
                where.append(" AND is_podcast=1");
                if (async != null) {
                    async.startQuery(0, null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, where.toString(), keywords, "_id");
                    ret = null;
                } else {
                    ret = MusicUtils.query(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, where.toString(), keywords, "_id");
                }
            } else if (this.mPlaylistId.equals("recentlyadded")) {
                LOG("mPlaylist = recentlyadded");
                int X = MusicUtils.getIntPref(this, "numweeks", 2) * 604800;
                where.append(" AND date_added>");
                where.append((System.currentTimeMillis() / 1000) - X);
                if (async != null) {
                    async.startQuery(0, null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, where.toString(), keywords, "_id");
                    ret = null;
                } else {
                    ret = MusicUtils.query(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, where.toString(), keywords, "_id");
                }
            } else {
                LOG("Enter the when mTrackListAdapter == null");
                this.mSortOrder = "play_order";
                if (async != null) {
                    async.startQuery(0, null, MediaStore.Audio.Playlists.Members.getContentUri("external", Long.valueOf(this.mPlaylistId).longValue()), this.mPlaylistMemberCols, where.toString(), keywords, this.mSortOrder);
                    ret = null;
                } else {
                    ret = MusicUtils.query(this, MediaStore.Audio.Playlists.Members.getContentUri("external", Long.valueOf(this.mPlaylistId).longValue()), this.mPlaylistMemberCols, where.toString(), keywords, this.mSortOrder);
                }
            }
        } else {
            if (this.mAlbumId != null) {
                where.append(" AND album_id=" + this.mAlbumId);
                this.mSortOrder = "track, " + this.mSortOrder;
            }
            if (this.mArtistId != null) {
                where.append(" AND artist_id=" + this.mArtistId);
            }
            where.append(" AND is_music=1");
            if (async != null) {
                async.startQuery(0, null, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, where.toString(), keywords, this.mSortOrder);
                ret = null;
            } else {
                ret = MusicUtils.query(this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCursorCols, where.toString(), keywords, this.mSortOrder);
            }
        }
        if (!(ret == null || async == null)) {
            initTrackCursor(ret);
        }
        LOG("ret = " + ret);
        return ret;
    }

    public void init(Cursor c, int kind) {
        LOG("Enter the init()" + c);
        switch (kind) {
            case 0:
                initTrackCursor(c);
                return;
            case 1:
                initArtistCursor(c);
                return;
            case 2:
                initAlbumCursor(c);
                return;
            case 3:
                initGenreCursor(c);
                return;
            case 4:
                initPlaylistCursor(c);
                return;
            case 5:
            default:
                return;
        }
    }

    public void initArtistCursor(Cursor mewCursor) {
        this.mArtistListAdapter.changeCursor(mewCursor);
        if (this.mArtistCursor == null) {
            MusicUtils.displayDatabaseError(this);
            this.mReScanHandler.sendEmptyMessageDelayed(0, 100L);
            return;
        }
        MusicUtils.hideDatabaseError(this);
    }

    public void initGenreCursor(Cursor newCursor) {
        this.mGenreListAdapter.changeCursor(newCursor);
        if (this.mGenreCursor == null) {
            MusicUtils.displayDatabaseError(this);
            closeContextMenu();
            this.mReScanHandler.sendEmptyMessageDelayed(0, 100L);
            return;
        }
        MusicUtils.hideDatabaseError(this);
    }

    public void HintNoMusic() {
        Toast.makeText(this, (int) R.string.No_Music, 4000).show();
    }

    public void initTrackCursor(Cursor newCursor) {
        LOG("Enter initTrackCursor()");
        this.mTrackListAdapter.changeCursor(newCursor);
        if (this.mAdapter == 11) {
        }
        LOG("mTrackCursor = " + this.mTrackCursor + "newCursor = " + newCursor);
        if (this.mTrackCursor == null) {
            this.mDialogPleaseWait.dismiss();
            MusicUtils.displayDatabaseError(this);
            this.mReScanHandler.sendEmptyMessageDelayed(0, 100L);
            return;
        }
        LOG("mTrackCursor.moveToFirst() = " + this.mTrackCursor.moveToFirst());
        if (this.mTrackCursor.moveToFirst()) {
            MusicUtils.hideDatabaseError(this);
            IntentFilter f = new IntentFilter();
            f.addAction(AudioPlaybackService.META_CHANGED);
            f.addAction(AudioPlaybackService.QUEUE_CHANGED);
            if ("nowplaying".equals(this.mPlaylistId)) {
                try {
                    int cur = MusicUtils.sService.getQueuePosition();
                    setSelection(cur);
                    registerReceiver(this.mNowPlayingListener, new IntentFilter(f));
                    this.mNowPlayingListener.onReceive(this, new Intent(AudioPlaybackService.META_CHANGED));
                } catch (RemoteException e) {
                }
            } else {
                String key = this.mArtistId;
                if (key != null) {
                    int keyidx = this.mTrackCursor.getColumnIndexOrThrow("artist_id");
                    this.mTrackCursor.moveToFirst();
                    while (true) {
                        if (this.mTrackCursor.isAfterLast()) {
                            break;
                        }
                        String artist = this.mTrackCursor.getString(keyidx);
                        if (artist.equals(key)) {
                            setSelection(this.mTrackCursor.getPosition());
                            break;
                        }
                        this.mTrackCursor.moveToNext();
                    }
                }
                registerReceiver(this.mTrackListListener, new IntentFilter(f));
                this.mTrackListListener.onReceive(this, new Intent(AudioPlaybackService.META_CHANGED));
            }
            LOG(" --------------- before  dismiss()  ");
            this.mDialogPleaseWait.dismiss();
        } else if (this.mAdapter == 0) {
            if (this.mDialogPleaseWait != null) {
                this.mDialogPleaseWait.dismiss();
            }
            MusicUtils.displayDatabaseError(this);
            this.mReScanHandler.sendEmptyMessageDelayed(0, 100L);
        } else if (this.mAdapter == 4) {
            Toast.makeText(this, "Please Add Songs To The PlayList!", 0).show();
        } else {
            Toast.makeText(this, "No Songs!", 0).show();
        }
    }

    public void initAlbumCursor(Cursor newCursor) {
        this.mAlbumListAdapter.changeCursor(newCursor);
        if (this.mAlbumCursor == null) {
            MusicUtils.displayDatabaseError(this);
            this.mReScanHandler.sendEmptyMessageDelayed(0, 100L);
            return;
        }
        MusicUtils.hideDatabaseError(this);
    }

    private void initPlaylistCursor(Cursor newCursor) {
        this.mPlaylistListAdapter.changeCursor(newCursor);
        if (this.mPlaylistCursor == null) {
            MusicUtils.displayDatabaseError(this);
            closeContextMenu();
            this.mReScanHandler.sendEmptyMessageDelayed(0, 100L);
            return;
        }
        MusicUtils.hideDatabaseError(this);
    }

    private void setMyTitle(int kind) {
    }

    private void setMyTitle(String s) {
    }

    @Override // android.app.ListActivity
    protected void onListItemClick(ListView l, View v, int position, long id) {
        LOG("mAdapter = " + this.mAdapter);
        switch (this.mAdapter) {
            case 0:
                this.position_now = position;
                this.mState = 0;
                changedisplay(false);
                this.mAdapter = 10;
                if (this.mTrackCursor.getCount() == 0) {
                    LOG("--- in onListItemClick  return");
                    return;
                } else {
                    this.mBindHandler.postDelayed(this.mBindRun, 10L);
                    return;
                }
            case 1:
                this.mState = 1;
                this.mArtistId = Long.valueOf(id).toString();
                this.backupid = this.mArtistId;
                LOG("mArtistId = " + this.mArtistId);
                this.mAdapter = 10;
                TrackDisplay();
                this.mArtistId = null;
                return;
            case 2:
                this.mState = 2;
                this.mAlbumId = Long.valueOf(id).toString();
                this.backupid = this.mAlbumId;
                LOG("mAlbumId = " + this.mAlbumId);
                this.mAdapter = 10;
                TrackDisplay();
                this.mAlbumId = null;
                return;
            case 3:
                this.mState = 3;
                this.mGenreId = Long.valueOf(id).toString();
                this.backupid = this.mGenreId;
                LOG("mGenreId = " + this.mGenreId);
                this.mAdapter = 10;
                TrackDisplay();
                this.mGenreId = null;
                return;
            case 4:
                this.mState = 4;
                this.mPlaylistId = Long.valueOf(id).toString();
                this.backupid = this.mPlaylistId;
                this.mSelectedId = (int) id;
                this.mAdapter = 10;
                TrackDisplay();
                this.mPlaylistId = null;
                return;
            case 5:
            case 6:
            case 7:
            case 8:
            default:
                return;
            case 9:
                changedisplay(false);
                this.position_now = this.savearray.get(position).position;
                this.mTrackCursor = this.savearray.get(position).cur;
                LOG("mTrackCursor.getCount()= " + this.mTrackCursor.getCount());
                LOG("position_now = " + this.position_now);
                this.mBindHandler.postDelayed(this.mBindRun, 10L);
                return;
            case 10:
                this.position_now = position;
                changedisplay(false);
                if (this.mTrackCursor.getCount() == 0) {
                    LOG("---------------- in onListItemClick  return");
                    return;
                }
                this.mPlaylistId = null;
                this.mBindHandler.postDelayed(this.mBindRun, 10L);
                return;
            case 11:
                Resources resources = getResources();
                TrackInfo tep = this.mTrackAdapterForPlaylist.getItem(position);
                int songid = tep.id;
                if (!MusicUtils.IsTrackAlreadyIn(this, songid, this.SelectPlaylistId)) {
                    this.mTrackAdapterForPlaylist.getItem(position).icon = resources.getDrawable(R.drawable.playlist_trackchoice_click);
                    int[] list = {songid};
                    this.mTrackAdapterForPlaylist.notifyDataSetChanged();
                    MusicUtils.addToPlaylist(this, list, this.SelectPlaylistId);
                    v.clearFocus();
                    return;
                }
                this.mTrackAdapterForPlaylist.getItem(position).icon = resources.getDrawable(R.drawable.playlist_trackchoice_notclick);
                this.mTrackAdapterForPlaylist.notifyDataSetChanged();
                MusicUtils.deleteFromPlaylist(this, songid, this.SelectPlaylistId);
                v.clearFocus();
                return;
        }
    }

    private int getSelectSongId(ListView l, View v, int position) {
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
        if (c == null && position > c.getCount()) {
            return -1;
        }
        c.move(position);
        int SongId = c.getInt(c.getColumnIndex("_id"));
        LOG("SongId = " + SongId);
        return SongId;
    }

    /* loaded from: classes.dex */
    public class NowPlayingCursor extends AbstractCursor {
        private String[] mCols;
        private int mCurPos;
        private Cursor mCurrentPlaylistCursor;
        private int[] mCursorIdxs;
        private int[] mNowPlaying;
        private IAudioPlaybackService mService;
        private int mSize;

        public NowPlayingCursor(IAudioPlaybackService service, String[] cols) {
            this.mCols = cols;
            this.mService = service;
            makeNowPlayingCursor();
        }

        private void makeNowPlayingCursor() {
            this.mCurrentPlaylistCursor = null;
            try {
                this.mNowPlaying = this.mService.getQueue();
            } catch (RemoteException e) {
                this.mNowPlaying = new int[0];
            }
            this.mSize = this.mNowPlaying.length;
            if (this.mSize != 0) {
                StringBuilder where = new StringBuilder();
                where.append("_id IN (");
                for (int i = 0; i < this.mSize; i++) {
                    where.append(this.mNowPlaying[i]);
                    if (i < this.mSize - 1) {
                        where.append(",");
                    }
                }
                where.append(")");
                this.mCurrentPlaylistCursor = MusicUtils.query(RockAudioPlayer.this, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.mCols, where.toString(), null, "_id");
                if (this.mCurrentPlaylistCursor == null) {
                    this.mSize = 0;
                    return;
                }
                int size = this.mCurrentPlaylistCursor.getCount();
                this.mCursorIdxs = new int[size];
                this.mCurrentPlaylistCursor.moveToFirst();
                int colidx = this.mCurrentPlaylistCursor.getColumnIndexOrThrow("_id");
                for (int i2 = 0; i2 < size; i2++) {
                    this.mCursorIdxs[i2] = this.mCurrentPlaylistCursor.getInt(colidx);
                    this.mCurrentPlaylistCursor.moveToNext();
                }
                this.mCurrentPlaylistCursor.moveToFirst();
                this.mCurPos = -1;
                int removed = 0;
                try {
                    for (int i3 = this.mNowPlaying.length - 1; i3 >= 0; i3--) {
                        int trackid = this.mNowPlaying[i3];
                        int crsridx = Arrays.binarySearch(this.mCursorIdxs, trackid);
                        if (crsridx < 0) {
                            removed += this.mService.removeTrack(trackid);
                        }
                    }
                    if (removed > 0) {
                        this.mNowPlaying = this.mService.getQueue();
                        this.mSize = this.mNowPlaying.length;
                        if (this.mSize == 0) {
                            this.mCursorIdxs = null;
                        }
                    }
                } catch (RemoteException e2) {
                    this.mNowPlaying = new int[0];
                }
            }
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public int getCount() {
            return this.mSize;
        }

        @Override // android.database.AbstractCursor, android.database.CrossProcessCursor
        public boolean onMove(int oldPosition, int newPosition) {
            if (oldPosition == newPosition) {
                return true;
            }
            if (this.mNowPlaying == null || this.mCursorIdxs == null) {
                return false;
            }
            int newid = this.mNowPlaying[newPosition];
            int crsridx = Arrays.binarySearch(this.mCursorIdxs, newid);
            this.mCurrentPlaylistCursor.moveToPosition(crsridx);
            this.mCurPos = newPosition;
            return true;
        }

        public boolean removeItem(int which) {
            try {
                if (this.mService.removeTracks(which, which) == 0) {
                    return false;
                }
            } catch (RemoteException e) {
                return false;
            }
            this.mSize--;
            for (int i = which; i < this.mSize; i++) {
                this.mNowPlaying[i] = this.mNowPlaying[i + 1];
            }
            onMove(-1, this.mCurPos);
            return true;
        }

        public void moveItem(int from, int to) {
            try {
                this.mService.moveQueueItem(from, to);
                this.mNowPlaying = this.mService.getQueue();
                onMove(-1, this.mCurPos);
            } catch (RemoteException e) {
            }
        }

        private void dump() {
            String where = "(";
            for (int i = 0; i < this.mSize; i++) {
                where = where + this.mNowPlaying[i];
                if (i < this.mSize - 1) {
                    where = where + ",";
                }
            }
            RockAudioPlayer.this.LOG("NowPlayingCursor: " + (where + ")"));
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public String getString(int column) {
            try {
                return this.mCurrentPlaylistCursor.getString(column);
            } catch (Exception e) {
                onChange(true);
                return "";
            }
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public short getShort(int column) {
            return this.mCurrentPlaylistCursor.getShort(column);
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public int getInt(int column) {
            try {
                return this.mCurrentPlaylistCursor.getInt(column);
            } catch (Exception e) {
                onChange(true);
                return 0;
            }
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public long getLong(int column) {
            try {
                return this.mCurrentPlaylistCursor.getLong(column);
            } catch (Exception e) {
                onChange(true);
                return 0L;
            }
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public float getFloat(int column) {
            return this.mCurrentPlaylistCursor.getFloat(column);
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public double getDouble(int column) {
            return this.mCurrentPlaylistCursor.getDouble(column);
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public boolean isNull(int column) {
            return this.mCurrentPlaylistCursor.isNull(column);
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public String[] getColumnNames() {
            return this.mCols;
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public void deactivate() {
            if (this.mCurrentPlaylistCursor != null) {
                this.mCurrentPlaylistCursor.deactivate();
            }
        }

        @Override // android.database.AbstractCursor, android.database.Cursor
        public boolean requery() {
            makeNowPlayingCursor();
            return true;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x00e4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    boolean searchfile(java.lang.String r23) {
        /*
            Method dump skipped, instructions count: 259
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.rk.RockAudioPlayer.RockAudioPlayer.searchfile(java.lang.String):boolean");
    }

    public void hidesearchview() {
        View searchView = findViewById(R.id.search_layout);
        if (searchView.getVisibility() == 0) {
            Animation myAnimation_out = new ScaleAnimation(1.1f, 0.0f, 1.1f, 0.0f, 1, 0.5f, 1, 0.5f);
            searchView.setAnimation(myAnimation_out);
            myAnimation_out.setDuration(400L);
            searchView.setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPauseButtonImage() {
        try {
            if (this.mDisplayService == null || !this.mDisplayService.isPlaying()) {
                this.mPauseButton.setImageResource(R.drawable.play_play_btu);
                MusicUtils.SetTitilePause(false);
                if (this.lyric_layout.getVisibility() == 0) {
                    this.lyricmanager.pause(true);
                }
            } else {
                this.mPauseButton.setImageResource(R.drawable.play_pause_btu);
                if (!this.manimation) {
                }
                MusicUtils.SetTitilePause(true);
                if (this.lyric_layout.getVisibility() == 0) {
                    this.lyricmanager.pause(false);
                }
            }
        } catch (RemoteException e) {
        }
    }

    public void setTitleself(String tmp_title) {
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                ProgressDialog dialog_p = new ProgressDialog(this);
                dialog_p.setMessage("Please wait while loading...");
                dialog_p.setIndeterminate(true);
                dialog_p.setCancelable(true);
                return dialog_p;
            case 1:
            case 2:
            case 4:
            default:
                return null;
            case 3:
                return new AlertDialog.Builder(this).setTitle(R.string.playfxstr).setSingleChoiceItems(R.array.playfxarray, 0, new DialogInterface.OnClickListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.10
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).create();
            case 5:
                final Cursor c = getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null, null, "_id");
                int leng = c.getCount();
                String[] items = new String[leng];
                if (!c.moveToFirst()) {
                    return new AlertDialog.Builder(this).setTitle(R.string.displayliststr).setItems(items, new DialogInterface.OnClickListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.11
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            c.moveToFirst();
                            if (c.moveToPosition(which)) {
                                int PlaylistId = c.getInt(c.getColumnIndex("_id"));
                                int playAudioId = 0;
                                try {
                                    playAudioId = RockAudioPlayer.this.mDisplayService.getAudioId();
                                    RockAudioPlayer.this.LOG("playAudioId = " + playAudioId);
                                } catch (RemoteException e) {
                                    RockAudioPlayer.this.LOG("Add to List error, list = " + c.getString(c.getColumnIndex("name")));
                                }
                                if (playAudioId != -1) {
                                    int[] list = {playAudioId};
                                    MusicUtils.addToPlaylist(RockAudioPlayer.this, list, PlaylistId);
                                }
                            }
                        }
                    }).create();
                }
                int i2 = 0;
                do {
                    items[i2] = c.getString(c.getColumnIndex("name"));
                    i2++;
                } while (c.moveToNext());
                return new AlertDialog.Builder(this).setTitle(R.string.displayliststr).setItems(items, new DialogInterface.OnClickListener() { // from class: android.rk.RockAudioPlayer.RockAudioPlayer.11
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        c.moveToFirst();
                        if (c.moveToPosition(which)) {
                            int PlaylistId = c.getInt(c.getColumnIndex("_id"));
                            int playAudioId = 0;
                            try {
                                playAudioId = RockAudioPlayer.this.mDisplayService.getAudioId();
                                RockAudioPlayer.this.LOG("playAudioId = " + playAudioId);
                            } catch (RemoteException e) {
                                RockAudioPlayer.this.LOG("Add to List error, list = " + c.getString(c.getColumnIndex("name")));
                            }
                            if (playAudioId != -1) {
                                int[] list = {playAudioId};
                                MusicUtils.addToPlaylist(RockAudioPlayer.this, list, PlaylistId);
                            }
                        }
                    }
                }).create();
        }
    }

    public void changedisplay(boolean fir) {
        LOG("---changedisplay ");
        View audioInfo_tmp = findViewById(R.id.audio_info);
        if (fir) {
            displayonStop();
            this.play_display_sec.setVisibility(4);
            this.play_display_fir.setVisibility(0);
            return;
        }
        try {
            if ("nowplaying".equals(this.mPlaylistId)) {
                unregisterReceiverSafe(this.mNowPlayingListener);
            } else {
                unregisterReceiverSafe(this.mTrackListListener);
            }
        } catch (IllegalArgumentException e) {
        }
        unregisterReceiverSafe(this.mScanListener);
        this.play_display_fir.setVisibility(4);
        this.play_display_sec.setVisibility(0);
        bindService();
        if (MusicUtils.getisLyric()) {
            this.lyric_layout.setVisibility(0);
            audioInfo_tmp.setVisibility(4);
            this.lyricmanager.pause(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void AudioABStop() {
        MusicUtils.setABRequire(0);
    }

    private void AudioSetAB_A() {
        try {
            MusicUtils.setABRequire(1);
            MusicUtils.setABStart(this.mDisplayService.position());
        } catch (RemoteException e) {
        }
    }

    private void SetAudioABImageButton() {
        try {
            if (this.mDisplayService != null && this.mDisplayService.isPlaying()) {
                if (MusicUtils.getABRequire() == 0) {
                    AudioSetAB_A();
                } else if (1 == MusicUtils.getABRequire()) {
                    MusicUtils.setABEnd(this.mDisplayService.position());
                    LOG("AudioABEnd" + AudioABEnd);
                    MusicUtils.setABRequire(2);
                } else {
                    AudioABStop();
                }
                this.AB_mode.setImageResource(R.drawable.audioab00 + MusicUtils.getABRequire());
            }
        } catch (RemoteException e) {
        }
    }

    private void SetAudioRepeatModeImageButton() {
        if (this.mDisplayService != null) {
            try {
                int mode = this.mDisplayService.getRepeatMode() + 1;
                if (mode > 4) {
                    mode = 0;
                }
                this.mDisplayService.setRepeatMode(mode);
                this.SuffleModeImage.setImageResource(R.drawable.shuffle_01 + mode);
            } catch (RemoteException e) {
            }
        }
    }

    protected void showToast() {
        View view = inflateView(R.layout.aboutlayout);
        Toast toast = new Toast(this);
        toast.setView(view);
        toast.setDuration(1);
        if (this.ShowAboutFlag) {
            this.ShowAboutFlag = false;
            toast.show();
            this.ShowAboutHandler.postDelayed(this.ShowAboutRunnable, 4000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void AudioSetVolume(boolean flag) {
        this.Volume_view.setVisibility(0);
        this.myProgress.setProgress(this.volume);
        if (flag) {
            this.audioMa.adjustStreamVolume(3, 1, 0);
        } else {
            this.audioMa.adjustStreamVolume(3, -1, 0);
        }
        this.volume = this.audioMa.getStreamVolume(3);
        LOG("volume" + this.volume);
        this.myProgress.setProgress(this.volume);
    }

    private View inflateView(int resource) {
        LayoutInflater vi = (LayoutInflater) getSystemService("layout_inflater");
        return vi.inflate(resource, (ViewGroup) null);
    }

    public void inAnimation(boolean type) {
        Animation mHandleInAnimation;
        if (type) {
            mHandleInAnimation = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
        } else {
            mHandleInAnimation = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
        }
        View main_view = findViewById(R.id.play_main_layout);
        main_view.setAnimation(mHandleInAnimation);
    }

    public Context getcontext() {
        return this;
    }

    public void setlyrictextview() {
        this.list_textview = new ArrayList<TextView>();
        TextView temp = (TextView) findViewById(R.id.lyric_1);
        this.list_textview.add(temp);
        TextView temp2 = (TextView) findViewById(R.id.lyric_2);
        this.list_textview.add(temp2);
        TextView temp3 = (TextView) findViewById(R.id.lyric_3);
        this.list_textview.add(temp3);
        TextView temp4 = (TextView) findViewById(R.id.lyric_4);
        this.list_textview.add(temp4);
        TextView temp5 = (TextView) findViewById(R.id.lyric_5);
        this.list_textview.add(temp5);
        TextView temp6 = (TextView) findViewById(R.id.lyric_6);
        this.list_textview.add(temp6);
    }

    public void lyriccontrol(String artist_lyr, String songname, String displayname, String song_dir) {
        this.lyricmanager.showDialogshow();
        View audioInfo_tmp = findViewById(R.id.audio_info);
        if (MusicUtils.getisLyric()) {
            this.lyric_layout.setVisibility(0);
            audioInfo_tmp.setVisibility(4);
        }
        this.textview_lyric.setText(getString(R.string.lyric));
        this.textview_lyric.setText(getString(R.string.lyric_search_false));
        Log.d("TAG", "lyriccontrol is now ");
        this.lyricmanager.setSongInfo(songname, displayname, song_dir);
        this.lyricmanager.loadLRC();
    }

    public void WhetherDisplayLrc() {
        View audioInfo_tmp = findViewById(R.id.audio_info);
        if (!MusicUtils.getisLyric()) {
            this.lyric_layout.setVisibility(0);
            audioInfo_tmp.setVisibility(4);
            Log.d("TAG", "the MusicUtils.getisLyric() == false");
            getLrc();
            MusicUtils.setisLyric(true);
            this.lyricmanager.pause(false);
            return;
        }
        this.lyric_layout.setVisibility(4);
        audioInfo_tmp.setVisibility(0);
        MusicUtils.setisLyric(false);
        this.lyricmanager.pause(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long refreshNow() {
        if (this.mDisplayService == null) {
            return 500L;
        }
        try {
            long pos = this.mPosOverride < 0 ? this.mDisplayService.position() : this.mPosOverride;
            long remaining = 1000 - (pos % 1000);
            if (this.mDuration < 0) {
                this.mDuration = this.mDisplayService.duration();
                this.mTotalTime.setText(MusicUtils.makeTimeString(this, this.mDuration / 1000));
            }
            if (pos < 0 || this.mDuration <= 0) {
                this.mCurrentTime.setText("00:00:00");
                this.seekBar.setProgress(0);
                setPauseButtonImage();
            } else {
                if (pos >= this.mDuration) {
                    pos = this.mDuration;
                }
                this.mCurrentTime.setText(MusicUtils.makeTimeString(this, pos / 1000));
                if (this.mDisplayService.isPlaying()) {
                    this.mCurrentTime.setVisibility(0);
                } else {
                    int vis = this.mCurrentTime.getVisibility();
                    this.mCurrentTime.setVisibility(vis == 4 ? 0 : 4);
                    remaining = 500;
                    setPauseButtonImage();
                }
                this.seekBar.setProgress((int) ((1000 * pos) / this.mDuration));
            }
            return remaining;
        } catch (RemoteException e) {
            LOG("refreshNow wrong");
            return 500L;
        }
    }

    private void doPauseResume() {
        try {
            if (this.mDisplayService != null) {
                if (this.mDisplayService.isPlaying()) {
                    this.mDisplayService.pause();
                } else {
                    this.mDisplayService.play();
                }
                refreshNow();
                setPauseButtonImage();
            }
        } catch (RemoteException e) {
        }
    }

    public void playonCreat(Bundle savedInstanceState) {
        LOG("playonCreat");
        this.fir_start_display_ser = true;
        this.lyric_layout = findViewById(R.id.lyric);
        this.textview_lyric = (TextView) findViewById(R.id.lyric_5);
        setlyrictextview();
        this.lyricmanager = new LyricManager(this, this.list_textview);
        setVolumeControlStream(3);
        this.AB_mode = (ImageView) findViewById(R.id.A_B_mode);
        this.ArtistImage = (ImageView) findViewById(R.id.artist_icon);
        this.AlbumImage = (ImageView) findViewById(R.id.Album_icon);
        this.songnums = (TextView) findViewById(R.id.song_num);
        this.bitrate = (TextView) findViewById(R.id.bitrate);
        this.samplingrate = (TextView) findViewById(R.id.sampling_rate);
        this.songtitlestring = (TextView) findViewById(R.id.song_title);
        this.albumstring = (TextView) findViewById(R.id.album_name);
        this.artiststring = (TextView) findViewById(R.id.artist_name);
        this.mCurrentTime = (TextView) findViewById(R.id.currenttime);
        this.mTotalTime = (TextView) findViewById(R.id.totaltime);
        this.Volume_view = findViewById(R.id.volumegrogress);
        this.audioMa = (AudioManager) getSystemService("audio");
        this.myProgress = (ProgressBar) findViewById(R.id.myProgress);
        this.volume = this.audioMa.getStreamVolume(3);
        this.song_info = findViewById(R.id.audio_info);
        this.song_info.setOnLongClickListener(this);
        this.mPrevButton = (RepeatingImageButton) findViewById(R.id.pre_button);
        this.mPrevButton.setRepeatListener(this.mRewListener, 260L);
        this.mVol1Button = (RepeatingImageButton) findViewById(R.id.vol1_button);
        this.mVol1Button.setRepeatListener(this.mVol1Listener, 260L);
        this.mVol2Button = (RepeatingImageButton) findViewById(R.id.vol2_button);
        this.mVol2Button.setRepeatListener(this.mVol2Listener, 260L);
        this.mPauseButton = (ImageView) findViewById(R.id.play_button);
        this.mNextButton = (RepeatingImageButton) findViewById(R.id.next_button);
        this.mNextButton.setRepeatListener(this.mFfwdListener, 260L);
        this.mQueueButton = (ImageView) findViewById(R.id.addtoplaylist_button);
        this.mShuffleButton = (ImageView) findViewById(R.id.shuffle_button);
        this.mRepeatButton = (ImageView) findViewById(R.id.abrepeat_button);
        this.mLrcImageButton = (ImageView) findViewById(R.id.settings_button);
        this.DiskPlayImageButton = (ImageButton) findViewById(R.id.Disk_Spectrum);
        this.DiskPlayImageButton.setBackgroundResource(R.drawable.disk);
        Resources r = getResources();
        Bitmap b = BitmapFactory.decodeResource(r, R.drawable.disk_1);
        this.DefaultAlbumIcon = new BitmapDrawable(b);
        this.DefaultAlbumIcon.setFilterBitmap(false);
        this.DefaultAlbumIcon.setDither(false);
        this.seekBar = (SeekBar) findViewById(R.id.timebar);
        this.seekBar.setOnSeekBarChangeListener(this.mSeekListener);
        this.seekBar.setMax(1000);
        if (savedInstanceState != null) {
            Log.d("TAG", "the savedInstanceState is not null 546+465");
            this.mRelaunchAfterConfigChange = savedInstanceState.getBoolean("configchange");
            this.mOneShot = savedInstanceState.getBoolean("oneshot");
        } else {
            Log.d("TAG", "the savedInstanceState is  null 121321231");
        }
        LOG("playonCreat mOneShot = " + this.mOneShot);
        this.mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
    }

    TextView textViewForContainer(View v) {
        View vv = v.findViewById(R.id.artist_name);
        if (vv != null) {
            return (TextView) vv;
        }
        View vv2 = v.findViewById(R.id.album_name);
        if (vv2 != null) {
            return (TextView) vv2;
        }
        return null;
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        TextView tv = textViewForContainer(v);
        if (tv == null) {
            return false;
        }
        if (action == 0) {
            v.setBackgroundColor(-10461088);
            int x = (int) event.getX();
            this.mLastX = x;
            this.mInitialX = x;
            this.mDraggingLabel = false;
        } else if (action == 1 || action == 3) {
            v.setBackgroundColor(0);
            if (this.mDraggingLabel) {
                Message msg = this.mLabelScroller.obtainMessage(0, tv);
                this.mLabelScroller.sendMessageDelayed(msg, 1000L);
            }
        } else if (action == 2) {
            if (this.mDraggingLabel) {
                int scrollx = tv.getScrollX();
                int x2 = (int) event.getX();
                int delta = this.mLastX - x2;
                if (delta != 0) {
                    this.mLastX = x2;
                    int scrollx2 = scrollx + delta;
                    if (scrollx2 > this.mTextWidth) {
                        scrollx2 = (scrollx2 - this.mTextWidth) - this.mViewWidth;
                    }
                    if (scrollx2 < (-this.mViewWidth)) {
                        scrollx2 = scrollx2 + this.mViewWidth + this.mTextWidth;
                    }
                    tv.scrollTo(scrollx2, 0);
                }
                return true;
            } else if (Math.abs(this.mInitialX - ((int) event.getX())) > this.mTouchSlop) {
                this.mLabelScroller.removeMessages(0, tv);
                if (tv.getEllipsize() != null) {
                    tv.setEllipsize(null);
                }
                Layout ll = tv.getLayout();
                if (ll == null) {
                    return false;
                }
                this.mTextWidth = (int) tv.getLayout().getLineWidth(0);
                this.mViewWidth = tv.getWidth();
                if (this.mViewWidth > this.mTextWidth) {
                    tv.setEllipsize(TextUtils.TruncateAt.END);
                    v.cancelLongPress();
                    return false;
                }
                this.mDraggingLabel = true;
                tv.setHorizontalFadingEdgeEnabled(true);
                v.cancelLongPress();
                return true;
            }
        }
        return false;
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanBackward(int repcnt, long delta) {
        long delta2;
        if (this.mDisplayService != null) {
            try {
                if (repcnt == 0) {
                    this.mStartSeekPos = this.mDisplayService.position();
                    this.mLastSeekEventTime = 0L;
                    this.mSeeking = false;
                    return;
                }
                this.mSeeking = true;
                if (delta < 5000) {
                    delta2 = delta * 10;
                } else {
                    delta2 = 50000 + ((delta - 5000) * 40);
                }
                long newpos = this.mStartSeekPos - delta2;
                if (newpos < 0) {
                    this.mDisplayService.prev();
                    if (!this.mOneShot) {
                        this.mDisplayService.opennext();
                        updateTrackInfo();
                        setPauseButtonImage();
                        queueNextRefresh(1L);
                    }
                    long duration = this.mDisplayService.duration();
                    this.mStartSeekPos += duration;
                    newpos += duration;
                }
                if (delta2 - this.mLastSeekEventTime > 250 || repcnt < 0) {
                    LOG("newpos" + newpos);
                    this.mDisplayService.seek(newpos);
                    this.mLastSeekEventTime = delta2;
                }
                if (repcnt >= 0) {
                    this.mPosOverride = newpos;
                } else {
                    this.mPosOverride = -1L;
                }
                refreshNow();
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scanForward(int repcnt, long delta) {
        long delta2;
        if (this.mDisplayService != null) {
            try {
                if (repcnt == 0) {
                    this.mStartSeekPos = this.mDisplayService.position();
                    this.mLastSeekEventTime = 0L;
                    this.mSeeking = false;
                    return;
                }
                this.mSeeking = true;
                if (delta < 5000) {
                    delta2 = delta * 10;
                } else {
                    delta2 = 50000 + ((delta - 5000) * 40);
                }
                long newpos = this.mStartSeekPos + delta2;
                long duration = this.mDisplayService.duration();
                if (newpos >= duration) {
                    this.mDisplayService.next();
                    if (!this.mOneShot) {
                        this.mDisplayService.opennext();
                        updateTrackInfo();
                        setPauseButtonImage();
                        queueNextRefresh(1L);
                    }
                    this.mStartSeekPos -= duration;
                    newpos -= duration;
                }
                if (delta2 - this.mLastSeekEventTime > 250 || repcnt < 0) {
                    this.mDisplayService.seek(newpos);
                    this.mLastSeekEventTime = delta2;
                }
                if (repcnt >= 0) {
                    this.mPosOverride = newpos;
                } else {
                    this.mPosOverride = -1L;
                }
                refreshNow();
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public void displayonStop() {
        this.paused = true;
        this.mHandler.removeMessages(1);
        unregisterReceiver(this.mStatusListener);
        this.lyricmanager.rmovePost();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void AudioRecentAdd() {
        int[] listsong = new int[1];
        try {
            listsong[0] = this.mDisplayService.getAudioId();
            Resources res = getResources();
            String LastPlayedName = res.getString(R.string.recentlyadded);
            long playlistid = MusicUtils.getPlaylistId(this, LastPlayedName);
            LOG("playlistid = " + playlistid);
            LOG("SongsInPlaylist = " + MusicUtils.getSongnumInPlaylist(this, (int) playlistid));
            if (MusicUtils.getSongnumInPlaylist(this, (int) playlistid) < 20) {
                MusicUtils.addToHeadNoDelete(this, listsong, playlistid);
            } else {
                MusicUtils.addToHead(this, listsong, playlistid);
            }
            this.mRecentListHander.removeCallbacks(this.mRecentListRunnable);
        } catch (RemoteException e) {
        }
    }
}
