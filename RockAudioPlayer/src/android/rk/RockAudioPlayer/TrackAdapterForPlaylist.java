package android.rk.RockAudioPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class TrackAdapterForPlaylist extends ArrayAdapter<TrackInfo> {
    private final LayoutInflater mInflater;

    public TrackAdapterForPlaylist(Context context, ArrayList<TrackInfo> apps) {
        super(context, 0, apps);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        TrackInfo info = getItem(position);
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.track_item_forplaylist, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setBackgroundDrawable(info.icon);
        textView.setText(info.name);
        return convertView;
    }
}
