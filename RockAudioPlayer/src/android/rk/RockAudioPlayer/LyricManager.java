package android.rk.RockAudioPlayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

/* loaded from: classes.dex */
public class LyricManager {
    private static final boolean DEBUG = false;
    protected static final int GET_DATA_FAIL = 2;
    protected static final int GET_DATA_SUCCESS = 1;
    private static String TAG = "LyricManager";
    private Context context;
    private ArrayList<String> downloadSource;
    private String fName;
    private boolean get;
    public String html_info_str;
    private ArrayList<TextView> list_textview;
    private ArrayList<Info> lyric;
    private ProgressDialog pdlrc;
    private String keywords = "http://mp3.baidu.com/m?f=ms&tn=baidump3lyric&ct=150994944&lf=2&rn=10&word=";
    private String SEARCH_PATH = "/flash/lrc";
    ArrayList<String> temp_line_str = new ArrayList<String>();
    private Handler mSearchHandler = new Handler();
    private Runnable mSearchRunable = new Runnable() { // from class: android.rk.RockAudioPlayer.LyricManager.2
        @Override // java.lang.Runnable
        public void run() {
            LyricManager.this.searchLRC();
        }
    };
    Handler mostViewHandler = new Handler() { // from class: android.rk.RockAudioPlayer.LyricManager.3
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int lrc_p = 0;
                    try {
                        Vector<String> source = new Vector<String>();
                        String search_lrc_down = null;
                        while (lrc_p != -1) {
                            try {
                                String search_lrc_down2 = new String(LyricManager.this.context.getString(R.string.search_lrc_down));
                                lrc_p = LyricManager.this.html_info_str.indexOf(search_lrc_down2, lrc_p + 1);
                                if (lrc_p != -1) {
                                    int begin = LyricManager.this.html_info_str.indexOf("http", lrc_p + 1);
                                    int end = LyricManager.this.html_info_str.indexOf("</a>", lrc_p + 1);
                                    String lry_http_addr = new String(LyricManager.this.html_info_str.substring(begin, end));
                                    source.addElement(lry_http_addr);
                                    search_lrc_down = search_lrc_down2;
                                } else {
                                    search_lrc_down = search_lrc_down2;
                                }
                            } catch (Exception e) {
                                if (LyricManager.this.pdlrc != null) {
                                    LyricManager.this.pdlrc.dismiss();
                                }
                                super.handleMessage(msg);
                            }
                        }
                        if (source.size() > 0) {
                            LyricManager.this.downloadSource = new ArrayList();
                            for (int i = 0; i < source.size(); i++) {
                                LyricManager.this.downloadSource.add(source.get(i));
                            }
                            LyricManager.this.download();
                        }
                    } catch (Exception e2) {
                    }
                case 2:
                    LyricManager.this.setnulltextview(LyricManager.this.song_now);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private boolean finished = DEBUG;
    private boolean loaded = DEBUG;
    private StringBuffer html_info = new StringBuffer();
    private boolean loading = true;
    private int current = 0;
    private int lyric_size = 0;
    public boolean pause = DEBUG;
    private int oldposition = 0;
    private int nowposition = 0;
    private String song_now = null;
    private String song_dir_now = null;
    private String songlrcname = null;
    IAudioPlaybackService mService = MusicUtils.getService();
    private Handler mLrcHandler = new Handler();
    private Runnable mLrcRunnable = new Runnable() { // from class: android.rk.RockAudioPlayer.LyricManager.1
        @Override // java.lang.Runnable
        public void run() {
            if (LyricManager.this.pause) {
                LyricManager.this.mLrcHandler.postDelayed(LyricManager.this.mLrcRunnable, 10L);
                return;
            }
            try {
                LyricManager.this.nowposition = LyricManager.this.findposition(LyricManager.this.mService.position());
                if (LyricManager.this.nowposition != LyricManager.this.oldposition) {
                    LyricManager.this.oldposition = LyricManager.this.nowposition;
                    LyricManager.this.moveposition(LyricManager.this.nowposition);
                }
            } catch (Exception e) {
            }
            if (LyricManager.this.nowposition != LyricManager.this.lyric_size || LyricManager.this.lyric_size == 0) {
                LyricManager.this.mLrcHandler.postDelayed(LyricManager.this.mLrcRunnable, 1L);
                return;
            }
            LyricManager.this.moveposition(LyricManager.this.nowposition);
            LyricManager.this.mLrcHandler.postDelayed(LyricManager.this.mLrcRunnable, 20L);
        }
    };

    private void LOG(String str) {
    }

    public LyricManager(Context context, ArrayList<TextView> list_textview) {
        this.list_textview = list_textview;
        this.context = context;
    }

    public void setSongInfo(String song, String song_displayname, String song_dir) {
        this.song_now = song;
        this.songlrcname = song_displayname.substring(0, song_displayname.length() - 4).toLowerCase();
        this.song_dir_now = song_dir;
    }

    public void showDialogshow() {
        String Dilog_tile = this.context.getResources().getString(R.string.lrcsearch);
        String Dilog_wait = this.context.getString(R.string.lrcwait);
        this.pdlrc = ProgressDialog.show(this.context, Dilog_tile, Dilog_wait, true, true);
    }

    public void loadLRC() {
        File lyric_file;
        LOG("loadLRC ##############");
        if (this.mService == null) {
            this.mService = MusicUtils.getService();
        }
        this.lyric = new ArrayList<Info>();
        File lrc = new File(this.SEARCH_PATH);
        try {
            if (!lrc.exists()) {
                lrc.mkdir();
            }
            File[] list = lrc.listFiles();
            this.fName = new String("");
            this.get = DEBUG;
            this.lyric_size = 0;
            this.mLrcHandler.removeCallbacks(this.mLrcRunnable);
            this.temp_line_str.clear();
            this.pause = true;
            LOG("read file now ");
            int len$ = list.length;
            int i$ = 0;
            while (true) {
                if (i$ >= len$) {
                    break;
                }
                File file = list[i$];
                if (file.isFile()) {
                    this.fName = file.getName();
                    String song_name = this.fName.substring(0, this.fName.length() - 4).toLowerCase();
                    String suffix = this.fName.toLowerCase();
                    LOG("the songlrcname --- file name is " + this.songlrcname);
                    LOG(" the song_name ---curent opened lrcname " + song_name + "\n the song_now ---title name is " + this.song_now);
                    if ((song_name.equals(this.song_now.toLowerCase()) || song_name.equals(this.songlrcname.toLowerCase())) && suffix.endsWith(".lrc")) {
                        this.get = true;
                        break;
                    }
                }
                i$++;
            }
            if (!this.get) {
                Log.d("TAG", "the file is not find");
                this.mSearchHandler.postDelayed(this.mSearchRunable, 50L);
            } else {
                Log.d("TAG", "the file finded");
            }
            if (this.get && !this.fName.equals("")) {
                LOG("the file is read now!!");
                if (0 == 0) {
                    lyric_file = new File(this.SEARCH_PATH + "/" + this.fName);
                } else {
                    lyric_file = new File(this.song_dir_now + "/" + this.fName);
                }
                FileInputStream temp_fileinput = new FileInputStream(lyric_file);
                byte[] temp_lrc_byte_fir = new byte[(int) lyric_file.length()];
                int temp_fileinput_len = temp_fileinput.read(temp_lrc_byte_fir);
                LOG(" the origin  lyric_file len  is " + lyric_file.length());
                LOG("the change to byte len  is " + temp_fileinput_len);
                byte[] temp_lrc_byte_sec = new byte[temp_fileinput_len];
                remove_0D(temp_lrc_byte_sec, temp_lrc_byte_fir);
                LOG("the file remove 0d len is " + temp_lrc_byte_sec.length);
                if (temp_fileinput_len > 0) {
                    String temp_lrc_str = new String(temp_lrc_byte_sec, "GB2312");
                    int j = 0;
                    int line_tmp = 0;
                    LOG("the char change to GB2312 len  is " + temp_lrc_str.length());
                    for (int i = 0; i < temp_lrc_str.length(); i++) {
                        if (temp_lrc_str.charAt(i) == '\n') {
                            this.temp_line_str.add(temp_lrc_str.substring(j, i));
                            j = i + 1;
                            line_tmp++;
                        }
                    }
                    LOG("the line num is " + line_tmp);
                    LOG("add the last line lrc ");
                    LOG("the j is " + j + " and the temp_lrc_str.length() - 1 is " + (temp_lrc_str.length() - 1));
                    if (j < temp_lrc_str.length() - 1) {
                        this.temp_line_str.add(temp_lrc_str.substring(j, temp_lrc_str.length() - 1));
                    }
                }
                LOG(" the temp_line_str.size() is " + this.temp_line_str.size());
                temp_fileinput.close();
                for (int str_line = 0; str_line < this.temp_line_str.size(); str_line++) {
                    Log.d("TAG", "the line num is " + str_line);
                    String temp = new String(this.temp_line_str.get(str_line));
                    if (temp.startsWith("[")) {
                        StringTokenizer token = new StringTokenizer(temp, "[]");
                        int count = token.countTokens();
                        LOG("the token is " + token.toString());
                        LOG("the count is " + count);
                        String[] st = new String[count];
                        for (int i2 = 0; i2 < count; i2++) {
                            st[i2] = token.nextToken();
                            LOG("the st[] is " + st[i2]);
                        }
                        if (this.lyric_size < 4) {
                            this.lyric.add(new Info(0L, st[0]));
                            this.lyric_size++;
                        } else {
                            if (count == 0) {
                                this.lyric.add(new Info(transfer(st[0]), ""));
                            }
                            int[] index = new int[count];
                            int num = findLrcToken(st, count, index);
                            if (num > 1) {
                                for (int j2 = 0; j2 < num; j2++) {
                                    int m = 0;
                                    if (j2 != 0) {
                                        m = index[j2 - 1] + 1;
                                    }
                                    while (m < count - 1) {
                                        this.lyric.add(new Info(transfer(st[m]), st[index[j2]]));
                                        m++;
                                    }
                                    if (this.lyric_size == 4) {
                                        long time_begin_4 = this.lyric.get(4).time / 4;
                                        for (int set_begin_4 = 0; set_begin_4 < 4; set_begin_4++) {
                                            this.lyric.set(set_begin_4, new Info(set_begin_4 * time_begin_4, this.lyric.get(set_begin_4).info));
                                        }
                                    }
                                    this.lyric_size++;
                                }
                            } else {
                                for (int i3 = 0; i3 < count - 1; i3++) {
                                    this.lyric.add(new Info(transfer(st[i3]), st[count - 1]));
                                    if (this.lyric_size == 4) {
                                        long time_begin_42 = this.lyric.get(4).time / 4;
                                        for (int set_begin_42 = 0; set_begin_42 < 4; set_begin_42++) {
                                            this.lyric.set(set_begin_42, new Info(set_begin_42 * time_begin_42, this.lyric.get(set_begin_42).info));
                                        }
                                    }
                                    this.lyric_size++;
                                }
                            }
                        }
                    }
                }
                Collections.sort(this.lyric, new Compare());
                this.mLrcHandler.post(this.mLrcRunnable);
                moveposition(0);
                this.nowposition = findposition(this.mService.position());
                moveposition(this.nowposition);
            }
            this.finished = true;
            if (!this.get || this.fName.equals("")) {
                Log.d("TAG", "the get && !fName.equals() is false");
                setnulltextview(this.song_now);
                this.loaded = DEBUG;
            } else {
                Log.d("TAG", "the get && !fName.equals()");
                this.loaded = true;
            }
        } catch (Exception e) {
            Log.d("TAG", "Exception e");
            e.printStackTrace();
            setnulltextview(this.song_now);
            if (this.get) {
                File file_del = new File(this.SEARCH_PATH + "/" + this.fName);
                if (file_del.exists()) {
                    file_del.delete();
                }
            }
            this.loaded = DEBUG;
            if (this.pdlrc != null) {
                this.pdlrc.dismiss();
            }
        }
        this.finished = true;
        if (this.pdlrc != null) {
            this.pdlrc.dismiss();
        }
    }

    /* JADX WARN: Type inference failed for: r0v0, types: [android.rk.RockAudioPlayer.LyricManager$4] */
    public void searchLRC() {
        new Thread() { // from class: android.rk.RockAudioPlayer.LyricManager.4
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                LyricManager.this.readFile();
            }
        }.start();
    }

