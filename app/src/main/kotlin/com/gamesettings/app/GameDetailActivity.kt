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

        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val image = findViewById<ImageView>(R.id.detail_image)
        val greenSection = findViewById<LinearLayout>(R.id.green_section)
        val yellowSection = findViewById<LinearLayout>(R.id.yellow_section)
        val greenText = findViewById<TextView>(R.id.detail_settings_green)
        val yellowText = findViewById<TextView>(R.id.detail_settings_yellow)

        val name = intent.getStringExtra("name").orEmpty()
        val imageUrl = intent.getStringExtra("imageUrl").orEmpty()
        val settingsGreen = intent.getStringExtra("settingsGreen").orEmpty()
        val settingsYellow = intent.getStringExtra("settingsYellow").orEmpty()

        title = name
        toolbar.title = name

        image.load(imageUrl) {
            crossfade(true)
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

        // اگر هیچ‌کدام موجود نبود، پیام مناسب نشان بده
        if (settingsGreen.isBlank() && settingsYellow.isBlank()) {
            greenSection.visibility = View.VISIBLE
            greenText.text = "برای این بازی هنوز تنظیماتی ثبت نشده."
        }
    }
}
