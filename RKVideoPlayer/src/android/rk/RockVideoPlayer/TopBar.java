package android.rk.RockVideoPlayer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/* loaded from: classes.dex */
public class TopBar extends RelativeLayout {
    static final int BAR_DEFAULT_HEIGHT = 40;
    static final int CHILD_ID_BASE = 2000;
    static final int ICON_DEFAULT_HEIGHT = 40;
    static final int ICON_DEFAULT_WIDTH = 25;
    static boolean LOG_LOCAL_SWITCH = false;
    private static final String TOPBAR_HOME = "home";
    private static final String TOPBAR_NEXT = "next";
    private static final String TOPBAR_PAUSE = "pause";
    private static final String TOPBAR_PRE = "pre";
    private static final String TOPBAR_RETURN = "return";
    private static final String TOPBAR_VOLUMEMINUS = "volumeminus";
    private static final String TOPBAR_VOLUMEPLUS = "volumeplus";
    static final int USR_ALIGN_LEFT = 0;
    static final int USR_ALIGN_RIGHT = 1;
    private Context mContext;
    private TopBarControl mPlayer;
    private boolean mShow;
    private final boolean DEBUG = true;
    private final String TAG = "TopBar";
    private int mChildCnt = USR_ALIGN_LEFT;
    boolean is_down = false;
    private final int ADJUST_VOLUME_DELAY = 200;
    private final int ADJUST_TIME_DELAY = 300;
    private final int VOLUMEPLUS = 1;
    private final int VOLUMEMINUS = -1;
    View.OnClickListener mHomeListener = new View.OnClickListener() { // from class: android.rk.RockVideoPlayer.TopBar.1
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            TopBar.this.mPlayer.TopBar_home();
        }
    };
    View.OnClickListener mPauseListener = new View.OnClickListener() { // from class: android.rk.RockVideoPlayer.TopBar.2
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            TopBar.this.mPlayer.TopBar_pause();
        }
    };
    View.OnClickListener mReturnListener = new View.OnClickListener() { // from class: android.rk.RockVideoPlayer.TopBar.3
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            TopBar.this.mPlayer.TopBar_return();
        }
    };
    View.OnTouchListener mVolumePlusTouchListener = new View.OnTouchListener() { // from class: android.rk.RockVideoPlayer.TopBar.4
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                TopBar.this.is_down = true;
                TopBar.this.mPlayer.TopBar_volume(1);
                TopBar.this.getButton(TopBar.TOPBAR_VOLUMEPLUS).setBackgroundResource(R.drawable.add_pressed);
                TopBar.this.maddHandler.postDelayed(TopBar.this.maddRun, 200L);
            } else if (event.getAction() == 1) {
                TopBar.this.is_down = false;
                TopBar.this.maddHandler.removeCallbacks(TopBar.this.maddRun);
                TopBar.this.getButton(TopBar.TOPBAR_VOLUMEPLUS).setBackgroundResource(R.drawable.add_normal);
            }
            return true;
        }
    };
    private Handler maddHandler = new Handler();
    private Runnable maddRun = new Runnable() { // from class: android.rk.RockVideoPlayer.TopBar.5
        @Override // java.lang.Runnable
        public void run() {
            TopBar.this.maddHandler.removeCallbacks(TopBar.this.maddRun);
            if (TopBar.this.is_down) {
                TopBar.this.mPlayer.TopBar_volume(1);
                TopBar.this.maddHandler.postDelayed(TopBar.this.maddRun, 200L);
            }
        }
    };
    View.OnTouchListener mVolumeMinusTouchListener = new View.OnTouchListener() { // from class: android.rk.RockVideoPlayer.TopBar.6
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                TopBar.this.is_down = true;
                TopBar.this.mPlayer.TopBar_volume(-1);
                TopBar.this.getButton(TopBar.TOPBAR_VOLUMEMINUS).setBackgroundResource(R.drawable.sub_pressed);
            } else if (event.getAction() == 1) {
                TopBar.this.is_down = false;
                TopBar.this.msubHandler.removeCallbacks(TopBar.this.msubRun);
                TopBar.this.getButton(TopBar.TOPBAR_VOLUMEMINUS).setBackgroundResource(R.drawable.sub_normal);
            }
            return true;
        }
    };
    private Handler msubHandler = new Handler();
    private Runnable msubRun = new Runnable() { // from class: android.rk.RockVideoPlayer.TopBar.7
        @Override // java.lang.Runnable
        public void run() {
            TopBar.this.msubHandler.removeCallbacks(TopBar.this.msubRun);
            if (TopBar.this.is_down) {
                TopBar.this.mPlayer.TopBar_volume(-1);
                TopBar.this.msubHandler.postDelayed(TopBar.this.msubRun, 200L);
            }
        }
    };
    View.OnTouchListener mPreListener = new View.OnTouchListener() { // from class: android.rk.RockVideoPlayer.TopBar.8
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                TopBar.this.is_down = true;
                TopBar.this.mPlayer.TopBar_prev(1, 300);
                TopBar.this.mpreHandler.postDelayed(TopBar.this.mpreRun, 300L);
            } else if (event.getAction() == 1) {
                TopBar.this.is_down = false;
                TopBar.this.mpreHandler.removeCallbacks(TopBar.this.mpreRun);
            }
            return true;
        }
    };
    private Handler mpreHandler = new Handler();
    private Runnable mpreRun = new Runnable() { // from class: android.rk.RockVideoPlayer.TopBar.9
        int count = 1;

        @Override // java.lang.Runnable
        public void run() {
            this.count++;
            TopBar.this.mpreHandler.removeCallbacks(TopBar.this.mpreRun);
            if (TopBar.this.is_down) {
                TopBar.this.mPlayer.TopBar_prev(this.count, this.count * 300);
                TopBar.this.mpreHandler.postDelayed(TopBar.this.mpreRun, 300L);
            }
        }
    };
    View.OnTouchListener mNextListener = new View.OnTouchListener() { // from class: android.rk.RockVideoPlayer.TopBar.10
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                TopBar.this.is_down = true;
                TopBar.this.mPlayer.TopBar_next(1, 300);
                TopBar.this.mnextHandler.postDelayed(TopBar.this.mnextRun, 300L);
            } else if (event.getAction() == 1) {
                TopBar.this.is_down = false;
                TopBar.this.mnextHandler.removeCallbacks(TopBar.this.mnextRun);
            }
            return true;
        }
    };
    private Handler mnextHandler = new Handler();
    private Runnable mnextRun = new Runnable() { // from class: android.rk.RockVideoPlayer.TopBar.11
        int count = 1;

        @Override // java.lang.Runnable
        public void run() {
            this.count++;
            TopBar.this.mnextHandler.removeCallbacks(TopBar.this.mnextRun);
            if (TopBar.this.is_down) {
                TopBar.this.mPlayer.TopBar_next(this.count, this.count * 300);
                TopBar.this.mnextHandler.postDelayed(TopBar.this.mnextRun, 300L);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface TopBarControl {
        void TopBar_home();

        void TopBar_next(int i, int i2);

        void TopBar_pause();

        void TopBar_prev(int i, int i2);

        void TopBar_return();

        void TopBar_volume(int i);
    }

    public void LOG(String msg) {
        Log.d("TopBar", msg);
    }

    public TopBar(Context context) {
        super(context);
        this.mContext = context;
        initTopBar();
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initTopBar();
    }

    private void initTopBar() {
        setBackgroundResource(R.drawable.statusbar_background);
        setGravity(48);
        addChild(TOPBAR_HOME, null, R.drawable.home_btn, 65, 40, USR_ALIGN_LEFT, true, this.mHomeListener, null);
        addChild(TOPBAR_VOLUMEPLUS, null, R.drawable.add_normal, 65, 40, USR_ALIGN_LEFT, true, null, this.mVolumePlusTouchListener);
        addChild(TOPBAR_VOLUMEMINUS, null, R.drawable.sub_normal, 65, 40, USR_ALIGN_LEFT, true, null, this.mVolumeMinusTouchListener);
        addChild(TOPBAR_PRE, null, R.drawable.pre_btn, 65, 40, USR_ALIGN_LEFT, true, null, this.mPreListener);
        addChild(TOPBAR_PAUSE, null, R.drawable.video_button_play, 65, 40, USR_ALIGN_LEFT, true, this.mPauseListener, null);
        addChild(TOPBAR_NEXT, null, R.drawable.next_btn, 65, 40, USR_ALIGN_LEFT, true, null, this.mNextListener);
        addChild(TOPBAR_RETURN, null, R.drawable.back_btn, 65, 40, USR_ALIGN_LEFT, true, this.mReturnListener, null);
    }

    /* loaded from: classes.dex */
    public static class TopBarButton extends Button {
        public boolean enable = true;
        public int mAlign;
        public String mName;

        public TopBarButton(Context context, int align, String name) {
            super(context);
            this.mAlign = align;
            this.mName = new String(name);
        }
    }

    public int addChild(String name, CharSequence text, int iconid, int width, int height, int align, boolean clickable, View.OnClickListener l, View.OnTouchListener n) {
        int relativeviewid = -1;
        for (int id = this.mChildCnt; id > 0 && this.mChildCnt > 0; id--) {
            TopBarButton child = (TopBarButton) getChildAt(id - 1);
            if (child.mAlign == align && child.getId() > relativeviewid) {
                relativeviewid = child.getId();
            }
        }
        TopBarButton child2 = new TopBarButton(getContext(), align, name);
        if (width == 0) {
            width = ICON_DEFAULT_WIDTH;
        }
        if (height == 0) {
            height = 40;
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        if (relativeviewid == -1) {
            if (align == 0) {
                params.addRule(9);
            } else {
                params.addRule(11);
            }
        } else if (align == 0) {
            params.addRule(1, relativeviewid);
        } else {
            params.addRule(USR_ALIGN_LEFT, relativeviewid);
        }
        params.addRule(15);
        child2.setGravity(16);
        child2.setLayoutParams(params);
        child2.setBackgroundResource(iconid);
        child2.setText(text);
        child2.setClickable(clickable);
        if (clickable) {
            child2.setOnClickListener(l);
        }
        if (clickable) {
            child2.setOnTouchListener(n);
        }
        int id2 = this.mChildCnt;
        this.mChildCnt = id2 + 1;
        child2.setId(id2 + CHILD_ID_BASE);
        addView(child2);
        return id2 + CHILD_ID_BASE;
    }

    public int removeChild(int id) {
        TopBarButton child;
        int index = USR_ALIGN_LEFT;
        while (true) {
            int index2 = index + 1;
            if (index >= this.mChildCnt) {
                break;
            }
            TopBarButton child2 = (TopBarButton) getChildAt(index2 - 1);
            if (child2.getId() == id) {
                int align = child2.mAlign;
                removeView(child2);
                break;
            }
            index = index2;
        }
        int index3 = this.mChildCnt - 1;
        this.mChildCnt = index3;
        while (true) {
            index3--;
            if (index3 < 0 || (child = (TopBarButton) getChildAt(index3)) == null) {
                break;
            }
            int tmpid = child.getId();
            if (tmpid > id) {
                child.setId(tmpid - 1);
            }
        }
        logChildId();
        for (int index4 = 1; index4 <= this.mChildCnt; index4++) {
            TopBarButton child3 = (TopBarButton) getChildAt(index4 - 1);
            int align2 = child3.mAlign;
            int relativeviewid = getRelativeViewId(child3.getId(), align2);
            int width = child3.getLayoutParams().width;
            int height = child3.getLayoutParams().height;
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
            if (relativeviewid == -1) {
                if (align2 == 0) {
                    params.addRule(9);
                } else {
                    params.addRule(11);
                }
            } else if (align2 == 0) {
                params.addRule(1, relativeviewid);
            } else {
                params.addRule(USR_ALIGN_LEFT, relativeviewid);
            }
            LOG(child3.mName + "    id = " + child3.getId() + "    align=" + child3.mAlign + "    relid = " + relativeviewid);
            params.addRule(15);
            child3.setLayoutParams(params);
        }
        return id;
    }

    public void removeChildByName(String name) {
        TopBarButton child = getButton(name);
        if (child != null) {
            removeChild(child.getId());
        }
    }

    public TopBarButton getButton(String name) {
        TopBarButton button;
        int index = USR_ALIGN_LEFT;
        while (true) {
            button = (TopBarButton) getChildAt(index);
            if (button == null || button.mName.equals(name)) {
                break;
            }
            index++;
        }
        return button;
    }

    private void logChildId() {
        if (LOG_LOCAL_SWITCH) {
            int index = USR_ALIGN_LEFT;
            while (true) {
                TopBarButton child = (TopBarButton) getChildAt(index);
                if (child == null) {
                    LOG("log end");
                    return;
                } else {
                    LOG("child id = " + child.getId() + "    align = " + child.mAlign);
                    index++;
                }
            }
        }
    }

    private int getRelativeViewId(int id, int align) {
        int ret = -1;
        for (int index = USR_ALIGN_LEFT; index < id - CHILD_ID_BASE; index++) {
            TopBarButton child = (TopBarButton) getChildAt(index);
            if (child.mAlign == align) {
                ret = child.getId();
            }
        }
        return ret;
    }

    public void setTopBarPlayer(TopBarControl player) {
        this.mPlayer = player;
        LOG(">>>>>>>>>>>>>>>>>>>> TopControl  player= <<<<<<<<<<<<<<" + this.mPlayer);
    }

    public boolean isShowing() {
        return this.mShow;
    }

    public void show() {
        setVisibility(USR_ALIGN_LEFT);
        this.mShow = true;
    }

    public void hide() {
        setVisibility(4);
        this.mShow = false;
    }
}
