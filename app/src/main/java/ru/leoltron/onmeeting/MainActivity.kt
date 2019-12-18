package ru.leoltron.onmeeting

import android.os.Bundle

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.leoltron.onmeeting.api.IOnMeetingApi
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.CardViewModel
import ru.leoltron.onmeeting.api.model.TagViewModel
import ru.leoltron.onmeeting.api.model.UserModel

import android.view.View
import android.view.Menu
import android.view.MenuItem

import java.sql.Timestamp
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private var onMeetingApi: IOnMeetingApi? = null
    private var recyclerView: RecyclerView? = null
    private var viewAdapter: RecyclerView.Adapter<*>? = null
    private var viewManager: RecyclerView.LayoutManager? = null
    private val cards = ArrayList<CardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        onMeetingApi = OnMeetingApiService.getInstance().api

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        initCardList()
    }

    private fun initCardList() {
        recyclerView = findViewById(R.id.cardList)
        cards.add(CardViewModel("Title",
                "Name",
                0,
                "location",
                Timestamp(System.currentTimeMillis()), null,
                ArrayList(),
                ArrayList()))
        viewAdapter = CardAdapter(cards)
        viewManager = LinearLayoutManager(this)

        recyclerView!!.adapter = viewAdapter
        recyclerView!!.layoutManager = viewManager

        updateCards()
    }

    private fun updateCards() {
        onMeetingApi!!.participating.enqueue(object : Callback<List<CardViewModel>> {
            override fun onResponse(call: Call<List<CardViewModel>>, response: Response<List<CardViewModel>>) {
                if (response.isSuccessful && response.body() != null) {
                    cards.clear()
                    cards.addAll(response.body()!!)
                    viewAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<CardViewModel>>, t: Throwable) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_update_cards) {
            updateCards()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
