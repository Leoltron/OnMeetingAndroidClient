package ru.leoltron.onmeeting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.CardViewModel
import java.sql.Timestamp


fun CardViewModel.toJson(): String = OnMeetingApiService.getInstance().gson.toJson(this)

fun CardViewModel.toEditIntent(ctx: Context): Intent = Intent(ctx, AddCardActivity::class.java).putExtra("card", this.toJson())

fun CardViewModel.startEditActivity(ctx: Activity, reqCode: Int) = ctx.startActivityForResult(this.toEditIntent(ctx), reqCode)

fun String.toCardViewModel(): CardViewModel = OnMeetingApiService.getInstance().gson.fromJson(this, CardViewModel::class.java)

fun TextView.hideIfBlank() {
    this.visibility = if (this.text.isNullOrBlank()) View.GONE else View.VISIBLE
}

fun TextView.setTextAndHideIfBlank(s: String?) {
    this.text = s
    this.hideIfBlank()
}

fun LocalDateTime.toTimestamp(): Timestamp = Timestamp(removeOffset(this.toDateTime().millis))
fun Timestamp.toLocalDateTime(): LocalDateTime = DateTime(this.time).withZoneRetainFields(DateTimeZone.UTC).withZone(DateTimeZone.getDefault()).toLocalDateTime()


fun removeOffset(time: Long): Long = time - DateTimeZone.getDefault().getOffset(time).toLong()