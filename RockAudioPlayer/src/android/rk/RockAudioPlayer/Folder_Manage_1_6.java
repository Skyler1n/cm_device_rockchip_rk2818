package android.rk.RockAudioPlayer;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

/* loaded from: classes.dex */
public class Folder_Manage_1_6 extends ListActivity implements View.OnClickListener, ServiceConnection, AdapterView.OnItemLongClickListener {
    private static final int AZ_COMPOSITOR = 0;
    private static final String FLASH_PATH = "/flash";
    private static final String HOST_PATH = "/usb1";
    static final String LOG_Cong = "--[Folder_Manage_1_6.java]";
    private static final String SD_PATH = "/sdcard";
    private static final int SIZE_COMPOSITOR = 2;
    private static final int TIME_COMPOSITOR = 1;
    private static final int TYPE_COMPOSITOR = 3;
    private static final String[] music_postfix = {".mp3", ".mp1", ".mp2", ".wma", ".wav", ".ape", ".3gp", ".aac", ".ogg", ".flac", ".mid", ".m4a", ".amr", ".awb", ".oga", ".midi", ".xmf", ".rtttl", ".smf", ".imy", ".rtx", ".ota", ".3gpp", ".3g2", ".3gp2", ".3gpp2", "m4r"};
    private ImageView Freturn_button;
    private String[] audiocols;
    private Cursor cur;
    private int currently_state;
    private ArrayList<FileInfo> folder_array;
    private Intent intent_back;
    private boolean isfinish;
    private ListView main_ListView;
    private Animation myAnimation_in;
    private Animation myAnimation_out;
    private ContentResolver resolver;
    private Resources resources;
    private ArrayList<FileInfo> savearray;
    private View searchView;
    private ImageView search_button;
    private Button searchbutton;
    private EditText searchtext;
    private ImageView sort_button;
    private ArrayList<FileInfo> text_music_file;
    private TextView titleTV;
    private ArrayList<FileInfo> type_compositor_file;
    private View.OnClickListener all_music_button_listener = null;
    private View.OnClickListener playlist_button_listener = null;
    private String currently_parent = null;
    private String currently_path = null;
    private int[] size_postfix = new int[music_postfix.length];
    private int[] pit_postfix = new int[music_postfix.length];
    private boolean reset_currently_parent = false;
    private boolean SDexist = false;
    private boolean infirInter = true;
    private File sdcard = Environment.getExternalStorageDirectory();
    private boolean searchShow = false;
    private boolean searchResult = false;
    private BroadcastReceiver mFloderScanListener = new BroadcastReceiver() { // from class: android.rk.RockAudioPlayer.Folder_Manage_1_6.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.MEDIA_EJECT") || action.equals("android.intent.action.MEDIA_UNMOUNTED")) {
                MusicUtils.setSpinnerState(Folder_Manage_1_6.this);
                Folder_Manage_1_6.this.finish();
                return;
            }
            if (action.equals("android.intent.action.MEDIA_MOUNTED")) {
                Folder_Manage_1_6.this.first_fill();
            }
            if ("android.intent.action.MEDIA_SCANNER_STARTED".equals(action) || "android.intent.action.MEDIA_SCANNER_FINISHED".equals(action)) {
                MusicUtils.setSpinnerState(Folder_Manage_1_6.this);
            }
        }
    };
    private int delpos = 0;

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.folder_main);
        this.main_ListView = getListView();
        this.main_ListView.setOnItemLongClickListener(this);
        this.resources = getResources();
        this.currently_parent = FLASH_PATH;
        this.currently_path = FLASH_PATH;
        this.currently_state = 2;
        this.sort_button = (ImageView) findViewById(R.id.sort_button);
        this.search_button = (ImageView) findViewById(R.id.search_button);
        this.sort_button.setOnClickListener(this);
        this.search_button.setOnClickListener(this);
        this.searchtext = (EditText) findViewById(R.id.SearchText);
        this.searchView = findViewById(R.id.search_layout);
        this.searchbutton = (Button) findViewById(R.id.SearchButton);
        this.searchbutton.setOnClickListener(this);
        this.Freturn_button = (ImageView) findViewById(R.id.freturn_button);
        this.Freturn_button.setOnClickListener(this);
        this.isfinish = true;
        this.audiocols = new String[]{"_id", "artist", "album", "title", "_data", "mime_type", "year", "_display_name", "composer"};
        this.resolver = getContentResolver();
        Bundle extras = getIntent().getExtras();
        String start_path = new String(extras.getString("start_path"));
        if (start_path.equals("/")) {
            this.SDexist = true;
            first_fill();
        } else {
            File file_start_check = new File(start_path);
            this.currently_path = start_path;
            fill(file_start_check.listFiles());
            this.currently_parent = file_start_check.getParent();
            this.infirInter = false;
            this.isfinish = false;
            this.SDexist = true;
        }
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() { // from class: android.rk.RockAudioPlayer.Folder_Manage_1_6.1
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File file;
                FileInfo playfile;
                if (!Folder_Manage_1_6.this.searchShow) {
                    Folder_Manage_1_6.this.infirInter = false;
                    Folder_Manage_1_6.this.isfinish = false;
                    if (Folder_Manage_1_6.this.searchResult) {
                        file = new File(((FileInfo) Folder_Manage_1_6.this.savearray.get(position)).path);
                    } else {
                        file = new File(((FileInfo) Folder_Manage_1_6.this.folder_array.get(position)).path);
                    }
                    Log.d("zsszsszss", "file = " + file);
                    if (file.isDirectory()) {
                        Folder_Manage_1_6.this.currently_path = ((FileInfo) Folder_Manage_1_6.this.folder_array.get(position)).path;
                        Folder_Manage_1_6.this.currently_parent = ((FileInfo) Folder_Manage_1_6.this.folder_array.get(position)).parent;
                        Folder_Manage_1_6.this.fill(file.listFiles());
                        return;
                    }
                    Folder_Manage_1_6.this.cur = Folder_Manage_1_6.this.resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Folder_Manage_1_6.this.audiocols, "is_music=1", null, "title_key");
                    if (Folder_Manage_1_6.this.cur != null) {
                        if (Folder_Manage_1_6.this.searchResult) {
                            playfile = new FileInfo((FileInfo) Folder_Manage_1_6.this.savearray.get(position));
                        } else {
                            playfile = new FileInfo((FileInfo) Folder_Manage_1_6.this.folder_array.get(position));
                        }
                        if (Folder_Manage_1_6.this.cur.moveToFirst()) {
                            int cur_position = 0;
                            do {
                                new String();
                                new String();
                                String path = Folder_Manage_1_6.this.cur.getString(Folder_Manage_1_6.this.cur.getColumnIndex("_data"));
                                String path_file = new String(playfile.path);
                                if (path.equals(path_file)) {
                                    int[] mlist = MusicUtils.getSongListForCursor(Folder_Manage_1_6.this.cur);
                                    Folder_Manage_1_6.this.intent_back.putExtra("isgeneralfinish", 0);
                                    Folder_Manage_1_6.this.intent_back.putExtra("isselectFile", 1);
                                    Folder_Manage_1_6.this.intent_back.putExtra("now_path", Folder_Manage_1_6.this.currently_path);
                                    Folder_Manage_1_6.this.intent_back.putExtra("cur_list", mlist);
                                    Folder_Manage_1_6.this.intent_back.putExtra("cur_position", cur_position);
                                    Folder_Manage_1_6.this.finish();
                                    return;
                                }
                                cur_position++;
                            } while (Folder_Manage_1_6.this.cur.moveToNext());
                        }
                    }
                }
            }
        };
        this.main_ListView.setOnItemClickListener(listener);
        MusicUtils.bindToService(this, this);
        this.intent_back = getIntent();
        setResult(-1, this.intent_back);
        this.intent_back.putExtra("isselectFile", 0);
        this.intent_back.putExtra("isgeneralfinish", 1);
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        IntentFilter f = new IntentFilter();
        f.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        f.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        f.addAction("android.intent.action.MEDIA_UNMOUNTED");
        f.addAction("android.intent.action.MEDIA_MOUNTED");
        f.addDataScheme("file");
        registerReceiver(this.mFloderScanListener, f);
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override // android.app.Activity
    public void onRestart() {
        super.onRestart();
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
    }

    @Override // android.app.ListActivity, android.app.Activity
    public void onDestroy() {
        MusicUtils.unbindFromService(this);
        unregisterReceiver(this.mFloderScanListener);
        super.onDestroy();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.sort_button && !this.infirInter) {
            switch (this.currently_state) {
                case 0:
                    this.currently_state = 1;
                    this.sort_button.setImageDrawable(this.resources.getDrawable(R.drawable.filetime));
                    Toast.makeText(this, "TIME_COMPOSITOR---", 0).show();
                    break;
                case 1:
                    this.currently_state = 2;
                    this.sort_button.setImageDrawable(this.resources.getDrawable(R.drawable.filesize));
                    Toast.makeText(this, "SIZE_COMPOSITOR---", 0).show();
                    break;
                case 2:
                    this.currently_state = 3;
                    this.sort_button.setImageDrawable(this.resources.getDrawable(R.drawable.filetype));
                    Toast.makeText(this, "TYPE_COMPOSITOR---", 0).show();
                    break;
                case 3:
                    this.currently_state = 0;
                    this.sort_button.setImageDrawable(this.resources.getDrawable(R.drawable.filename));
                    Toast.makeText(this, "AZ_COMPOSITOR---", 0).show();
                    break;
            }
            if (this.searchResult) {
                switch (this.currently_state) {
                    case 0:
                    case 1:
                        sort_AZ_str(this.savearray);
                        break;
                    case 2:
                        sort_SIZE_str(this.savearray);
                        break;
                    case 3:
                        int music_file_size = this.savearray.size();
                        this.type_compositor_file = new ArrayList<FileInfo>(music_file_size);
                        init_pit_postfix();
                        init_type_compositor_file(music_file_size);
                        for (int i = 0; i < music_file_size; i++) {
                            FileInfo tmp = new FileInfo(this.savearray.get(i));
                            evaluate(this.type_compositor_file.get(this.pit_postfix[tmp.musicType]), tmp);
                            int[] iArr = this.pit_postfix;
                            int i2 = tmp.musicType;
                            iArr[i2] = iArr[i2] + 1;
                        }
                        this.savearray = new ArrayList<FileInfo>();
                        for (int i3 = 0; i3 < music_file_size; i3++) {
                            this.savearray.add(this.type_compositor_file.get(i3));
                        }
                        break;
                }
                Folder_Adapter drawerAdapter = new Folder_Adapter(this, this.savearray);
                this.main_ListView.setAdapter((ListAdapter) drawerAdapter);
                return;
            }
            fill(new File(this.currently_path).listFiles());
        } else if (v == this.search_button) {
            this.searchShow = !this.searchShow;
            this.myAnimation_in = new ScaleAnimation(0.0f, 1.1f, 0.0f, 1.1f, 1, 0.5f, 1, 0.5f);
            this.myAnimation_out = new ScaleAnimation(1.1f, 0.0f, 1.1f, 0.0f, 1, 0.5f, 1, 0.5f);
            if (this.searchShow) {
                InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
                imm.showSoftInput(this.searchtext, 0);
                this.searchView.setAnimation(this.myAnimation_in);
                this.myAnimation_in.setDuration(400L);
                this.searchView.setVisibility(0);
                this.searchtext.setFocusable(true);
                this.searchtext.setInputType(1);
                return;
            }
            this.searchView.setAnimation(this.myAnimation_out);
            this.myAnimation_out.setDuration(400L);
            this.searchView.setVisibility(4);
        } else if (v == this.searchbutton) {
            CharSequence searchcontent = this.searchtext.getText();
            String strsearchcontent = new String(searchcontent.toString());
            if (strsearchcontent.equals("")) {
                Toast.makeText(this, (int) R.string.search_noinput, 0).show();
                return;
            }
            this.searchtext.setText("");
            this.savearray = new ArrayList<FileInfo>();
            if (searchfile(this.text_music_file, this.savearray, strsearchcontent)) {
                Folder_Adapter drawerAdapter2 = new Folder_Adapter(this, this.savearray);
                this.main_ListView.setAdapter((ListAdapter) drawerAdapter2);
                this.searchShow = !this.searchShow;
                this.myAnimation_out = new ScaleAnimation(1.1f, 0.0f, 1.1f, 0.0f, 1, 0.5f, 1, 0.5f);
                this.searchView.setAnimation(this.myAnimation_out);
                this.myAnimation_out.setDuration(300L);
                this.searchView.setVisibility(4);
                this.currently_parent = this.currently_path;
                return;
            }
            Toast.makeText(this, (int) R.string.search_false, 1).show();
        } else if (v == this.Freturn_button) {
            if (this.searchShow) {
                this.searchShow = false;
                this.myAnimation_out = new ScaleAnimation(1.1f, 0.0f, 1.1f, 0.0f, 1, 0.5f, 1, 0.5f);
                this.searchView.setAnimation(this.myAnimation_out);
                this.myAnimation_out.setDuration(300L);
                this.searchView.setVisibility(4);
                this.currently_parent = this.currently_path;
                this.searchResult = false;
                return;
            }
            this.searchResult = false;
            if (this.currently_path.equals(FLASH_PATH) || this.currently_path.equals(SD_PATH) || this.currently_path.equals(HOST_PATH)) {
                if (this.isfinish) {
                    finish();
                    this.isfinish = false;
                }
                if (this.SDexist) {
                    first_fill();
                    return;
                }
                return;
            }
            fill(new File(this.currently_parent).listFiles());
            this.currently_path = this.currently_parent;
            File file = new File(this.currently_path);
            this.currently_parent = file.getParent();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fill(File[] files) {
        this.folder_array = new ArrayList<FileInfo>();
        this.text_music_file = new ArrayList<FileInfo>();
        new FileInfo();
        init_type_array();
        for (File file : files) {
            FileInfo temp = new FileInfo();
            temp.path = file.getPath();
            temp.name = file.getName();
            temp.parent = file.getParent();
            temp.size = file.length();
            temp.musicType = isMusicFile(temp.name);
            if (file.isDirectory()) {
                Drawable d = this.resources.getDrawable(R.drawable.fileicon);
                temp.icon = d;
                this.folder_array.add(temp);
            } else if (temp.musicType >= 0) {
                Drawable d2 = this.resources.getDrawable(R.drawable.music_icon);
                temp.icon = d2;
                this.text_music_file.add(temp);
                int[] iArr = this.size_postfix;
                int i = temp.musicType;
                iArr[i] = iArr[i] + 1;
            }
        }
        switch (this.currently_state) {
            case 0:
            case 1:
                sort_AZ_str(this.folder_array);
                sort_AZ_str(this.text_music_file);
                for (int i2 = 0; i2 < this.text_music_file.size(); i2++) {
                    this.folder_array.add(this.text_music_file.get(i2));
                }
                break;
            case 2:
                sort_SIZE_str(this.folder_array);
                sort_SIZE_str(this.text_music_file);
                for (int i3 = 0; i3 < this.text_music_file.size(); i3++) {
                    this.folder_array.add(this.text_music_file.get(i3));
                }
                break;
            case 3:
                int music_file_size = this.text_music_file.size();
                this.type_compositor_file = new ArrayList<FileInfo>(music_file_size);
                init_pit_postfix();
                init_type_compositor_file(music_file_size);
                for (int i4 = 0; i4 < music_file_size; i4++) {
                    FileInfo tmp = new FileInfo(this.text_music_file.get(i4));
                    evaluate(this.type_compositor_file.get(this.pit_postfix[tmp.musicType]), tmp);
                    int[] iArr2 = this.pit_postfix;
                    int i5 = tmp.musicType;
                    iArr2[i5] = iArr2[i5] + 1;
                }
                for (int i6 = 0; i6 < music_file_size; i6++) {
                    this.folder_array.add(this.type_compositor_file.get(i6));
                }
                break;
        }
        Folder_Adapter drawerAdapter = new Folder_Adapter(this, this.folder_array);
        this.main_ListView.setAdapter((ListAdapter) drawerAdapter);
    }

    void sort_AZ_str(ArrayList<FileInfo> sort_text) {
        int num = sort_text.size();
        Collator.getInstance(Locale.CHINESE);
        for (int top = 0; top < num; top++) {
            for (int seek = top + 1; seek < num; seek++) {
                if (pinyin_compare(sort_text.get(top).name, sort_text.get(seek).name) > 0) {
                    FileInfo temp = new FileInfo();
                    evaluate(temp, sort_text.get(top));
                    evaluate(sort_text.get(top), sort_text.get(seek));
                    evaluate(sort_text.get(seek), temp);
                }
            }
        }
    }

    void sort_SIZE_str(ArrayList<FileInfo> sort_text) {
        int num = sort_text.size();
        for (int top = 0; top < num; top++) {
            for (int seek = top + 1; seek < num; seek++) {
                if (sort_text.get(top).size > sort_text.get(seek).size) {
                    FileInfo temp = new FileInfo();
                    evaluate(temp, sort_text.get(top));
                    evaluate(sort_text.get(top), sort_text.get(seek));
                    evaluate(sort_text.get(seek), temp);
                }
            }
        }
    }

    void evaluate(FileInfo a, FileInfo b) {
        a.name = b.name;
        a.path = b.path;
        a.parent = b.parent;
        a.icon = b.icon;
        a.itemType = b.itemType;
        a.filtered = b.filtered;
        a.size = b.size;
        a.musicType = b.musicType;
    }

    int isMusicFile(String b) {
        String a = new String(b);
        for (int i = 0; i < music_postfix.length; i++) {
            if (stringendcompare(a, music_postfix[i])) {
                return i;
            }
        }
        return -1;
    }

    boolean stringendcompare(String a, String b) {
        char[] a_tmp = a.toCharArray();
        char[] b_tmp = b.toCharArray();
        int a_length = a.length();
        int b_length = b.length();
        int a_begin = a_length - b_length;
        if (a_begin > 0 && a_tmp[a_begin] == '.') {
            for (int i = 1; i < b_length; i++) {
                if (!(a_tmp[a_begin + i] == b_tmp[i] || a_tmp[a_begin + i] == b_tmp[i] - ' ' || a_tmp[a_begin + i] == b_tmp[i] - 65504)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    void init_type_array() {
        for (int i = 0; i < music_postfix.length; i++) {
            this.size_postfix[i] = 0;
        }
    }

    void init_pit_postfix() {
        this.pit_postfix[0] = 0;
        for (int i = 1; i < music_postfix.length; i++) {
            this.pit_postfix[i] = this.size_postfix[i - 1] + this.pit_postfix[i - 1];
        }
    }

    void init_type_compositor_file(int size) {
        for (int i = 0; i < size; i++) {
            FileInfo tmp = new FileInfo();
            this.type_compositor_file.add(tmp);
        }
    }

    public static String getHexString(String s, String charset) {
        byte[] b = null;
        StringBuffer sb = new StringBuffer();
        try {
            b = s.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (byte b2 : b) {
            sb.append(Integer.toHexString(b2 & 255));
        }
        return sb.toString();
    }

    public int pinyin_compare(String a, String b) {
        String charset = new String("gb2312");
        Comparator cmp = Collator.getInstance(Locale.CHINA);
        char[] a_tmp = a.toCharArray();
        char[] b_tmp = b.toCharArray();
        if (((a_tmp[0] > 'a' && a_tmp[0] < 'z') || (a_tmp[0] > 'A' && a_tmp[0] < 'Z')) && ((b_tmp[0] > 'a' && b_tmp[0] < 'z') || (b_tmp[0] > 'A' && b_tmp[0] < 'Z'))) {
            return cmp.compare(a, b);
        }
        for (int i = 0; i < a.length() && i < b.length(); i++) {
            int mcompareTo = getHexString(new String(new char[]{a_tmp[i]}), charset).compareTo(getHexString(new String(new char[]{b_tmp[i]}), charset));
            if (mcompareTo > 0) {
                return 1;
            }
            if (mcompareTo < 0) {
                return -1;
            }
        }
        return a.length() - b.length();
    }

    public int QuickSort(ArrayList<FileInfo> pData, int left, int right) {
        int i = left;
        int j = right;
        if (right == 0) {
            return 0;
        }
        FileInfo middle = new FileInfo(pData.get(left));
        FileInfo temp = new FileInfo();
        while (true) {
            i++;
            if (i >= right - 1 || pinyin_compare(pData.get(i).name, middle.name) >= 0) {
                do {
                    j--;
                    if (j <= left) {
                        break;
                    }
                } while (pinyin_compare(pData.get(j).name, middle.name) > 0);
                if (i >= j) {
                    break;
                }
                evaluate(temp, pData.get(i));
                evaluate(pData.get(i), pData.get(j));
                evaluate(pData.get(j), temp);
            }
        }
        evaluate(pData.get(left), pData.get(j));
        evaluate(pData.get(j), middle);
        if (left < j) {
            QuickSort(pData, left, j);
        }
        if (right > i) {
            QuickSort(pData, i, right);
        }
        return 1;
    }

    void first_fill() {
        this.folder_array = new ArrayList<FileInfo>();
        FileInfo SYSpath = new FileInfo();
        SYSpath.name = new String("flash");
        SYSpath.path = FLASH_PATH;
        SYSpath.parent = FLASH_PATH;
        Drawable ds = this.resources.getDrawable(R.drawable.flashmemery);
        SYSpath.icon = ds;
        this.folder_array.add(SYSpath);
        if (Environment.getExternalStorageState().equals("mounted")) {
            FileInfo SDcard = new FileInfo();
            SDcard.name = new String("Sdcard");
            SDcard.path = SD_PATH;
            SDcard.parent = SD_PATH;
            Drawable d = this.resources.getDrawable(R.drawable.sdcard);
            SDcard.icon = d;
            this.folder_array.add(SDcard);
        }
        if (new File(HOST_PATH).exists()) {
            FileInfo USBpath = new FileInfo();
            USBpath.name = new String("USB");
            USBpath.path = HOST_PATH;
            USBpath.parent = HOST_PATH;
            Drawable usbicon = this.resources.getDrawable(R.drawable.flashmemery);
            USBpath.icon = usbicon;
            this.folder_array.add(USBpath);
        }
        this.infirInter = true;
        Folder_Adapter drawerAdapter = new Folder_Adapter(this, this.folder_array);
        this.main_ListView.setAdapter((ListAdapter) drawerAdapter);
        this.isfinish = true;
    }

    boolean searchfile(ArrayList<FileInfo> searcharray, ArrayList<FileInfo> savearray, String filename) {
        boolean ret = false;
        init_type_array();
        this.cur = this.resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.audiocols, "is_music=1", null, "title_key");
        String searchstr = new String(filename);
        String finalsearchstr = new String(searchstr.toLowerCase());
        if (!this.cur.moveToFirst()) {
            return ret;
        }
        do {
            new String();
            new String();
            String name = this.cur.getString(this.cur.getColumnIndex("_display_name"));
            String path = this.cur.getString(this.cur.getColumnIndex("_data"));
            String finalname = new String(name.toLowerCase());
            Log.d("", "finalname = " + finalname);
            if (finalname.contains(finalsearchstr)) {
                FileInfo searchfileinfo = new FileInfo();
                File searchfile = new File(path);
                searchfileinfo.name = new String(name);
                searchfileinfo.path = new String(path);
                searchfileinfo.parent = new String(searchfile.getParent());
                searchfileinfo.icon = this.resources.getDrawable(R.drawable.music_icon);
                searchfileinfo.musicType = isMusicFile(name);
                searchfileinfo.size = searchfile.length();
                savearray.add(searchfileinfo);
                int[] iArr = this.size_postfix;
                int i = searchfileinfo.musicType;
                iArr[i] = iArr[i] + 1;
                this.searchResult = true;
                ret = true;
            }
        } while (this.cur.moveToNext());
        return ret;
    }

    public String chinastr(byte[] changestr, String type) {
        try {
            String retstr = new String(changestr, type);
            return retstr;
        } catch (Exception ex) {
            Log.d("MediaPlaybackActivity", "china change error~" + ex);
            return null;
        }
    }

    private void deletefile() {
        this.cur = this.resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.audiocols, "is_music=1", null, "title_key");
        this.cur.moveToPosition(this.delpos);
        int id_idx = this.cur.getColumnIndexOrThrow("_id");
        Log.d("Folder_manage_1_6", "id_idx = " + id_idx);
        int[] list = {this.cur.getInt(id_idx)};
        Log.d("Folder_manage_1_6", "list[0] = " + list[0]);
        MusicUtils.deleteTracks(this, list);
        this.folder_array.remove(this.delpos - 1);
        Folder_Adapter drawerAdapter = new Folder_Adapter(this, this.folder_array);
        this.main_ListView.setAdapter((ListAdapter) drawerAdapter);
    }

    @Override // android.widget.AdapterView.OnItemLongClickListener
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        return false;
    }
}
