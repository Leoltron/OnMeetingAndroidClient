package ru.leoltron.onmeeting.api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class SessionCookieJar(private val cookieName: String) : CookieJar {
    private var sessionId: Cookie? = null

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        sessionId = cookies.firstOrNull { cookie -> cookie.name() == cookieName } ?: sessionId;
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
            if (sessionId != null) listOf(sessionId!!) else listOf()
}
