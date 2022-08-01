package com.example.android.pocketartapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.pocketartapp.ui.DetailsActivity;
import com.example.android.pocketartapp.ui.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class HomeScreenWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                Bitmap bitmap, int elementId, int appWidgetId) {

        // Construct the RemoteViews object
        Intent intent;
        if (bitmap == null) {
            intent = new Intent(context, MainActivity.class);
        } else {
            intent = new Intent(context, DetailsActivity.class);
        }

        intent.putExtra(MainActivity.STARTING_POSITION_KEY, elementId);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.home_screen_widget);
        views.setOnClickPendingIntent(R.id.widget_frame_layout, pendingIntent);
        if (bitmap == null) {
            // If there is nothing returned from the data base, a default text message will appear on the widget.
            views.setViewVisibility(R.id.widget_image_view, View.GONE);
            views.setViewVisibility(R.id.widget_text_view, View.VISIBLE);
            appWidgetManager.updateAppWidget(appWidgetId, views);

        } else {
            // If there is a painting returned from the data base, the image will appear on the widget.
            views.setImageViewBitmap(R.id.widget_image_view, bitmap);
            views.setViewVisibility(R.id.widget_text_view, View.GONE);
            views.setViewVisibility(R.id.widget_image_view, View.VISIBLE);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updatePaintingsWidgets(Context context, AppWidgetManager appWidgetManager,
                                              Bitmap bitmap, int elementId, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, bitmap, elementId, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            PaintingsWidgetService.startActionSetPaintingToWidget(context);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

