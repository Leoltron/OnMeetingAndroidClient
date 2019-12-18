package ru.leoltron.onmeeting.api.model
import java.sql.Timestamp

data class CardViewModel(
        val title: String,
        val username: String,
        val cardId: Int,
        val locationString: String?,
        val startDate: Timestamp?,
        val endDate: Timestamp?,
        val participants: List<UserModel>,
        val tags: List<TagViewModel>)
