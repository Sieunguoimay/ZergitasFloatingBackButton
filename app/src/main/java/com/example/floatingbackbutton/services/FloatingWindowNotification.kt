package com.example.floatingbackbutton.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.floatingbackbutton.R
import com.example.floatingbackbutton.scenes.MainActivity
private const val TAG ="FW_NOTIFICATION"
class FloatingWindowNotification(
    var context:Context,
    var service: Service
) {
    private val NOTIFICATION_ID = 144
    private val CHANNEL_ID = "1"
    private var notification:Notification? = null


    init{
        createNotificationChannel()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.noti_home_ic)
            .setContent(createRemoteViews())
            .setCustomBigContentView(createRemoteViews())
            .setContentIntent(pendingIntent)
            .setContentTitle("Floating back button active")
            .setOnlyAlertOnce(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
        }

        notification = builder.build()
        with(NotificationManagerCompat.from(context)) { notify(NOTIFICATION_ID, notification!!) }

    }
    fun stopForeground(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            service.stopForeground(true)
            Log.d(TAG, "Stopped foreground " + Build.VERSION.SDK_INT+" "+Build.VERSION_CODES.O)
        }
    }
    fun startForeground(){

        Log.d(TAG,"Notification created")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            service.startForeground(NOTIFICATION_ID,notification)
            Log.d(TAG,"Start the foreground")
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "floating_window_noti_channel"
            val descriptionText = "A cool channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply { description = descriptionText }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createRemoteViews(): RemoteViews {
        //now we create a remoteViews that has assign all the intent action to the buttons of the notification layout
        //those intents will then be captured by the MusicPlayerService for controlling the mediaPlayer
        val remoteViews = RemoteViews(context.packageName,R.layout.notification_floating_window)
        remoteViews.setOnClickPendingIntent(R.id.bt_noti_back,createPendingIntentWithAction(
            FloatingWindowAccessibilityService.FloatingBackAccessibilityServiceActions.BACK))
        remoteViews.setOnClickPendingIntent(R.id.bt_noti_home,createPendingIntentWithAction(
            FloatingWindowAccessibilityService.FloatingBackAccessibilityServiceActions.HOME))
        remoteViews.setOnClickPendingIntent(R.id.bt_noti_recent,createPendingIntentWithAction(
            FloatingWindowAccessibilityService.FloatingBackAccessibilityServiceActions.RECENT))
        remoteViews.setOnClickPendingIntent(R.id.bt_noti_setting,createPendingIntentWithAction(
            FloatingWindowAccessibilityService.FloatingBackAccessibilityServiceActions.SETTING))
        return remoteViews
    }
    private fun createPendingIntentWithAction(action:String):PendingIntent{
        val intent = Intent(context, FloatingWindowAccessibilityService::class.java)
        intent.action = action
        return PendingIntent.getService(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}