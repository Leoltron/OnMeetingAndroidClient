package ru.leoltron.onmeeting.api

import android.os.Handler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ExternalList<T>(private val update: () -> Call<List<T>>) : ArrayList<T>(), Callback<List<T>> {
    private val callbacks: ArrayList<(List<T>) -> Unit> = ArrayList()
    private var updating: Boolean = false

    @Synchronized
    fun refresh(callback: ((List<T>) -> Unit)? = null) {
        if (callback != null)
            callbacks.add(callback)
        if (!updating) {
            updating = true
            update().enqueue(this)
        }
    }

    override fun onFailure(call: Call<List<T>>, t: Throwable) {
        waitAndRefresh()
    }

    override fun onResponse(call: Call<List<T>>, response: Response<List<T>>) {
        if (response.isSuccessful) {
            this.clear()
            val elements = response.body()
            if (elements != null)
                this.addAll(elements)
            callbacks.forEach { it(this) }
            callbacks.clear()
            updating = false
        } else {
            waitAndRefresh()
        }
    }

    private fun waitAndRefresh() {
        Handler().postDelayed({ update().enqueue(this) }, 3 * 1000)
    }
}