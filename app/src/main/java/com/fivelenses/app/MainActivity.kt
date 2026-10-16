package com.fivelenses.app

import android.app.*
import android.content.*
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : Activity() {
    private val bg = 0xFF0B0E16.toInt()
    private val panel = 0xFF131827.toInt()
    private val panel2 = 0xFF191F31.toInt()
    private val white = 0xFFF4F6FF.toInt()
    private val muted = 0xFF9BA5BD.toInt()
    private val lime = 0xFFC9F36A.toInt()
    private lateinit var root: LinearLayout
    private var currentPage = "home"
    private var editingId: String? = null
    private var answers = MutableList(5) { Answer() }
    private var titleInput: EditText? = null
    private var areaInput: Spinner? = null
    private var problemInput: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); showHome() }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun shape(color: Int, radius: Int = 20, stroke: Int = 0): GradientDrawable =
        GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat(); if (stroke != 0) setStroke(dp(1), stroke) }
    private fun text(s: String, size: Float, color: Int = white, bold: Boolean = false): TextView =
        TextView(this).apply { text = s; textSize = size; setTextColor(color); if (bold) setTypeface(typeface, Typeface.BOLD); setLineSpacing(dp(3).toFloat(), 1f) }
    private fun button(s: String, primary: Boolean = false, action: () -> Unit): TextView =
        text(s, 14f, if (primary) bg else white, true).apply {
            gravity = Gravity.CENTER; setPadding(dp(15), dp(12), dp(15), dp(12))
            background = shape(if (primary) lime else panel2, 15, if (primary) lime else 0xFF30384B.toInt())
            setOnClickListener { action() }
        }
    private fun column(): LinearLayout = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
    private fun row(): LinearLayout = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
    private fun add(parent: LinearLayout, v: View, bottom: Int = 10, weight: Float = 0f) {
        parent.addView(v, LinearLayout.LayoutParams(if (weight > 0) 0 else ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, weight).apply { bottomMargin = dp(bottom) })
    }
    private fun card(parent: LinearLayout): LinearLayout = column().apply { setPadding(dp(17), dp(17), dp(17), dp(17)); background = shape(panel, 23, 0xFF252D40.toInt()); add(parent, this, 14) }
    private fun base(title: String, subtitle: String): LinearLayout {
        root = column().apply { setBackgroundColor(bg); setPadding(dp(17), dp(15), dp(17), dp(22)) }
        val scroller = ScrollView(this); scroller.addView(root); setContentView(scroller)
        val header = row()
        val brand = column(); add(brand, text("✳ FIVE LENSES", 12f, lime, true), 4); add(brand, text(title, 25f, white, true), 1)
        header.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))
        add(header, button("＋", false) { newReflection() }, 0)
        add(root, header, 6); add(root, text(subtitle, 13f, muted), 18)
        return root
    }
    private fun nav() {
        val r = row().apply { background = shape(panel2, 18); setPadding(dp(5), dp(5), dp(5), dp(5)) }
        listOf("Home" to "home", "Reflect" to "editor", "Notes" to "notes", "Solved" to "solved", "Settings" to "settings").forEach { (label, page) ->
            val b = text(label, 11f, if (currentPage == page) bg else muted, true).apply {
                gravity = Gravity.CENTER; setPadding(dp(4), dp(12), dp(4), dp(12)); background = shape(if (currentPage == page) lime else panel2, 12)
                setOnClickListener { when(page) { "home" -> showHome(); "editor" -> newReflection(); "notes" -> showNotes(); "solved" -> showSolved(); else -> showSettings() } }
            }; r.addView(b, LinearLayout.LayoutParams(0, -2, 1f))
        }
        add(root, r, 12)
    }
    private fun showHome() {
        currentPage = "home"; base("Look closer. See what matters.", SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date()))
        nav()
        val notes = Store.notes(this); val solved = notes.count { it.solved() }; val checks = notes.sumOf { it.progress() }
        val stat = card(root)
        add(stat, text("YOUR THINKING, IN MOTION", 11f, lime, true), 10)
        add(stat, text("${notes.size} reflections     $checks / ${notes.size * 5} checked answers", 17f, white, true), 7)
        add(stat, text("$solved solved problems     ${Store.attendanceCount(this)} days attended", 13f, muted), 0)
        val c = card(root)
        add(c, text("HOW THE FIVE QUESTIONS WORK TOGETHER", 11f, lime, true), 8)
        add(c, text("Apply five lenses to the same situation.", 18f, white, true), 12)
        LensData.titles.indices.forEach { i ->
            val r = row().apply { gravity = Gravity.TOP; setPadding(0, dp(12), 0, dp(12)) }
            val icon = text(LensData.symbols[i], 24f, bg, true).apply { gravity = Gravity.CENTER; background = shape(LensData.colors[i], 14); setPadding(dp(12), dp(9), dp(12), dp(9)) }
            r.addView(icon)
            val content = column().apply { setPadding(dp(12), 0, 0, 0) }
            add(content, text("${i+1}. ${LensData.titles[i]}", 17f, white, true), 5)
            add(content, text(LensData.questions[i], 15f, white), 5)
            add(content, text(LensData.descriptions[i], 13f, muted), 0)
            r.addView(content, LinearLayout.LayoutParams(0, -2, 1f)); add(c, r, 0)
            if (i < 4) { val line = View(this).apply { setBackgroundColor(0xFF2A3040.toInt()) }; add(c, line, 0); line.layoutParams.height = dp(1) }
        }
        val daily = card(root)
        add(daily, text("TODAY'S CHECK-IN", 11f, lime, true), 7)
        add(daily, text("Take a minute to examine something that matters.", 16f, white, true), 12)
        add(daily, button("Start daily reflection  ↗", true) { startDaily() }, 0)
        val week = card(root); add(week, text("YOUR WEEK", 16f, white, true), 8)
        val att = Store.attendance(this)
        val cal = row()
        val calText = text("M     T     W     T     F     S     S", 14f, lime, true)
        add(week, calText, 6)
        add(week, text("Attended days are saved when you save a check-in or reflection.", 12f, muted), 0)
    }
    private fun startDaily() {
        editingId = "daily"; answers = MutableList(5) { Answer() }; showEditor("Daily check-in", "Personal growth", "What deserves your attention today?")
    }
    private fun newReflection() { editingId = null; answers = MutableList(5) { Answer() }; showEditor("New reflection", "Personal growth", "") }
    private fun showEditor(pageTitle: String, area: String, problem: String) {
        currentPage = "editor"; base(pageTitle, "Work through the five lenses. Save when you're ready."); nav()
        val title = EditText(this).apply { setTextColor(white); textSize = 21f; hint = "What are you thinking through?"; setHintTextColor(muted); setSingleLine(); setText(editingTitle()) ; background = shape(panel2, 13); setPadding(dp(12), dp(12), dp(12), dp(12)) }
        titleInput = title; add(root, title, 12)
        val spinner = Spinner(this); val areas = listOf("Personal growth", "Study", "Health", "Money", "Work / business", "Relationships", "Technology", "Decision", "Other")
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, areas)
        spinner.setSelection(areas.indexOf(area).coerceAtLeast(0)); areaInput = spinner
        add(root, text("AREA OF LIFE", 11f, muted, true), 5); add(root, spinner, 12)
        val p = EditText(this).apply { setTextColor(white); setHintTextColor(muted); textSize = 14f; hint = "Describe the situation…"; setText(problem); minLines = 3; gravity = Gravity.TOP; background = shape(panel2, 13); setPadding(dp(12), dp(12), dp(12), dp(12)) }
        problemInput = p; add(root, p, 15)
        LensData.titles.indices.forEach { i ->
            val c = card(root); val r = row()
            add(r, text("${i+1}. ${LensData.titles[i]}", 17f, LensData.colors[i], true), 0)
            r.addView(Space(this), LinearLayout.LayoutParams(0, 1, 1f))
            val check = CheckBox(this).apply { buttonTintList = android.content.res.ColorStateList.valueOf(lime); isChecked = answers[i].done; setOnCheckedChangeListener { _, checked -> if (checked && answerInputs.getOrNull(i)?.text.isNullOrBlank()) { isChecked = false; Toast.makeText(this@MainActivity, "Write an answer first.", Toast.LENGTH_SHORT).show() } else answers[i].done = checked } }
            add(r, check, 0); add(c, r, 8)
            add(c, text(LensData.questions[i], 15f, white, true), 5)
            add(c, text(LensData.descriptions[i], 12f, muted), 8)
            val e = EditText(this).apply { setTextColor(white); setHintTextColor(muted); textSize = 13f; hint = "Write your thinking here…"; minLines = 3; gravity = Gravity.TOP; background = shape(0xFF090D17.toInt(), 12); setPadding(dp(11), dp(11), dp(11), dp(11)); setText(answers[i].text); addTextChangedListener(SimpleWatcher { answers[i].text = it }) }
            answerInputs.addOrReplace(i, e); add(c, e, 0)
        }
        add(root, button("Save reflection", true) { saveReflection() }, 9)
        add(root, button("Export as image", false) { exportImage() }, 9)
        if (editingId != null && editingId != "daily") add(root, button("Delete reflection", false) { deleteCurrent() }, 0)
    }
    private val answerInputs = mutableListOf<EditText>()
    private fun MutableList<EditText>.addOrReplace(i: Int, e: EditText) { while (size <= i) add(EditText(this@MainActivity)); this[i] = e }
    private fun editingTitle(): String { val id = editingId; if (id == null || id == "daily") return ""; return Store.notes(this).find { it.id == id }?.title ?: "" }
    private fun saveReflection() {
        val title = titleInput?.text?.toString()?.trim().orEmpty().ifBlank { "Untitled reflection" }
        val now = System.currentTimeMillis(); val notes = Store.notes(this); val id = editingId
        if (id == "daily") {
            val prefs = getSharedPreferences("five_lenses_store", MODE_PRIVATE)
            prefs.edit().putString("daily_${Store.dayKey(now)}", answersToString()).apply()
        } else {
            val old = notes.find { it.id == id }
            val n = Reflection(id ?: UUID.randomUUID().toString(), title, areaInput?.selectedItem?.toString() ?: "Other",
                problemInput?.text?.toString().orEmpty(), old?.created ?: now, now, answers)
            if (old == null) notes.add(0, n) else { notes.remove(old); notes.add(0, n) }
            Store.saveNotes(this, notes)
        }
        Store.markToday(this); Toast.makeText(this, "Saved to this device", Toast.LENGTH_SHORT).show(); showHome()
    }
    private fun answersToString() = answers.joinToString("\n---\n") { "${if (it.done) "✓" else "○"} ${it.text}" }
    private fun deleteCurrent() { val id = editingId ?: return; Store.saveNotes(this, Store.notes(this).filterNot { it.id == id }); showNotes() }
    private fun showNotes() {
        currentPage = "notes"; base("My reflections", "Your situations, examined from five angles."); nav()
        Store.notes(this).forEach { n ->
            val c = card(root); add(c, text(n.title, 17f, white, true), 5); add(c, text("${n.area}  ·  ${n.progress()}/5 completed", 12f, muted), 8)
            add(c, text(n.problem.ifBlank { "No situation description." }, 13f, muted), 10)
            add(c, button(if (n.solved()) "✦ Solved · Review" else "Open reflection  ↗", n.solved()) { openReflection(n) }, 0)
        }
        if (Store.notes(this).isEmpty()) add(root, text("No reflections yet. Tap ＋ to create your first one.", 14f, muted), 0)
    }
    private fun openReflection(n: Reflection) { editingId = n.id; answers = n.answers.map { it.copy() }.toMutableList(); showEditor(n.title, n.area, n.problem) }
    private fun showSolved() {
        currentPage = "solved"; base("Solved problems", "A problem counts when all five answers are written and checked."); nav()
        val ns = Store.notes(this); val solved = ns.filter { it.solved() }
        val c = card(root); add(c, text("${solved.size} solved", 28f, lime, true), 5); add(c, text("${if(ns.isEmpty()) 0 else solved.size * 100 / ns.size}% completion rate across ${ns.size} reflections", 13f, muted), 0)
        solved.forEach { n -> val sc = card(root); add(sc, text("✦  ${n.title}", 16f, white, true), 5); add(sc, text("${n.area} · ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(n.updated))}", 12f, muted), 9); add(sc, button("Review", false) { openReflection(n) }, 0) }
        if (solved.isEmpty()) add(root, text("No solved problems yet. Complete and check all five questions in a reflection.", 14f, muted), 0)
    }
    private fun showSettings() {
        currentPage = "settings"; base("Settings", "Shape your practice and keep your data safe."); nav()
        val c = card(root)
        add(c, text("DAILY REMINDER", 12f, lime, true), 10)
        val time = EditText(this).apply { setTextColor(white); setHintTextColor(muted); hint = "20:00"; inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_TIME; setText(Store.setting(this@MainActivity, "reminder_time", "20:00")); background = shape(panel2, 12); setPadding(dp(12), dp(12), dp(12), dp(12)) }
        add(c, time, 8)
        add(c, text("Reminders use Android notifications. Android may ask you to grant notification permission.", 12f, muted), 10)
        add(c, button("Save reminder time", true) { Store.setSetting(this, "reminder_time", time.text.toString().ifBlank { "20:00" }); ReminderScheduler.schedule(this); Toast.makeText(this, "Reminder scheduled where Android permits.", Toast.LENGTH_SHORT).show() }, 0)
        val b = card(root); add(b, text("DATA BACKUP", 12f, lime, true), 8)
        add(b, text("Reflections are stored locally on this phone. Export a backup before changing devices.", 13f, muted), 10)
        add(b, button("Export backup", false) { exportBackup() }, 0)
    }
    private fun exportBackup() {
        val data = org.json.JSONArray().apply { Store.notes(this@MainActivity).forEach { put(it.toJson()) } }.toString(2)
        val file = File(getExternalFilesDir(null), "five-lenses-backup.json"); file.writeText(data)
        Toast.makeText(this, "Backup saved: ${file.name}", Toast.LENGTH_LONG).show()
    }
    private fun exportImage() {
        val bitmap = Bitmap.createBitmap(1080, 1500, Bitmap.Config.ARGB_8888); val canvas = Canvas(bitmap); canvas.drawColor(bg)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG); paint.color = lime; paint.textSize = 30f; paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        canvas.drawText("✳ FIVE LENSES", 60f, 75f, paint)
        paint.color = white; paint.textSize = 42f; canvas.drawText(titleInput?.text?.toString()?.take(28) ?: "Reflection", 60f, 145f, paint)
        paint.textSize = 23f; var y = 220f
        LensData.titles.indices.forEach { i ->
            paint.color = LensData.colors[i]; paint.typeface = Typeface.DEFAULT_BOLD; canvas.drawText("${i+1}. ${LensData.titles[i]}", 60f, y, paint); y += 35f
            paint.color = white; paint.typeface = Typeface.DEFAULT; val txt = answers[i].text.take(110)
            canvas.drawText(if(txt.isBlank()) "Not answered yet" else txt, 60f, y, paint); y += 75f
        }
        val file = File(getExternalFilesDir(null), "five-lenses-${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        Toast.makeText(this, "Image saved in app files: ${file.name}", Toast.LENGTH_LONG).show()
    }
}
