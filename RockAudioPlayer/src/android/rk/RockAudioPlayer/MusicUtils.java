package android.rk.RockAudioPlayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScanner;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.Settings;
import android.rk.RockAudioPlayer.IAudioPlaybackService;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

/* loaded from: classes.dex */
public class MusicUtils {
    private static long AudioABEnd = 0;
    private static long AudioABStart = 0;
    private static String SearchContent = null;
    private static final String TAG = "MusicUtils";
    private static int currentsongnums;
    private static byte[] mCachedArt;
    private static int totalsongnums;
    private static boolean isLyric = false;
    private static boolean titileflag = false;
    public static int ABRequire = 0;
    private static ArrayList<SearchNetFileInfo> mSaveSearchNetResult = new ArrayList<SearchNetFileInfo>();
    private static boolean DownloadOnce = false;
    private static int DownloadStatus = 0;
    public static IAudioPlaybackService sService = null;
    private static HashMap<Context, ServiceBinder> sConnectionMap = new HashMap<Context, ServiceBinder>();
    private static final int[] sEmptyList = new int[0];
    private static StringBuilder sFormatBuilder = new StringBuilder();
    private static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    private static final Object[] sTimeArgs = new Object[7];
    private static int sArtId = -2;
    private static Bitmap mCachedBit = null;
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final HashMap<Integer, Drawable> sArtCache = new HashMap<Integer, Drawable>();
    private static int sArtCacheId = -1;
    private static final String sExternalMediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString();

    /* loaded from: classes.dex */
    public interface Defs {
        public static final int ADD_TO_PLAYLIST = 1;
        public static final int Audio_AB_A = 1;
        public static final int Audio_AB_NULL = 0;
        public static final int Audio_AB_PLAY = 2;
        public static final int CHILD_MENU_BASE = 13;
        public static final int DELETE_ITEM = 10;
        public static final int GOTO_PLAYBACK = 7;
        public static final int GOTO_START = 6;
        public static final int NEW_PLAYLIST = 4;
        public static final int OPEN_URL = 0;
        public static final int PARTY_SHUFFLE = 8;
        public static final int PLAYLIST_SELECTED = 3;
        public static final int PLAY_SELECTION = 5;
        public static final int QUEUE = 12;
        public static final int SCAN_DONE = 11;
        public static final int SHUFFLE_ALL = 9;
        public static final int USE_AS_RINGTONE = 2;
    }

