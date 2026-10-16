
package com.fivelenses.app

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.fivelenses.R

class AttendanceWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(
                context.packageName,
                R.layout.widget_attendance
            )

            val prefs = context.getSharedPreferences(
                "five_lenses",
                Context.MODE_PRIVATE
            )

            val attendance = prefs.getInt("attendance_count", 0)
            val reflections = prefs.getInt("reflection_count", 0)

            views.setTextViewText(
                R.id.widget_attendance,
                "Days attended: $attendance"
            )

            views.setTextViewText(
                R.id.widget_reflections,
                "Reflections: $reflections"
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
