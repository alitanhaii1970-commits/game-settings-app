package com.gamesettings.app

import android.os.Bundle
import android.widget.ImageButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppPreferences.applyLanguage(AppPreferences.getLanguage(this))
        AppPreferences.applyTheme(AppPreferences.getTheme(this))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton: ImageButton = findViewById(R.id.back_button)
        val languageGroup: RadioGroup = findViewById(R.id.language_group)
        val themeGroup: RadioGroup = findViewById(R.id.theme_group)

        backButton.setOnClickListener { finish() }

        // مقداردهی اولیه بر اساس تنظیمات فعلی کاربر
        when (AppPreferences.getLanguage(this)) {
            AppPreferences.LANG_EN -> languageGroup.check(R.id.lang_en)
            else -> languageGroup.check(R.id.lang_fa)
        }
        when (AppPreferences.getTheme(this)) {
            AppPreferences.THEME_LIGHT -> themeGroup.check(R.id.theme_light)
            AppPreferences.THEME_SYSTEM -> themeGroup.check(R.id.theme_system)
            else -> themeGroup.check(R.id.theme_dark)
        }

        languageGroup.setOnCheckedChangeListener { _, checkedId ->
            val lang = if (checkedId == R.id.lang_en) AppPreferences.LANG_EN else AppPreferences.LANG_FA
            if (lang != AppPreferences.getLanguage(this)) {
                AppPreferences.setLanguage(this, lang)
                recreate()
            }
        }

        themeGroup.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                R.id.theme_light -> AppPreferences.THEME_LIGHT
                R.id.theme_system -> AppPreferences.THEME_SYSTEM
                else -> AppPreferences.THEME_DARK
            }
            AppPreferences.setTheme(this, theme)
        }
    }
}
