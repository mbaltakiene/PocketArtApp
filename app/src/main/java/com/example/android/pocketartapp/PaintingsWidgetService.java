package com.example.android.pocketartapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.pocketartapp.ui.Painting;
import com.example.android.pocketartapp.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Random;

public class PaintingsWidgetService extends IntentService {

    public static final String ACTION_SET_PAINTING_TO_WIDGET = "com.example.android.mygarden.action.set_painting_to_widget";
    public final static int FOREGROUND_NOTIFICATION_ID = 1000;

    private ArrayList<Painting> mPaintings = new ArrayList<Painting>();
    private NotificationManager manager;


    public PaintingsWidgetService() {
        super("PaintingsWidgetService");
    }


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PaintingsWidgetService(String name) {
        super(name);
    }

    public static void startActionSetPaintingToWidget(Context context) {
        Intent intent = new Intent(context, PaintingsWidgetService.class);
        intent.setAction(ACTION_SET_PAINTING_TO_WIDGET);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification;
            NotificationChannel notificationChannel =
                    new NotificationChannel(getResources().getString(R.string.default_channel),
                            getResources().getString(R.string.default_channel),
                            NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(notificationChannel);

            notification = new Notification.Builder(this, notificationChannel.getId())
                    .setContentTitle(getResources().getString(R.string.content_title))
                    .setContentText(getResources().getString(R.string.content_text))
                    .build();
            startForeground(FOREGROUND_NOTIFICATION_ID, notification);
        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SET_PAINTING_TO_WIDGET.equals(action)) {
                handleActionSetPaintingToWidget();
            }
        }
    }

    private void handleActionSetPaintingToWidget() {

        final Context context = this;

        DatabaseReference mDatabase = FirebaseUtils.getDatabase();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            // Retrieve the data form database and pick a random element
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot entry : dataSnapshot.getChildren()) {
                    Painting painting = entry.getValue(Painting.class);
                    mPaintings.add(painting);
                }
                String imageUrl = "";
                int randomInt = 0;
                if (mPaintings != null && mPaintings.size() > 0) {
                    randomInt = new Random().nextInt(mPaintings.size());
                    imageUrl = mPaintings.get(randomInt).getUrl();
                }

                final int elementId = randomInt;
                // Get a resized bitmap and pass it to the widget
                Picasso.with(context).load(imageUrl).resize(500, 500).noFade().into(new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, HomeScreenWidgetProvider.class));
                        HomeScreenWidgetProvider.updatePaintingsWidgets(context, appWidgetManager, bitmap, elementId, appWidgetIds);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }

                });

            }

            // On authorisation error, the widget will be empty
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, HomeScreenWidgetProvider.class));
                HomeScreenWidgetProvider.updatePaintingsWidgets(context, appWidgetManager, null, 0, appWidgetIds);
            }
        });

    }

}
