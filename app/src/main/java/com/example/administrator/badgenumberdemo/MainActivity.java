package com.example.administrator.badgenumberdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.badgenumberdemo.utils.BadgeUtil;
import com.jauker.widget.BadgeView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btBadge;
    private TextView textView;
    private Button btn;
    private ImageView imageView;
    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        btBadge=(Button) findViewById(R.id.bt_badge);
        btBadge.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.tv1);
        btn = (Button) findViewById(R.id.btn1);
        imageView = (ImageView) findViewById(R.id.imageView1);
        layout = (LinearLayout) findViewById(R.id.layout1);

        setBadge();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_badge:
//                BadgeUtil.setBadgeCount(MainActivity.this,20);
                sendBadgeNumber();
                break;
        }
    }


    private void sendicon() {
        Intent intent = new Intent();
        intent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        intent.putExtra(
                "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",
                "com.example.badgedemo.MainActivity");
        intent.putExtra(
                "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", true);
        intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE",
                "99");
        intent.putExtra(
                "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",
                "com.example.badgedemo");
        sendBroadcast(intent);
    }

    private void sendBadgeNumber() {
        String number = "35";
        if (TextUtils.isEmpty(number)) {
            number = "0";
        } else {
            int numInt = Integer.valueOf(number);
            number = String.valueOf(Math.max(0, Math.min(numInt, 99)));
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            // sendToXiaoMi(number);
            sendToXiaoMi(10);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            sendToSony(number);
        } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            sendToSamsumg(number);
        } else {
            Toast.makeText(this, "Not Support", Toast.LENGTH_LONG).show();
        }
    }

    private void sendToXiaoMi(int number) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    this);
            builder.setContentTitle("您有" + number + "未读消息");
            builder.setTicker("您有" + number + "未读消息");
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            Class miuiNotificationClass = Class
                    .forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField(
                    "messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, number);// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);
            Toast.makeText(this, "Xiaomi=>isSendOk=>1", Toast.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("INFO", "**************  " + e.getMessage());
            // miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent(
                    "android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra(
                    "android.intent.extra.update_application_component_name",
                    getPackageName() + "/" + MainActivity.class);
            localIntent.putExtra(
                    "android.intent.extra.update_application_message_text",
                    number);
            sendBroadcast(localIntent);
        } finally {
            if (notification != null && isMiUIV6) {
                // miui6以上版本需要使用通知发送
                nm.notify(101010, notification);
            }
        }

    }

    private void sendToSony(String number) {
        boolean isShow = true;
        if ("0".equals(number)) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",isShow);// 是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",MainActivity.class);// 启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", number);// 数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",getPackageName());// 包名
        sendBroadcast(localIntent);
        Toast.makeText(this, "Sony," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    private void sendToSamsumg(String number) {
        Intent localIntent = new Intent(
                "android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", number);// 数字
        localIntent.putExtra("badge_count_package_name", getPackageName());// 包名
        localIntent.putExtra("badge_count_class_name", MainActivity.class); // 启动页
        sendBroadcast(localIntent);
        Toast.makeText(this, "Samsumg," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    private void setBadge(){
        BadgeView badgeView = new com.jauker.widget.BadgeView(this);
        badgeView.setTargetView(textView);
        badgeView.setBadgeCount(3);

        badgeView = new BadgeView(this);
        badgeView.setTargetView(btn);
        badgeView.setBadgeCount(-7);

        // 图片貌似不能使用badgeview
        badgeView = new BadgeView(this);
        badgeView.setTargetView(imageView);

        badgeView.setBadgeCount(0);

        badgeView = new BadgeView(this);
        badgeView.setTargetView(layout);
        badgeView.setBackground(12, Color.parseColor("#9b2eef"));
        badgeView.setText("提示");

        badgeView = new BadgeView(this);
        badgeView.setTargetView(layout);
        badgeView.setBadgeGravity(Gravity.BOTTOM | Gravity.CENTER);
        badgeView.setBadgeCount(4);

        badgeView = new BadgeView(this);
        badgeView.setTargetView(layout);
        badgeView.setBadgeGravity(Gravity.CENTER);
        badgeView.setBackgroundColor(Color.RED);
        badgeView.setBadgeMargin(-1);
        badgeView.setTextColor(Color.BLACK);
        badgeView.setBadgeCount(10);

        badgeView = new BadgeView(this);
        badgeView.setTargetView(layout);
        badgeView.setBadgeGravity(Gravity.LEFT | Gravity.CENTER);
        badgeView.setBackground(20, Color.RED);
        badgeView.setTextColor(Color.BLACK);
        badgeView.setBadgeCount(-6);

        badgeView = new BadgeView(this);
        badgeView.setTargetView(layout);
        badgeView.setBadgeGravity(Gravity.TOP | Gravity.LEFT);
        badgeView.setTypeface(Typeface.create(Typeface.SANS_SERIF,
                Typeface.ITALIC));
        badgeView.setShadowLayer(2, -1, -1, Color.GREEN);
        badgeView.setBadgeCount(2);
        //badgeView.setVisibility(View.GONE);
    }


}