    public void readFile() {
        try {
            new String();
            String song_not_space = URLEncoder.encode(this.song_now, "GB2312");
            byte[] temp_lrc_byte_fir = new byte[50000];
            URL url_tmp = new URL(this.keywords + song_not_space + "&lm=-1");
            HttpURLConnection http_tmp = (HttpURLConnection) url_tmp.openConnection();
            InputStream read_tmp = http_tmp.getInputStream();
            byte[] b = new byte[1024];
            int temp_lrc_byte_fir_p = 0;
            do {
                int temp = read_tmp.read(b);
                if (temp <= 0) {
                    break;
                }
                for (int i = 0; i < temp; i++) {
                    temp_lrc_byte_fir[temp_lrc_byte_fir_p] = b[i];
                    temp_lrc_byte_fir_p++;
                }
            } while (temp_lrc_byte_fir_p <= 50000);
            this.html_info_str = new String(temp_lrc_byte_fir, "GB2312");
            Log.d(TAG, "html_info_str" + this.html_info_str);
            read_tmp.reset();
            Message m = new Message();
            m.what = 1;
            m.arg1 = 0;
            this.mostViewHandler.sendMessage(m);
        } catch (Exception e) {
            Message m2 = new Message();
            m2.what = 2;
            this.mostViewHandler.sendMessage(m2);
        }
    }

