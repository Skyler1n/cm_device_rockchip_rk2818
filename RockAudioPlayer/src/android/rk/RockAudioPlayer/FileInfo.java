package android.rk.RockAudioPlayer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FileInfo {
    Cursor cur;
    boolean filtered;
    Drawable icon;
    Intent.ShortcutIconResource iconResource;
    int itemType;
    int musicType;
    String name;
    String parent;
    String path;
    int position;
    long size;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FileInfo() {
        this.itemType = 0;
    }

    public FileInfo(FileInfo info) {
        this.name = new String(info.name.toString());
        this.icon = info.icon;
        this.filtered = info.filtered;
        this.size = info.size;
        this.path = new String(info.path);
        this.parent = new String(info.parent);
        this.musicType = info.musicType;
        this.itemType = info.itemType;
    }

    public String toString() {
        return this.path.toString();
    }
}
