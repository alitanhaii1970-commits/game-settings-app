package com.gamesettings.app

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source

/**
 * تمام ارتباط با Firestore از اینجا انجام میشه.
 *
 * ⚠️ نکته‌ی مهم درباره‌ی مصرف Firestore:
 * همه‌ی بازی‌ها به‌جای این‌که هرکدوم یک «سند» (document) جدا باشن،
 * همگی داخل یک سند واحد (data/games) و به‌شکل یک آرایه ذخیره می‌شن.
 *
 * دلیل: Firestore به‌ازای هر سندی که در جواب یک query برمی‌گردونه،
 * یک بار «خوندن» (read) حساب می‌کنه. اگه هر بازی سند جدا بود، با N بازی
 * هر بار باز کردن اپ = N بار خوندن حساب می‌شد (خیلی زود سقف رایگان روزانه
 * تموم می‌شد). با این ساختار، خوندن کل لیست فقط ۱ بار حساب می‌شه —
 * صرف‌نظر از این‌که چند صد بازی داخلش باشه. کیفیت و محتوا کاملاً همونه،
 * فقط نحوه‌ی ذخیره‌سازی تغییر کرده.
 */
class GameRepository {

    private val db = FirebaseFirestore.getInstance()
    private val gamesDoc = db.collection("data").document("games")

    @Suppress("UNCHECKED_CAST")
    fun fetchGames(
        forceServer: Boolean,
        onSuccess: (List<Game>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val source = if (forceServer) Source.SERVER else Source.DEFAULT

        gamesDoc
            .get(source)
            .addOnSuccessListener { snapshot ->
                val rawList = snapshot.get("list") as? List<Map<String, Any?>> ?: emptyList()
                val games = rawList.mapNotNull { map -> parseGameMap(map) }
                    .sortedBy { it.name.lowercase() }
                onSuccess(games)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    private fun parseGameMap(map: Map<String, Any?>): Game? {
        val name = map["name"] as? String ?: return null
        return Game(
            id = map["id"] as? String ?: name,
            name = name,
            imageUrl = map["imageUrl"] as? String ?: "",
            settingsGreen = map["settingsGreen"] as? String ?: "",
            settingsYellow = map["settingsYellow"] as? String ?: "",
            youtubeUrl = map["youtubeUrl"] as? String ?: "",
            showYoutubeButton = map["showYoutubeButton"] as? Boolean ?: false,
            updatedAt = (map["updatedAt"] as? Number)?.toLong() ?: 0L
        )
    }
}
