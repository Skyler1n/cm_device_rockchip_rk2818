package android.rk.RockAudioPlayer;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Downloads;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/* loaded from: classes.dex */
public class BrowserDownloadAdapter extends ResourceCursorAdapter {
    private int mCurrentBytesColumnId;
    private int mDateColumnId;
    private int mDescColumnId;
    private int mFilenameColumnId;
    private int mMimetypeColumnId;
    private int mStatusColumnId;
    private int mTitleColumnId;
    private int mTotalBytesColumnId;

    public BrowserDownloadAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c);
        this.mFilenameColumnId = c.getColumnIndexOrThrow("_data");
        this.mTitleColumnId = c.getColumnIndexOrThrow("title");
        this.mDescColumnId = c.getColumnIndexOrThrow("description");
        this.mStatusColumnId = c.getColumnIndexOrThrow("status");
        this.mTotalBytesColumnId = c.getColumnIndexOrThrow("total_bytes");
        this.mCurrentBytesColumnId = c.getColumnIndexOrThrow("current_bytes");
        this.mMimetypeColumnId = c.getColumnIndexOrThrow("mimetype");
        this.mDateColumnId = c.getColumnIndexOrThrow("lastmod");
    }

    @Override // android.widget.CursorAdapter
    public void bindView(View view, Context context, Cursor cursor) {
        Resources r = context.getResources();
        String mimeType = cursor.getString(this.mMimetypeColumnId);
        ImageView iv = (ImageView) view.findViewById(R.id.download_icon);
        if ("application/vnd.oma.drm.message".equalsIgnoreCase(mimeType)) {
            iv.setImageResource(R.drawable.ic_launcher_drm_file);
        } else if (mimeType == null) {
            iv.setVisibility(4);
        } else {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromParts("file", "", null), mimeType);
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(intent, 65536);
            if (list.size() > 0) {
                Drawable icon = list.get(0).activityInfo.loadIcon(pm);
                iv.setImageDrawable(icon);
                iv.setVisibility(0);
            } else {
                iv.setVisibility(4);
            }
        }
        TextView tv = (TextView) view.findViewById(R.id.download_title);
        String title = cursor.getString(this.mTitleColumnId);
        if (title == null) {
            String fullFilename = cursor.getString(this.mFilenameColumnId);
            if (fullFilename == null) {
                title = r.getString(R.string.download_unknown_filename);
            } else {
                title = new File(fullFilename).getName();
                ContentValues values = new ContentValues();
                values.put("title", title);
                context.getContentResolver().update(ContentUris.withAppendedId(Downloads.CONTENT_URI, cursor.getLong(0)), values, null, null);
            }
        }
        tv.setText(title);
        TextView tv2 = (TextView) view.findViewById(R.id.domain);
        tv2.setText(cursor.getString(this.mDescColumnId));
        long totalBytes = cursor.getLong(this.mTotalBytesColumnId);
        int status = cursor.getInt(this.mStatusColumnId);
        if (Downloads.isStatusCompleted(status)) {
            View v = view.findViewById(R.id.progress_text);
            v.setVisibility(8);
            View v2 = view.findViewById(R.id.download_progress);
            v2.setVisibility(8);
            TextView tv3 = (TextView) view.findViewById(R.id.complete_text);
            tv3.setVisibility(0);
            if (Downloads.isStatusError(status)) {
                tv3.setText(getErrorText(status));
            } else {
                tv3.setText(r.getString(R.string.download_success, Formatter.formatFileSize(this.mContext, totalBytes)));
            }
            long time = cursor.getLong(this.mDateColumnId);
            Date d = new Date(time);
            DateFormat df = DateFormat.getDateInstance(3);
            TextView tv4 = (TextView) view.findViewById(R.id.complete_date);
            tv4.setVisibility(0);
            tv4.setText(df.format(d));
            return;
        }
        TextView tv5 = (TextView) view.findViewById(R.id.progress_text);
        tv5.setVisibility(0);
        View progress = view.findViewById(R.id.download_progress);
        progress.setVisibility(0);
        View v3 = view.findViewById(R.id.complete_date);
        v3.setVisibility(8);
        View v4 = view.findViewById(R.id.complete_text);
        v4.setVisibility(8);
        if (status == 190) {
            tv5.setText(r.getText(R.string.download_pending));
        } else if (status == 191) {
            tv5.setText(r.getText(R.string.download_pending_network));
        } else {
            ProgressBar pb = (ProgressBar) progress;
            StringBuilder sb = new StringBuilder();
            if (status == 192) {
                sb.append(r.getText(R.string.download_running));
            } else {
                sb.append(r.getText(R.string.download_running_paused));
            }
            if (totalBytes > 0) {
                long currentBytes = cursor.getLong(this.mCurrentBytesColumnId);
                int progressAmount = (int) ((100 * currentBytes) / totalBytes);
                sb.append(' ');
                sb.append(progressAmount);
                sb.append("% (");
                sb.append(Formatter.formatFileSize(this.mContext, currentBytes));
                sb.append("/");
                sb.append(Formatter.formatFileSize(this.mContext, totalBytes));
                sb.append(")");
                pb.setIndeterminate(false);
                pb.setProgress(progressAmount);
            } else {
                pb.setIndeterminate(true);
            }
            tv5.setText(sb.toString());
        }
    }

    public static int getErrorText(int status) {
        switch (status) {
            case 406:
                return R.string.download_not_acceptable;
            case 411:
                return R.string.download_length_required;
            case 412:
                return R.string.download_precondition_failed;
            case 490:
                return R.string.download_canceled;
            case 492:
                return R.string.download_file_error;
            default:
                return R.string.download_error;
        }
    }
}
