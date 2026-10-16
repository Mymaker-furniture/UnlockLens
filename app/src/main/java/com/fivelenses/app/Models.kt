package com.fivelenses.app

import org.json.JSONArray
import org.json.JSONObject

data class Answer(var text: String = "", var done: Boolean = false)
data class Reflection(
    var id: String, var title: String, var area: String, var problem: String,
    var created: Long, var updated: Long, var answers: MutableList<Answer>
) {
    fun progress() = answers.count { it.done && it.text.isNotBlank() }
    fun solved() = answers.size == 5 && answers.all { it.done && it.text.isNotBlank() }
    fun toJson() = JSONObject().apply {
        put("id", id); put("title", title); put("area", area); put("problem", problem)
        put("created", created); put("updated", updated)
        put("answers", JSONArray().apply { answers.forEach { put(JSONObject().put("text", it.text).put("done", it.done)) } })
    }
    companion object {
        fun fromJson(o: JSONObject): Reflection {
            val a = mutableListOf<Answer>(); val arr = o.optJSONArray("answers") ?: JSONArray()
            for (i in 0 until arr.length()) { val x = arr.optJSONObject(i) ?: JSONObject(); a.add(Answer(x.optString("text"), x.optBoolean("done"))) }
            while (a.size < 5) a.add(Answer())
            return Reflection(o.optString("id"), o.optString("title"), o.optString("area"), o.optString("problem"),
                o.optLong("created"), o.optLong("updated"), a.take(5).toMutableList())
        }
    }
}

object LensData {
    val titles = listOf(
        "Reality", "Causes", "Blind Spots", "Consequences", "Leverage"
    )
    val questions = listOf(
        "What do I actually know?",
        "What is producing this outcome?",
        "What could I be missing?",
        "Where does this lead over time?",
        "What matters most, and what should I test?"
    )
    val descriptions = listOf(
        "Separates evidence from assumption.",
        "Reveals mechanisms and root causes.",
        "Reveals alternative explanations and overlooked information.",
        "Reveals feedback loops, trade-offs, and compounding effects.",
        "Turns insight into an effective action."
    )
    val symbols = listOf("◉", "⌘", "◎", "⌁", "⊙")
    val colors = listOf(0xFFC9F36A.toInt(), 0xFFA99BFF.toInt(), 0xFF75E1DF.toInt(), 0xFFFF9FCB.toInt(), 0xFFFFCE7B.toInt())
}
