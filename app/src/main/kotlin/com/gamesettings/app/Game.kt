package com.gamesettings.app

import com.google.firebase.firestore.PropertyName

/**
 * مدل داده هر بازی که از Firestore خونده می‌شه.
 * اسم فیلدها باید دقیقاً با اسم فیلدهای Firestore یکی باشه.
 */
data class Game(
    var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl")
    var imageUrl: String = "",
    @get:PropertyName("settings") @set:PropertyName("settings")
    var settings: String = "",
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Long = 0L
)