    public void download() {
        int url_p = 1;
        try {
            URL url = new URL(this.downloadSource.get(0));
            try {
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                while (http == null && url_p < this.downloadSource.size()) {
                    URL url2 = new URL(this.downloadSource.get(url_p));
                    http = (HttpURLConnection) url2.openConnection();
                    url_p++;
                    url = url2;
                }
                File f = new File(this.SEARCH_PATH + "/" + this.song_now + ".lrc");
                this.fName = this.song_now + ".lrc";
                InputStream read = http.getInputStream();
                FileOutputStream out = new FileOutputStream(f);
                byte[] b = new byte[1024];
                while (true) {
                    int temp = read.read(b);
                    if (temp <= 0) {
                        break;
                    }
                    out.write(b, 0, temp);
                }
                read.close();
                out.close();
                this.get = true;
                this.loaded = true;
            } catch (Exception e) {
                this.get = DEBUG;
                this.loaded = DEBUG;
                setnulltextview(this.song_now);
                this.loading = DEBUG;
            }
        } catch (Exception e2) {
        }
        this.loading = DEBUG;
    }

    public void downloadDialog() {
    }

    public boolean finished() {
        return this.finished;
    }

    public boolean loaded() {
        return this.loaded;
    }

    public int findLrcToken(String[] st, int size, int[] index) {
        int id;
        int lrctokennum = 0;
        if (size > st.length) {
            Log.d("TAG", "the size OutOfBounds ");
            size = st.length;
        }
        int i = 0;
        int id2 = 0;
        while (i < size) {
            if (transfer(st[i]) == 0) {
                lrctokennum++;
                id = id2 + 1;
                index[id2] = i;
            } else {
                id = id2;
            }
            i++;
            id2 = id;
        }
        Log.d("TAG", "the lrctokennum is " + lrctokennum);
        return lrctokennum;
    }

