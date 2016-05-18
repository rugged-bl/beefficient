package com.beefficient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.beefficient.main.MainActivity;

public class BackgroundService extends Service {
    public static final int DEFAULT_NOTIFICATION_ID = 101;

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        //Send Foreground Notification
        sendNotification("Ticker", "Title", "Text");

        //Task
        //doTask();

        //return Service.START_STICKY;
        return START_REDELIVER_INTENT;
    }

    //Send custom notification
    public void sendNotification(String Ticker, String Title, String Text) {

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                //.setOngoing(true)   //Can't be swiped out
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.large))   // большая картинка
                .setTicker(Ticker)
                .setContentTitle(Title) //Заголовок
                .setContentText(Text) // Текст уведомления
                .setWhen(System.currentTimeMillis());

        Notification notification;

        notification = builder.build();


        startForeground(DEFAULT_NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Removing any notifications
        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);

        //Disabling service
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
