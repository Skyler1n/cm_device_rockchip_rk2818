package android.rk.RockVideoPlayer;

import java.io.File;

/* loaded from: classes.dex */
public class DebugUtil {
    public static final boolean PACKAGE_DEBUG = true;

    public static boolean isDebugFileExist() {
        File file = new File("/flash/rkvideodebug");
        return file.exists();
    }
}
