package com.example.temphummqtt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private MQTTHelper mqttHelper;
    private LineChart chartHum;
    private LineChart chartTemp;
    private ChartHelper mChartHum;
    private ChartHelper mChartTemp;
    private TextView tvHum;
    private TextView tvTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        tvHum = findViewById(R.id.tvHum);
        tvTemp = findViewById(R.id.tvTemp);
        chartHum = findViewById(R.id.chart_hum);
        chartTemp = findViewById(R.id.chart_temp);
        mChartHum = new ChartHelper(chartHum, getResources().getColor(R.color.colorAccent));
        mChartTemp = new ChartHelper(chartTemp, getResources().getColor(R.color.colorPrimary));


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    Random random = new Random();
                    int a = random.nextInt(50);
                    int b = random.nextInt(100);
                    jsonObject.put("temp", a);
                    jsonObject.put("hum", b);
                    mqttHelper.publishMessage(jsonObject.toString(), 0, Constan.TOPIC_GET);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            mqttHelper = new MQTTHelper(getApplicationContext());
            mqttHelper.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {

                }

                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    btn.setText(message.toString());
                    JSONObject jsonObject = new JSONObject(message.toString());
                    tvHum.setText(jsonObject.getString("hum"));
                    tvTemp.setText(jsonObject.getString("temp"));
                    mChartHum.addEntry(Float.parseFloat(jsonObject.getString("hum")), getResources().getColor(R.color.colorAccent), "Humidity");
                    mChartTemp.addEntry(Float.parseFloat(jsonObject.getString("temp")), getResources().getColor(R.color.colorPrimary), "Temperature");
                    if (Float.parseFloat(jsonObject.getString("hum"))>80){
                        sendNotification("Humidity");
                    }
                    if (Float.parseFloat(jsonObject.getString("hum"))>40){
                        sendNotification("Temperature");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String title) {
        String content;
        if (title.equals("Humidity")){
            content = "Độ ẩm cao!!!";
        }else {
            content = "Nhiệt độ cao";
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_temp);
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Channel Name";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;

        Notification.Builder notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Create a notification and set the notification channel.
            notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_humidity)
                    .setLargeIcon(largeIcon)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(notifyID, notification.build());
        } else {
            Notification noti = new android.support.v4.app.NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_humidity)
                    .setLargeIcon(largeIcon)
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(notifyID, noti);
        }
    }
}
