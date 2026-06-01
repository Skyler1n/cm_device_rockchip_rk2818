package android.rk.RockVideoPlayer;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.util.Formatter;
import java.util.Locale;

/* loaded from: classes.dex */
public class VideoListAdapter extends SimpleCursorAdapter implements SectionIndexer {
    private static final boolean DEBUG = true;
    private static final String TAG = "VideoListAdapter";
    int bookmarkIdx;
    int durationIdx;
    int idIdx;
    private RockVideoPlayer mActivity;
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private AlphabetIndexer mIndexer;
    private AsyncQueryHandler mQueryHandler;
    int mimetypeIdx;
    int nameIdx;
    int pahtIdx;
    int resource;
    int sizeIdx;
    Uri uriIdx;

    public void LOG(String msg) {
        Log.d(TAG, msg);
    }

    public VideoListAdapter(Context context, RockVideoPlayer currentactivity, int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to);
        this.mActivity = null;
        this.mActivity = currentactivity;
        getColumnIndices(cursor);
        this.mQueryHandler = new QueryHandler(context.getContentResolver());
        LOG("Built mQueryHandler = " + this.mQueryHandler);
    }

    public void setActivity(RockVideoPlayer newactivity) {
        this.mActivity = newactivity;
    }

    /* loaded from: classes.dex */
    class ViewHolder {
        CharArrayBuffer buffer1;
        char[] buffer2;
        ImageView video_icon;
        TextView video_name;
        TextView video_path;
        TextView video_size;
        TextView video_time;
        TextView video_type;

        ViewHolder() {
        }
    }

    /* loaded from: classes.dex */
    public class QueryHandler extends AsyncQueryHandler {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        QueryHandler(ContentResolver res) {
            super(res);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            VideoListAdapter.this.mActivity.initVideoCursor(cursor);
        }
    }

    public AsyncQueryHandler getQueryHandler() {
        return this.mQueryHandler;
    }

    private void getColumnIndices(Cursor cur) {
        LOG("Enter getColumnIndices() and cur = " + cur);
        Uri uri = MediaStore.Video.Media.getContentUri("external");
        if (cur != null) {
            cur.moveToFirst();
            this.nameIdx = cur.getColumnIndexOrThrow("_display_name");
            LOG("nameIdx= " + this.nameIdx);
            this.uriIdx = ContentUris.withAppendedId(uri, cur.getColumnIndexOrThrow("_id"));
            LOG("uriIdx= " + this.uriIdx);
            this.idIdx = cur.getColumnIndexOrThrow("_id");
            this.mimetypeIdx = cur.getColumnIndexOrThrow("mime_type");
            LOG("mimetypeIdx= " + this.mimetypeIdx);
            this.bookmarkIdx = cur.getColumnIndexOrThrow("bookmark");
            LOG("bookmarkIdx= " + this.bookmarkIdx);
            this.durationIdx = cur.getColumnIndexOrThrow("duration");
            LOG("durationIdx= " + this.durationIdx);
            this.sizeIdx = cur.getColumnIndexOrThrow("_size");
            LOG("sizeIdx= " + this.sizeIdx);
            this.pahtIdx = cur.getColumnIndexOrThrow("_data");
            LOG("pahtIdx= " + this.pahtIdx);
        }
    }

    @Override // android.widget.ResourceCursorAdapter, android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = super.newView(context, cursor, parent);
        ViewHolder vh = new ViewHolder();
        vh.video_icon = (ImageView) v.findViewById(R.id.video_image);
        vh.video_name = (TextView) v.findViewById(R.id.video_name);
        vh.video_time = (TextView) v.findViewById(R.id.time_info);
        vh.video_type = (TextView) v.findViewById(R.id.type_info);
        vh.video_size = (TextView) v.findViewById(R.id.size_info);
        vh.video_path = (TextView) v.findViewById(R.id.path_info);
        vh.buffer1 = new CharArrayBuffer(100);
        vh.buffer2 = new char[200];
        v.setTag(vh);
        return v;
    }

    @Override // android.widget.SimpleCursorAdapter, android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        cursor.copyStringToBuffer(this.nameIdx, vh.buffer1);
        vh.video_name.setText(vh.buffer1.data, 0, vh.buffer1.sizeCopied);
        this.mFormatBuilder = new StringBuilder();
        this.mFormatter = new Formatter(this.mFormatBuilder, Locale.getDefault());
        vh.video_time.setText(makeTimeString(cursor.getInt(this.bookmarkIdx), cursor.getInt(this.durationIdx)));
        vh.video_type.setText(cursor.getString(this.mimetypeIdx));
        vh.video_size.setText(makeSizeString(cursor.getInt(this.sizeIdx)));
        vh.video_path.setText(cursor.getString(this.pahtIdx));
        vh.video_icon.setImageResource(R.drawable.app_icon);
    }

    @Override // android.widget.CursorAdapter
    public void changeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor != this.mActivity.mVideoCursor) {
            super.changeCursor(cursor);
            this.mActivity.mVideoCursor = cursor;
            getColumnIndices(cursor);
        }
    }

    @Override // android.widget.CursorAdapter
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        if (this.mConstraintIsValid && ((s == null && this.mConstraint == null) || (s != null && s.equals(this.mConstraint)))) {
            return getCursor();
        }
        Cursor c = this.mActivity.getVideoCursor(null);
        this.mConstraint = s;
        this.mConstraintIsValid = true;
        return c;
    }

    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        this.mFormatBuilder.setLength(0);
        return hours > 0 ? this.mFormatter.format("%02d:%02d:%02d", Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)).toString() : this.mFormatter.format("%02d:%02d", Integer.valueOf(minutes), Integer.valueOf(seconds)).toString();
    }

    public String makeTimeString(int time1, int time2) {
        StringBuilder time = new StringBuilder();
        time.append(stringForTime(time1) + '/' + stringForTime(time2));
        return time.toString();
    }

    public String makeSizeString(int size) {
        StringBuilder sizeBuilder = new StringBuilder();
        if (size <= 0) {
            sizeBuilder.append("0 K");
            return sizeBuilder.toString();
        }
        int sizeK = size / 1024;
        int sizeM = sizeK / 1024;
        if (sizeK <= 0 || sizeK >= 1024) {
            sizeBuilder.append(sizeM);
            sizeBuilder.append(" M");
            return sizeBuilder.toString();
        }
        sizeBuilder.append(sizeK);
        sizeBuilder.append(" K");
        return sizeBuilder.toString();
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int section) {
        int pos = this.mIndexer.getPositionForSection(section);
        return pos;
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        if (this.mIndexer != null) {
            return this.mIndexer.getSections();
        }
        return null;
    }
}
