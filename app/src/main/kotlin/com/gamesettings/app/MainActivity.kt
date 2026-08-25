package com.gamesettings.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var repository: GameRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GameAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView
    private lateinit var searchBox: EditText
    private lateinit var refreshButton: ImageButton
    private lateinit var settingsButton: ImageButton

    private var allGames: List<Game> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        // اعمال زبان و تم ذخیره‌شده کاربر پیش از رسم صفحه
        AppPreferences.applyLanguage(AppPreferences.getLanguage(this))
        AppPreferences.applyTheme(AppPreferences.getTheme(this))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = GameRepository()

        recyclerView = findViewById(R.id.recycler_games)
        progressBar = findViewById(R.id.progress_bar)
        emptyText = findViewById(R.id.empty_text)
        searchBox = findViewById(R.id.search_box)
        refreshButton = findViewById(R.id.refresh_button)
        settingsButton = findViewById(R.id.settings_button)

        adapter = GameAdapter { game ->
            val intent = Intent(this, GameDetailActivity::class.java)
            intent.putExtra("name", game.name)
            intent.putExtra("imageUrl", game.imageUrl)
            intent.putExtra("settingsGreen", game.settingsGreen)
            intent.putExtra("settingsYellow", game.settingsYellow)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        refreshButton.setOnClickListener {
            loadGames(forceServer = true)
        }

        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        searchBox.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterGames(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        // اولین بار: اول از حافظه محلی (سریع)، بعد خودش سعی می‌کنه سینک کنه
        loadGames(forceServer = false)
    }

    private fun loadGames(forceServer: Boolean) {
        progressBar.visibility = View.VISIBLE
        emptyText.visibility = View.GONE

        repository.fetchGames(
            forceServer = forceServer,
            onSuccess = { games ->
                progressBar.visibility = View.GONE
                allGames = games
                filterGames(searchBox.text?.toString().orEmpty())
                if (forceServer) {
                    Toast.makeText(this, "لیست به‌روز شد ✅", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { e ->
                progressBar.visibility = View.GONE
                if (allGames.isEmpty()) {
                    emptyText.visibility = View.VISIBLE
                    emptyText.text = "اتصال به اینترنت برقرار نیست.\nلیست قبلی موجود نیست."
                } else {
                    Toast.makeText(this, "اتصال برقرار نشد، لیست قبلی نشون داده می‌شه", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun filterGames(query: String) {
        val filtered = if (query.isBlank()) {
            allGames
        } else {
            allGames.filter { it.name.contains(query, ignoreCase = true) }
        }
        adapter.submitList(filtered)
        emptyText.visibility = if (filtered.isEmpty() && allGames.isNotEmpty()) View.VISIBLE else View.GONE
        if (filtered.isEmpty() && allGames.isNotEmpty()) {
            emptyText.text = "بازی‌ای با این اسم پیدا نشد"
        }
    }
}