    private boolean isChar(String str) {
        if (str == null) {
            return DEBUG;
        }
        byte[] bArr = new byte[str.length()];
        char[] temp_byte = str.toCharArray();
        for (int i = 0; i < temp_byte.length; i++) {
            if (temp_byte[i] < '0' || temp_byte[i] > '9') {
                return true;
            }
        }
        return DEBUG;
    }

    public long transfer(String time) {
        if (time.length() < 6) {
            int pos1 = time.indexOf(":");
            if (pos1 == -1) {
                pos1 = time.indexOf(".");
            }
            if (pos1 == -1) {
                return 0L;
            }
            String min = time.substring(0, pos1);
            String sec = time.substring(pos1 + 1);
            if (isChar(min) || isChar(sec)) {
                return 0L;
            }
            long lrc = (Integer.parseInt(min) * 60 * 1000) + (Integer.parseInt(sec) * 1000);
            return 200 + lrc;
        }
        int pos12 = time.indexOf(":");
        if (pos12 == -1) {
            pos12 = time.indexOf(".");
        }
        if (pos12 == -1) {
            return 0L;
        }
        String min2 = time.substring(0, pos12);
        LOG("the min is " + min2);
        if (isChar(min2)) {
            return 0L;
        }
        int pos2 = time.indexOf(".");
        if (pos2 == pos12) {
            pos2 = time.lastIndexOf(".");
        }
        if (pos2 == -1) {
            return 0L;
        }
        String sec2 = time.substring(pos12 + 1, pos2);
        if (isChar(sec2)) {
            return 0L;
        }
        String mil = null;
        LOG("the sec is " + sec2);
        if (pos2 < time.length()) {
            mil = time.substring(pos2 + 1);
            LOG("the mil is " + mil);
            if (isChar(mil)) {
                return 0L;
            }
        }
        long lrc2 = (Integer.parseInt(min2) * 60 * 1000) + (Integer.parseInt(sec2) * 1000) + Integer.parseInt(mil);
        return 200 + lrc2;
    }

