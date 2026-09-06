package com.gamesettings.app

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

/**
 * صفحه‌ی سوالات متداول — پاسخ به پرتکرارترین سوالات کاربران درباره‌ی
 * تنظیمات سبز/زرد، لینک یوتیوب، تغییر تم و فونت.
 */
class FaqActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppPreferences.applyLanguage(AppPreferences.getLanguage(this))
        AppPreferences.applyTheme(AppPreferences.getTheme(this))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        val toolbar = findViewById<Toolbar>(R.id.faq_toolbar)
        setSupportActionBar(toolbar)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.text_primary))
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val container = findViewById<LinearLayout>(R.id.faq_container)
        val inflater = LayoutInflater.from(this)

        val faqItems = listOf(
            R.string.faq_q1 to R.string.faq_a1,
            R.string.faq_q2 to R.string.faq_a2,
            R.string.faq_q3 to R.string.faq_a3,
            R.string.faq_q4 to R.string.faq_a4,
            R.string.faq_q5 to R.string.faq_a5
        )

        faqItems.forEach { (questionRes, answerRes) ->
            val itemView = inflater.inflate(R.layout.item_faq, container, false)
            itemView.findViewById<TextView>(R.id.faq_question).text = getString(questionRes)
            itemView.findViewById<TextView>(R.id.faq_answer).text = getString(answerRes)
            GlassStyler.applyCard(this, itemView)
            container.addView(itemView)
        }

        val rootView = findViewById<android.view.View>(android.R.id.content)
        FontManager.applyToViewTree(this, rootView)
        rootView.alpha = 0f
        rootView.animate().alpha(1f).setDuration(240).start()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
