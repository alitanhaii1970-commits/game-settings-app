package com.gamesettings.app

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import coil.load

class GameDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppPreferences.applyLanguage(AppPreferences.getLanguage(this))
        AppPreferences.applyTheme(AppPreferences.getTheme(this))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)

        val rootView = findViewById<View>(android.R.id.content)
        rootView.alpha = 0f
        rootView.animate().alpha(1f).setDuration(280).start()

        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val image = findViewById<ImageView>(R.id.detail_image)
        val greenSection = findViewById<LinearLayout>(R.id.green_section)
        val yellowSection = findViewById<LinearLayout>(R.id.yellow_section)
        val frameGenSection = findViewById<LinearLayout>(R.id.framegen_section)
        val greenText = findViewById<TextView>(R.id.detail_settings_green)
        val yellowText = findViewById<TextView>(R.id.detail_settings_yellow)
        val frameGenText = findViewById<TextView>(R.id.detail_settings_framegen)

        // ظاهر شیشه‌ای روی کادرهای تنظیمات (در صورت فعال بودن)
        GlassStyler.applyCard(this, greenText)
        GlassStyler.applyCard(this, yellowText)
        GlassStyler.applyCard(this, frameGenText)

        val name = intent.getStringExtra("name").orEmpty()
        val imageUrl = intent.getStringExtra("imageUrl").orEmpty()
        val settingsGreen = intent.getStringExtra("settingsGreen").orEmpty()
        val settingsYellow = intent.getStringExtra("settingsYellow").orEmpty()
        val settingsFrameGen = intent.getStringExtra("settingsFrameGen").orEmpty()

        title = name
        toolbar.title = name

        image.load(imageUrl) {
            crossfade(400)
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }

        // هر بخش فقط اگر محتوا دارد نمایش داده می‌شود
        if (settingsGreen.isNotBlank()) {
            greenSection.visibility = View.VISIBLE
            greenText.text = settingsGreen
        } else {
            greenSection.visibility = View.GONE
        }

        if (settingsYellow.isNotBlank()) {
            yellowSection.visibility = View.VISIBLE
            yellowText.text = settingsYellow
        } else {
            yellowSection.visibility = View.GONE
        }

        if (settingsFrameGen.isNotBlank()) {
            frameGenSection.visibility = View.VISIBLE
            frameGenText.text = settingsFrameGen
        } else {
            frameGenSection.visibility = View.GONE
        }

        // اگر هیچ‌کدام موجود نبود، پیام مناسب نشان بده
        if (settingsGreen.isBlank() && settingsYellow.isBlank() && settingsFrameGen.isBlank()) {
            greenSection.visibility = View.VISIBLE
            greenText.text = "برای این بازی هنوز تنظیماتی ثبت نشده."
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
