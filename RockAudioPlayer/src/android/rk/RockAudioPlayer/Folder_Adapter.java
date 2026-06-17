package android.rk.RockAudioPlayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class Folder_Adapter extends ArrayAdapter<FileInfo> {
    private final LayoutInflater mInflater;

    public Folder_Adapter(Context context, ArrayList<FileInfo> apps) {
        super(context, 0, apps);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        FileInfo info = getItem(position);
        if (convertView == null) {
            convertView = this.mInflater.inflate(R.layout.folder_listview_adapter, parent, false);
        }
        TextView textView = (TextView) convertView;
        textView.setCompoundDrawablesWithIntrinsicBounds(info.icon, (Drawable) null, (Drawable) null, (Drawable) null);
        textView.setText(info.name);
        return convertView;
    }
}
