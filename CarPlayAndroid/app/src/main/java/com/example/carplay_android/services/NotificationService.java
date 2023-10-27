package com.example.carplay_android.services;

import static com.example.carplay_android.javabeans.JavaBeanFilters.*;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.clj.fastble.BleManager;
import com.example.carplay_android.R;
import com.example.carplay_android.utils.BroadcastUtils;
import com.example.carplay_android.utils.DirectionUtils;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class NotificationService extends NotificationListenerService {

    private BleService.BleBinder controlBle;
    private ServiceConnToBle serviceConnToBle;
    private Boolean deviceStatus = false;
    private Timer timerSendNotification;
    private Boolean ifSendNotification = false;
    private static String[] informationMessageSentLastTime = new String[7];

    public NotificationService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        init();

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
//        Log.d("isGMapNotification(sbn)", String.valueOf(isGMapNotification(sbn)));

        if (isGMapNotification(sbn)) {

            Notification not = sbn.getNotification();

            Bundle bun = not.extras;

//            for (String key : bun.keySet()) {
//                try {
//                    Log.d(key, bun.get(key).toString());
//                } catch (Exception e) {
//                }
//            }

            String hex = (String) "";
            try {
                Object largeIcon = bun.get("android.largeIcon");
                Icon icon = (Icon) largeIcon;

                Drawable drawable = icon.loadDrawable(this);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                byte[] bitmapBytes = baos.toByteArray();
                Log.i("bitmapBytes", Arrays.toString(bitmapBytes));
                // shorten byeteArray
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("MD5");
                } catch (Exception ignored) { }

                byte[] MD5Bytes = md.digest(bitmapBytes);

                for (byte key : MD5Bytes) {
                    hex = hex + Integer.toHexString(Math.abs(key));
                }
            } catch (Exception ignored) { }


            Log.e("hex", hex);

            String[] beginCodes = {
                   "2be7863432a727174291335104b3120"
            };
            String[] straightCodes = {
                    "770a543365614577b5963224c959"
            };
            String[] rightCodes = {
                    "124f4c726d254f207476c5b5b177b",
                    "52185e477fa2227526f2d1e1f627a8" //slight right
//                    still need sharp right
            };
            String[] leftCodes = {
                    "7a7374491f4242527c297e5464756810",
                    "2c603e264355203f4737327b48b1c5" //sharp left
//                    still need slight left
            };
            String[] uturnCodes = {

            };
            String[] endCodes = {

            };
//            unknown codes:
//            472c12564c381460645b53413472b25
//            5d271677202741134812697c13694962
//            2c767778672784d19702c173c3b515c

            String direction = (String) "x";
            String directionText = (String) "unknown";
            if (Arrays.asList(beginCodes).contains(hex)) { direction = "b"; directionText = "Lets go!"; }
            if (Arrays.asList(straightCodes).contains(hex)) { direction = "s"; directionText = "Continue straight"; }
            if (Arrays.asList(rightCodes).contains(hex)) { direction = "r"; directionText = "Turn right"; }
            if (Arrays.asList(leftCodes).contains(hex)) { direction = "l"; directionText = "Turn left"; }
            if (Arrays.asList(uturnCodes).contains(hex)) { direction = "u"; directionText = "Make a U-turn"; }
            if (Arrays.asList(endCodes).contains(hex)) { direction = "e"; directionText = "You have arrived!"; }


            String distanceString = bun.get("android.title").toString();
            Log.i("distanceString", distanceString);

            Double distance = 0.0;
            try {
                if (!distanceString.split(" ")[1].equals("m")) {
                    distance = Double.valueOf(distanceString.split(" ")[0]) * 1000;
                } else {
                    distance = Double.valueOf(distanceString.split(" ")[0]);
                }
            } catch (Exception e) {
                distance = 0.0  ;
            }

            Log.d("distance", String.valueOf(Math.round(distance)));

            String message = direction + ":" + String.valueOf(Math.round(distance));

            Log.i("message", message);
            Log.d("directionText", directionText);

            if (deviceStatus) {
                controlBle.sendDirection(message);
            }
        }


