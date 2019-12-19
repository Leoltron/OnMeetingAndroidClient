package ru.leoltron.onmeeting

import android.view.View
import android.widget.TextView

fun TextView.hideIfBlank() {
    this.visibility = if (this.text.isNullOrBlank()) View.GONE else View.VISIBLE
}

fun TextView.setTextAndHideIfBlank(s: String?) {
    this.text = s
    this.hideIfBlank()
}