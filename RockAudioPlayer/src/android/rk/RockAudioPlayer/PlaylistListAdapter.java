package android.rk.RockAudioPlayer;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.rk.RockAudioPlayer.RockAudioPlayer;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/* loaded from: classes.dex */
public class PlaylistListAdapter extends SimpleCursorAdapter implements RockAudioPlayer.Def {
    private static final long RECENTLY_ADDED_PLAYLIST = -1;
    private RockAudioPlayer mActivity;
    int mIdIdx;
    private AsyncQueryHandler mQueryHandler;
    int mTitleIdx;
    public View.OnClickListener playlistListenr;
    private final String TAG = "PlaylistListAdapter";
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;

    /* loaded from: classes.dex */
    class QueryHandler extends AsyncQueryHandler {
        QueryHandler(ContentResolver res) {
            super(res);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            PlaylistListAdapter.this.mActivity.init(cursor, 4);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PlaylistListAdapter(Context context, RockAudioPlayer currentactivity, int layout, Cursor cursor, String[] from, int[] to, View.OnClickListener PlaylistListener) {
        super(context, layout, cursor, from, to);
        this.mActivity = null;
        this.mActivity = currentactivity;
        getColumnIndices(cursor);
        this.mQueryHandler = new QueryHandler(context.getContentResolver());
        this.playlistListenr = PlaylistListener;
    }

    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
            this.mTitleIdx = cursor.getColumnIndexOrThrow("name");
            this.mIdIdx = cursor.getColumnIndexOrThrow("_id");
        }
    }

    public void setActivity(RockAudioPlayer newactivity) {
        this.mActivity = newactivity;
    }

    public AsyncQueryHandler getQueryHandler() {
        return this.mQueryHandler;
    }

    @Override // android.widget.SimpleCursorAdapter, android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.playlist_playlistname);
        String name = cursor.getString(this.mTitleIdx);
        tv.setText(name);
        cursor.getLong(this.mIdIdx);
        ImageView iv = (ImageView) view.findViewById(R.id.playlist_playlistimage);
        iv.setImageResource(R.drawable.playlist_playlistimage);
        ImageView imageView = (ImageView) view.findViewById(R.id.playlist_operate_add);
        View v = view.findViewById(R.id.playlist_operate_add);
        v.setOnClickListener(this.playlistListenr);
        ImageView imageView2 = (ImageView) view.findViewById(R.id.playlist_operate_delete);
        View v2 = view.findViewById(R.id.playlist_operate_delete);
        v2.setOnClickListener(this.playlistListenr);
    }

    @Override // android.widget.CursorAdapter
    public void changeCursor(Cursor cursor) {
        if (cursor != this.mActivity.mPlaylistCursor) {
            this.mActivity.mPlaylistCursor = cursor;
            super.changeCursor(cursor);
            getColumnIndices(cursor);
        }
    }

    @Override // android.widget.CursorAdapter
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        if (this.mConstraintIsValid && ((s == null && this.mConstraint == null) || (s != null && s.equals(this.mConstraint)))) {
            return getCursor();
        }
        Cursor c = this.mActivity.getPlaylistCursor(null, s);
        this.mConstraint = s;
        this.mConstraintIsValid = true;
        return c;
    }
}
