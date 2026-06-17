package android.rk.RockAudioPlayer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.Uri;
import android.net.WebAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Downloads;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

/* loaded from: classes.dex */
public class SearchNetActivity extends ListActivity implements View.OnClickListener, ServiceConnection, DownloadListener {
    private static final int CLASSIC_OLDIES = 3;
    static final int DIALOG_DETAIL_INFO = 5;
    static final int DIALOG_DOWNLOAD = 2;
    static final int DIALOG_MORE_LINK = 6;
    static final int DIALOG_NEXT_PAGE = 3;
    private static final int DIALOG_NO_FREE_SPACE = 2;
    static final int DIALOG_PLAY = 1;
    static final int DIALOG_PRE_PAGE = 4;
    private static final int DIALOG_YES_NO_DOWNLOAD = 1;
    static final int DIDALOG_HOMEPAGE = 1;
    private static final int DOWNLOADFINISH = 1;
    private static final int DOWNLOADREFRESH = 3;
    private static final int DOWNLOADSUSPEND = 2;
    private static final int EURAMERICAN_POP = 4;
    private static final int HOMEPAGESEARCHEXCEPTION = 5;
    private static final int HOMEPAGESEARCHFINISH = 4;
    private static final int HOTSONG = 2;
    private static final String HomePage = "http://music.soso.com/index.html";
    private static final int JK_POP = 5;
    private static int LongPrssPosition = 0;
    private static final int NEWSONG = 1;
    private static final String SEARCH_PATH = "/sdcard/DownLoad";
    ArrayList<SearchNetFileInfo> ClassicSongSaveArray;
    private int DownThreadNum;
    private Button Downstatus;
    ArrayList<SearchNetFileInfo> EAPopSongSaveArray;
    ArrayList<SearchNetFileInfo> HotSongSaveArray;
    ArrayList<SearchNetFileInfo> JKPopSongSaveArray;
    ArrayList<SearchNetFileInfo> NewSongSaveArray;
    private Button NextButton;
    private Button PreButton;
    private Button ReturnButton;
    private EditText SearchText;
    private View SearchView;
    private ProgressDialog homepd;
    private InputMethodManager imm;
    private ProgressDialog mDialogLoading;
    private HomePageSearch mhomesearchpage;
    private ProgressDialog pd;
    ArrayList<SearchNetFileInfo> savearray;
    private String statusbar_fileName;
    private boolean stop;
    private String strsearchcontent;
    private String[] targetStr;
    private static int SearchPage = 1;
    private static int SongKind = 0;
    public static boolean DEBUG = false;
    Animation myAnimation_in = new ScaleAnimation(0.0f, 1.1f, 0.0f, 1.1f, 1, 0.5f, 1, 0.5f);
    Animation myAnimation_out = new ScaleAnimation(1.1f, 0.0f, 1.1f, 0.0f, 1, 0.5f, 1, 0.5f);
    private String prefix1 = "http://mp3.baidu.com/m?f=ms&tn=baidump3&ct=134217728&lf=&rn=&lm=-1&word=";
    private String prefix2 = "http://mp3.baidu.com/m?z=0&cl=3&ct=134217728&sn=&lm=-1&cm=1&sc=1&bu=&rn=30&tn=baidump3&word=";
    private String prefix_ss_0 = "http://cgi.music.soso.com/fcgi-bin/m.q?w=";
    private String prefix_ss_1 = "&p=";
    private String prefix_ss_2 = "&t=1";
    private final String TAG = "*****SearchNet.java*****";
    private boolean HomePageFlag = false;
    private boolean mSearchfinish = false;
    private float downpercent = 0.0f;
    private Handler mSearchHandler = new Handler();
    private Runnable mSearchRunable = new Runnable() { // from class: android.rk.RockAudioPlayer.SearchNetActivity.1
        @Override // java.lang.Runnable
        public void run() {
            try {
                SearchNetActivity.this.searchfile(SearchNetActivity.SearchPage, MusicUtils.GetSearchContent());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };
    private BroadcastReceiver mStatusListener = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.SearchNetActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            SearchNetActivity.this.LOG("action = " + action);
            if (action.equals(AudioPlaybackService.MEDIAPLAY_ERROR)) {
                if (SearchNetActivity.this.pd != null) {
                    SearchNetActivity.this.pd.dismiss();
                }
                SearchNetActivity.this.ToastSatausShow(R.string.mediaplayerror);
                return;
            }
            try {
                MusicUtils.sService.play();
            } catch (RemoteException e) {
            }
            Intent intent2 = new Intent("android.rk.RockAudioPlayer.PLAYBACK_VIEWER");
            intent2.putExtra("oneshot", true);
            intent2.putExtra("searchnetflag", true);
            intent2.putExtra("checkLayout", 1);
            SearchNetActivity.this.startActivity(intent2);
            if (SearchNetActivity.this.pd != null) {
                SearchNetActivity.this.pd.dismiss();
            }
            SearchNetActivity.this.finish();
        }
    };
    private boolean KeySearchFlag = false;
    private Runnable mDetailsRunnable = new Runnable() { // from class: android.rk.RockAudioPlayer.SearchNetActivity.3
        @Override // java.lang.Runnable
        public void run() {
            final View details_layout = SearchNetActivity.this.findViewById(R.id.song_details);
            details_layout.setVisibility(0);
            int position = SearchNetActivity.this.GetpositionLongPress();
            String song_name = SearchNetActivity.this.GetSongName(SearchNetActivity.this.savearray, position);
            TextView song_name_content = (TextView) SearchNetActivity.this.findViewById(R.id.song_name_content);
            song_name_content.setText("Name:" + song_name);
            String song_album = SearchNetActivity.this.GetSongAlbum(SearchNetActivity.this.savearray, position);
            TextView song_Album_content = (TextView) SearchNetActivity.this.findViewById(R.id.song_album_content);
            song_Album_content.setText("Album:" + song_album);
            String song_format = SearchNetActivity.this.GetSongFormat(SearchNetActivity.this.savearray, position);
            TextView song_format_content = (TextView) SearchNetActivity.this.findViewById(R.id.song_type_content);
            song_format_content.setText("Format:" + song_format);
            String song_size = SearchNetActivity.this.GetSongFileSize(SearchNetActivity.this.savearray, position);
            TextView song_size_content = (TextView) SearchNetActivity.this.findViewById(R.id.song_size_content);
            song_size_content.setText("FileSize:" + song_size);
            String song_artist = SearchNetActivity.this.GetSongArtist(SearchNetActivity.this.savearray, position);
            TextView song_artist_content = (TextView) SearchNetActivity.this.findViewById(R.id.song_artist_content);
            song_artist_content.setText("Artist:" + song_artist);
            String song_connection_ratio = SearchNetActivity.this.GetSongConnection_Ratio(SearchNetActivity.this.savearray, position);
            TextView song_connection_ratio_content = (TextView) SearchNetActivity.this.findViewById(R.id.song_connection_ratio_content);
            song_connection_ratio_content.setText("Connection_Ratio:" + song_connection_ratio);
            String song_path = SearchNetActivity.this.GetSongUrl(SearchNetActivity.this.savearray, position);
            TextView song_path_content = (TextView) SearchNetActivity.this.findViewById(R.id.song_path_content);
            song_path_content.setText("URL:" + song_path);
            ImageView close_details = (ImageView) SearchNetActivity.this.findViewById(R.id.close_details);
            close_details.setOnClickListener(new View.OnClickListener() { // from class: android.rk.RockAudioPlayer.SearchNetActivity.3.1
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    details_layout.setVisibility(8);
                }
            });
        }
    };
    private boolean bhomesearchfinish = false;
    private final Handler mHandler = new Handler() { // from class: android.rk.RockAudioPlayer.SearchNetActivity.4
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 4:
                    SearchNetActivity.this.bhomesearchfinish = true;
                    if (SearchNetActivity.this.JKPopSongSaveArray.size() == 0) {
                        SearchNetActivity.this.ToastSatausShow(R.string.searchfail);
                    }
                    if (SearchNetActivity.this.KeySearchFlag) {
                        SearchNetActivity.this.KeySearchFlag = false;
                        SearchNetActivity.this.DisplaySearchHomeResult(SearchNetActivity.SongKind);
                    }
                    if (SearchNetActivity.this.homepd != null) {
                        SearchNetActivity.this.homepd.dismiss();
                        return;
                    }
                    return;
                case 5:
                    SearchNetActivity.this.ToastSatausShow(R.string.searchexception);
                    if (SearchNetActivity.this.homepd != null) {
                        SearchNetActivity.this.homepd.dismiss();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void LOG(String msg) {
        if (DEBUG) {
            Log.d("*****SearchNet.java*****", msg);
        }
    }

    private void mDialogLoadingshow() {
        String Dilog_tile = getResources().getString(R.string.load_title);
        String Dilog_wait = getResources().getString(R.string.wait);
        this.mDialogLoading = ProgressDialog.show(this, Dilog_tile, Dilog_wait, true, true);
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchnetmain);
        View b = findViewById(R.id.SearchButton);
        b.setOnClickListener(this);
        this.savearray = new ArrayList<SearchNetFileInfo>();
        this.Downstatus = (Button) findViewById(R.id.downstatus);
        this.Downstatus.setOnClickListener(this);
        this.NewSongSaveArray = new ArrayList<SearchNetFileInfo>();
        this.HotSongSaveArray = new ArrayList<SearchNetFileInfo>();
        this.ClassicSongSaveArray = new ArrayList<SearchNetFileInfo>();
        this.EAPopSongSaveArray = new ArrayList<SearchNetFileInfo>();
        this.JKPopSongSaveArray = new ArrayList<SearchNetFileInfo>();
        this.SearchView = findViewById(R.id.search_layout);
        getListView().setOnCreateContextMenuListener(this);
        MusicUtils.bindToService(this, this);
        this.SearchText = (EditText) findViewById(R.id.SearchText);
        this.imm = (InputMethodManager) getSystemService("input_method");
        this.imm.showSoftInput(this.SearchText, 2);
        this.SearchView.setAnimation(this.myAnimation_in);
        this.myAnimation_in.setDuration(400L);
        this.SearchText.setFocusable(true);
        this.SearchText.setInputType(1);
        this.ReturnButton = (Button) findViewById(R.id.search_return);
        this.ReturnButton.setOnClickListener(this);
        this.PreButton = (Button) findViewById(R.id.prebutton);
        this.PreButton.setOnClickListener(this);
        this.NextButton = (Button) findViewById(R.id.nextbutton);
        this.NextButton.setOnClickListener(this);
        this.savearray = MusicUtils.GetSaveNetResult();
        DisplaySearchResult();
        this.mhomesearchpage = new HomePageSearch(this);
        this.mhomesearchpage.start();
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        IntentFilter f = new IntentFilter();
        f.addAction(AudioPlaybackService.ASYNC_OPEN_COMPLETE);
        f.addAction(AudioPlaybackService.MEDIAPLAY_ERROR);
        registerReceiver(this.mStatusListener, new IntentFilter(f));
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override // android.app.ListActivity, android.app.Activity
    public void onDestroy() {
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
        super.onDestroy();
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfoIn) {
        if (!this.HomePageFlag) {
            menu.add(0, 1, 0, R.string.search_play);
            menu.add(0, 2, 0, R.string.search_down);
            menu.add(0, 3, 0, R.string.search_nextpage);
            menu.add(0, 4, 0, R.string.search_prepage);
            menu.add(0, 5, 0, R.string.search_detailinfo);
        } else {
            menu.add(0, 1, 0, R.string.homepage);
        }
        menu.setHeaderTitle(R.string.search_action);
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo mi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String seachecontent = null;
        SetpositionLongPress(mi.position);
        if (!this.HomePageFlag) {
            switch (item.getItemId()) {
                case 1:
                    String uri = GetSongUrl(this.savearray, mi.position);
                    String Dilog_tile = getResources().getString(R.string.load_title);
                    this.pd = ProgressDialog.show(this, Dilog_tile, uri, true, true);
                    try {
                        MusicUtils.sService.openfileAsync(uri);
                        break;
                    } catch (RemoteException e) {
                        break;
                    }
                case 2:
                    String sourceName = GetSongUrl(this.savearray, mi.position);
                    ToastSatausShow(R.string.downloading_file);
                    onDownloadStartNoStream(sourceName, null, null, null, -1L);
                    break;
                case 3:
                    this.savearray.removeAll(this.savearray);
                    mDialogLoadingshow();
                    SearchPage++;
                    this.mSearchHandler.postDelayed(this.mSearchRunable, 50L);
                    break;
                case 4:
                    this.savearray.removeAll(this.savearray);
                    mDialogLoadingshow();
                    SearchPage--;
                    this.mSearchHandler.postDelayed(this.mSearchRunable, 50L);
                    break;
                case 5:
                    this.mDetailsRunnable.run();
                    break;
            }
        } else if (this.bhomesearchfinish) {
            switch (item.getItemId()) {
                case 1:
                    switch (SongKind) {
                        case 1:
                            seachecontent = GetSongName(this.NewSongSaveArray, mi.position);
                            break;
                        case 2:
                            seachecontent = GetSongName(this.HotSongSaveArray, mi.position);
                            break;
                        case 3:
                            seachecontent = GetSongName(this.ClassicSongSaveArray, mi.position);
                            break;
                        case 4:
                            seachecontent = GetSongName(this.EAPopSongSaveArray, mi.position);
                            break;
                        case 5:
                            seachecontent = GetSongName(this.JKPopSongSaveArray, mi.position);
                            break;
                    }
                    LOG("seachecontent = " + seachecontent);
                    this.strsearchcontent = seachecontent;
                    MusicUtils.SaveSearchContent(this.strsearchcontent);
                    mDialogLoadingshow();
                    SearchPage = 1;
                    this.mSearchHandler.postDelayed(this.mSearchRunable, 50L);
                    break;
            }
        } else {
            ToastSatausShow(R.string.loading_wait);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void DisplaySearchHomeResult(int id) {
        switch (id) {
            case 1:
                this.HomePageFlag = true;
                SongKind = 1;
                Song_Adapter drawerAdapter = new Song_Adapter(this, R.layout.song_listview_adapter, this.NewSongSaveArray);
                setListAdapter(drawerAdapter);
                return;
            case 2:
                this.HomePageFlag = true;
                SongKind = 2;
                Song_Adapter drawerAdapter2 = new Song_Adapter(this, R.layout.song_listview_adapter, this.HotSongSaveArray);
                setListAdapter(drawerAdapter2);
                return;
            case 3:
                this.HomePageFlag = true;
                SongKind = 3;
                Song_Adapter drawerAdapter3 = new Song_Adapter(this, R.layout.song_listview_adapter, this.ClassicSongSaveArray);
                setListAdapter(drawerAdapter3);
                return;
            case 4:
                this.HomePageFlag = true;
                SongKind = 4;
                Song_Adapter drawerAdapter4 = new Song_Adapter(this, R.layout.song_listview_adapter, this.EAPopSongSaveArray);
                setListAdapter(drawerAdapter4);
                return;
            case 5:
                this.HomePageFlag = true;
                SongKind = 5;
                Song_Adapter drawerAdapter5 = new Song_Adapter(this, R.layout.song_listview_adapter, this.JKPopSongSaveArray);
                setListAdapter(drawerAdapter5);
                return;
            default:
                return;
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, R.string.newsong);
        menu.add(0, 2, 1, R.string.hotsong);
        menu.add(0, 3, 2, R.string.Classic);
        menu.add(0, 4, 3, R.string.EA_Pop);
        menu.add(0, 5, 4, R.string.JK_Pop);
        menu.setGroupCheckable(0, true, true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        LOG("NewSongSaveArray = " + this.NewSongSaveArray.size());
        if (this.NewSongSaveArray.size() == 0) {
            String Dilog_tile = getResources().getString(R.string.load_title);
            String Dilog_wait = getResources().getString(R.string.netsearchmesg);
            this.homepd = ProgressDialog.show(this, Dilog_tile, Dilog_wait, true, true);
            this.mhomesearchpage = new HomePageSearch(this);
            this.mhomesearchpage.start();
            this.KeySearchFlag = true;
        }
        DisplaySearchHomeResult(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void SetpositionLongPress(int pos) {
        LongPrssPosition = pos;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int GetpositionLongPress() {
        return LongPrssPosition;
    }

    public String getInputString() {
        return this.strsearchcontent;
    }

    public int getpage() {
        return SearchPage;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SearchButton /* 2131427404 */:
                EditText searchtextread = (EditText) findViewById(R.id.SearchText);
                CharSequence searchcontent = searchtextread.getText();
                this.strsearchcontent = new String(searchcontent.toString());
                MusicUtils.SaveSearchContent(this.strsearchcontent);
                this.imm.hideSoftInputFromWindow(this.SearchText.getWindowToken(), 0);
                if (this.strsearchcontent.equals("") || this.strsearchcontent.equals(Integer.valueOf((int) R.string.netsearch_input))) {
                    ToastSatausShow(R.string.search_noinput);
                    return;
                } else if (!this.mSearchfinish) {
                    this.mSearchfinish = true;
                    mDialogLoadingshow();
                    SearchPage = 1;
                    this.mSearchHandler.postDelayed(this.mSearchRunable, 50L);
                    return;
                } else {
                    return;
                }
            case R.id.downstatus /* 2131427450 */:
                viewDownloads(null);
                return;
            case R.id.prebutton /* 2131427451 */:
                this.mSearchHandler.removeCallbacks(this.mSearchRunable);
                if (MusicUtils.GetSaveNetResult().size() == 0) {
                    ToastSatausShow(R.string.HintSearchConten);
                    return;
                }
                this.savearray.removeAll(this.savearray);
                mDialogLoadingshow();
                SearchPage--;
                if (SearchPage < 1) {
                    SearchPage = 1;
                }
                this.mSearchHandler.postDelayed(this.mSearchRunable, 50L);
                return;
            case R.id.nextbutton /* 2131427452 */:
                this.mSearchHandler.removeCallbacks(this.mSearchRunable);
                if (MusicUtils.GetSaveNetResult().size() == 0) {
                    ToastSatausShow(R.string.HintSearchConten);
                    return;
                }
                this.savearray.removeAll(this.savearray);
                mDialogLoadingshow();
                SearchPage++;
                this.mSearchHandler.postDelayed(this.mSearchRunable, 50L);
                return;
            case R.id.search_return /* 2131427453 */:
                Intent intent = new Intent();
                intent.setClass(this, RockAudioPlayer.class);
                startActivity(intent);
                finish();
                return;
            default:
                return;
        }
    }

    private String[] RemoveMoreTokens(String str) throws UnsupportedEncodingException {
        if (str == null) {
            ToastSatausShow(R.string.HintSearchConten);
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(URLEncoder.encode(str, "GB2312"), " ");
        this.targetStr = new String[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            String temp = tokenizer.nextToken();
            this.targetStr[i] = temp;
            i++;
        }
        return this.targetStr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void searchfile(int page, String inputstring) throws UnsupportedEncodingException {
        if (inputstring == null) {
            ToastSatausShow(R.string.HintSearchConten);
            return;
        }
        this.HomePageFlag = false;
        this.savearray.removeAll(this.savearray);
        RemoveMoreTokens(inputstring);
        RealizeSearch(page);
        MusicUtils.SaveNetSearchResult(this.savearray);
        DisplaySearchResult();
    }

    private void DisplaySearchResult() {
        Song_Adapter drawerAdapter = new Song_Adapter(this, R.layout.song_listview_adapter, this.savearray);
        setListAdapter(drawerAdapter);
    }

    public void RealizeSearch(int page) {
        if (page <= 0) {
            page = 1;
        }
        try {
            StringBuffer forSearch = new StringBuffer();
            for (int j = 0; j < this.targetStr.length - 1; j++) {
                forSearch.append(this.targetStr[j] + "+");
            }
            forSearch.append(this.targetStr[this.targetStr.length - 1]);
            URL url = new URL(this.prefix_ss_0 + ((Object) forSearch) + this.prefix_ss_1 + page + this.prefix_ss_2);
            LOG("url = " + url);
            StringBuffer fileInfo = readFile(url);
            getDownload(fileInfo.toString());
            this.mSearchfinish = false;
            if (this.mDialogLoading != null) {
                this.mDialogLoading.dismiss();
            }
            if (this.savearray.size() == 0) {
                ToastSatausShow(R.string.searchfail);
            }
        } catch (Exception e) {
            if (this.mDialogLoading != null) {
                this.mDialogLoading.dismiss();
            }
        }
    }

    public int GetResponseCode(int position) {
        int i;
        try {
            String sURL = GetSongUrl(this.savearray, position);
            if (sURL == null) {
                i = -1;
            } else {
                URL log = new URL(sURL);
                HttpURLConnection http = (HttpURLConnection) log.openConnection();
                int responseCode = http.getResponseCode();
                if (responseCode >= 400) {
                    this.savearray.remove(position);
                    i = -2;
                } else {
                    http.disconnect();
                    i = 0;
                }
            }
            return i;
        } catch (IOException e) {
            this.savearray.remove(position);
            e.printStackTrace();
            return -2;
        }
    }

    public StringBuffer readFile(URL url) throws IOException {
        StringBuffer finalString = new StringBuffer(1000);
        LOG("Current Site:" + url.toString());
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.connect();
        InputStream urlStream = http.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlStream, "GB2312"));
        while (true) {
            String str = reader.readLine();
            if (str != null) {
                finalString.append(str);
            } else {
                reader.close();
                return finalString;
            }
        }
    }

    /* loaded from: classes.dex */
    class HomePageSearch extends Thread {
        private SearchNetActivity parent;

        public HomePageSearch(SearchNetActivity parent) {
            this.parent = parent;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            try {
                HomePageSearch();
            } catch (UnsupportedEncodingException e) {
                Message msg = SearchNetActivity.this.mHandler.obtainMessage(5);
                SearchNetActivity.this.mHandler.sendMessageDelayed(msg, 100L);
                e.printStackTrace();
            } catch (MalformedURLException e2) {
                Message msg2 = SearchNetActivity.this.mHandler.obtainMessage(5);
                SearchNetActivity.this.mHandler.sendMessageDelayed(msg2, 100L);
                e2.printStackTrace();
            } catch (IOException e3) {
                Message msg3 = SearchNetActivity.this.mHandler.obtainMessage(5);
                SearchNetActivity.this.mHandler.sendMessageDelayed(msg3, 100L);
                e3.printStackTrace();
            }
        }

        public void HomePageSearch() throws IOException {
            URL url = new URL(SearchNetActivity.HomePage);
            StringBuffer fileInfo = this.parent.readFile(url);
            GetDownLoadHomePage(fileInfo.toString());
            Message msg = SearchNetActivity.this.mHandler.obtainMessage(4);
            SearchNetActivity.this.mHandler.sendMessageDelayed(msg, 100L);
        }

        public ArrayList<SearchNetFileInfo> SearchKeyWord(StringTokenizer token) throws UnsupportedEncodingException {
            String title = null;
            String singer = null;
            boolean already = false;
            Resources resources = this.parent.getResources();
            ArrayList<SearchNetFileInfo> pSaveArray = new ArrayList<SearchNetFileInfo>();
            while (token.hasMoreTokens() && !already) {
                if (token.nextToken().startsWith("tbody")) {
                    while (token.hasMoreTokens()) {
                        token.nextToken();
                        if (!already) {
                            while (true) {
                                if (token.hasMoreTokens() && !already) {
                                    String temp = token.nextToken();
                                    if (temp.startsWith("/tbody")) {
                                        already = true;
                                        break;
                                    } else if (-1 != temp.indexOf("song")) {
                                        String temp2 = token.nextToken();
                                        String title2 = new String(temp2.substring(temp2.indexOf("title") + 7, temp2.length() - 1));
                                        title = HTMLDecoder.decode(title2);
                                    } else if (-1 != temp.indexOf("singer")) {
                                        String temp3 = token.nextToken();
                                        String singer2 = new String(temp3.substring(temp3.indexOf("title") + 7, temp3.length() - 1));
                                        singer = HTMLDecoder.decode(singer2);
                                    } else if (temp.equals("/tr")) {
                                        SearchNetFileInfo searchfileinfo = new SearchNetFileInfo();
                                        searchfileinfo.name = title;
                                        searchfileinfo.Singer = singer;
                                        searchfileinfo.Icon = resources.getDrawable(R.drawable.music_icon);
                                        pSaveArray.add(searchfileinfo);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return pSaveArray;
        }

        public void GetDownLoadHomePage(String finalString) throws UnsupportedEncodingException {
            StringTokenizer token = new StringTokenizer(finalString, "<>");
            while (token.hasMoreTokens()) {
                String temp = token.nextToken();
                if (-1 != temp.indexOf("song_box toplist_new")) {
                    SearchNetActivity.this.NewSongSaveArray = SearchKeyWord(token);
                } else if (-1 != temp.indexOf("song_box toplist_hot")) {
                    SearchNetActivity.this.HotSongSaveArray = SearchKeyWord(token);
                } else if (-1 != temp.indexOf("song_box toplist_old")) {
                    SearchNetActivity.this.ClassicSongSaveArray = SearchKeyWord(token);
                } else if (-1 != temp.indexOf("song_box toplist_occident")) {
                    SearchNetActivity.this.EAPopSongSaveArray = SearchKeyWord(token);
                } else if (-1 != temp.indexOf("song_box toplist_jk")) {
                    SearchNetActivity.this.JKPopSongSaveArray = SearchKeyWord(token);
                }
            }
        }
    }

    public ArrayList<String> getDownload(String finalString) throws UnsupportedEncodingException {
        StringTokenizer token = new StringTokenizer(finalString, "><");
        String name = null;
        String Singer = null;
        String Album = null;
        String Format = null;
        String FileSize = null;
        String Connection_Ratio = null;
        String targetadress = new String();
        ArrayList<String> link = new ArrayList<String>();
        Resources resources = getResources();
        boolean already = false;
        boolean flag = false;
        int position = -1;
        while (token.hasMoreTokens()) {
            String temp = token.nextToken();
            if (temp.startsWith("tr  onmouseover=")) {
                while (true) {
                    if (token.hasMoreTokens()) {
                        String temp2 = token.nextToken();
                        if (!already && temp2.indexOf("data") != -1) {
                            int i = 0;
                            already = true;
                            String temp3 = token.nextToken();
                            int end = temp3.indexOf(";");
                            targetadress = new String(temp3.substring(temp3.indexOf("FIhttp") + 2, end));
                            int pos = targetadress.lastIndexOf(".");
                            String postfix = targetadress.substring(pos, targetadress.length());
                            while (true) {
                                if (!postfix.equalsIgnoreCase(".mp3")) {
                                    String path_temp = temp3.substring(end + 2, temp3.length());
                                    int begin = path_temp.indexOf("http");
                                    end = path_temp.indexOf(";");
                                    if (begin >= 0 && begin <= end && end >= 0) {
                                        targetadress = new String(path_temp.substring(begin, end));
                                        int pos2 = targetadress.lastIndexOf(".");
                                        postfix = targetadress.substring(pos2, targetadress.length());
                                        i++;
                                        if (i >= 10) {
                                            flag = true;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                            flag = true;
                        } else if (!flag && temp2.indexOf("song") != -1) {
                            while (true) {
                                if (!token.hasMoreTokens()) {
                                    break;
                                } else if (token.nextToken().indexOf("s_name") != -1) {
                                    String temp4 = token.nextToken();
                                    if (temp4.indexOf("strong") != -1) {
                                        name = new String(token.nextToken().toString());
                                    } else {
                                        name = new String(temp4.toString());
                                    }
                                }
                            }
                        } else if (flag || temp2.indexOf("singer") == -1) {
                            if (flag || temp2.indexOf("ablum") == -1) {
                                if (flag || temp2.indexOf("format") == -1) {
                                    if (flag || temp2.indexOf("size") == -1) {
                                        if (flag || temp2.indexOf("speedbar") == -1) {
                                            if (temp2.equals("/tr")) {
                                                position++;
                                                already = false;
                                                if (flag) {
                                                    flag = false;
                                                } else {
                                                    SearchNetFileInfo searchfileinfo = new SearchNetFileInfo();
                                                    searchfileinfo.path = targetadress;
                                                    searchfileinfo.Album = Album;
                                                    searchfileinfo.Connection_Ratio = Connection_Ratio;
                                                    searchfileinfo.FileSize = FileSize;
                                                    searchfileinfo.Format = Format;
                                                    searchfileinfo.name = name;
                                                    searchfileinfo.Singer = Singer;
                                                    searchfileinfo.Icon = resources.getDrawable(R.drawable.music_icon);
                                                    link.add(searchfileinfo.path);
                                                    link.add(searchfileinfo.Album);
                                                    link.add(searchfileinfo.Connection_Ratio);
                                                    link.add(searchfileinfo.FileSize);
                                                    link.add(searchfileinfo.Format);
                                                    link.add(searchfileinfo.Singer);
                                                    link.add(searchfileinfo.name);
                                                    this.savearray.add(searchfileinfo);
                                                }
                                            }
                                        } else if (token.hasMoreTokens()) {
                                            String temp5 = token.nextToken();
                                            Connection_Ratio = new String(temp5.substring(temp5.indexOf("width:") + 6, temp5.indexOf(";")));
                                        }
                                    } else if (token.hasMoreTokens()) {
                                        FileSize = new String(token.nextToken().toString());
                                    }
                                } else if (token.hasMoreTokens()) {
                                    Format = new String(token.nextToken().toString());
                                }
                            } else if (token.hasMoreTokens()) {
                                String temp6 = token.nextToken();
                                if (temp6.indexOf("title") != -1) {
                                    Album = new String(temp6.substring(temp6.indexOf("title") + 7, temp6.length() - 1));
                                } else {
                                    Album = new String("UnkonwAlbum");
                                }
                            }
                        } else if (token.hasMoreTokens()) {
                            String temp7 = token.nextToken();
                            if (temp7.indexOf("title") != -1) {
                                Singer = new String(temp7.substring(temp7.indexOf("title") + 7, temp7.length() - 1));
                            } else {
                                Singer = new String("UnkonwArtist");
                            }
                        }
                    }
                }
            } else if (temp.equals("/tbody")) {
                break;
            }
        }
        return link;
    }

    public int getFileSize(String sURL) {
        HttpURLConnection http = null;
        int responseCode = -1;
        String head;
        int size = -1;
        try {
            URL log = new URL(sURL);
            http = (HttpURLConnection) log.openConnection();
            responseCode = http.getResponseCode();
        } catch (Exception e) {
            return -1;
        }
        if (responseCode >= 400) {
            LOG("http connect is wrong!!");
            return -2;
        }
        new String("");
        int i = 1;
        while (true) {
            head = http.getHeaderFieldKey(i);
            if (head == null) {
                break;
            } else if (head.equals("content-length") || head.equals("Content-Length")) {
                break;
            } else {
                i++;
            }
        }
        size = Integer.parseInt(http.getHeaderField(head));
        http.disconnect();
        return size;
    }

    public String GetSongUrl(ArrayList<SearchNetFileInfo> array, int position) {
        if (array != null) {
            return array.get(position).path;
        }
        return null;
    }

    public String GetSongName(ArrayList<SearchNetFileInfo> array, int position) {
        if (array != null) {
            return HTMLDecoder.decode(array.get(position).name);
        }
        return null;
    }

    public String GetSongAlbum(ArrayList<SearchNetFileInfo> array, int position) {
        if (array != null) {
            return HTMLDecoder.decode(array.get(position).Album);
        }
        return null;
    }

    public String GetSongConnection_Ratio(ArrayList<SearchNetFileInfo> array, int position) {
        if (array != null) {
            return HTMLDecoder.decode(array.get(position).Connection_Ratio);
        }
        return null;
    }

    public String GetSongFileSize(ArrayList<SearchNetFileInfo> array, int position) {
        if (array != null) {
            return HTMLDecoder.decode(array.get(position).FileSize);
        }
        return null;
    }

    public String GetSongFormat(ArrayList<SearchNetFileInfo> array, int position) {
        if (array != null) {
            return HTMLDecoder.decode(array.get(position).Format);
        }
        return null;
    }

    public String GetSongArtist(ArrayList<SearchNetFileInfo> array, int position) {
        if (array != null) {
            return HTMLDecoder.decode(array.get(position).Singer);
        }
        return null;
    }

    @Override // android.app.ListActivity
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!this.HomePageFlag) {
            LOG("position = " + position);
            String searchpath = new String(GetSongUrl(this.savearray, position));
            LOG("searchpath = " + searchpath);
            Uri uri = Uri.parse(searchpath);
            String Dilog_tile = getResources().getString(R.string.load_title);
            String Dilog_wait = getResources().getString(R.string.netsearchmesg);
            this.pd = ProgressDialog.show(this, Dilog_tile, Dilog_wait, true, true);
            try {
                MusicUtils.sService.openfileAsync(uri.toString());
            } catch (RemoteException e) {
            }
        } else {
            ToastSatausShow(R.string.ishomepage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ToastSatausShow(int resId) {
        Toast.makeText(this, resId, 1).show();
    }

    public String GetSavePath(int position) {
        String sourceName = GetSongUrl(this.savearray, position);
        String fileName = URLDecoder.decode(sourceName.substring(sourceName.lastIndexOf("/") + 1));
        this.statusbar_fileName = "/sdcard/DownLoad/" + fileName;
        return "/sdcard/DownLoad/" + fileName;
    }

    void onDownloadStartNoStream(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        try {
            String newUrl = new String(URLUtil.decode(url.getBytes()));
            WebAddress w = new WebAddress(newUrl);
            String frag = null;
            String query = null;
            String path = w.mPath;
            if (path.length() > 0) {
                int idx = path.lastIndexOf(35);
                if (idx != -1) {
                    frag = path.substring(idx + 1);
                    path = path.substring(0, idx);
                }
                int idx2 = path.lastIndexOf(63);
                if (idx2 != -1) {
                    query = path.substring(idx2 + 1);
                    path = path.substring(0, idx2);
                }
            }
            URI uri = new URI(w.mScheme, w.mAuthInfo, w.mHost, w.mPort, path, query, frag);
            ContentValues values = new ContentValues();
            values.put("uri", uri.toString());
            values.put("useragent", userAgent);
            values.put("notificationpackage", getPackageName());
            values.put("notificationclass", BrowserDownloadPage.class.getCanonicalName());
            values.put("visibility", (Integer) 1);
            values.put("mimetype", mimetype);
            values.put("hint", filename);
            values.put("description", uri.getHost());
            if (contentLength > 0) {
                values.put("total_bytes", Long.valueOf(contentLength));
            }
            if (mimetype == null) {
                new FetchUrlMimeType(this).execute(values);
                return;
            }
            Uri contentUri = getContentResolver().insert(Downloads.CONTENT_URI, values);
            viewDownloads(contentUri);
        } catch (Exception e) {
            Log.e("*****SearchNet.java*****", "Could not parse url for download: " + url, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void viewDownloads(Uri downloadRecord) {
        LOG("viewDownloads " + downloadRecord);
        Intent intent = new Intent(this, BrowserDownloadPage.class);
        intent.setData(downloadRecord);
        startActivity(intent);
    }

    @Override // android.webkit.DownloadListener
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        onDownloadStartNoStream(url, userAgent, contentDisposition, mimetype, contentLength);
    }
}
