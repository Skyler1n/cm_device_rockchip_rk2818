package android.rk.RockAudioPlayer;

import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
class TrackInfo {
    boolean alreadyin;
    Drawable icon;
    int id;
    String name;
    int[] playlistid;

    public TrackInfo() {
    }

    public TrackInfo(TrackInfo info) {
        this.name = new String(info.name.toString());
        this.icon = info.icon;
        this.id = info.id;
        this.playlistid = info.playlistid;
    }
}
