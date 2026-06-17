package android.rk.RockAudioPlayer;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.rk.RockAudioPlayer.RockAudioPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/* loaded from: classes.dex */
public class ArtistListAdapter extends SimpleCursorAdapter implements SectionIndexer, RockAudioPlayer.Def {
    private RockAudioPlayer mActivity;
    private int mAlbumIdx;
    private final String mAlbumSongSeparator;
    private int mArtistIdIdx;
    private int mArtistIdx;
    private final Context mContext;
    private final BitmapDrawable mDefaultAlbumIcon;
    private MusicAlphabetIndexer mIndexer;
    private AsyncQueryHandler mQueryHandler;
    private final Resources mResources;
    private int mSongIdx;
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    private final String TAG = "ArtistListAdapter";
    private final StringBuilder mBuffer = new StringBuilder();
    private final Object[] mFormatArgs = new Object[1];
    private final Object[] mFormatArgs3 = new Object[3];
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;

    /* loaded from: classes.dex */
    class ViewHolder {
        ImageView artist_image;
        TextView artist_name;
        TextView artist_songsnum;

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
            ArtistListAdapter.this.mActivity.init(cursor, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArtistListAdapter(Context context, RockAudioPlayer currentactivity, int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to);
        this.mActivity = null;
        this.mActivity = currentactivity;
        this.mQueryHandler = new QueryHandler(context.getContentResolver());
        Resources r = context.getResources();
        this.mDefaultAlbumIcon = (BitmapDrawable) r.getDrawable(R.drawable.artist_image);
        this.mDefaultAlbumIcon.setFilterBitmap(false);
        this.mDefaultAlbumIcon.setDither(false);
        this.mContext = context;
        getColumnIndices(cursor);
        this.mResources = context.getResources();
        this.mAlbumSongSeparator = context.getString(R.string.albumsongseparator);
        this.mUnknownAlbum = context.getString(R.string.unknown_album_name);
        this.mUnknownArtist = context.getString(R.string.unknown_artist_name);
    }

    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
            this.mArtistIdIdx = cursor.getColumnIndexOrThrow("_id");
            this.mArtistIdx = cursor.getColumnIndexOrThrow("artist");
            this.mAlbumIdx = cursor.getColumnIndexOrThrow("number_of_albums");
            this.mSongIdx = cursor.getColumnIndexOrThrow("number_of_tracks");
            if (this.mIndexer != null) {
                this.mIndexer.setCursor(cursor);
            } else {
                this.mIndexer = new MusicAlphabetIndexer(cursor, this.mArtistIdx, this.mResources.getString(17040127));
            }
        }
    }

    public void setActivity(RockAudioPlayer newactivity) {
        this.mActivity = newactivity;
    }

    public AsyncQueryHandler getQueryHandler() {
        return this.mQueryHandler;
    }

    @Override // android.widget.ResourceCursorAdapter, android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = super.newView(context, cursor, parent);
        ViewHolder vh = new ViewHolder();
        vh.artist_name = (TextView) v.findViewById(R.id.artist_artistname);
        vh.artist_songsnum = (TextView) v.findViewById(R.id.artist_artistsongsnum);
        vh.artist_image = (ImageView) v.findViewById(R.id.artist_artistimage);
        v.setTag(vh);
        return v;
    }

    @Override // android.widget.SimpleCursorAdapter, android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        String artist = cursor.getString(this.mArtistIdx);
        String displayname = artist;
        boolean unknown = artist == null || artist.equals("<unknown>");
        if (unknown) {
            displayname = this.mUnknownArtist;
        }
        vh.artist_name.setText(displayname);
        int numalbums = cursor.getInt(this.mAlbumIdx);
        int numsongs = cursor.getInt(this.mSongIdx);
        String songs_albums = MusicUtils.makeAlbumsLabel(context, numalbums, numsongs, unknown);
        vh.artist_songsnum.setText(songs_albums);
        ImageView iv = vh.artist_image;
        iv.setImageResource(R.drawable.artist_image);
    }

    @Override // android.widget.CursorAdapter
    public void changeCursor(Cursor cursor) {
        if (cursor != this.mActivity.mArtistCursor) {
            this.mActivity.mArtistCursor = cursor;
            getColumnIndices(cursor);
            super.changeCursor(cursor);
        }
    }

    @Override // android.widget.CursorAdapter
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        String s = constraint.toString();
        if (this.mConstraintIsValid && ((s == null && this.mConstraint == null) || (s != null && s.equals(this.mConstraint)))) {
            return getCursor();
        }
        Cursor c = this.mActivity.getArtistCursor(null, s);
        this.mConstraint = s;
        this.mConstraintIsValid = true;
        return c;
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        return this.mIndexer.getSections();
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int sectionIndex) {
        return this.mIndexer.getPositionForSection(sectionIndex);
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int position) {
        return 0;
    }
}
