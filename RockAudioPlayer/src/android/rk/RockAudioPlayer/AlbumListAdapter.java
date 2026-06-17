package android.rk.RockAudioPlayer;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.rk.RockAudioPlayer.RockAudioPlayer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/* loaded from: classes.dex */
public class AlbumListAdapter extends SimpleCursorAdapter implements SectionIndexer, RockAudioPlayer.Def {
    private static final String ALBUM_COVER_URI_PATH = "content://media/external/audio/albumart";
    private RockAudioPlayer mActivity;
    private int mAlbumArtIndex;
    private int mAlbumIdx;
    private final String mAlbumSongSeparator;
    private String mArtistId;
    private int mArtistIdx;
    private final BitmapDrawable mDefaultAlbumIcon;
    private int mGroupAlbumIdx;
    private int mGroupSongIdx;
    private AlphabetIndexer mIndexer;
    private int mNumSongsIdx;
    private AsyncQueryHandler mQueryHandler;
    private final Resources mResources;
    private final String mUnknownAlbum;
    private final String mUnknownArtist;
    private final Drawable mNowPlayingOverlay = null;
    private final StringBuilder mStringBuilder = new StringBuilder();
    private final Object[] mFormatArgs = new Object[1];
    private String mConstraint = null;
    private boolean mConstraintIsValid = false;

    /* loaded from: classes.dex */
    class ViewHolder {
        ImageView album_image;
        TextView album_name;
        TextView album_songsnum;

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
            AlbumListAdapter.this.mActivity.init(cursor, 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AlbumListAdapter(Context context, RockAudioPlayer currentactivity, int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to);
        this.mActivity = null;
        this.mActivity = currentactivity;
        this.mQueryHandler = new QueryHandler(context.getContentResolver());
        this.mUnknownAlbum = context.getString(R.string.unknown_album_name);
        this.mUnknownArtist = context.getString(R.string.unknown_artist_name);
        this.mAlbumSongSeparator = context.getString(R.string.albumsongseparator);
        Resources r = context.getResources();
        Bitmap b = BitmapFactory.decodeResource(r, R.drawable.audio_no_album);
        this.mDefaultAlbumIcon = new BitmapDrawable(b);
        this.mDefaultAlbumIcon.setFilterBitmap(false);
        this.mDefaultAlbumIcon.setDither(false);
        getColumnIndices(cursor);
        this.mResources = context.getResources();
    }

    private void getColumnIndices(Cursor cursor) {
        if (cursor != null) {
            this.mAlbumIdx = cursor.getColumnIndexOrThrow("album");
            this.mArtistIdx = cursor.getColumnIndexOrThrow("artist");
            this.mNumSongsIdx = cursor.getColumnIndexOrThrow("numsongs");
            this.mAlbumArtIndex = cursor.getColumnIndexOrThrow("album_art");
            if (this.mIndexer != null) {
                this.mIndexer.setCursor(cursor);
            } else {
                this.mIndexer = new MusicAlphabetIndexer(cursor, this.mAlbumIdx, this.mResources.getString(17040127));
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
        vh.album_name = (TextView) v.findViewById(R.id.album_name);
        vh.album_songsnum = (TextView) v.findViewById(R.id.album_songsnum);
        vh.album_image = (ImageView) v.findViewById(R.id.album_image);
        v.setTag(vh);
        return v;
    }

    @Override // android.widget.SimpleCursorAdapter, android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        String name = cursor.getString(this.mAlbumIdx);
        String displayname = name;
        boolean unknown = name == null || name.equals("<unknown>");
        if (unknown) {
            displayname = this.mUnknownAlbum;
        }
        vh.album_name.setText(displayname);
        int numalbums = cursor.getInt(this.mAlbumIdx);
        int numsongs = cursor.getInt(this.mNumSongsIdx);
        String songs_albums = MusicUtils.makeAlbumsLabel(context, numalbums, numsongs, unknown);
        vh.album_songsnum.setText(songs_albums);
        ImageView iv = vh.album_image;
        String art = cursor.getString(this.mAlbumArtIndex);
        if (unknown || art == null || art.length() == 0) {
            iv.setImageResource(R.drawable.audio_no_album);
            return;
        }
        cursor.getInt(0);
        Bitmap bm = getAlbumCover(cursor, context);
        if (bm != null) {
            iv.setImageBitmap(bm);
        } else {
            iv.setImageResource(R.drawable.audio_no_album);
        }
    }

    public Bitmap getAlbumCover(Cursor cursor, Context context) {
        Uri AlbumUriPath = Uri.parse(ALBUM_COVER_URI_PATH);
        int albumID = cursor.getInt(0);
        Uri uri = ContentUris.withAppendedId(AlbumUriPath, albumID);
        Log.d("AlbumListAdapter", " uri" + uri);
        ParcelFileDescriptor fd = null;
        try {
            ParcelFileDescriptor fd2 = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fdc = fd2.getFileDescriptor();
            Bitmap bmp = BitmapFactory.decodeFileDescriptor(fdc);
            if (fd2 != null) {
                try {
                    fd2.close();
                } catch (IOException e) {
                }
            }
            return bmp;
        } catch (FileNotFoundException e2) {
            FileDescriptor fdc2 = fd.getFileDescriptor();
            BitmapFactory.decodeFileDescriptor(fdc2);
            if (0 == 0) {
                return null;
            }
            try {
                fd.close();
                return null;
            } catch (IOException e3) {
                return null;
            }
        } catch (Throwable th) {
            FileDescriptor fdc3 = fd.getFileDescriptor();
            BitmapFactory.decodeFileDescriptor(fdc3);
            if (0 != 0) {
                try {
                    fd.close();
                } catch (IOException e4) {
                }
            }
            throw new RuntimeException(th);
        }
    }

    @Override // android.widget.CursorAdapter
    public void changeCursor(Cursor cursor) {
        if (cursor != this.mActivity.mAlbumCursor) {
            this.mActivity.mAlbumCursor = cursor;
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
        Cursor c = this.mActivity.getAlbumCursor(null, s);
        this.mConstraint = s;
        this.mConstraintIsValid = true;
        return c;
    }

    @Override // android.widget.SectionIndexer
    public Object[] getSections() {
        return this.mIndexer.getSections();
    }

    @Override // android.widget.SectionIndexer
    public int getPositionForSection(int section) {
        return this.mIndexer.getPositionForSection(section);
    }

    @Override // android.widget.SectionIndexer
    public int getSectionForPosition(int position) {
        return 0;
    }
}
