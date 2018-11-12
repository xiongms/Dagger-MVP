package com.xiongms.libcore.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.io.File;

import static android.app.PendingIntent.getActivity;


/**
 *
 */
public class NotificationUtil {

    private static final int downloadNotifcationId = 0x4316;


    /**
     * 展示下载成功通知
     *
     * @param context               上下文
     * @param file                  下载的apk文件
     * @param notificationIconResId 通知图标资源id
     * @param notificationTitle     通知标题
     * @param isCanClear            通知是否可被清除
     */
    public static void showDownloadSuccessNotification(Context context, File file, String fileProvider, Bitmap largeIcon, int notificationIconResId, String notificationTitle, String notificationContent, boolean isCanClear) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //ChannelId为"1",ChannelName为"Channel1"
            NotificationChannel channel = new NotificationChannel("1",
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            manager.createNotificationChannel(channel);
        }

        Intent installIntent = new Intent();
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if("apk".equals(FileUtil.getExtensionName(file.getName()))) {
            installIntent.setAction(Intent.ACTION_VIEW);
            Uri uri = FileUtil.getUri(context, file, fileProvider);
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            installIntent.setAction(Intent.ACTION_SEND);
            installIntent.putExtra(Intent.EXTRA_STREAM, FileUtil.getUri(context, file, fileProvider));
            installIntent.setType("*/*");
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1"); //与channelId对应
        builder.setAutoCancel(true)
                .setShowWhen(true)
                .setOngoing(false)
                .setSmallIcon(notificationIconResId)
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setNumber(3); //久按桌面图标时允许的此条通知的数量

        builder.setLargeIcon(largeIcon);

        PendingIntent pendingIntent = getActivity(context, 0, installIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();// 获取一个Notification
        notification.defaults = Notification.DEFAULT_SOUND;// 设置为默认的声音
        notification.flags = isCanClear ? Notification.FLAG_ONLY_ALERT_ONCE : Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_NO_CLEAR;
        manager.notify(downloadNotifcationId, notification);// 显示通知
    }

    /**
     * 展示实时下载进度通知
     *
     * @param context               上下文
     * @param currentProgress       当前进度
     * @param totalProgress         总进度
     * @param notificationIconResId 通知图标资源id
     * @param notificationTitle     通知标题
     * @param isCanClear            通知是否可被清除
     */
    public static void showDownloadingNotification(Context context, int currentProgress, int totalProgress,  Bitmap largeIcon, int notificationIconResId, String notificationTitle, boolean isCanClear) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //ChannelId为"1",ChannelName为"Channel1"
            NotificationChannel channel = new NotificationChannel("1",
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1");
        builder.setAutoCancel(true)
                .setShowWhen(true)
                .setOngoing(false)
                .setSmallIcon(notificationIconResId)
                .setContentTitle(notificationTitle).setLargeIcon(largeIcon)
                .setProgress(totalProgress, currentProgress, false);
        Notification notification = builder.build();// 获取一个Notification
        notification.defaults = Notification.DEFAULT_SOUND;// 设置为默认的声音
        notification.flags = isCanClear ? Notification.FLAG_ONLY_ALERT_ONCE : Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_NO_CLEAR;
        manager.notify(downloadNotifcationId, notification);// 显示通知
    }

    /**
     * 展示下载失败通知
     *
     * @param context               上下文
     * @param notificationContent   通知内容,比如:下载失败,点击重新下载
     * @param intent                该intent用来重新下载应用
     * @param notificationIconResId 通知图标资源id
     * @param notificationTitle     通知标题
     * @param isCanClear            通知是否可被清除
     */
    public static void showDownloadFailureNotification(Context context, Intent intent,  Bitmap largeIcon, int notificationIconResId, String notificationTitle, String notificationContent, boolean isCanClear) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //ChannelId为"1",ChannelName为"Channel1"
            NotificationChannel channel = new NotificationChannel("1",
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1");
        builder.setAutoCancel(true)
                .setShowWhen(true)
                .setOngoing(false)
                .setSmallIcon(notificationIconResId)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle).setContentText(notificationContent);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();// 获取一个Notification
        notification.defaults = Notification.DEFAULT_SOUND;// 设置为默认的声音
        notification.flags = isCanClear ? Notification.FLAG_ONLY_ALERT_ONCE : Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_NO_CLEAR;
        manager.notify(downloadNotifcationId, notification);// 显示通知
    }

}