    static {
        sBitmapOptionsCache.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptionsCache.inDither = false;
        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptions.inDither = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void LOG(String msg) {
        if (RockAudioPlayer.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void setABStart(long pos) {
        AudioABStart = pos;
    }

    public static long getABStart() {
        return AudioABStart;
    }

    public static void setABEnd(long pos) {
        AudioABEnd = pos;
    }

    public static long getABEnd() {
        return AudioABEnd;
    }

    public static int getABRequire() {
        return ABRequire;
    }

    public static void setABRequire(int ABtype) {
        ABRequire = ABtype;
    }

    public static String makeAlbumsLabel(Context context, int numalbums, int numsongs, boolean isUnknown) {
        LOG("Enter makeAlbumsLabel()");
        StringBuilder songs_albums = new StringBuilder();
        Resources r = context.getResources();
        if (numsongs == 1) {
            songs_albums.append(context.getString(R.string.onesong));
        } else {
            String f = r.getQuantityText(R.plurals.Nsongs, numsongs).toString();
            sFormatBuilder.setLength(0);
            sFormatter.format(f, Integer.valueOf(numsongs));
            songs_albums.append((CharSequence) sFormatBuilder);
        }
        return songs_albums.toString();
    }

    public static IAudioPlaybackService getService() {
        return sService;
    }

    public static String makeAlbumsSongsLabel(Context context, int numalbums, int numsongs, boolean isUnknown) {
        StringBuilder songs_albums = new StringBuilder();
        if (numsongs == 1) {
            songs_albums.append(context.getString(R.string.onesong));
        } else {
            Resources r = context.getResources();
            if (!isUnknown) {
                String f = r.getQuantityText(R.plurals.Nalbums, numalbums).toString();
                sFormatBuilder.setLength(0);
                sFormatter.format(f, Integer.valueOf(numalbums));
                songs_albums.append((CharSequence) sFormatBuilder);
                songs_albums.append(context.getString(R.string.albumsongseparator));
            }
            String f2 = r.getQuantityText(R.plurals.Nsongs, numsongs).toString();
            sFormatBuilder.setLength(0);
            sFormatter.format(f2, Integer.valueOf(numsongs));
            songs_albums.append((CharSequence) sFormatBuilder);
        }
        return songs_albums.toString();
    }

    public static boolean bindToService(Context context) {
        return bindToService(context, null);
    }

    public static boolean bindToService(Context context, ServiceConnection callback) {
        LOG("Enter bindToService()");
        context.startService(new Intent(context, AudioPlaybackService.class));
        ServiceBinder sb = new ServiceBinder(callback);
        sConnectionMap.put(context, sb);
        return context.bindService(new Intent().setClass(context, AudioPlaybackService.class), sb, 0);
    }

    public static void unbindFromService(Context context) {
        LOG("Enter unbindFromService()");
        ServiceBinder sb = sConnectionMap.remove(context);
        if (sb == null) {
            Log.e(TAG, "Trying to unbind for unknown Context");
            return;
        }
        context.unbindService(sb);
        if (sConnectionMap.isEmpty()) {
            sService = null;
        }
        LOG("Quit unbindFromService()");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ServiceBinder implements ServiceConnection {
        ServiceConnection mCallback;

        ServiceBinder(ServiceConnection callback) {
            this.mCallback = callback;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicUtils.LOG("Enter onServiceConnected()");
            MusicUtils.sService = IAudioPlaybackService.Stub.asInterface(service);
            MusicUtils.initAlbumArtCache();
            if (this.mCallback != null) {
                this.mCallback.onServiceConnected(className, service);
            }
            MusicUtils.LOG("Quit onServiceConnected()");
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName className) {
            MusicUtils.LOG("Enter onServiceDisconnected()");
            if (this.mCallback != null) {
                this.mCallback.onServiceDisconnected(className);
            }
            MusicUtils.sService = null;
            MusicUtils.LOG("Quit onServiceDisconnected()");
        }
    }

    public static int getCurrentAlbumId() {
        LOG("Enter getCurrentAlbumId()");
        if (sService != null) {
            try {
                return sService.getAudioId();
            } catch (RemoteException e) {
            }
        }
        LOG("Quit getCurrentAlbumId()");
        return -1;
    }

    public static int getCurrentArtistId() {
        LOG("Enter getCurrentArtistId()");
        if (sService != null) {
            try {
                return sService.getArtistId();
            } catch (RemoteException e) {
            }
        }
        LOG("Quit getCurrentArtistId()");
        return -1;
    }

    public static int getCurrentAudioId() {
        LOG("Enter getCurrentAudioId");
        if (sService != null) {
            try {
                return sService.getAudioId();
            } catch (RemoteException e) {
            }
        }
        LOG("Quit getCurrentAudioId");
        return -1;
    }

    public static int getCurrentShuffleMode() {
        LOG("Enter getCurrentShuffleMode()");
        int mode = 0;
        if (sService != null) {
            try {
                mode = sService.getShuffleMode();
            } catch (RemoteException e) {
            }
        }
        LOG("Quit getCurrentShuffleMode()");
        return mode;
    }

    public static boolean isMusicLoaded() {
        LOG("Enter isMusicLoaded()");
        if (sService != null) {
            try {
                return sService.getPath() != null;
            } catch (RemoteException e) {
                LOG("Quit isMusicLoaded()");
            }
        }
        return false;
    }

    public static int[] getSongListForCursor(Cursor cursor) {
        int colidx;
        LOG("Enter getSongListForCursor()");
        if (cursor == null) {
            return sEmptyList;
        }
        int len = cursor.getCount();
        int[] list = new int[len];
        cursor.moveToFirst();
        try {
            colidx = cursor.getColumnIndexOrThrow("audio_id");
        } catch (IllegalArgumentException e) {
            colidx = cursor.getColumnIndexOrThrow("_id");
        }
        for (int i = 0; i < len; i++) {
            list[i] = cursor.getInt(colidx);
            cursor.moveToNext();
        }
        cursor.close();
        LOG("Quit getSongListForCursor()");
        return list;
    }

    public static int[] getSongListForArtist(Context context, int id) {
        LOG("Enter getSongListForArtist()");
        String[] ccols = {"_id"};
        String where = "artist_id=" + id + " AND is_music=1";
        Cursor cursor = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ccols, where, null, "album_key,track");
        if (cursor != null) {
            int[] list = getSongListForCursor(cursor);
            cursor.close();
            return list;
        }
        LOG("Quit getSongListForArtist()");
        return sEmptyList;
    }

    public static int[] getSongListForAlbum(Context context, int id) {
        LOG("Enter getSongListForAlbum()");
        String[] ccols = {"_id"};
        String where = "album_id=" + id + " AND is_music=1";
        Cursor cursor = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ccols, where, null, "track");
        if (cursor == null) {
            return sEmptyList;
        }
        int[] list = getSongListForCursor(cursor);
        cursor.close();
        return list;
    }

    public static int[] getSongListForPlaylist(Context context, long plid) {
        LOG("Enter getSongListForPlaylist()");
        String[] ccols = {"audio_id"};
        Cursor cursor = query(context, MediaStore.Audio.Playlists.Members.getContentUri("external", plid), ccols, null, null, "play_order");
        if (cursor != null) {
            int[] list = getSongListForCursor(cursor);
            cursor.close();
            return list;
        }
        LOG("Quit getSongListForPlaylist()");
        return sEmptyList;
    }

    public static void playPlaylist(Context context, long plid) {
        LOG("Enter playPlaylist()");
        int[] list = getSongListForPlaylist(context, plid);
        if (list != null) {
            playAll(context, list, -1, false);
        }
        LOG("Quit playPlaylist()");
    }

    public static int[] getAllSongs(Context context) {
        LOG("Enter getAllSongs()");
        Cursor c = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "is_music=1", null, null);
        if (c != null) {
            try {
                if (c.getCount() != 0) {
                    int len = c.getCount();
                    int[] list = new int[len];
                    for (int i = 0; i < len; i++) {
                        c.moveToNext();
                        list[i] = c.getInt(0);
                    }
                    if (c != null) {
                        c.close();
                    }
                    return list;
                }
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        return null;
    }

    public static void SaveSearchContent(String content) {
        SearchContent = content;
    }

    public static String GetSearchContent() {
        return SearchContent;
    }

    public static void setDownLoadLimit(boolean flag) {
        DownloadOnce = flag;
    }

    public static boolean getDownLoadLimit() {
        return DownloadOnce;
    }

    public static void SaveNetSearchResult(ArrayList<SearchNetFileInfo> save) {
        mSaveSearchNetResult = save;
    }

    public static ArrayList<SearchNetFileInfo> GetSaveNetResult() {
        return mSaveSearchNetResult;
    }

    public static int getcurrentsongnums() {
        return currentsongnums;
    }

    public static int gettotalsongnums() {
        return totalsongnums;
    }

    public static void setcurrentsongnums(int nums) {
        currentsongnums = nums;
    }

    public static void settotalsongnums(int nums) {
        totalsongnums = nums;
    }

    public static void setisLyric(boolean tempisLyric) {
        isLyric = tempisLyric;
    }

    public static boolean getisLyric() {
        return isLyric;
    }

    public static void SetTitilePause(boolean tempisLyric) {
        titileflag = tempisLyric;
    }

    public static boolean GetTitilePause() {
        return titileflag;
    }

    public static void makePlaylistMenu(Context context, SubMenu sub) {
        LOG("Enter makePlaylistMenu()");
        LOG("Quit makePlaylistMenu()");
    }

    public static void clearPlaylist(Context context, int plid) {
        LOG("Enter clearPlaylist()");
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", plid);
        context.getContentResolver().delete(uri, null, null);
        LOG("Quit clearPlaylist()");
    }

    public static void deleteTracks(Context context, int[] list) {
        LOG("Enter deleteTracks()");
        String[] cols = {"_id", "_data", "album_id"};
        StringBuilder where = new StringBuilder();
        where.append("_id IN (");
        for (int i = 0; i < list.length; i++) {
            where.append(list[i]);
            if (i < list.length - 1) {
                where.append(",");
            }
        }
        where.append(")");
        Cursor c = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols, where.toString(), null, null);
        if (c != null) {
            try {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    int id = c.getInt(0);
                    sService.removeTrack(id);
                    int id2 = c.getInt(2);
                    synchronized (sArtCache) {
                        sArtCache.remove(Integer.valueOf(id2));
                    }
                    c.moveToNext();
                }
            } catch (RemoteException e) {
            }
            context.getContentResolver().delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, where.toString(), null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                String name = c.getString(1);
                File f = new File(name);
                try {
                    if (!f.delete()) {
                        Log.e(TAG, "Failed to delete file " + name);
                    }
                    c.moveToNext();
                } catch (SecurityException e2) {
                    c.moveToNext();
                }
            }
            c.close();
        }
        String message = context.getResources().getQuantityString(R.plurals.NNNtracksdeleted, list.length, Integer.valueOf(list.length));
        Toast.makeText(context, message, 0).show();
        context.getContentResolver().notifyChange(Uri.parse("content://media"), null);
        LOG("Quit deleteTracks()");
    }

