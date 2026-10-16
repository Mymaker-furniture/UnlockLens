package com.fivelenses.app

import android.content.Context
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

object Store {
    private const val PREF = "five_lenses_store"
    private fun prefs(c: Context) = c.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    fun notes(c: Context): MutableList<Reflection> {
        val arr = JSONArray(prefs(c).getString("notes", "[]"))
        return MutableList(arr.length()) { Reflection.fromJson(arr.getJSONObject(it)) }
    }
    fun saveNotes(c: Context, notes: List<Reflection>) {
        prefs(c).edit().putString("notes", JSONArray().apply { notes.forEach { put(it.toJson()) } }.toString()).apply()
        AttendanceWidget.refresh(c)
    }
    fun attendance(c: Context): MutableSet<String> = prefs(c).getStringSet("attendance", emptySet())?.toMutableSet() ?: mutableSetOf()
    fun markToday(c: Context) {
        val set = attendance(c); set.add(dayKey(System.currentTimeMillis()))
        prefs(c).edit().putStringSet("attendance", set).apply(); AttendanceWidget.refresh(c)
    }
    fun dayKey(t: Long): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(t))
    fun setting(c: Context, k: String, d: String) = prefs(c).getString(k, d) ?: d
    fun setSetting(c: Context, k: String, v: String) { prefs(c).edit().putString(k, v).apply() }
    fun attendanceCount(c: Context) = attendance(c).size
}
