package android.rk.RockAudioPlayer;

import android.content.Context;
import android.database.CharArrayBuffer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Formatter;

/* loaded from: classes.dex */
class Song_Adapter extends ArrayAdapter {
    private static final boolean DEBUG = false;
    private static final String TAG = "VideoListAdapter";
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private final LayoutInflater mInflater;
    int resource;

    public void LOG(String msg) {
    }

    public Song_Adapter(Context context, int ResourceId, ArrayList<SearchNetFileInfo> items) {
        super(context, ResourceId, ResourceId, items);
        this.resource = ResourceId;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout SearchNetView;
        LOG("Enter getView()");
        if (position >= getCount() || position < 0) {
            return null;
        }
        SearchNetFileInfo info = (SearchNetFileInfo) getItem(position);
        if (convertView == null) {
            SearchNetView = new LinearLayout(getContext());
            this.mInflater.inflate(this.resource, (ViewGroup) SearchNetView, true);
        } else {
            SearchNetView = (LinearLayout) convertView;
        }
        SearchNetHolder vh = new SearchNetHolder();
        vh.Music_icon = (ImageView) SearchNetView.findViewById(R.id.music_icon);
        vh.msong_name = (TextView) SearchNetView.findViewById(R.id.song_name);
        vh.msong_artist = (TextView) SearchNetView.findViewById(R.id.song_artist);
        vh.msong_filesize = (TextView) SearchNetView.findViewById(R.id.song_filesize);
        vh.msong_connection_ratio = (TextView) SearchNetView.findViewById(R.id.song_connection_ratio);
        vh.buffer1 = new CharArrayBuffer(100);
        vh.buffer2 = new char[200];
        SearchNetView.setTag(vh);
        vh.Music_icon.setBackgroundDrawable(info.Icon);
        vh.msong_name.setText(info.name);
        vh.msong_artist.setText(info.Singer);
        vh.msong_filesize.setText(info.FileSize);
        vh.msong_connection_ratio.setText(info.Connection_Ratio);
        return SearchNetView;
    }

    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        this.mFormatBuilder.setLength(0);
        return this.mFormatter.format("%02d:%02d:%02d", Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)).toString();
    }

    public String makeTimeString(int time1, int time2) {
        StringBuilder time = new StringBuilder();
        time.append(stringForTime(time1) + '/' + stringForTime(time2));
        return time.toString();
    }
}
