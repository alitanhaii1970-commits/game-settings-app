package com.gamesettings.app

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppPreferences.applyLanguage(AppPreferences.getLanguage(this))
        AppPreferences.applyTheme(AppPreferences.getTheme(this))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val rootView = findViewById<View>(android.R.id.content)
        val backButton: ImageButton = findViewById(R.id.back_button)
        val languageGroup: RadioGroup = findViewById(R.id.language_group)
        val themeGroup: RadioGroup = findViewById(R.id.theme_group)
        val glassSwitch: SwitchCompat = findViewById(R.id.glass_switch)
        val languageCard: LinearLayout = findViewById(R.id.language_card)
        val themeCard: LinearLayout = findViewById(R.id.theme_card)
        val glassCard: LinearLayout = findViewById(R.id.glass_card)

        // ظاهر شیشه‌ای روی کارت‌های همین صفحه (در صورت فعال بودن)
        GlassStyler.applyCard(this, languageCard)
        GlassStyler.applyCard(this, themeCard)
        GlassStyler.applyCard(this, glassCard)

        rootView.alpha = 0f
        rootView.animate().alpha(1f).setDuration(260).start()

        backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

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
        glassSwitch.isChecked = AppPreferences.getGlassEffect(this)

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

        glassSwitch.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.setGlassEffect(this, isChecked)
            GlassStyler.applyCard(this, languageCard)
            GlassStyler.applyCard(this, themeCard)
            GlassStyler.applyCard(this, glassCard)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
