package android.rk.RockAudioPlayer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.RemoteViews;

/* loaded from: classes.dex */
public class AudioPlayerAppWidgetProvider extends AppWidgetProvider {
    public static final String CMDAPPWIDGETUPDATE = "appwidgetupdate";
    static final String TAG = "AudioPlayerAppWidgetProvider";
    static final ComponentName THIS_APPWIDGET = new ComponentName("android.rk.RockAudioPlayer", "android.rk.RockAudioPlayer.AudioPlayerAppWidgetProvider");
    private static AudioPlayerAppWidgetProvider sInstance;

    private void LOG(String msg) {
        if (RockAudioPlayer.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static synchronized AudioPlayerAppWidgetProvider getInstance() {
        AudioPlayerAppWidgetProvider audioPlayerAppWidgetProvider;
        synchronized (AudioPlayerAppWidgetProvider.class) {
            if (sInstance == null) {
                sInstance = new AudioPlayerAppWidgetProvider();
            }
            audioPlayerAppWidgetProvider = sInstance;
        }
        return audioPlayerAppWidgetProvider;
    }

    @Override // android.appwidget.AppWidgetProvider
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        defaultAppWidget(context, appWidgetIds);
        Intent updateIntent = new Intent(AudioPlaybackService.SERVICECMD);
        updateIntent.putExtra(AudioPlaybackService.CMDNAME, CMDAPPWIDGETUPDATE);
        updateIntent.putExtra("appWidgetIds", appWidgetIds);
        updateIntent.addFlags(1073741824);
        context.sendBroadcast(updateIntent);
    }

    private void defaultAppWidget(Context context, int[] appWidgetIds) {
        Resources res = context.getResources();
        RemoteViews views = new RemoteViews(context.getPackageName(), (int) R.layout.audioplayer_appwidget);
        views.setViewVisibility(R.id.title, 8);
        views.setTextViewText(R.id.artist, res.getText(R.string.emptyplaylist));
        linkButtons(context, views, false, false);
        pushUpdate(context, appWidgetIds, views);
    }

    private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        AppWidgetManager gm = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            gm.updateAppWidget(appWidgetIds, views);
        } else {
            gm.updateAppWidget(THIS_APPWIDGET, views);
        }
    }

    private boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(THIS_APPWIDGET);
        return appWidgetIds.length > 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyChange(AudioPlaybackService service, String what) {
        LOG("notifyChange() : service = " + service + ", what = " + what);
        if (hasInstances(service)) {
            LOG("notifyChange() : There are some instances of AudioPlayerAppWidgetProvider in AppWidgetManager.");
            if (AudioPlaybackService.PLAYBACK_COMPLETE.equals(what) || AudioPlaybackService.META_CHANGED.equals(what) || AudioPlaybackService.PLAYSTATE_CHANGED.equals(what)) {
                performUpdate(service, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void performUpdate(AudioPlaybackService service, int[] appWidgetIds) {
        Resources res = service.getResources();
        RemoteViews views = new RemoteViews(service.getPackageName(), (int) R.layout.audioplayer_appwidget);
        int queuePosition = service.getQueuePosition() + 1;
        CharSequence titleName = service.getTrackName();
        CharSequence artistName = service.getArtistName();
        CharSequence errorState = null;
        if (titleName == null) {
            errorState = res.getText(R.string.emptyplaylist);
        }
        if (errorState != null) {
            views.setViewVisibility(R.id.title, 8);
            views.setTextViewText(R.id.artist, errorState);
        } else {
            views.setViewVisibility(R.id.title, 0);
            views.setTextViewText(R.id.title, titleName);
            views.setTextViewText(R.id.artist, artistName);
        }
        boolean playing = service.isPlaying();
        LOG("performUpdate() : playing = " + playing);
        if (playing) {
            views.setImageViewResource(R.id.control_play, R.drawable.appwidget_pause);
        } else {
            views.setImageViewResource(R.id.control_play, R.drawable.appwidget_play);
        }
        boolean hasPlayListInited = service.hasPlayListInited();
        linkButtons(service, views, hasPlayListInited, service.isOneShot());
        pushUpdate(service, appWidgetIds, views);
    }

    private void linkButtons(Context context, RemoteViews views, boolean hasPlayListInited, boolean isOneShot) {
        LOG("linkButtons() : hasPlayListInited = " + hasPlayListInited + ", isOneShot = " + isOneShot);
        Intent intent = new Intent(context, RockAudioPlayer.class);
        intent.setFlags(335544320);
        ComponentName serviceName = new ComponentName(context, AudioPlaybackService.class);
        if (hasPlayListInited) {
            intent.putExtra("checkLayout", 1);
            if (isOneShot) {
                intent.putExtra("oneshot", true);
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.album_appwidget, pendingIntent);
        if (hasPlayListInited) {
            Intent intent2 = new Intent(AudioPlaybackService.TOGGLEPAUSE_ACTION);
            intent2.setComponent(serviceName);
            PendingIntent pendingIntent2 = PendingIntent.getService(context, 0, intent2, 0);
            views.setOnClickPendingIntent(R.id.control_play, pendingIntent2);
            Intent intent3 = new Intent(AudioPlaybackService.PREVIOUS_ACTION);
            intent3.setComponent(serviceName);
            PendingIntent pendingIntent3 = PendingIntent.getService(context, 0, intent3, 0);
            views.setOnClickPendingIntent(R.id.control_pre, pendingIntent3);
            Intent intent4 = new Intent(AudioPlaybackService.NEXT_ACTION);
            intent4.setComponent(serviceName);
            PendingIntent pendingIntent4 = PendingIntent.getService(context, 0, intent4, 0);
            views.setOnClickPendingIntent(R.id.control_next, pendingIntent4);
        }
    }
}
