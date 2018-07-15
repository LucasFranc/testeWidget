package com.lucasfranco.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.codinginflow.widgetexample.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ExampleAppWidgetProvider extends AppWidgetProvider {

    final String CONST_PLUS = "plus";
    final String CONST_MINUS = "minus";
    final String CONST_ALERT = "alert";
    private static Integer airPower = 0;
    private static Boolean isWarning = false;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_widget);
            views.setOnClickPendingIntent(R.id.btn_plus_air, returnPendingIntentWithAction(CONST_PLUS, context));
            views.setOnClickPendingIntent(R.id.btn_minus_air, returnPendingIntentWithAction(CONST_MINUS, context));
            views.setOnClickPendingIntent(R.id.btn_alert, returnPendingIntentWithAction(CONST_ALERT, context));
            if(isWarning){
                // TODO: 13/07/2018 startAnimation
                views.setInt(R.id.btn_alert, "setBackgroundColor", ContextCompat.getColor(context,R.color.amber));
            }else{
                views.setInt(R.id.btn_alert, "setBackgroundColor", ContextCompat.getColor(context,R.color.bg_grey));
            }
            views.setOnClickPendingIntent(R.id.btn_config, returnPendingIntentConfig(context));
            views.setTextViewText(R.id.btn_power_air, airPower.toString());
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        switch (intent.getAction()) {
            case CONST_PLUS: {
                if (airPower < 4) {
                    airPower++;
                    updateWidget(context);
                }
                break;
            }
            case CONST_MINUS: {
                if (airPower > 0) {
                    airPower--;
                    updateWidget(context);
                }
                break;
            }
            case CONST_ALERT: {
                isWarning = !isWarning;
                updateWidget(context);
                break;
            }
        }
    }

    public void updateWidget(final Context context) {
        Intent intent = new Intent(context, ExampleAppWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, ExampleAppWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private PendingIntent returnPendingIntentWithAction(String action, Context context) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private PendingIntent returnPendingIntentConfig(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, intent, 0);
    }

}