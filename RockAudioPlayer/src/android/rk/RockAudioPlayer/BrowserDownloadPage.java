package android.rk.RockAudioPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Downloads;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.io.File;

/* loaded from: classes.dex */
public class BrowserDownloadPage extends Activity implements View.OnCreateContextMenuListener, AdapterView.OnItemClickListener {
    private int mContextMenuPosition;
    private BrowserDownloadAdapter mDownloadAdapter;
    private Cursor mDownloadCursor;
    private int mIdColumnId;
    private ListView mListView;
    private int mStatusColumnId;
    private int mTitleColumnId;

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        int position;
        super.onCreate(icicle);
        setContentView(R.layout.browser_downloads_page);
        setTitle(getText(R.string.download_title));
        this.mListView = (ListView) findViewById(R.id.list);
        LayoutInflater factory = LayoutInflater.from(this);
        View v = factory.inflate(R.layout.no_downloads, (ViewGroup) null);
        addContentView(v, new ViewGroup.LayoutParams(-1, -1));
        this.mListView.setEmptyView(v);
        this.mDownloadCursor = managedQuery(Downloads.CONTENT_URI, new String[]{"_id", "title", "status", "total_bytes", "current_bytes", "_data", "description", "mimetype", "lastmod", "visibility"}, null, null);
        if (this.mDownloadCursor != null) {
            this.mStatusColumnId = this.mDownloadCursor.getColumnIndexOrThrow("status");
            this.mIdColumnId = this.mDownloadCursor.getColumnIndexOrThrow("_id");
            this.mTitleColumnId = this.mDownloadCursor.getColumnIndexOrThrow("title");
            this.mDownloadAdapter = new BrowserDownloadAdapter(this, R.layout.browser_download_item, this.mDownloadCursor);
            this.mListView.setAdapter((ListAdapter) this.mDownloadAdapter);
            this.mListView.setScrollBarStyle(16777216);
            this.mListView.setOnCreateContextMenuListener(this);
            this.mListView.setOnItemClickListener(this);
            Intent intent = getIntent();
            if (intent != null && intent.getData() != null && (position = checkStatus(ContentUris.parseId(intent.getData()))) >= 0) {
                this.mListView.setSelection(position);
            }
        }
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.mDownloadCursor == null) {
            return true;
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.downloadhistory, menu);
        return true;
    }

    @Override // android.app.Activity
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean showCancel;
        if (getCancelableCount() > 0) {
            showCancel = true;
        } else {
            showCancel = false;
        }
        menu.findItem(R.id.download_menu_cancel_all).setEnabled(showCancel);
        boolean showClear = getClearableCount() > 0;
        menu.findItem(R.id.download_menu_clear_all).setEnabled(showClear);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download_menu_clear_all /* 2131427514 */:
                promptClearList();
                return true;
            case R.id.download_menu_cancel_all /* 2131427515 */:
                promptCancelAll();
                return true;
            default:
                return false;
        }
    }

    @Override // android.app.Activity
    public boolean onContextItemSelected(MenuItem item) {
        this.mDownloadCursor.moveToPosition(this.mContextMenuPosition);
        switch (item.getItemId()) {
            case R.id.download_menu_clear /* 2131427516 */:
            case R.id.download_menu_cancel /* 2131427518 */:
                getContentResolver().delete(ContentUris.withAppendedId(Downloads.CONTENT_URI, this.mDownloadCursor.getLong(this.mIdColumnId)), null, null);
                return true;
            case R.id.download_menu_open /* 2131427517 */:
                hideCompletedDownload();
                openCurrentDownload();
                return true;
            default:
                return false;
        }
    }

    @Override // android.app.Activity, android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (this.mDownloadCursor != null) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            this.mDownloadCursor.moveToPosition(info.position);
            this.mContextMenuPosition = info.position;
            menu.setHeaderTitle(this.mDownloadCursor.getString(this.mTitleColumnId));
            MenuInflater inflater = getMenuInflater();
            int status = this.mDownloadCursor.getInt(this.mStatusColumnId);
            if (Downloads.isStatusSuccess(status)) {
                inflater.inflate(R.menu.downloadhistorycontextfinished, menu);
            } else if (Downloads.isStatusError(status)) {
                inflater.inflate(R.menu.downloadhistorycontextfailed, menu);
            } else {
                inflater.inflate(R.menu.downloadhistorycontextrunning, menu);
            }
        }
    }

    int checkStatus(final long id) {
        int position = -1;
        this.mDownloadCursor.moveToFirst();
        while (true) {
            if (this.mDownloadCursor.isAfterLast()) {
                break;
            } else if (id == this.mDownloadCursor.getLong(this.mIdColumnId)) {
                position = this.mDownloadCursor.getPosition();
                break;
            } else {
                this.mDownloadCursor.moveToNext();
            }
        }
        if (!this.mDownloadCursor.isAfterLast()) {
            int status = this.mDownloadCursor.getInt(this.mStatusColumnId);
            if (Downloads.isStatusError(status)) {
                if (status == 492) {
                    String title = this.mDownloadCursor.getString(this.mTitleColumnId);
                    if (title == null || title.length() == 0) {
                        title = getString(R.string.download_unknown_filename);
                    }
                    String msg = getString(R.string.download_file_error_dlg_msg, new Object[]{title});
                    new AlertDialog.Builder(this).setTitle(R.string.download_file_error_dlg_title).setIcon(17301597).setMessage(msg).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null).setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() { // from class: android.rk.RockAudioPlayer.BrowserDownloadPage.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int whichButton) {
                            BrowserDownloadPage.this.resumeDownload(id);
                        }
                    }).show();
                } else {
                    new AlertDialog.Builder(this).setTitle(R.string.download_failed_generic_dlg_title).setIcon(R.drawable.ssl_icon).setMessage(BrowserDownloadAdapter.getErrorText(status)).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null).show();
                }
            }
        }
        return position;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resumeDownload(long id) {
    }

    private void promptClearList() {
        new AlertDialog.Builder(this).setTitle(R.string.download_clear_dlg_title).setIcon(R.drawable.ssl_icon).setMessage(R.string.download_clear_dlg_msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: android.rk.RockAudioPlayer.BrowserDownloadPage.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int whichButton) {
                BrowserDownloadPage.this.clearAllDownloads();
            }
        }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
    }

    private int getCancelableCount() {
        int count = 0;
        if (this.mDownloadCursor != null) {
            this.mDownloadCursor.moveToFirst();
            while (!this.mDownloadCursor.isAfterLast()) {
                int status = this.mDownloadCursor.getInt(this.mStatusColumnId);
                if (!Downloads.isStatusCompleted(status)) {
                    count++;
                }
                this.mDownloadCursor.moveToNext();
            }
        }
        return count;
    }

    private void promptCancelAll() {
        int count = getCancelableCount();
        if (count != 0) {
            if (count == 1) {
                cancelAllDownloads();
                return;
            }
            String msg = getString(R.string.download_cancel_dlg_msg, new Object[]{Integer.valueOf(count)});
            new AlertDialog.Builder(this).setTitle(R.string.download_cancel_dlg_title).setIcon(R.drawable.ssl_icon).setMessage(msg).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: android.rk.RockAudioPlayer.BrowserDownloadPage.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int whichButton) {
                    BrowserDownloadPage.this.cancelAllDownloads();
                }
            }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelAllDownloads() {
        if (this.mDownloadCursor.moveToFirst()) {
            StringBuilder where = new StringBuilder();
            boolean firstTime = true;
            while (!this.mDownloadCursor.isAfterLast()) {
                int status = this.mDownloadCursor.getInt(this.mStatusColumnId);
                if (!Downloads.isStatusCompleted(status)) {
                    if (firstTime) {
                        firstTime = false;
                    } else {
                        where.append(" OR ");
                    }
                    where.append("( ");
                    where.append("_id");
                    where.append(" = '");
                    where.append(this.mDownloadCursor.getLong(this.mIdColumnId));
                    where.append("' )");
                }
                this.mDownloadCursor.moveToNext();
            }
            if (!firstTime) {
                getContentResolver().delete(Downloads.CONTENT_URI, where.toString(), null);
            }
        }
    }

    private int getClearableCount() {
        int count = 0;
        if (this.mDownloadCursor.moveToFirst()) {
            while (!this.mDownloadCursor.isAfterLast()) {
                int status = this.mDownloadCursor.getInt(this.mStatusColumnId);
                if (Downloads.isStatusCompleted(status)) {
                    count++;
                }
                this.mDownloadCursor.moveToNext();
            }
        }
        return count;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearAllDownloads() {
        if (this.mDownloadCursor.moveToFirst()) {
            StringBuilder where = new StringBuilder();
            boolean firstTime = true;
            while (!this.mDownloadCursor.isAfterLast()) {
                int status = this.mDownloadCursor.getInt(this.mStatusColumnId);
                if (Downloads.isStatusCompleted(status)) {
                    if (firstTime) {
                        firstTime = false;
                    } else {
                        where.append(" OR ");
                    }
                    where.append("( ");
                    where.append("_id");
                    where.append(" = '");
                    where.append(this.mDownloadCursor.getLong(this.mIdColumnId));
                    where.append("' )");
                }
                this.mDownloadCursor.moveToNext();
            }
            if (!firstTime) {
                getContentResolver().delete(Downloads.CONTENT_URI, where.toString(), null);
            }
        }
    }

    private void openCurrentDownload() {
        int filenameColumnId = this.mDownloadCursor.getColumnIndexOrThrow("_data");
        String filename = this.mDownloadCursor.getString(filenameColumnId);
        int mimetypeColumnId = this.mDownloadCursor.getColumnIndexOrThrow("mimetype");
        String mimetype = this.mDownloadCursor.getString(mimetypeColumnId);
        Uri path = Uri.parse(filename);
        if (path.getScheme() == null) {
            path = Uri.fromFile(new File(filename));
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(path, mimetype);
        intent.setFlags(67108864);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(this).setTitle(R.string.download_failed_generic_dlg_title).setIcon(R.drawable.ssl_icon).setMessage(R.string.download_no_application).setPositiveButton(R.string.ok, (DialogInterface.OnClickListener) null).show();
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        this.mDownloadCursor.moveToPosition(position);
        hideCompletedDownload();
        int status = this.mDownloadCursor.getInt(this.mStatusColumnId);
        if (Downloads.isStatusSuccess(status)) {
            openCurrentDownload();
        } else {
            checkStatus(id);
        }
    }

    private void hideCompletedDownload() {
        int status = this.mDownloadCursor.getInt(this.mStatusColumnId);
        int visibilityColumn = this.mDownloadCursor.getColumnIndexOrThrow("visibility");
        int visibility = this.mDownloadCursor.getInt(visibilityColumn);
        if (Downloads.isStatusCompleted(status) && visibility == 1) {
            ContentValues values = new ContentValues();
            values.put("visibility", (Integer) 0);
            getContentResolver().update(ContentUris.withAppendedId(Downloads.CONTENT_URI, this.mDownloadCursor.getLong(this.mIdColumnId)), values, null, null);
        }
    }
}
