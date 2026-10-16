package com.fivelenses.app

import android.app.*
import android.content.*
import android.os.Build

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(NotificationChannel("daily_lens", "Daily reflection", NotificationManager.IMPORTANCE_DEFAULT))
        val open = PendingIntent.getActivity(context, 1, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(context, "daily_lens") else Notification.Builder(context)
        manager.notify(501, notification.setSmallIcon(android.R.drawable.ic_menu_view).setContentTitle("Five Lenses · Daily check-in")
            .setContentText("What is true? What causes this? What am I missing? Where does it lead? What matters most?")
            .setContentIntent(open).setAutoCancel(true).build())
    }
}
object ReminderScheduler {
    fun schedule(c: Context) {
        val time = Store.setting(c, "reminder_time", "20:00").split(":")
        val h = time.getOrNull(0)?.toIntOrNull() ?: 20; val m = time.getOrNull(1)?.toIntOrNull() ?: 0
        val cal = java.util.Calendar.getInstance().apply { set(java.util.Calendar.HOUR_OF_DAY,h); set(java.util.Calendar.MINUTE,m); set(java.util.Calendar.SECOND,0); if (timeInMillis <= System.currentTimeMillis()) add(java.util.Calendar.DAY_OF_YEAR,1) }
        val intent = Intent(c, ReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(c, 501, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val am = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pi)
    }
}
