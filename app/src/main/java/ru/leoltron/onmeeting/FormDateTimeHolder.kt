package ru.leoltron.onmeeting

import android.widget.Button
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class FormDateTimeHolder(private val button: Button) {
    private val formatter: DateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")
    var dateTime: LocalDateTime? = null
        set(value) {
            field = value
            button.text = if (value != null) formatter.print(value) else ""
        }

    fun setOnClickListener(listener: (holder: FormDateTimeHolder) -> Any) {
        button.setOnClickListener { listener(this) }
    }
}