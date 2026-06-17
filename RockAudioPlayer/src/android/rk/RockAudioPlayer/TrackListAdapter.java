package android.rk.RockAudioPlayer;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.RemoteException;
import android.rk.RockAudioPlayer.RockAudioPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/* loaded from: classes.dex */
public class TrackListAdapter extends SimpleCursorAdapter implements SectionIndexer, RockAudioPlayer.Def {
    private RockAudioPlayer mActivity;
    int mAlbumIdx;
    int mArtistIdx;
    int mAudioIdIdx;
    boolean mDisableNowPlayingIndicator;
    int mDurationIdx;
    private AlphabetIndexer mIndexer;
    int mIsAddtoPlaylist;
    boolean mIsNowPlaying;
    private AsyncQueryHandler mQueryHandler;
    int mTitleIdx;
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    private final String TAG = "TrackListAdapter";
    private final StringBuilder mBuilder = new StringBuilder();
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;

    /* loaded from: classes.dex */
    class ViewHolder {
        TextView artistname;
        CharArrayBuffer buffer1;
        char[] buffer2;
        TextView duration;
        ImageView play_ornot_indicator;
        TextView trackname;

        ViewHolder() {
        }
    }

    /* loaded from: classes.dex */
    class QueryHandler extends AsyncQueryHandler {
        QueryHandler(ContentResolver res) {
            super(res);
        }

        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            TrackListAdapter.this.mActivity.init(cursor, 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TrackListAdapter(Context context, RockAudioPlayer currentactivity, int layout, Cursor cursor, String[] from, int[] to, boolean isnowplaying, int isaddtoplaylist) {
        super(context, layout, cursor, from, to);
        this.mActivity = null;
        this.mActivity = currentactivity;
        getColumnIndices(cursor);
        this.mIsNowPlaying = isnowplaying;
        this.mIsAddtoPlaylist = isaddtoplaylist;
        this.mUnknownArtist = context.getString(R.string.unknown_artist_name);
        this.mUnknownAlbum = context.getString(R.string.unknown_album_name);
        this.mQueryHandler = new QueryHandler(context.getContentResolver());
    }

    public void setActivity(RockAudioPlayer newactivity) {
        this.mActivity = newactivity;
    }

    public AsyncQueryHandler getQueryHandler() {
        return this.mQueryHandler;
    }

    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
            this.mTitleIdx = cursor.getColumnIndexOrThrow("_display_name");
            this.mArtistIdx = cursor.getColumnIndexOrThrow("artist");
            this.mAlbumIdx = cursor.getColumnIndexOrThrow("album");
            this.mDurationIdx = cursor.getColumnIndexOrThrow("duration");
            try {
                this.mAudioIdIdx = cursor.getColumnIndexOrThrow("audio_id");
            } catch (IllegalArgumentException e) {
                this.mAudioIdIdx = cursor.getColumnIndexOrThrow("_id");
            }
            if (this.mIndexer != null) {
                this.mIndexer.setCursor(cursor);
                return;
            }
            String alpha = this.mActivity.getString(17040127);
            this.mIndexer = new MusicAlphabetIndexer(cursor, this.mTitleIdx, alpha);
        }
    }

    @Override // android.widget.ResourceCursorAdapter, android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = super.newView(context, cursor, parent);
        ViewHolder vh = new ViewHolder();
        vh.trackname = (TextView) v.findViewById(R.id.track_name);
        vh.artistname = (TextView) v.findViewById(R.id.track_artistname);
        vh.duration = (TextView) v.findViewById(R.id.track_duration);
        vh.play_ornot_indicator = (ImageView) v.findViewById(R.id.play_ornot_icon);
        vh.buffer1 = new CharArrayBuffer(100);
        vh.buffer2 = new char[200];
        v.setTag(vh);
        return v;
    }

    @Override // android.widget.SimpleCursorAdapter, android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        cursor.copyStringToBuffer(this.mTitleIdx, vh.buffer1);
        vh.trackname.setText(vh.buffer1.data, 0, vh.buffer1.sizeCopied);
        int secs = cursor.getInt(this.mDurationIdx) / 1000;
        if (secs == 0) {
            vh.duration.setText("");
        } else {
            vh.duration.setText(MusicUtils.makeTimeString(context, secs));
        }
        StringBuilder builder = this.mBuilder;
        builder.delete(0, builder.length());
        String name = cursor.getString(this.mArtistIdx);
        if (name == null || name.equals("<unknown>")) {
            builder.append(this.mUnknownArtist);
        } else {
            builder.append(name);
        }
        int len = builder.length();
        if (vh.buffer2.length < len) {
            vh.buffer2 = new char[len];
        }
        builder.getChars(0, len, vh.buffer2, 0);
        vh.artistname.setText(vh.buffer2, 0, len);
        ImageView iv = vh.play_ornot_indicator;
        int id = -1;
        if (MusicUtils.sService != null) {
            try {
                if (this.mIsNowPlaying) {
                    id = MusicUtils.sService.getQueuePosition();
                } else {
                    id = MusicUtils.sService.getAudioId();
                }
            } catch (RemoteException e) {
            }
        }
        if ((this.mIsNowPlaying && cursor.getPosition() == id) || (!this.mIsNowPlaying && !this.mDisableNowPlayingIndicator && cursor.getInt(this.mAudioIdIdx) == id)) {
            iv.setImageResource(R.drawable.track_playing_icon);
            iv.setVisibility(0);
        } else if (this.mIsAddtoPlaylist == 12) {
            iv.setImageResource(R.drawable.playlist_trackchoice_click);
            iv.setVisibility(0);
        } else if (this.mIsAddtoPlaylist == 13) {
            iv.setImageResource(R.drawable.playlist_trackchoice_notclick);
            iv.setVisibility(0);
        } else {
            iv.setImageResource(R.drawable.track_music_icon);
            iv.setVisibility(0);
        }
    }

    @Override // android.widget.CursorAdapter
    public void changeCursor(Cursor cursor) {
        if (cursor != this.mActivity.mTrackCursor) {
            this.mActivity.mTrackCursor = cursor;
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
        Cursor c = this.mActivity.getTrackCursor(null, s);
        this.mConstraint = s;
        this.mConstraintIsValid = true;
        return c;
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        if (this.mIndexer != null) {
            return this.mIndexer.getSections();
        }
        return null;
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int section) {
        int pos = this.mIndexer.getPositionForSection(section);
        return pos;
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int position) {
        return 0;
    }
}
