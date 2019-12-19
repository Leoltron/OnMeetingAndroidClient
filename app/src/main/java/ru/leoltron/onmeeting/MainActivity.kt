package ru.leoltron.onmeeting

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.leoltron.onmeeting.api.IOnMeetingApi
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.CardViewModel
import java.sql.Timestamp
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var onMeetingApi: IOnMeetingApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var loadingFL: FrameLayout
    private val cards = ArrayList<CardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        loadingFL = findViewById(R.id.cardListLoadingFrame)

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

        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager

        updateCards()
    }

    private fun updateCards() {
        loadingFL.visibility = View.VISIBLE
        onMeetingApi.participating.enqueue(object : Callback<List<CardViewModel>> {
            override fun onResponse(call: Call<List<CardViewModel>>, response: Response<List<CardViewModel>>) {
                if (response.isSuccessful && response.body() != null) {
                    cards.clear()
                    cards.addAll(response.body()!!)
                    viewAdapter.notifyDataSetChanged()
                }
                loadingFL.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<CardViewModel>>, t: Throwable) {
                loadingFL.visibility = View.GONE
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
