package com.example.temphum;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private LineChart mChart;
    private int temp = 30;
    private int hum = 60;
    private TextView tvTemp;
    private TextView tvHum;
    private TextView tvOn;
    private TextView tvOff;
    private ArrayList<Entry> yVals1 = new ArrayList<Entry>();
    private ArrayList<Entry> yVals2 = new ArrayList<Entry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        handler = new Handler();
        handler.postDelayed(runnable, 3000);

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getData();
        }
    };

    private void getData() {
//        APIService apiService = ApiUtils.getSOService();
//        apiService.getItem().enqueue(new Callback<Data>() {
//            @Override
//            public void onResponse(Call<Data> call, Response<Data> response) {
//                if (response.isSuccessful()) {
//                    Data data = response.body();
//                    setData(data.getTemp(), data.getHum());
//                    handler.postDelayed(runnable, 3000);
//                } else {
//                    int statusCode = response.code();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<Data> call, Throwable t) {
//
//            }
//        });
        temp++;
        hum++;
        setData(temp, hum);
        handler.postDelayed(runnable, 3000);
    }

    private void initView() {
        tvTemp = (TextView) findViewById(R.id.tvTemp);
        tvHum = (TextView) findViewById(R.id.tvHum);
        tvOn = (TextView) findViewById(R.id.tvON);
        tvOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
            }
        });
        tvOff = (TextView) findViewById(R.id.tvOFF);

        mChart = (LineChart) findViewById(R.id.chart);
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setDragDecelerationFrictionCoef(0.9f);
        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);
        // add data
        setData(temp, hum);
        mChart.animateX(2500);
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAxisMaximum(9);
        xAxis.setAxisMinimum(0);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(200);
        leftAxis.setAxisMinimum(-50);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setAxisMaximum(200);
        rightAxis.setAxisMinimum(-50);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);
    }

    private void setData(int temp, int hum) {
        if (temp > 50) {
            sendNotification("Nhiệt độ quá cao, có thể có cháy");
        }
        if (hum > 100) {
            sendNotification("Độ ẩm quá cao, có thể trời đang mưa");
        }
        tvTemp.setText(temp + "");
        tvHum.setText(hum + " %");
        if (yVals1.size() > 10) {
            yVals1.remove(0);
            yVals1.add(new Entry(yVals1.size(), hum));
            for (int i = 0; i < yVals1.size(); i++) {
                yVals1.get(i).setX(i);
            }
        } else {
            yVals1.add(new Entry(yVals1.size(), hum));
        }

        if (yVals2.size() > 10) {
            yVals2.remove(0);
            yVals2.add(new Entry(yVals2.size(), temp));
            for (int i = 0; i < yVals2.size(); i++) {
                yVals2.get(i).setX(i);
            }
        } else {
            yVals2.add(new Entry(yVals2.size(), temp));
        }

        LineDataSet set1, set2;
        set1 = new LineDataSet(yVals1, "Temperature");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(2f);
        set1.setCircleRadius(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);
        // create a dataset and give it a type
        set2 = new LineDataSet(yVals2, "Humidity");
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColor(Color.RED);
        set2.setCircleColor(Color.BLACK);
        set2.setLineWidth(2f);
        set2.setCircleRadius(3f);
        set2.setFillAlpha(65);
        set2.setFillColor(Color.RED);
        set2.setDrawCircleHole(false);
        set2.setHighLightColor(Color.rgb(244, 117, 117));
        // create a data object with the datasets
        LineData data = new LineData(set1, set2);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);
        // set data
        mChart.setData(data);
        mChart.invalidate();

    }

    private void sendNotification(String messageBody) {

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_humidity);
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "Channel Name";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;

        Notification.Builder notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Create a notification and set the notification channel.
            notification = new Notification.Builder(this)
                    .setContentTitle("Cảnh báo!!!")
                    .setContentText(messageBody)
                    .setSmallIcon(R.mipmap.ic_temp)
                    .setLargeIcon(largeIcon)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);
            mNotificationManager.notify(notifyID, notification.build());
        } else {
            Notification noti = new android.support.v4.app.NotificationCompat.Builder(this)
                    .setContentTitle("Cảnh báo!!!")
                    .setContentText(messageBody)
                    .setSmallIcon(R.mipmap.ic_temp)
                    .setLargeIcon(largeIcon)
                    .setShowWhen(true)
                    .setAutoCancel(true)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(notifyID, noti);
        }
    }
}
