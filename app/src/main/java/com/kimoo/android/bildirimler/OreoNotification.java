package com.kimoo.android.bildirimler;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

public class OreoNotification extends ContextWrapper {
    private static final String ID = "mesajlar";
    private static final String NAME = "Mesajlar";
    private static final String ID2 = "begeniler";
    private static final String NAME2 = "Beğeniler";

    private NotificationManager notificationManager;


    public OreoNotification(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannel() {

        NotificationChannel channel = new NotificationChannel(ID, NAME,NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel);

        NotificationChannel channel2 = new NotificationChannel(ID2, NAME2,NotificationManager.IMPORTANCE_DEFAULT);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(channel2);
    }
    public NotificationManager getManager() {
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getOreoNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon){
        if(body.equals("Mesajınızı görüntülemek için tıklayın.")) {
            return new Notification.Builder(getApplicationContext(), ID)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(Integer.parseInt(icon))
                    .setSound(soundUri)
                    .setAutoCancel(true);
        }
        else if(body.equals("Sizi beğenenin kim olduğunu görmek için tıklayın.")){
            return new Notification.Builder(getApplicationContext(), ID2)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(Integer.parseInt(icon))
                    .setSound(soundUri)
                    .setAutoCancel(true);
        }
        else
            return null;

    }
}
