package android.rk.RockAudioPlayer;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
class SearchNetFileInfo {
    String Album;
    String Connection_Ratio;
    String FileSize;
    String Format;
    Drawable Icon;
    String Singer;
    Intent.ShortcutIconResource iconResource;
    String name;
    String path;
    int position;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SearchNetFileInfo() {
    }

    public SearchNetFileInfo(SearchNetFileInfo info) {
        this.path = info.path;
        this.name = info.name;
        this.Singer = info.Singer;
        this.Album = info.Album;
        this.Format = info.Format;
        this.FileSize = info.FileSize;
        this.Connection_Ratio = info.Connection_Ratio;
        this.Icon = info.Icon;
    }
}
