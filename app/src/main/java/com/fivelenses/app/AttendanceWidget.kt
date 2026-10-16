
package com.fivelenses.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.fivelenses.R

class AttendanceWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, AttendanceWidget::class.java)
            val ids = manager.getAppWidgetIds(component)
            for (id in ids) {
                updateWidget(context, manager, id)
            }
        }

        private fun updateWidget(
            context: Context,
            manager: AppWidgetManager,
            id: Int
        ) {
            val views = RemoteViews(
                context.packageName,
                R.layout.widget_attendance
            )

            val count = Store.attendanceCount(context)
            val solved = Store.notes(context).count { it.completed }

            views.setTextViewText(
                R.id.widget_stats,
                "$solved reflections solved · $count days attended"
            )

            views.setTextViewText(
                R.id.widget_date,
                "YOUR DAILY CLARITY"
            )

            manager.updateAppWidget(id, views)
        }
    }
}
