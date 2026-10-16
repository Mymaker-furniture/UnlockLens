package com.fivelenses.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.ComponentName
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class AttendanceWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { ids.forEach { update(context, manager, it) } }
    companion object {
        fun refresh(c: Context) { val m = AppWidgetManager.getInstance(c); val ids = m.getAppWidgetIds(ComponentName(c, AttendanceWidget::class.java)); ids.forEach { update(c,m,it) } }
        private fun update(c: Context, m: AppWidgetManager, id: Int) {
            val v = RemoteViews(c.packageName, R.layout.widget_attendance)
            val notes = Store.notes(c); val solved = notes.count { it.solved() }
            v.setTextViewText(R.id.widget_date, SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date()))
            v.setTextViewText(R.id.widget_stats, "$solved problems solved  ·  ${Store.attendanceCount(c)} days attended")
            val days = Store.attendance(c); val cal = Calendar.getInstance(); val out = StringBuilder()
            for (i in 6 downTo 0) { val d = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }; val key = Store.dayKey(d.timeInMillis); out.append(if(days.contains(key)) "● " else "○ ") }
            v.setTextViewText(R.id.widget_calendar, out.toString())
            m.updateAppWidget(id, v)
        }
    }
}