//        if (sbn != null && isGMapNotification(sbn)) {
//            handleGMapNotification(sbn);
//        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Log.d("Notification", "removed");
    }

    public static void cleanLastTimeSent() {
        Arrays.fill(informationMessageSentLastTime, "");
    }


    private boolean isGMapNotification(StatusBarNotification sbn) {
        if (!sbn.isOngoing() || !sbn.getPackageName().contains("com.google.android.apps.maps")) {
            return false;
        }
        return (sbn.getId() == 1);
    }


    private void handleGMapNotification(StatusBarNotification sbn) {
        Bundle bundle = sbn.getNotification().extras;

        Log.d("bundle", String.valueOf(bundle));
//        Log.d("bundle", bundle.getString(bundle.);

        String[] informationMessage = new String[7];

        Log.d("Notification", String.valueOf(Notification.EXTRA_TEXT));


        String string = bundle.getString(Notification.EXTRA_TEXT);


        String[] strings = string.split("-");//destination
        informationMessage[0] = strings[0].trim();
        strings = strings[1].trim().split(" ");
        if (strings.length == 3) {
            strings[0] = strings[0] + " ";//concat a " "
            strings[0] = strings[0] + strings[1];//if use 12 hour type, then concat the time and AM/PM
        }
        informationMessage[1] = strings[0];// get the ETA

        string = bundle.getString(Notification.EXTRA_TITLE);
        strings = string.split("-");
        if (strings.length == 2) {
            informationMessage[2] = strings[1].trim();//Distance to next direction
            informationMessage[3] = strings[0].trim();//Direction to somewhere
        } else if (strings.length == 1) {
            informationMessage[2] = strings[0].trim();//Direction to somewhere
            informationMessage[3] = "N/A";//Distance to next direction
        }

        string = bundle.getString(Notification.EXTRA_SUB_TEXT);
        strings = string.split("·");
        informationMessage[4] = strings[0].trim();//ETA in Minutes
        informationMessage[5] = strings[1].trim();//Distance
        BitmapDrawable bitmapDrawable = (BitmapDrawable) sbn.getNotification().getLargeIcon().loadDrawable(getApplicationContext());

        informationMessage[6] = String.valueOf(DirectionUtils.getDirectionNumber(DirectionUtils.getDirectionByComparing(bitmapDrawable.getBitmap())));

        if (deviceStatus) {
            if (!informationMessage[0].equals(informationMessageSentLastTime[0])) {//destination
                controlBle.sendDestination(informationMessage[0]);
                informationMessageSentLastTime[0] = informationMessage[0];
            }
            if (!Objects.equals(informationMessage[1], informationMessageSentLastTime[1])) {//ETA
                controlBle.sendEta(informationMessage[1]);
                informationMessageSentLastTime[1] = informationMessage[1];
            }
            if (!Objects.equals(informationMessage[2], informationMessageSentLastTime[2])) {//direction

                if (informationMessage[2].length() > 20) {
                    controlBle.sendDirection(informationMessage[2].substring(0, 20) + "..");
                } else {
                    controlBle.sendDirection(informationMessage[2]);
                }

                informationMessageSentLastTime[2] = informationMessage[2];
            }
            if (!Objects.equals(informationMessage[3], informationMessageSentLastTime[3])) {

                controlBle.sendDirectionDistances(informationMessage[3]);

                informationMessageSentLastTime[3] = informationMessage[3];
            }
            if (!Objects.equals(informationMessage[4], informationMessageSentLastTime[4])) {

                controlBle.sendEtaInMinutes(informationMessage[4]);

                informationMessageSentLastTime[4] = informationMessage[4];
            }
            if (!Objects.equals(informationMessage[5], informationMessageSentLastTime[5])) {

                controlBle.sendDistance(informationMessage[5]);

                informationMessageSentLastTime[5] = informationMessage[5];
            }
            if (!Objects.equals(informationMessage[6], informationMessageSentLastTime[6])) {

                controlBle.sendDirectionPrecise(informationMessage[6]);

                informationMessageSentLastTime[6] = informationMessage[6];
            }
            Log.d("d", "done");
            informationMessageSentLastTime = informationMessage;
            ifSendNotification = false;//reduce the frequency of sending messages
            //why not just check if two messages are the same,  why still need to send same message every half second:
            //because if the device lost connection before, we have to keep send message to it to keep it does not
            //receive any wrong message.
        }

    }


    private void init() {
        Arrays.fill(informationMessageSentLastTime, "");

        initService();
        initBroadcastReceiver();
        setSendNotificationTimer();
        BroadcastUtils.sendStatus(true, getFILTER_NOTIFICATION_STATUS(), getApplicationContext());
        DirectionUtils.loadSamplesFromAsserts(getApplicationContext());
    }

    private void initService() {
        serviceConnToBle = new ServiceConnToBle();
        Intent intent = new Intent(this, BleService.class);
        bindService(intent, serviceConnToBle, BIND_AUTO_CREATE);
        startService(intent);//bind the service
    }

    private void initBroadcastReceiver() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        ReceiverForDeviceStatus receiverForDeviceStatus = new ReceiverForDeviceStatus();
        IntentFilter intentFilterForDeviceStatus = new IntentFilter(getFILTER_DEVICE_STATUS());
        localBroadcastManager.registerReceiver(receiverForDeviceStatus, intentFilterForDeviceStatus);
    }

    private class ServiceConnToBle implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            controlBle = (BleService.BleBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    private class ReceiverForDeviceStatus extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            deviceStatus = intent.getBooleanExtra(getFILTER_DEVICE_STATUS(), false);
        }
    }

    public void setSendNotificationTimer() {
        if (timerSendNotification == null) {
            timerSendNotification = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    ifSendNotification = true;
                }
            };
            timerSendNotification.schedule(timerTask, 10, 2000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BroadcastUtils.sendStatus(false, getFILTER_NOTIFICATION_STATUS(), getApplicationContext());
        unbindService(serviceConnToBle);
    }
}