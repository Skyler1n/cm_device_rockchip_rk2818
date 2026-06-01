package android.rk.RockVideoPlayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.rk.RockVideoPlayer.DBUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/* loaded from: classes.dex */
public class RockVideoPlayer extends ListActivity implements View.OnCreateContextMenuListener, DBUtils.Def {
    private static final boolean DEBUG = true;
    static final int DIALOG_DELETE_CHOICE = 1;
    static final int DIALOG_DELETE_CONFIRM = 2;
    private static final int PlayDone = 1;
    private static final String TAG = "RockVideoPlayer";
    private View MainView;
    public int mBrightMode;
    private String mCurrentVideoFilename;
    private Uri mCurrentVideoUri;
    private Dialog mMediaScanningDialog;
    int mOldBrightness;
    private BroadcastReceiver mReceiver;
    private String mSortOrder;
    private Uri mUri;
    public Cursor mVideoCursor;
    private ListView mVideoList;
    private VideoListAdapter mVideoListAdapter;
    public int mVolumeMode;
    private ProgressDialog pd;
    int screenOn;
    int timeoutmode;
    private String[] mCols = {"_id", "title", "_display_name", "duration", "mime_type", "_size", "bookmark", "_data"};
    private int mLastPosition = 0;
    boolean mForbidenClick = false;
    public final int mSysBright = 1;
    public final int mUserBright = 2;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.3
        @Override // android.widget.AdapterView.OnItemLongClickListener
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur;
            String videoid = Long.valueOf(id).toString();
            RockVideoPlayer.this.mCurrentVideoUri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoid);
            if (!(RockVideoPlayer.this.mCurrentVideoUri == null || (cur = DBUtils.getCurrentCursor(RockVideoPlayer.this, RockVideoPlayer.this.mCurrentVideoUri)) == null)) {
                RockVideoPlayer.this.mCurrentVideoFilename = cur.getString(cur.getColumnIndexOrThrow("_data"));
                cur.close();
                RockVideoPlayer.this.showDialog(1);
                return true;
            }
            return false;
        }
    };
    Handler mHandler = new Handler();
    Runnable deleteCallback = new Runnable() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.7
        @Override // java.lang.Runnable
        public void run() {
            DBUtils.deleteCurrentVideo(RockVideoPlayer.this, RockVideoPlayer.this.mCurrentVideoUri);
            DBUtils.deleteViedoFile(RockVideoPlayer.this, RockVideoPlayer.this.mCurrentVideoFilename);
            RockVideoPlayer.this.VideoDisplayVisible();
        }
    };
    private BroadcastReceiver mVideoListListener = new BroadcastReceiver() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.8
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            RockVideoPlayer.this.getListView().invalidateViews();
        }
    };
    private Handler mReScanHandler = new Handler() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.9
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            RockVideoPlayer.this.LOG("mReScanHandler  mTrackCursor / mVideoListAdapter = " + RockVideoPlayer.this.mVideoCursor + " / " + RockVideoPlayer.this.mVideoListAdapter);
            RockVideoPlayer.this.getVideoCursor(RockVideoPlayer.this.mVideoListAdapter.getQueryHandler());
        }
    };
    private Context mContext = this;

    public void LOG(String msg) {
        Log.d(TAG, msg);
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_display_land);
        this.MainView = findViewById(R.id.main_layout);
        this.mBrightMode = 2;
        this.mVolumeMode = 2;
    }

    public void VideoDisplayVisible() {
        LOG("Begin to setListAdapter");
        VideoDisplay();
        this.mForbidenClick = false;
        getListView().setSelection(this.mLastPosition);
        getListView().setOnItemLongClickListener(this.mOnItemLongClickListener);
    }

    public void VideoDisplayVInVisible() {
        this.MainView.setBackgroundResource(R.drawable.novideofile);
    }

    @Override // android.app.Activity
    public void onResume() {
        LOG("Enter onResume()");
        try {
            dismissDialog(1);
        } catch (Exception e) {
        }
        try {
            dismissDialog(2);
        } catch (Exception e2) {
        }
        super.onResume();
        String Dilog_tile = getResources().getString(R.string.load_title);
        String Dilog_wait = getResources().getString(R.string.wait);
        this.pd = ProgressDialog.show(this, Dilog_tile, Dilog_wait, true, false);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addDataScheme("file");
        this.mReceiver = new BroadcastReceiver() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                RockVideoPlayer.this.LOG("mReceiver.onReceive() : action = " + action);
                if (!action.equals("android.intent.action.MEDIA_MOUNTED")) {
                    if (action.equals("android.intent.action.MEDIA_EJECT") || action.equals("android.intent.action.MEDIA_UNMOUNTED")) {
                        RockVideoPlayer.this.finish();
                    } else if (action.equals("android.intent.action.MEDIA_SCANNER_STARTED") || action.equals("android.intent.action.MEDIA_SCANNER_FINISHED")) {
                        RockVideoPlayer.this.mReScanHandler.sendEmptyMessage(0);
                    }
                }
            }
        };
        registerReceiver(this.mReceiver, new IntentFilter(intentFilter));
        VideoDisplayVisible();
    }

    public void updataAdapter() {
        LOG("Enter updataAdapter()");
        LOG("mVideoListAdapter = " + this.mVideoListAdapter);
        setVideoDisplayViewBackground();
        setListAdapter(this.mVideoListAdapter);
    }

    public void setVideoDisplayViewBackground() {
        LOG("Enter setVideoDisplayViewBackground() ");
        if (this.mVideoListAdapter == null) {
            this.MainView.setBackgroundResource(R.drawable.novideofile);
        } else if (this.mVideoCursor == null) {
            this.MainView.setBackgroundResource(R.drawable.novideofile);
        } else if (this.mVideoCursor.moveToFirst() && this.mVideoCursor.getCount() != 0) {
            this.MainView.setBackgroundResource(R.color.black);
            getListView().setSelection(this.mLastPosition);
            getListView().setOnItemLongClickListener(this.mOnItemLongClickListener);
        } else if (!this.mVideoCursor.moveToFirst()) {
            this.MainView.setBackgroundResource(R.drawable.novideofile);
        } else {
            this.MainView.setBackgroundResource(R.drawable.novideofile);
        }
    }

    @Override // android.app.Activity
    public void onRestart() {
        LOG("Enter onRestart()");
        super.onRestart();
        getListView().setSelection(this.mLastPosition);
        super.onRestart();
    }

    /* JADX WARN: Type inference failed for: r1v7, types: [android.rk.RockVideoPlayer.RockVideoPlayer$2] */
    @Override // android.app.Activity
    public void onPause() {
        LOG("Enter onPause()");
        super.onPause();
        Uri uri = this.mUri;
        this.mReScanHandler.removeCallbacksAndMessages(null);
        unregisterReceiverSafe(this.mReceiver);
        try {
            dismissDialog(1);
        } catch (Exception e) {
        }
        try {
            dismissDialog(2);
        } catch (Exception e2) {
        }
        new Thread() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                System.gc();
            }
        }.start();
    }

    @Override // android.app.Activity
    public void onStop() {
        super.onStop();
        recoverStatus();
    }

    private void recoverStatus() {
        unregisterReceiverSafe(this.mReceiver);
    }

    @Override // android.app.ListActivity, android.app.Activity
    public void onDestroy() {
        LOG("Enter onDestroy()");
        super.onDestroy();
        recoverStatus();
        System.exit(0);
    }

    private void unregisterReceiverSafe(BroadcastReceiver receiver) {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
        }
    }

    public void VideoDisplay() {
        LOG("Enter VideoDisplay()");
        this.mVideoList = getListView();
        if (this.mVideoListAdapter == null) {
            this.mVideoListAdapter = new VideoListAdapter(this, this, R.layout.video_item_land, null, new String[0], new int[0]);
            LOG("mVideoListAdapter = " + this.mVideoListAdapter);
            this.mVideoListAdapter.setActivity(this);
            setListAdapter(this.mVideoListAdapter);
            getVideoCursor(this.mVideoListAdapter.getQueryHandler());
            return;
        }
        this.mVideoListAdapter.setActivity(this);
        this.mVideoCursor = this.mVideoListAdapter.getCursor();
        LOG("mVideoCursor = " + this.mVideoCursor);
        getVideoCursor(this.mVideoListAdapter.getQueryHandler());
        LOG("mVideoCursor2 = " + this.mVideoCursor);
        setListAdapter(this.mVideoListAdapter);
        setVideoDisplayViewBackground();
    }

    @Override // android.app.ListActivity
    protected void onListItemClick(ListView l, View v, int position, long id) {
        LOG("onListItemClick:mForbidenClick = " + this.mForbidenClick);
        if (!this.mForbidenClick) {
            this.mLastPosition = position;
            String videoid = Long.valueOf(id).toString();
            Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoid);
            if (uri != null) {
                Cursor cur = DBUtils.getCurrentCursor(this, uri);
                if (cur == null) {
                    Log.e(TAG, "onListItemClick() : Failed to Cursor for content uri : " + cur);
                    return;
                }
                String tepMimetype = cur.getString(cur.getColumnIndexOrThrow("mime_type"));
                if (DBUtils.checkVideoAvailable(this, cur)) {
                    cur.close();
                    Intent intent = new Intent("android.intent.action.VIEW", uri);
                    intent.setClass(this, VideoPlayActivity.class);
                    intent.putExtra("mediaTypes", tepMimetype);
                    startActivity(intent);
                    this.mForbidenClick = true;
                    return;
                }
                Toast.makeText(this, (int) R.string.alert_checkfile, 0).show();
            }
        }
    }

    private void startActivityForresult(Intent intent) {
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                String str = getResources().getString(R.string.delete_delete);
                String str2 = getResources().getString(R.string.delete_file_title);
                CharSequence[] items = {str};
                return new AlertDialog.Builder(this).setTitle(str2).setItems(items, new DialogInterface.OnClickListener() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.4
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int item) {
                        RockVideoPlayer.this.showDialog(2);
                    }
                }).create();
            case 2:
                return new AlertDialog.Builder(this).setMessage(R.string.delete_confirm).setCancelable(false).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.6
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int id2) {
                        if (DBUtils.isMediaScannerScanning(RockVideoPlayer.this.mContext)) {
                            Toast.makeText(RockVideoPlayer.this.mContext, (int) R.string.toast_isScanning, 0).show();
                        } else {
                            RockVideoPlayer.this.mHandler.post(RockVideoPlayer.this.deleteCallback);
                        }
                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() { // from class: android.rk.RockVideoPlayer.RockVideoPlayer.5
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int id2) {
                        dialog.cancel();
                    }
                }).create();
            default:
                return null;
        }
    }

    public Cursor getVideoCursor(AsyncQueryHandler async) {
        LOG("Enter getVideoCursor()");
        Cursor ret = null;
        this.mSortOrder = "_id";
        StringBuilder where = new StringBuilder();
        where.append("_id != ''");
        where.append(" AND mime_type NOT LIKE 'audio%'");
        if (async != null) {
            LOG("getVideoCursor() : startQuery()");
            async.startQuery(0, null, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this.mCols, where.toString(), null, this.mSortOrder);
        } else {
            ret = DBUtils.query(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, this.mCols, where.toString(), null, this.mSortOrder);
        }
        LOG("ret/async = " + ret + "/" + async);
        if (!(ret == null || async == null)) {
            LOG("getVideoCursor:initVideoCursor()");
            initVideoCursor(ret);
        }
        return ret;
    }

    public void initVideoCursor(Cursor newCursor) {
        LOG("Enter initVideoCursor() and newCursor = " + newCursor);
        this.mVideoListAdapter.changeCursor(newCursor);
        LOG("mVideoCursor = " + this.mVideoCursor + "newCursor = " + newCursor);
        if (this.mVideoCursor == null) {
            this.pd.dismiss();
            VideoDisplayVInVisible();
            this.mReScanHandler.sendEmptyMessageDelayed(0, 1000L);
            return;
        }
        LOG("mVideoCursor.moveToFirst() = and mVideoCursor.getCount() = " + this.mVideoCursor.moveToFirst() + this.mVideoCursor.getCount());
        if (!this.mVideoCursor.moveToFirst() || this.mVideoCursor.getCount() == 0) {
            if (this.pd != null) {
                this.pd.dismiss();
            }
            VideoDisplayVInVisible();
            HintNoVideo();
        } else if (this.mVideoCursor.getCount() != 0) {
            if (this.pd != null) {
                this.pd.dismiss();
            }
            this.MainView.setBackgroundResource(R.color.black);
            getListView().setSelection(this.mLastPosition);
            getListView().setOnItemLongClickListener(this.mOnItemLongClickListener);
        }
        this.pd.dismiss();
    }

    public void HintNoVideo() {
        Toast.makeText(this, (int) R.string.no_mediafiles, 2000).show();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1 && data != null) {
            this.mUri = data.getData();
            LOG("onActivityResult:mUri = " + this.mUri);
        }
        int result = DBUtils.FindPosition(this, this.mUri);
        if (result >= 0) {
            this.mLastPosition = result;
        }
    }
}
