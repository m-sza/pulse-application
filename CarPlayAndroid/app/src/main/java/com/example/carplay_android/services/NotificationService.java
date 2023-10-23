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
import com.example.carplay_android.utils.BroadcastUtils;
import com.example.carplay_android.utils.DirectionUtils;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
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
            Log.d("*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+", "*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+");
            Log.d("*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+", "*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+*/+");
            Log.d("", "");


            Log.d("sbn", String.valueOf(sbn));

            Notification not = sbn.getNotification();
            Log.d("not", String.valueOf(not));
            Log.d("not.extras", String.valueOf(not.extras));

            Bundle bun = not.extras;

            for (String key: bun.keySet())
            {
                try {
                    Log.d (key, bun.get(key).toString());
                    if (bun.get(key).toString() == "Icon(typ=BITMAP size=84x84)") {
                        Log.d("ok", "THIS");
                    }
                }
                catch (Exception e) {

                }
            }
//            fuck yeah
            Log.i("test", bun.get("android.largeIcon").toString());
            String distance = (String) "";

            Object largeIcon  = bun.get("android.largeIcon");

            Icon icon = (Icon) largeIcon;

            Log.i("icon.getType()", String.valueOf(icon.getType()));
            Log.i("Icon.TYPE_BITMAP", String.valueOf(Icon.TYPE_BITMAP));

            Context context = this; // Assuming you are within an Activity.
            Drawable drawable = icon.loadDrawable(context);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            byte[] bitmapBytes = baos.toByteArray();


            Log.i("bitmap", bitmap.toString());
            Log.i("bitmapBytes", Arrays.toString(bitmapBytes));

            String hex = (String) "";
            for (byte key: bitmapBytes) {
                Log.i("key", Integer.toHexString(key));
                hex = hex + Integer.toHexString(key);
            }
            Log.i("hex", hex);

            String[] beginCodes = {
                    "ffffff89504e47da1aa000d494844520005400054860001c6b10ffffffc10001735247420ffffffaeffffffce1cffffffe900047342495488887c864ffffff88002ffffffcd4944415478ffffff9cffffffedffffffd93b6b14510ffffffc5fffffff173ffffffdd1451ffffffb031ffffffa0ffffffb5ffffff9222463ffffffbeffffff82ffffffb57e253fffffff80183bbffffffdbffffffd8816ffffff828ffffffa2ffffff9dffffff85ffffff8d606150ffffff8458ffffff88ffffff95ffffffb65a6958c46ffffffd22affffff92ffffff98731fffffffaffffffb7ffffffc8ffffffddffffffb8cffffffc9ffffffeeffffffccffffffceffffff9d7dffffff9e1f2c69ffffffee6be27ffffffb333ffffff896466666666666638ffffff982ffffffa67a7dffffff8effffffa110ffffffc3fffffffc143feffffffb5ffffff8c1866ffffff9dffffffffffffffeaeffffffb5434dffffffcdffffffcc7253ffffff8b3affffffa0ffffff99596e6a5e2dffffff9affffffe9ffffffa61654cffffffb769ffffffa6ffffff9bffffff9a57c73ffffffa940ffffff98dffffff9fffffff81ffffffe95effffff9fffffffbfffffffafffffffc430ffffff973b8ffffffb361ffffffd9ffffffa14625ffffff9affffff99ffffffb534fffffff2ffffffa1266866ffffffd6ffffffe836ffffff9574ffffffcdffffffcc1affffffbdffffffa6233ffffffa46d66ffffffd6ffffffe83435ffffff8659ffffffe4ffffffd1ffffffa853ffffffc3ffffffdfffffffd418ffffffe64a17ffffffc26c586667a7dffffffddffffff95ffffffa07bffffffcdffffffccffffffaaf5dffffffa874ffffffbfffffff99592b43132a70ffffff8e62ffffffcdffffffdc2ffffffdeffffffe418fffffff72fffffff85960ffffffddffffffae34fffffff548ffffff95ffffff8b3ffffffb3ffffff925e49ffffff9affffffcc39655bffffffd21d49b39ffffffc62e4affffffba29692dffffffe7ffffffdaffffff93ffffff925ee6c53ffffff8159ffffffe04bffffffc166ffffffce1ffffffb5fffffff8ffffffb3ffffff9d39200ffffff97ffffff80ffffffb57e6b6a52ffffffc05960ffffffb560ffffff98fffffff3402dffffffceffffffcf15681c1bffffff80ffffffebffffffc07affffff81fffffffd5618ffffff94472a602785f30ffffffcc6bffffffc058ffffffd31affffffb93ffffff8dffffffe3370ffffffa560ffffffa8ffffffefffffff8013ffffffa9ffffffafffffffbfffffff8a7bffffffe829496772ffffff8e6dffffffdc331fffffff8610763bffffffdd30ffffff84ffffff80ffffffa427ffffff92ffffffe6256dffffffe4ffffff9c765affffffd2ffffffc94efffffff73cffffffcc58fffffffb21ffffff856d28ffffffdf456d4bffffffbaffffffa1ffffffbd30ffffffffffffff94ffffffdd34ffffff8407057ffffffd2f49fffffff7251d6b33ffffffe557fffffffc24554543ffffffbf497a2affffffa955ffffffe3ffffff9affffff9b593affffffccffffff8610ffffffc25f49ffffff8f25ffffffddffffff96ffffffb4ffffffd562ffffffe8ffffffaeffffffa46721ffffff84ffffffd5547b57a1876e1ffffffbbffffff87ffffffdc33ffffffe7ffffff885f40ffffff87ffffffcc2f74f3d607edffffffb81afffffff7ffffffcafffffffa1dffffffcf365effffffcdffffffd557438a3c276ffffff9a2e66ffffff93ffffffa66ffffffff316734b51affffffd7ffffffa8ffffffc5ffffffbd36ffffff9bffffffe6ffffffec0f62effffffccffffff8618ffffffea65ffffffe035fffffff01cffffffb840ffffffd3ffffffb779ffffff8b79ffffffa53ffffff8dffffffebffffff8c117ffffff81560ffffff91ffffffbd67ffffffd6ffffffc1cffffffb3ffffff8c54ffffff81fffffff642ffffffa5ffffffafffffff9effffffa3ffffffc8ffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc41c6862e34317ffffff9affffff9834dffffffccffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc4ffffffaafffffff81748a1f24ffffffddffffffcb31ffffffc6ffffffccffffffcc32fffffffc177bffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c6862fffffffdfffffffaffffffa6ffffffb426ffffffe96bffffff8e317d27fffffff4fffffffa071ffffff8e4bffffff9a6833ffffffec7b861ffffffbd1bffffffe7311b1efffffffdfffffffa2b3f2bffffffe97cffffff9b616f4381fffffffbb71ffffff9effffff81ffffffe7574fffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c68627e6c323333333333ffffffb3ffffff8afffffffd3ffffffa9ffffff80ffffffcdfffffff1ffffff80207d6e000049454e44ffffffae4260ffffff82"
            };
            String[] straightCodes = {
                    "ffffff89504e47da1aa000d494844520005400054860001c6b10ffffffc10001735247420ffffffaeffffffce1cffffffe900047342495488887c864ffffff88002ffffffcd4944415478ffffff9cffffffedffffffd93b6b14510ffffffc5fffffff173ffffffdd1451ffffffb031ffffffa0ffffffb5ffffff9222463ffffffbeffffff82ffffffb57e253fffffff80183bbffffffdbffffffd8816ffffff828ffffffa2ffffff9dffffff85ffffff8d606150ffffff8458ffffff88ffffff95ffffffb65a6958c46ffffffd22affffff92ffffff98731fffffffaffffffb7ffffffc8ffffffddffffffb8cffffffc9ffffffeeffffffccffffffceffffff9d7dffffff9e1f2c69ffffffee6be27ffffffb333ffffff896466666666666638ffffff982ffffffa67a7dffffff8effffffa110ffffffc3fffffffc143feffffffb5ffffff8c1866ffffff9dffffffffffffffeaeffffffb5434dffffffcdffffffcc7253ffffff8b3affffffa0ffffff99596e6a5e2dffffff9affffffe9ffffffa61654cffffffb769ffffffa6ffffff9bffffff9a57c73ffffffa940ffffff98dffffff9fffffff81ffffffe95effffff9fffffffbfffffffafffffffc430ffffff973b8ffffffb361ffffffd9ffffffa14625ffffff9affffff99ffffffb534fffffff2ffffffa1266866ffffffd6ffffffe836ffffff9574ffffffcdffffffcc1affffffbdffffffa6233ffffffa46d66ffffffd6ffffffe83435ffffff8659ffffffe4ffffffd1ffffffa853ffffffc3ffffffdfffffffd418ffffffe64a17ffffffc26c586667a7dffffffddffffff95ffffffa07bffffffcdffffffccffffffaaf5dffffffa874ffffffbfffffff99592b43132a70ffffff8e62ffffffcdffffffdc2ffffffdeffffffe418fffffff72fffffff85960ffffffddffffffae34fffffff548ffffff95ffffff8b3ffffffb3ffffff925e49ffffff9affffffcc39655bffffffd21d49b39ffffffc62e4affffffba29692dffffffe7ffffffdaffffff93ffffff925ee6c53ffffff8159ffffffe04bffffffc166ffffffce1ffffffb5fffffff8ffffffb3ffffff9d39200ffffff97ffffff80ffffffb57e6b6a52ffffffc05960ffffffb560ffffff98fffffff3402dffffffceffffffcf15681c1bffffff80ffffffebffffffc07affffff81fffffffd5618ffffff94472a602785f30ffffffcc6bffffffc058ffffffd31affffffb93ffffff8dffffffe3370ffffffa560ffffffa8ffffffefffffff8013ffffffa9ffffffafffffffbfffffff8a7bffffffe829496772ffffff8e6dffffffdc331fffffff8610763bffffffdd30ffffff84ffffff80ffffffa427ffffff92ffffffe6256dffffffe4ffffff9c765affffffd2ffffffc94efffffff73cffffffcc58fffffffb21ffffff856d28ffffffdf456d4bffffffbaffffffa1ffffffbd30ffffffffffffff94ffffffdd34ffffff8407057ffffffd2f49fffffff7251d6b33ffffffe557fffffffc24554543ffffffbf497a2affffffa955ffffffe3ffffff9affffff9b593affffffccffffff8610ffffffc25f49ffffff8f25ffffffddffffff96ffffffb4ffffffd562ffffffe8ffffffaeffffffa46721ffffff84ffffffd5547b57a1876e1ffffffbbffffff87ffffffdc33ffffffe7ffffff885f40ffffff87ffffffcc2f74f3d607edffffffb81afffffff7ffffffcafffffffa1dffffffcf365effffffcdffffffd557438a3c276ffffff9a2e66ffffff93ffffffa66ffffffff316734b51affffffd7ffffffa8ffffffc5ffffffbd36ffffff9bffffffe6ffffffec0f62effffffccffffff8618ffffffea65ffffffe035fffffff01cffffffb840ffffffd3ffffffb779ffffff8b79ffffffa53ffffff8dffffffebffffff8c117ffffff81560ffffff91ffffffbd67ffffffd6ffffffc1cffffffb3ffffff8c54ffffff81fffffff642ffffffa5ffffffafffffff9effffffa3ffffffc8ffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc41c6862e34317ffffff9affffff9834dffffffccffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc4ffffffaafffffff81748a1f24ffffffddffffffcb31ffffffc6ffffffccffffffcc32fffffffc177bffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c6862fffffffdfffffffaffffffa6ffffffb426ffffffe96bffffff8e317d27fffffff4fffffffa071ffffff8e4bffffff9a6833ffffffec7b861ffffffbd1bffffffe7311b1efffffffdfffffffa2b3f2bffffffe97cffffff9b616f4381fffffffbb71ffffff9effffff81ffffffe7574fffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c68627e6c323333333333ffffffb3ffffff8afffffffd3ffffffa9ffffff80ffffffcdfffffff1ffffff80207d6e000049454e44ffffffae4260ffffff82"
            };
            String[] rightCodes = {
                    "ffffff89504e47da1aa000d494844520005400054860001c6b10ffffffc10001735247420ffffffaeffffffce1cffffffe900047342495488887c864ffffff88002ffffffcd4944415478ffffff9cffffffedffffffd93b6b14510ffffffc5fffffff173ffffffdd1451ffffffb031ffffffa0ffffffb5ffffff9222463ffffffbeffffff82ffffffb57e253fffffff80183bbffffffdbffffffd8816ffffff828ffffffa2ffffff9dffffff85ffffff8d606150ffffff8458ffffff88ffffff95ffffffb65a6958c46ffffffd22affffff92ffffff98731fffffffaffffffb7ffffffc8ffffffddffffffb8cffffffc9ffffffeeffffffccffffffceffffff9d7dffffff9e1f2c69ffffffee6be27ffffffb333ffffff896466666666666638ffffff982ffffffa67a7dffffff8effffffa110ffffffc3fffffffc143feffffffb5ffffff8c1866ffffff9dffffffffffffffeaeffffffb5434dffffffcdffffffcc7253ffffff8b3affffffa0ffffff99596e6a5e2dffffff9affffffe9ffffffa61654cffffffb769ffffffa6ffffff9bffffff9a57c73ffffffa940ffffff98dffffff9fffffff81ffffffe95effffff9fffffffbfffffffafffffffc430ffffff973b8ffffffb361ffffffd9ffffffa14625ffffff9affffff99ffffffb534fffffff2ffffffa1266866ffffffd6ffffffe836ffffff9574ffffffcdffffffcc1affffffbdffffffa6233ffffffa46d66ffffffd6ffffffe83435ffffff8659ffffffe4ffffffd1ffffffa853ffffffc3ffffffdfffffffd418ffffffe64a17ffffffc26c586667a7dffffffddffffff95ffffffa07bffffffcdffffffccffffffaaf5dffffffa874ffffffbfffffff99592b43132a70ffffff8e62ffffffcdffffffdc2ffffffdeffffffe418fffffff72fffffff85960ffffffddffffffae34fffffff548ffffff95ffffff8b3ffffffb3ffffff925e49ffffff9affffffcc39655bffffffd21d49b39ffffffc62e4affffffba29692dffffffe7ffffffdaffffff93ffffff925ee6c53ffffff8159ffffffe04bffffffc166ffffffce1ffffffb5fffffff8ffffffb3ffffff9d39200ffffff97ffffff80ffffffb57e6b6a52ffffffc05960ffffffb560ffffff98fffffff3402dffffffceffffffcf15681c1bffffff80ffffffebffffffc07affffff81fffffffd5618ffffff94472a602785f30ffffffcc6bffffffc058ffffffd31affffffb93ffffff8dffffffe3370ffffffa560ffffffa8ffffffefffffff8013ffffffa9ffffffafffffffbfffffff8a7bffffffe829496772ffffff8e6dffffffdc331fffffff8610763bffffffdd30ffffff84ffffff80ffffffa427ffffff92ffffffe6256dffffffe4ffffff9c765affffffd2ffffffc94efffffff73cffffffcc58fffffffb21ffffff856d28ffffffdf456d4bffffffbaffffffa1ffffffbd30ffffffffffffff94ffffffdd34ffffff8407057ffffffd2f49fffffff7251d6b33ffffffe557fffffffc24554543ffffffbf497a2affffffa955ffffffe3ffffff9affffff9b593affffffccffffff8610ffffffc25f49ffffff8f25ffffffddffffff96ffffffb4ffffffd562ffffffe8ffffffaeffffffa46721ffffff84ffffffd5547b57a1876e1ffffffbbffffff87ffffffdc33ffffffe7ffffff885f40ffffff87ffffffcc2f74f3d607edffffffb81afffffff7ffffffcafffffffa1dffffffcf365effffffcdffffffd557438a3c276ffffff9a2e66ffffff93ffffffa66ffffffff316734b51affffffd7ffffffa8ffffffc5ffffffbd36ffffff9bffffffe6ffffffec0f62effffffccffffff8618ffffffea65ffffffe035fffffff01cffffffb840ffffffd3ffffffb779ffffff8b79ffffffa53ffffff8dffffffebffffff8c117ffffff81560ffffff91ffffffbd67ffffffd6ffffffc1cffffffb3ffffff8c54ffffff81fffffff642ffffffa5ffffffafffffff9effffffa3ffffffc8ffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc41c6862e34317ffffff9affffff9834dffffffccffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc4ffffffaafffffff81748a1f24ffffffddffffffcb31ffffffc6ffffffccffffffcc32fffffffc177bffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c6862fffffffdfffffffaffffffa6ffffffb426ffffffe96bffffff8e317d27fffffff4fffffffa071ffffff8e4bffffff9a6833ffffffec7b861ffffffbd1bffffffe7311b1efffffffdfffffffa2b3f2bffffffe97cffffff9b616f4381fffffffbb71ffffff9effffff81ffffffe7574fffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c68627e6c323333333333ffffffb3ffffff8afffffffd3ffffffa9ffffff80ffffffcdfffffff1ffffff80207d6e000049454e44ffffffae4260ffffff82"
            };
            String[] leftCodes = {
                    "ffffff89504e47da1aa000d494844520005400054860001c6b10ffffffc10001735247420ffffffaeffffffce1cffffffe900047342495488887c864ffffff88002ffffffcd4944415478ffffff9cffffffedffffffd93b6b14510ffffffc5fffffff173ffffffdd1451ffffffb031ffffffa0ffffffb5ffffff9222463ffffffbeffffff82ffffffb57e253fffffff80183bbffffffdbffffffd8816ffffff828ffffffa2ffffff9dffffff85ffffff8d606150ffffff8458ffffff88ffffff95ffffffb65a6958c46ffffffd22affffff92ffffff98731fffffffaffffffb7ffffffc8ffffffddffffffb8cffffffc9ffffffeeffffffccffffffceffffff9d7dffffff9e1f2c69ffffffee6be27ffffffb333ffffff896466666666666638ffffff982ffffffa67a7dffffff8effffffa110ffffffc3fffffffc143feffffffb5ffffff8c1866ffffff9dffffffffffffffeaeffffffb5434dffffffcdffffffcc7253ffffff8b3affffffa0ffffff99596e6a5e2dffffff9affffffe9ffffffa61654cffffffb769ffffffa6ffffff9bffffff9a57c73ffffffa940ffffff98dffffff9fffffff81ffffffe95effffff9fffffffbfffffffafffffffc430ffffff973b8ffffffb361ffffffd9ffffffa14625ffffff9affffff99ffffffb534fffffff2ffffffa1266866ffffffd6ffffffe836ffffff9574ffffffcdffffffcc1affffffbdffffffa6233ffffffa46d66ffffffd6ffffffe83435ffffff8659ffffffe4ffffffd1ffffffa853ffffffc3ffffffdfffffffd418ffffffe64a17ffffffc26c586667a7dffffffddffffff95ffffffa07bffffffcdffffffccffffffaaf5dffffffa874ffffffbfffffff99592b43132a70ffffff8e62ffffffcdffffffdc2ffffffdeffffffe418fffffff72fffffff85960ffffffddffffffae34fffffff548ffffff95ffffff8b3ffffffb3ffffff925e49ffffff9affffffcc39655bffffffd21d49b39ffffffc62e4affffffba29692dffffffe7ffffffdaffffff93ffffff925ee6c53ffffff8159ffffffe04bffffffc166ffffffce1ffffffb5fffffff8ffffffb3ffffff9d39200ffffff97ffffff80ffffffb57e6b6a52ffffffc05960ffffffb560ffffff98fffffff3402dffffffceffffffcf15681c1bffffff80ffffffebffffffc07affffff81fffffffd5618ffffff94472a602785f30ffffffcc6bffffffc058ffffffd31affffffb93ffffff8dffffffe3370ffffffa560ffffffa8ffffffefffffff8013ffffffa9ffffffafffffffbfffffff8a7bffffffe829496772ffffff8e6dffffffdc331fffffff8610763bffffffdd30ffffff84ffffff80ffffffa427ffffff92ffffffe6256dffffffe4ffffff9c765affffffd2ffffffc94efffffff73cffffffcc58fffffffb21ffffff856d28ffffffdf456d4bffffffbaffffffa1ffffffbd30ffffffffffffff94ffffffdd34ffffff8407057ffffffd2f49fffffff7251d6b33ffffffe557fffffffc24554543ffffffbf497a2affffffa955ffffffe3ffffff9affffff9b593affffffccffffff8610ffffffc25f49ffffff8f25ffffffddffffff96ffffffb4ffffffd562ffffffe8ffffffaeffffffa46721ffffff84ffffffd5547b57a1876e1ffffffbbffffff87ffffffdc33ffffffe7ffffff885f40ffffff87ffffffcc2f74f3d607edffffffb81afffffff7ffffffcafffffffa1dffffffcf365effffffcdffffffd557438a3c276ffffff9a2e66ffffff93ffffffa66ffffffff316734b51affffffd7ffffffa8ffffffc5ffffffbd36ffffff9bffffffe6ffffffec0f62effffffccffffff8618ffffffea65ffffffe035fffffff01cffffffb840ffffffd3ffffffb779ffffff8b79ffffffa53ffffff8dffffffebffffff8c117ffffff81560ffffff91ffffffbd67ffffffd6ffffffc1cffffffb3ffffff8c54ffffff81fffffff642ffffffa5ffffffafffffff9effffffa3ffffffc8ffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc41c6862e34317ffffff9affffff9834dffffffccffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc4ffffffaafffffff81748a1f24ffffffddffffffcb31ffffffc6ffffffccffffffcc32fffffffc177bffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c6862fffffffdfffffffaffffffa6ffffffb426ffffffe96bffffff8e317d27fffffff4fffffffa071ffffff8e4bffffff9a6833ffffffec7b861ffffffbd1bffffffe7311b1efffffffdfffffffa2b3f2bffffffe97cffffff9b616f4381fffffffbb71ffffff9effffff81ffffffe7574fffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c68627e6c323333333333ffffffb3ffffff8afffffffd3ffffffa9ffffff80ffffffcdfffffff1ffffff80207d6e000049454e44ffffffae4260ffffff82"
            };
            String[] uturnCodes = {
                    "ffffff89504e47da1aa000d494844520005400054860001c6b10ffffffc10001735247420ffffffaeffffffce1cffffffe900047342495488887c864ffffff88002ffffffcd4944415478ffffff9cffffffedffffffd93b6b14510ffffffc5fffffff173ffffffdd1451ffffffb031ffffffa0ffffffb5ffffff9222463ffffffbeffffff82ffffffb57e253fffffff80183bbffffffdbffffffd8816ffffff828ffffffa2ffffff9dffffff85ffffff8d606150ffffff8458ffffff88ffffff95ffffffb65a6958c46ffffffd22affffff92ffffff98731fffffffaffffffb7ffffffc8ffffffddffffffb8cffffffc9ffffffeeffffffccffffffceffffff9d7dffffff9e1f2c69ffffffee6be27ffffffb333ffffff896466666666666638ffffff982ffffffa67a7dffffff8effffffa110ffffffc3fffffffc143feffffffb5ffffff8c1866ffffff9dffffffffffffffeaeffffffb5434dffffffcdffffffcc7253ffffff8b3affffffa0ffffff99596e6a5e2dffffff9affffffe9ffffffa61654cffffffb769ffffffa6ffffff9bffffff9a57c73ffffffa940ffffff98dffffff9fffffff81ffffffe95effffff9fffffffbfffffffafffffffc430ffffff973b8ffffffb361ffffffd9ffffffa14625ffffff9affffff99ffffffb534fffffff2ffffffa1266866ffffffd6ffffffe836ffffff9574ffffffcdffffffcc1affffffbdffffffa6233ffffffa46d66ffffffd6ffffffe83435ffffff8659ffffffe4ffffffd1ffffffa853ffffffc3ffffffdfffffffd418ffffffe64a17ffffffc26c586667a7dffffffddffffff95ffffffa07bffffffcdffffffccffffffaaf5dffffffa874ffffffbfffffff99592b43132a70ffffff8e62ffffffcdffffffdc2ffffffdeffffffe418fffffff72fffffff85960ffffffddffffffae34fffffff548ffffff95ffffff8b3ffffffb3ffffff925e49ffffff9affffffcc39655bffffffd21d49b39ffffffc62e4affffffba29692dffffffe7ffffffdaffffff93ffffff925ee6c53ffffff8159ffffffe04bffffffc166ffffffce1ffffffb5fffffff8ffffffb3ffffff9d39200ffffff97ffffff80ffffffb57e6b6a52ffffffc05960ffffffb560ffffff98fffffff3402dffffffceffffffcf15681c1bffffff80ffffffebffffffc07affffff81fffffffd5618ffffff94472a602785f30ffffffcc6bffffffc058ffffffd31affffffb93ffffff8dffffffe3370ffffffa560ffffffa8ffffffefffffff8013ffffffa9ffffffafffffffbfffffff8a7bffffffe829496772ffffff8e6dffffffdc331fffffff8610763bffffffdd30ffffff84ffffff80ffffffa427ffffff92ffffffe6256dffffffe4ffffff9c765affffffd2ffffffc94efffffff73cffffffcc58fffffffb21ffffff856d28ffffffdf456d4bffffffbaffffffa1ffffffbd30ffffffffffffff94ffffffdd34ffffff8407057ffffffd2f49fffffff7251d6b33ffffffe557fffffffc24554543ffffffbf497a2affffffa955ffffffe3ffffff9affffff9b593affffffccffffff8610ffffffc25f49ffffff8f25ffffffddffffff96ffffffb4ffffffd562ffffffe8ffffffaeffffffa46721ffffff84ffffffd5547b57a1876e1ffffffbbffffff87ffffffdc33ffffffe7ffffff885f40ffffff87ffffffcc2f74f3d607edffffffb81afffffff7ffffffcafffffffa1dffffffcf365effffffcdffffffd557438a3c276ffffff9a2e66ffffff93ffffffa66ffffffff316734b51affffffd7ffffffa8ffffffc5ffffffbd36ffffff9bffffffe6ffffffec0f62effffffccffffff8618ffffffea65ffffffe035fffffff01cffffffb840ffffffd3ffffffb779ffffff8b79ffffffa53ffffff8dffffffebffffff8c117ffffff81560ffffff91ffffffbd67ffffffd6ffffffc1cffffffb3ffffff8c54ffffff81fffffff642ffffffa5ffffffafffffff9effffffa3ffffffc8ffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc41c6862e34317ffffff9affffff9834dffffffccffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc4ffffffaafffffff81748a1f24ffffffddffffffcb31ffffffc6ffffffccffffffcc32fffffffc177bffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c6862fffffffdfffffffaffffffa6ffffffb426ffffffe96bffffff8e317d27fffffff4fffffffa071ffffff8e4bffffff9a6833ffffffec7b861ffffffbd1bffffffe7311b1efffffffdfffffffa2b3f2bffffffe97cffffff9b616f4381fffffffbb71ffffff9effffff81ffffffe7574fffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c68627e6c323333333333ffffffb3ffffff8afffffffd3ffffffa9ffffff80ffffffcdfffffff1ffffff80207d6e000049454e44ffffffae4260ffffff82"
            };
            String[] endCodes = {
                    "ffffff89504e47da1aa000d494844520005400054860001c6b10ffffffc10001735247420ffffffaeffffffce1cffffffe900047342495488887c864ffffff88002ffffffcd4944415478ffffff9cffffffedffffffd93b6b14510ffffffc5fffffff173ffffffdd1451ffffffb031ffffffa0ffffffb5ffffff9222463ffffffbeffffff82ffffffb57e253fffffff80183bbffffffdbffffffd8816ffffff828ffffffa2ffffff9dffffff85ffffff8d606150ffffff8458ffffff88ffffff95ffffffb65a6958c46ffffffd22affffff92ffffff98731fffffffaffffffb7ffffffc8ffffffddffffffb8cffffffc9ffffffeeffffffccffffffceffffff9d7dffffff9e1f2c69ffffffee6be27ffffffb333ffffff896466666666666638ffffff982ffffffa67a7dffffff8effffffa110ffffffc3fffffffc143feffffffb5ffffff8c1866ffffff9dffffffffffffffeaeffffffb5434dffffffcdffffffcc7253ffffff8b3affffffa0ffffff99596e6a5e2dffffff9affffffe9ffffffa61654cffffffb769ffffffa6ffffff9bffffff9a57c73ffffffa940ffffff98dffffff9fffffff81ffffffe95effffff9fffffffbfffffffafffffffc430ffffff973b8ffffffb361ffffffd9ffffffa14625ffffff9affffff99ffffffb534fffffff2ffffffa1266866ffffffd6ffffffe836ffffff9574ffffffcdffffffcc1affffffbdffffffa6233ffffffa46d66ffffffd6ffffffe83435ffffff8659ffffffe4ffffffd1ffffffa853ffffffc3ffffffdfffffffd418ffffffe64a17ffffffc26c586667a7dffffffddffffff95ffffffa07bffffffcdffffffccffffffaaf5dffffffa874ffffffbfffffff99592b43132a70ffffff8e62ffffffcdffffffdc2ffffffdeffffffe418fffffff72fffffff85960ffffffddffffffae34fffffff548ffffff95ffffff8b3ffffffb3ffffff925e49ffffff9affffffcc39655bffffffd21d49b39ffffffc62e4affffffba29692dffffffe7ffffffdaffffff93ffffff925ee6c53ffffff8159ffffffe04bffffffc166ffffffce1ffffffb5fffffff8ffffffb3ffffff9d39200ffffff97ffffff80ffffffb57e6b6a52ffffffc05960ffffffb560ffffff98fffffff3402dffffffceffffffcf15681c1bffffff80ffffffebffffffc07affffff81fffffffd5618ffffff94472a602785f30ffffffcc6bffffffc058ffffffd31affffffb93ffffff8dffffffe3370ffffffa560ffffffa8ffffffefffffff8013ffffffa9ffffffafffffffbfffffff8a7bffffffe829496772ffffff8e6dffffffdc331fffffff8610763bffffffdd30ffffff84ffffff80ffffffa427ffffff92ffffffe6256dffffffe4ffffff9c765affffffd2ffffffc94efffffff73cffffffcc58fffffffb21ffffff856d28ffffffdf456d4bffffffbaffffffa1ffffffbd30ffffffffffffff94ffffffdd34ffffff8407057ffffffd2f49fffffff7251d6b33ffffffe557fffffffc24554543ffffffbf497a2affffffa955ffffffe3ffffff9affffff9b593affffffccffffff8610ffffffc25f49ffffff8f25ffffffddffffff96ffffffb4ffffffd562ffffffe8ffffffaeffffffa46721ffffff84ffffffd5547b57a1876e1ffffffbbffffff87ffffffdc33ffffffe7ffffff885f40ffffff87ffffffcc2f74f3d607edffffffb81afffffff7ffffffcafffffffa1dffffffcf365effffffcdffffffd557438a3c276ffffff9a2e66ffffff93ffffffa66ffffffff316734b51affffffd7ffffffa8ffffffc5ffffffbd36ffffff9bffffffe6ffffffec0f62effffffccffffff8618ffffffea65ffffffe035fffffff01cffffffb840ffffffd3ffffffb779ffffff8b79ffffffa53ffffff8dffffffebffffff8c117ffffff81560ffffff91ffffffbd67ffffffd6ffffffc1cffffffb3ffffff8c54ffffff81fffffff642ffffffa5ffffffafffffff9effffffa3ffffffc8ffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc41c6862e34317ffffff9affffff9834dffffffccffffff8126ffffffe6401373ffffffa0ffffff8939ffffffd0ffffffc4ffffffaafffffff81748a1f24ffffffddffffffcb31ffffffc6ffffffccffffffcc32fffffffc177bffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c6862fffffffdfffffffaffffffa6ffffffb426ffffffe96bffffff8e317d27fffffff4fffffffa071ffffff8e4bffffff9a6833ffffffec7b861ffffffbd1bffffffe7311b1efffffffdfffffffa2b3f2bffffffe97cffffff9b616f4381fffffffbb71ffffff9effffff81ffffffe7574fffffffdbffffffe7401373ffffffa0ffffff8939ffffffd0ffffffc41c68627e6c323333333333ffffffb3ffffff8afffffffd3ffffffa9ffffff80ffffffcdfffffff1ffffff80207d6e000049454e44ffffffae4260ffffff82"
            };

            Log.i("WO", String.valueOf(Arrays.asList(beginCodes).contains(hex)));
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