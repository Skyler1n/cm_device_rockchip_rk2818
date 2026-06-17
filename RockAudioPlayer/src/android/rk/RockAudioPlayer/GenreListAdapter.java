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
public class GenreListAdapter extends SimpleCursorAdapter implements SectionIndexer, RockAudioPlayer.Def {
    private RockAudioPlayer mActivity;
    private final StringBuilder mBuffer = new StringBuilder();
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;
    private final Context mContext;
    private final BitmapDrawable mDefaultAlbumIcon;
    private int mGenreIdIdx;
    private int mGenreIdx;
    private final String mGenreSongSeparator;
    private int mGenreSongsIdx;
    private MusicAlphabetIndexer mIndexer;
    private AsyncQueryHandler mQueryHandler;
    private final Resources mResources;
    private final String mUnknownGenre;

    /* loaded from: classes.dex */
    class ViewHolder {
        ImageView genre_image;
        TextView genre_name;
        TextView genre_songsnum;

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
            GenreListAdapter.this.mActivity.init(cursor, 3);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GenreListAdapter(Context context, RockAudioPlayer currentactivity, int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to);
        this.mActivity = currentactivity;
        this.mQueryHandler = new QueryHandler(context.getContentResolver());
        Resources r = context.getResources();
        this.mDefaultAlbumIcon = (BitmapDrawable) r.getDrawable(R.drawable.genre_image);
        this.mDefaultAlbumIcon.setFilterBitmap(false);
        this.mDefaultAlbumIcon.setDither(false);
        this.mContext = context;
        getColumnIndices(cursor);
        this.mResources = context.getResources();
        this.mGenreSongSeparator = context.getString(R.string.genresongseparator);
        this.mUnknownGenre = context.getString(R.string.unknown_genre_name);
    }

    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
            this.mGenreIdIdx = cursor.getColumnIndexOrThrow("_id");
            this.mGenreIdx = cursor.getColumnIndexOrThrow("name");
            if (this.mIndexer != null) {
                this.mIndexer.setCursor(cursor);
            } else {
                this.mIndexer = new MusicAlphabetIndexer(cursor, this.mGenreIdx, this.mResources.getString(17040127));
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
        vh.genre_name = (TextView) v.findViewById(R.id.genre_genrename);
        vh.genre_songsnum = (TextView) v.findViewById(R.id.genre_genresongsnum);
        vh.genre_image = (ImageView) v.findViewById(R.id.genre_genreimage);
        v.setTag(vh);
        return v;
    }

    @Override // android.widget.SimpleCursorAdapter, android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        String artist = cursor.getString(this.mGenreIdx);
        String displayname = artist;
        boolean unknown = artist == null || artist.equals("<unknown>");
        if (unknown) {
            displayname = this.mUnknownGenre;
        }
        vh.genre_name.setText(displayname);
        ImageView iv = vh.genre_image;
        iv.setImageResource(R.drawable.genre_image);
    }

    @Override // android.widget.CursorAdapter
    public void changeCursor(Cursor cursor) {
        if (cursor != this.mActivity.mGenreCursor) {
            this.mActivity.mGenreCursor = cursor;
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
        Cursor c = this.mActivity.getGenreCursor(null, s);
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
