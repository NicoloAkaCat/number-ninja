package dev.nicoloakacat.numberninja

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

data class Notification(
    val icon: Int = R.drawable.ic_game_stroke,
    val title: String,
    val body: String,
    val channel: String,
    val context: Context,
    val autoCancel: Boolean,
    val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
)
interface INotificationService {
    fun sendNotification(notification: Notification)
}

class NotificationManager : INotificationService {
    override fun sendNotification(notification: Notification) {
        val intent = Intent(notification.context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(notification.context, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(notification.context, notification.channel)
            .setSmallIcon(notification.icon)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(notification.autoCancel)
            .setSound(notification.ringtoneUri)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(notification.context)) {
            if (ActivityCompat.checkSelfPermission(
                    notification.context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) { return }
            notify(1, notificationBuilder.build())
        }
    }
}

class FirebaseNotification : FirebaseMessagingService(), INotificationService {
    override fun sendNotification(notification: Notification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(notification.context, notification.channel)
            .setSmallIcon(notification.icon)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(notification.autoCancel)
            .setSound(notification.ringtoneUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }
    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            if(it.title == null || it.body == null) return

            val notification = Notification(
                title = it.title!!,
                body = it.body!!,
                channel = "default_channel",
                context = this,
                autoCancel = true
            )
            sendNotification(notification)
        }
    }
}