    public void pause(boolean b) {
        if (this.loaded) {
            this.pause = b;
            if (this.pause) {
                this.mLrcHandler.removeCallbacks(this.mLrcRunnable);
            } else {
                this.mLrcHandler.post(this.mLrcRunnable);
            }
        }
    }

    public boolean isPaused() {
        return this.pause;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class Compare implements Comparator {
        Compare() {
        }

        @Override // java.util.Comparator
        public int compare(Object a, Object b) {
            Info tempA = (Info) a;
            Info tempB = (Info) b;
            if (tempA.time > tempB.time) {
                return 1;
            }
            return tempA.time < tempB.time ? -1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class Info {
        public String info;
        public long time;

        public Info(long time, String info) {
            this.time = time;
            this.info = info;
        }
    }

    public void moveposition(int position) {
        int len_list_textview = this.list_textview.size();
        if (position < len_list_textview / 2 && position >= 0) {
            for (int i = 0; i < (len_list_textview / 2) - position; i++) {
                this.list_textview.get(i).setText("");
            }
            int j = 0;
            for (int i2 = (len_list_textview / 2) - position; i2 < len_list_textview; i2++) {
                this.list_textview.get(i2).setText(this.lyric.get(j).info);
                j++;
            }
        } else if (position >= len_list_textview / 2 && position < this.lyric_size - (len_list_textview / 2)) {
            for (int i3 = 0; i3 < len_list_textview / 2; i3++) {
                this.list_textview.get(i3).setText(this.lyric.get((position - (len_list_textview / 2)) + i3).info);
            }
            int j2 = 0;
            for (int i4 = len_list_textview / 2; i4 < len_list_textview; i4++) {
                this.list_textview.get(i4).setText(this.lyric.get(position + j2).info);
                j2++;
            }
        } else if (position >= this.lyric_size - (len_list_textview / 2) && position < this.lyric_size) {
            for (int i5 = 0; i5 < len_list_textview / 2; i5++) {
                this.list_textview.get(i5).setText(this.lyric.get((position - (len_list_textview / 2)) + i5).info);
            }
            int j3 = 0;
            for (int i6 = len_list_textview / 2; i6 < len_list_textview; i6++) {
                if (position + j3 < this.lyric_size) {
                    this.list_textview.get(i6).setText(this.lyric.get(position + j3).info);
                    j3++;
                } else {
                    this.list_textview.get(i6).setText(".");
                }
            }
        } else if (position == this.lyric_size) {
            int i7 = 0;
            while (i7 < len_list_textview - 1) {
                this.list_textview.get(i7).setText(this.list_textview.get(i7 + 1).getText());
                i7++;
            }
            this.list_textview.get(i7).setText(" ");
        }
    }

    public void setnulltextview(String songname) {
        int len_list_textview = this.list_textview.size();
        for (int i = 0; i < len_list_textview; i++) {
            if (i == len_list_textview / 2) {
                this.list_textview.get(i).setText(songname);
            } else {
                this.list_textview.get(i).setText("");
            }
        }
    }

    public int findposition(long time) {
        for (int find_p = 0; find_p < this.lyric_size - 1; find_p++) {
            if (this.lyric.get(find_p).time <= time && this.lyric.get(find_p + 1).time >= time) {
                return find_p;
            }
        }
        return this.lyric_size;
    }

    public void rmovePost() {
        this.mLrcHandler.removeCallbacks(this.mLrcRunnable);
    }

    public void remove_0D(byte[] temp_in, byte[] temp_out) {
        int in_p = 0;
        for (int out_p = 0; out_p < temp_out.length; out_p++) {
            if (temp_out[out_p] != 13) {
                temp_in[in_p] = temp_out[out_p];
                in_p++;
            }
        }
    }
}
