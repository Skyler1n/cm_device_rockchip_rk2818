package android.rk.RockAudioPlayer;

import android.content.ContentValues;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.provider.Downloads;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;

/* loaded from: classes.dex */
class FetchUrlMimeType extends AsyncTask<ContentValues, String, String> {
    SearchNetActivity mActivity;
    ContentValues mValues;

    public FetchUrlMimeType(SearchNetActivity activity) {
        this.mActivity = activity;
    }

    public String doInBackground(ContentValues... values) {
        Header header;
        int semicolonIndex;
        this.mValues = values[0];
        String uri = this.mValues.getAsString("uri");
        if (uri == null || uri.length() == 0) {
            return null;
        }
        AndroidHttpClient client = AndroidHttpClient.newInstance(this.mValues.getAsString("useragent"));
        HttpHead request = new HttpHead(uri);
        String cookie = this.mValues.getAsString("cookiedata");
        if (cookie != null && cookie.length() > 0) {
            request.addHeader("Cookie", cookie);
        }
        String referer = this.mValues.getAsString("referer");
        if (referer != null && referer.length() > 0) {
            request.addHeader("Referer", referer);
        }
        Boolean.valueOf(true);
        String mimeType = null;
        try {
            HttpResponse response = client.execute(request);
            if (!(response.getStatusLine().getStatusCode() != 200 || (header = response.getFirstHeader("Content-Type")) == null || (semicolonIndex = (mimeType = header.getValue()).indexOf(59)) == -1)) {
                mimeType = mimeType.substring(0, semicolonIndex);
            }
        } catch (IOException e) {
            request.abort();
        } catch (IllegalArgumentException e2) {
            request.abort();
        } finally {
            client.close();
        }
        return mimeType;
    }

    public void onPostExecute(String mimeType) {
        String newMimeType;
        if (mimeType != null) {
            String url = this.mValues.getAsString("uri");
            if ((mimeType.equalsIgnoreCase("text/plain") || mimeType.equalsIgnoreCase("application/octet-stream")) && (newMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))) != null) {
                this.mValues.put("mimetype", newMimeType);
            }
            String filename = URLUtil.guessFileName(url, null, mimeType);
            this.mValues.put("hint", filename);
        }
        Uri contentUri = this.mActivity.getContentResolver().insert(Downloads.CONTENT_URI, this.mValues);
        this.mActivity.viewDownloads(contentUri);
    }
}
