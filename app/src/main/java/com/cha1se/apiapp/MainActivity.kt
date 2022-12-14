package com.cha1se.apiapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RemoteViews
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    companion object {
        const val NOTIFICATION_ID = 101
        const val CHANNEL_ID = "Notify_channel"
    }

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        GlobalScope.launch {
            while (true) {
                UpdateWeather()
                delay(3600000)
            }
        }

    }

    public fun UpdateWeather() {

        var remoteViews: RemoteViews = RemoteViews(packageName, R.layout.notification_layout)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setSmallIcon(R.drawable.cloudy)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.cloudy))
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setCustomContentView(remoteViews)
        } else {

            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.cloudy)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.cloudy))
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setCustomContentView(remoteViews)
        }

        val apiInterface = WeatherAPI.create().getWeatherList()

        apiInterface.enqueue( object : Callback<DataList> {
            override fun onResponse(call: Call<DataList>?, response: Response<DataList>?) {

                if(response?.body() != null) {
                    remoteViews.setTextViewText(R.id.tempText, response.body()?.fact?.temp.toString() + "°")
                    remoteViews.setTextViewText(R.id.feels_like_text, "Но ощущается как " + response.body()?.fact?.feels_like.toString() + "°")
                    notificationManager.notify(1234, builder.build())
                }
            }

            override fun onFailure(call: Call<DataList>?, t: Throwable?) {
                Log.e("TAG", t?.message.toString())
            }
        })

    }

}