    public static int getPlaylistId(Context context, int AtlistviewPositon) {
        Cursor c = context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null, null, "name");
        if (c == null && !c.moveToFirst()) {
            return -1;
        }
        c.moveToPosition(AtlistviewPositon - 1);
        int i = c.getColumnIndex("_id");
        if (i < 0) {
            c.close();
            return -1;
        }
        int PlaylistId = c.getInt(i);
        c.getString(c.getColumnIndexOrThrow("name"));
        c.close();
        return PlaylistId;
    }

    public static int getPlaylistId(Context context, String playlistname) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.getContentUri("external");
        StringBuilder where = new StringBuilder();
        where.append("name = '" + playlistname + "'");
        Cursor cur = resolver.query(uri, new String[]{"_id"}, where.toString(), null, null);
        if (cur == null || !cur.moveToFirst()) {
            if (cur != null) {
                cur.close();
            }
            return -1;
        }
        int playlistid = cur.getInt(cur.getColumnIndex("_id"));
        cur.close();
        return playlistid;
    }

    public static void addToCurrentPlaylist(Context context, int[] list) {
        LOG("Enter addToCurrentPlaylist()");
        if (sService != null) {
            try {
                sService.enqueue(list, 3);
                context.getResources().getQuantityString(R.plurals.NNNtrackstoplaylist, list.length, Integer.valueOf(list.length));
            } catch (RemoteException e) {
            }
            LOG("Quit addToCurrentPlaylist()");
        }
    }

    public static boolean IsTrackAlreadyIn(Context context, int songid, long playlistid) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        LOG("playlist uri = " + uri);
        String[] cols = {"audio_id"};
        Cursor cur = resolver.query(uri, cols, null, null, null);
        if (cur == null) {
            return false;
        }
        int SongNumInPlaylist = cur.getCount();
        cur.moveToFirst();
        LOG("Song_id = " + songid);
        if (cur.moveToFirst()) {
            for (int i = 0; i < SongNumInPlaylist; i++) {
                int audio_id = cur.getInt(cur.getColumnIndex("audio_id"));
                LOG("audio_id = " + audio_id);
                if (audio_id == songid) {
                    LOG("audio_id = " + audio_id);
                    cur.close();
                    return true;
                }
                cur.moveToNext();
            }
        }
        cur.close();
        return false;
    }

    public static boolean IsPlaylistExist(Context context, String playlistname) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.getContentUri("external");
        Cursor cur = resolver.query(uri, new String[]{"name"}, null, null, null);
        if (cur != null && cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                String playlistName = cur.getString(cur.getColumnIndex("name"));
                if (playlistname.equals(playlistName)) {
                    cur.close();
                    return true;
                }
                cur.moveToNext();
            }
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
        return false;
    }

    public static int getPlaylistNum(Context context) {
        int playlistnum = 0;
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.getContentUri("external");
        Cursor cur = resolver.query(uri, new String[]{"_id"}, null, null, null);
        if (cur == null || !cur.moveToFirst()) {
            playlistnum = -1;
        } else {
            while (!cur.isAfterLast()) {
                playlistnum++;
                cur.moveToNext();
            }
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
        return playlistnum;
    }

    public static void addToHeadNoDelete(Context context, int[] listsong, long playlistid) {
        int base;
        LOG("Enter addToHeadNoDelete()");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        Cursor cur = resolver.query(uri, new String[]{"audio_id", "play_order"}, null, null, "play_order");
        if (cur != null) {
            cur.moveToFirst();
            if (cur.moveToFirst()) {
                base = cur.getInt(cur.getColumnIndexOrThrow("play_order"));
            } else {
                base = 21;
            }
            LOG("base1 = " + base);
            ContentValues[] values = new ContentValues[listsong.length];
            LOG("Step:1");
            LOG("SONGNUM = " + listsong.length);
            for (int i = 0; i < listsong.length; i++) {
                if (IsTrackAlreadyIn(context, listsong[i], playlistid)) {
                    StringBuilder where2 = new StringBuilder();
                    where2.append("audio_id='" + listsong[i] + "'");
                    resolver.delete(uri, where2.toString(), null);
                    LOG("where1 = " + ((Object) where2));
                }
                values[i] = new ContentValues();
                if (base - i <= 0) {
                    PlayOrederAdd(context, playlistid);
                    values[i].put("play_order", (Integer) 0);
                } else {
                    values[i].put("play_order", Integer.valueOf((base - i) - 1));
                }
                values[i].put("audio_id", Integer.valueOf(listsong[i]));
                LOG("values[i] = " + values[i]);
                resolver.insert(uri, values[i]);
                LOG("play_order1 = " + Integer.valueOf(base + i));
            }
            cur.close();
        }
    }

    public static void PlayOrederAdd(Context context, long playlistid) {
        LOG("Enter PlayOrederAdd()");
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        LOG("playlist uri = " + uri);
        String[] cols = {"audio_id", "play_order", "_id"};
        Cursor cur = resolver.query(uri, cols, null, null, null);
        if (cur != null) {
            int SongNumInPlaylist = cur.getCount();
            cur.moveToFirst();
            ContentValues[] values = new ContentValues[SongNumInPlaylist];
            if (cur.moveToFirst()) {
                for (int i = 0; i < SongNumInPlaylist; i++) {
                    int songid = cur.getInt(cur.getColumnIndexOrThrow("audio_id"));
                    int playorder = cur.getInt(cur.getColumnIndexOrThrow("play_order"));
                    Uri songuri = ContentUris.withAppendedId(MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid), cur.getInt(cur.getColumnIndexOrThrow("_id")));
                    values[i] = new ContentValues();
                    values[i].put("play_order", Integer.valueOf(playorder + 1));
                    StringBuilder where = new StringBuilder();
                    where.append("audio_id= ' " + songid + " ' ");
                    context.getContentResolver().update(songuri, values[i], null, null);
                    cur.moveToNext();
                }
            }
            cur.close();
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:7:0x0050 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static void addToHead(android.content.Context r10, int[] r11, long r12) {
        /*
            Method dump skipped, instructions count: 318
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.rk.RockAudioPlayer.MusicUtils.addToHead(android.content.Context, int[], long):void");
    }

    public static void addToPlaylist(Context context, int[] ids, long playlistid) {
        LOG("addToPlaylist the ids is = " + ids + "playlistid is = " + playlistid);
        if (ids == null) {
            Log.e("MusicBase", "ListSelection null");
        } else {
            LOG("Enter the addToPlaylist()");
            int size = ids.length;
            ContentValues[] values = new ContentValues[size];
            ContentResolver resolver = context.getContentResolver();
            String[] cols = {"count(*)"};
            Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
            LOG("Main playlist uri = " + uri);
            Cursor cur = resolver.query(uri, cols, null, null, null);
            LOG("The Main playlist cur = " + cur);
            if (cur != null) {
                cur.moveToFirst();
                int base = cur.getInt(0);
                cur.close();
                for (int i = 0; i < size; i++) {
                    LOG("ids[i] = " + ids[i]);
                    if (IsTrackAlreadyIn(context, ids[i], playlistid)) {
                        LOG("This song is already in the playlist");
                        if (playlistid != getPlaylistId(context, "Lastly played")) {
                            Toast.makeText(context, (int) R.string.song_already_in_playlist, 0).show();
                        }
                    } else {
                        values[i] = new ContentValues();
                        values[i].put("play_order", Integer.valueOf(base + i));
                        values[i].put("audio_id", Integer.valueOf(ids[i]));
                        if (playlistid != getPlaylistId(context, "Lastly played")) {
                            Toast.makeText(context, "Add Success", 0).show();
                        }
                    }
                }
                resolver.bulkInsert(uri, values);
            }
        }
        LOG("Quit addToPlaylis()");
    }

    public static void deleteFromPlaylist(Context context, int songid, long playlistid) {
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        ContentResolver resolver = context.getContentResolver();
        StringBuilder where = new StringBuilder();
        where.append("audio_id='" + songid + "'");
        resolver.delete(uri, where.toString(), null);
        Toast.makeText(context, "Cancle Success", 0).show();
    }

    public static void DeleteSongsInPlaylist(Context context, long playlistid) {
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        ContentResolver resolver = context.getContentResolver();
        Cursor cur = resolver.query(uri, new String[]{"audio_id"}, null, null, null);
        if (cur != null && cur.moveToFirst()) {
            while (!cur.moveToLast()) {
                int deleteid = cur.getInt(cur.getColumnIndexOrThrow("audio_id"));
                StringBuilder where = new StringBuilder();
                where.append("audio_id='" + deleteid + "'");
                resolver.delete(uri, where.toString(), null);
                cur.moveToNext();
            }
            cur.close();
        }
        if (cur != null) {
            cur.close();
        }
    }

    public static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        LOG("Enter query()");
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

    public static boolean isMediaScannerScanning(Context context) {
        LOG("Enter isMediaScannerScanning()");
        boolean result = false;
        Cursor cursor = query(context, MediaStore.getMediaScannerUri(), new String[]{"volume"}, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                result = "external".equals(cursor.getString(0));
            }
            cursor.close();
        }
        LOG("Quit isMediaScannerScanning() result = " + result);
        return result;
    }

    public static boolean setSpinnerState(Activity a) {
        LOG("Enter setSpinnerState()");
        if (isMediaScannerScanning(a)) {
            a.getWindow().setFeatureInt(5, -3);
            a.getWindow().setFeatureInt(5, -1);
            return true;
        }
        a.getWindow().setFeatureInt(5, -2);
        return false;
    }

    public static void displayDatabaseError(Activity a) {
        int title;
        int message;
        LOG("Enter displayDatabaseError()");
        String status = new File("/flash").exists() ? "mounted" : "removed";
        if (status.equals("shared") || status.equals("unmounted")) {
            title = R.string.flash_busy_title;
            message = R.string.flash_busy_message;
        } else if (status.equals("removed")) {
            title = R.string.flash_busy_title;
            message = R.string.flash_busy_message;
        } else if (status.equals("mounted")) {
            message = R.string.No_Music;
            title = R.string.No_Music;
        } else {
            message = R.string.No_Music;
            title = R.string.No_Music;
        }
        a.setTitle(title);
        View v = a.findViewById(R.id.sd_message);
        if (v != null) {
            v.setVisibility(0);
        }
        View v2 = a.findViewById(R.id.sd_icon);
        if (v2 != null) {
            v2.setVisibility(0);
        }
        View v3 = a.findViewById(16908298);
        if (v3 != null) {
            v3.setVisibility(8);
        }
        TextView tv = (TextView) a.findViewById(R.id.sd_message);
        tv.setText(message);
        LOG("Quit displayDatabaseError()");
    }

    public static void hideDatabaseError(Activity a) {
        LOG("Enter hideDatabaseError()");
        View v = a.findViewById(R.id.sd_message);
        if (v != null) {
            v.setVisibility(8);
        }
        View v2 = a.findViewById(R.id.sd_icon);
        if (v2 != null) {
            v2.setVisibility(8);
        }
        View v3 = a.findViewById(16908298);
        if (v3 != null) {
            v3.setVisibility(0);
        }
        View Vnomusic = a.findViewById(R.id.nomusic);
        if (Vnomusic != null) {
            Vnomusic.setVisibility(8);
        }
        LOG("Quit hideDatabaseError()");
    }

    protected static Uri getContentURIForPath(String path) {
        LOG("Enter getContentURIForPath()");
        return Uri.fromFile(new File(path));
    }

    public static String makeTimeString(Context context, long secs) {
        context.getString(R.string.durationformat);
        sFormatBuilder.setLength(0);
        long seconds = secs % 60;
        long minutes = (secs / 60) % 60;
        long hours = secs / 3600;
        return sFormatter.format("%02d:%02d:%02d", Long.valueOf(hours), Long.valueOf(minutes), Long.valueOf(seconds)).toString();
    }

    public static void shuffleAll(Context context, Cursor cursor) {
        LOG("Enter shuffleAll()");
        playAll(context, cursor, 0, true);
    }

    public static void playAll(Context context, Cursor cursor) {
        playAll(context, cursor, 0, false);
    }

    public static void playAll(Context context, Cursor cursor, int position) {
        playAll(context, cursor, position, false);
    }

    public static void playAll(Context context, int[] list, int position) {
        playAll(context, list, position, false);
    }

    private static void playAll(Context context, Cursor cursor, int position, boolean force_shuffle) {
        int[] list = getSongListForCursor(cursor);
        playAll(context, list, position, force_shuffle);
    }

    private static void playAll(Context context, int[] list, int position, boolean force_shuffle) {
        LOG("Enter playAll()");
        if (list.length == 0 || sService == null) {
            if (list.length == 0) {
                LOG("---------------------list is empty");
            }
            String message = context.getString(R.string.emptyplaylist, Integer.valueOf(list.length));
            Toast.makeText(context, message, 0).show();
            return;
        }
        if (position < 0) {
            position = 0;
        }
        try {
            setcurrentsongnums(position + 1);
            settotalsongnums(list.length);
            if (force_shuffle) {
                sService.setShuffleMode(1);
            }
            int curid = sService.getAudioId();
            int curpos = sService.getQueuePosition();
            if (position != -1 && curpos == position && curid == list[position]) {
                int[] playlist = sService.getQueue();
                if (Arrays.equals(list, playlist)) {
                    sService.play();
                    return;
                }
            }
            sService.open(list, force_shuffle ? -1 : position);
            sService.play();
        } catch (RemoteException e) {
        }
        LOG("Quit playAll()");
    }

    public static int getSongnumInPlaylist(Context context, int playlistid) {
        ContentResolver resolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
        LOG("playlist uri = " + uri);
        String[] cols = {"audio_id"};
        Cursor cur = resolver.query(uri, cols, null, null, null);
        if (cur == null) {
            return 0;
        }
        int ret = cur.getCount();
        cur.close();
        return ret;
    }

    public static void clearQueue() {
        LOG("Enter clearQueue()");
        try {
            sService.removeTracks(0, Integer.MAX_VALUE);
        } catch (RemoteException e) {
        }
        LOG("Quit clearQueue()");
    }

    /* loaded from: classes.dex */
    private static class FastBitmapDrawable extends Drawable {
        private Bitmap mBitmap;

        public FastBitmapDrawable(Bitmap b) {
            this.mBitmap = b;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, (Paint) null);
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -1;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter cf) {
        }
    }

    public static void initAlbumArtCache() {
        LOG("Enter initAlbumArtCache()");
        try {
            int id = sService.getMediaMountedCount();
            if (id != sArtCacheId) {
                clearAlbumArtCache();
                sArtCacheId = id;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        LOG("Quit initAlbumArtCache()");
    }

    public static void clearAlbumArtCache() {
        LOG("Enter ");
        synchronized (sArtCache) {
            sArtCache.clear();
        }
        LOG("Quit clearAlbumArtCache()");
    }

    public static Drawable getCachedArtwork(Context context, int artIndex, BitmapDrawable defaultArtwork) {
        Drawable d;
        LOG("Enter getCachedArtwork()");
        synchronized (sArtCache) {
            d = sArtCache.get(Integer.valueOf(artIndex));
        }
        if (d == null) {
            d = defaultArtwork;
            Bitmap icon = defaultArtwork.getBitmap();
            int w = icon.getWidth();
            int h = icon.getHeight();
            Bitmap b = getArtworkQuick(context, artIndex, w, h);
            if (b != null) {
                d = new FastBitmapDrawable(b);
                synchronized (sArtCache) {
                    Drawable value = sArtCache.get(Integer.valueOf(artIndex));
                    if (value == null) {
                        sArtCache.put(Integer.valueOf(artIndex), d);
                    } else {
                        d = value;
                    }
                }
            }
        }
        LOG("Quit getCachedArtwork()");
        return d;
    }

    public static Drawable getAlbumWork(Context context, int artIndex, BitmapDrawable defaultArtwork) {
        LOG("Enter getAlbumWork()");
        Drawable d = null;
        if (0 == 0) {
            d = defaultArtwork;
            Bitmap icon = defaultArtwork.getBitmap();
            int w = icon.getWidth();
            int h = icon.getHeight();
            Bitmap b = getArtworkQuick(context, artIndex, w, h);
            if (b != null) {
                d = new FastBitmapDrawable(b);
            }
        }
        LOG("Quit getAlbumWork()");
        return d;
    }

    private static Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
        LOG("Enter getArtworkQuick()w=");
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        LOG("uri= " + uri);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;
                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                for (int nextHeight = sBitmapOptionsCache.outHeight >> 1; nextWidth > w && nextHeight > h; nextHeight >>= 1) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                }
                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, sBitmapOptionsCache);
                if (!(b == null || (sBitmapOptionsCache.outWidth == w && sBitmapOptionsCache.outHeight == h))) {
                    Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                    if (tmp != b) {
                        b.recycle();
                    }
                    b = tmp;
                }
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e) {
                    }
                }
                return b;
            } catch (FileNotFoundException e2) {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e3) {
                    }
                }
            } catch (Throwable th) {
                if (fd != null) {
                    try {
                        fd.close();
                    } catch (IOException e4) {
                    }
                }
                throw new RuntimeException(th);
            }
        }
        LOG("Quit getArtworkQuick()");
        return null;
    }

    public static Drawable getArtwork(Context context, int album_id, BitmapDrawable defaultArtwork) {
        LOG("Enter getArtwork()");
        Bitmap icon = defaultArtwork.getBitmap();
        int w = icon.getWidth();
        int h = icon.getHeight();
        Bitmap b = getArtwork(context, album_id, true);
        if (b == null) {
            return null;
        }
        Drawable d = new FastBitmapDrawable(Bitmap.createScaledBitmap(b, w, h, true));
        return d;
    }

    public static Bitmap getArtwork(Context context, int album_id, boolean allowDefault) {
        LOG("Enter getArtwork() bitmap");
        Bitmap b = getArtworkQuick(context, album_id, 320, 320);
        if (b == null && allowDefault) {
            b = getArtworkFromFile(context, null, album_id);
        }
        return b;
    }

    private static boolean ensureFileExists(String path) {
        LOG("Enter ensureFileExists()");
        File file = new File(path);
        Log.d(TAG, "path = " + path);
        return file.exists();
    }

    private static Bitmap getArtworkFromFile(Context context, Uri uri, int albumid) {
        String path;
        String path2;
        byte[] art;
        Cursor c;
        Bitmap bm;
        Log.w("", "Enter getArtworkFromFile()");
        if (sArtId == albumid) {
            Log.i("@@@@@@ ", "reusing cached data" + mCachedBit);
            if (mCachedBit != null) {
                return mCachedBit;
            }
            art = mCachedArt;
        } else {
            if (uri == null) {
                try {
                    int curalbum = sService.getAlbumId();
                    if (curalbum == albumid || albumid < 0) {
                        path = sService.getPath();
                        if (path != null) {
                            try {
                                uri = Uri.parse(path);
                            } catch (NullPointerException e2) {
                                return null;
                            }
                        }
                    } else {
                        path = null;
                    }
                    path2 = path;
                } catch (RemoteException e3) {
                    path = null;
                } catch (NullPointerException e4) {
                    path = null;
                }
            } else {
                path2 = null;
            }
            if (uri == null && albumid >= 0 && (c = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "album"}, "album_id=?", new String[]{String.valueOf(albumid)}, null)) != null) {
                c.moveToFirst();
                if (!c.isAfterLast()) {
                    int trackid = c.getInt(0);
                    uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackid);
                }
                if (c.getString(1).equals("<unknown>")) {
                    albumid = -1;
                }
                c.close();
            }
            if (uri != null) {
                MediaScanner scanner = new MediaScanner(context);
                ParcelFileDescriptor pfd = null;
                try {
                    pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                    Log.i("@@@@@@ ", "pfd = " + pfd);
                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        art = scanner.extractAlbumArt(fd);
                    } else {
                        art = null;
                    }
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException e5) {
                        }
                    }
                } catch (IOException e6) {
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException e7) {
                            art = null;
                        }
                    }
                    art = null;
                } catch (SecurityException e8) {
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException e9) {
                            art = null;
                        }
                    }
                    art = null;
                } catch (Throwable th) {
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException e10) {
                        }
                    }
                    throw new RuntimeException(th);
                }
            } else {
                art = null;
            }
        }
        if (art != null) {
            try {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                opts.inSampleSize = 1;
                BitmapFactory.decodeByteArray(art, 0, art.length, opts);
                while (true) {
                    if (opts.outHeight <= 320 && opts.outWidth <= 320) {
                        break;
                    }
                    opts.outHeight /= 2;
                    opts.outWidth /= 2;
                    opts.inSampleSize *= 2;
                }
                opts.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeByteArray(art, 0, art.length, opts);
                if (albumid != -1) {
                    try {
                        sArtId = albumid;
                    } catch (Exception e11) {
                    }
                }
                mCachedArt = art;
                mCachedBit = bm;
            } catch (Exception e12) {
                bm = null;
            }
        } else {
            bm = null;
        }
        LOG("Quit getArtworkFromFile()");
        return bm;
    }

    private static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.disk_1), null, opts);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getIntPref(Context context, String name, int def) {
        LOG("Enter getIntPref()");
        SharedPreferences prefs = context.getSharedPreferences("android.rk.rockplayer", 0);
        LOG("Quit getIntPref()");
        return prefs.getInt(name, def);
    }

    static void setIntPref(Context context, String name, int value) {
        LOG("Enter setIntPref()");
        SharedPreferences prefs = context.getSharedPreferences("com.android.music", 0);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt(name, value);
        ed.commit();
        LOG("Quit setIntPref()");
    }

    static void setRingtone(Context context, long id) {
        LOG("Enter setRingtone()");
        ContentResolver resolver = context.getContentResolver();
        Uri ringUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        try {
            ContentValues values = new ContentValues(2);
            values.put("is_ringtone", "1");
            values.put("is_alarm", "1");
            resolver.update(ringUri, values, null, null);
            String[] cols = {"_id", "_data", "title"};
            String where = "_id=" + id;
            Cursor cursor = query(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols, where, null, null);
            if (cursor != null) {
                try {
                    if (cursor.getCount() == 1) {
                        cursor.moveToFirst();
                        Settings.System.putString(resolver, "ringtone", ringUri.toString());
                        String message = context.getString(R.string.ringtone_set, cursor.getString(2));
                        Toast.makeText(context, message, 0).show();
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "couldn't set ringtone flag for id " + id);
        }
    }
}
