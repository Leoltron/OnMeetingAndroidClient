package ru.leoltron.onmeeting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.leoltron.onmeeting.api.ExternalList
import ru.leoltron.onmeeting.api.IOnMeetingApi
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.CardViewModel
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val EDIT_TAGS_DIALOG_CODE: Int = 12
        const val ADD_CARD_REQ_CODE: Int = 1
        const val EDIT_CARD_REQ_CODE: Int = 2
    }


    private lateinit var onMeetingApi: IOnMeetingApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var loadingFL: FrameLayout
    private val cards = ArrayList<CardViewModel>()

    private lateinit var externalList: ExternalList<CardViewModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        loadingFL = findViewById(R.id.cardListLoadingFrame)

        onMeetingApi = OnMeetingApiService.getInstance().api

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            startActivityForResult(Intent(this, AddCardActivity::class.java), ADD_CARD_REQ_CODE)
        }

        initCardList()
    }

    private fun initCardList() {
        recyclerView = findViewById(R.id.cardList)
        viewAdapter = CardAdapter(cards, this)
        viewManager = LinearLayoutManager(this)

        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager

        externalList = OnMeetingApiService.getInstance().cards

        updateCards()
    }


    private fun updateCards() {
        loadingFL.visibility = View.VISIBLE

        externalList.refresh { c ->
            cards.clear()
            cards.addAll(c.sortedBy { e -> e.startDate })
            viewAdapter.notifyDataSetChanged()
            loadingFL.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_update_cards) {
            updateCards()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK)
            updateCards()
    }

    fun onLongClick(cardId: Int): Boolean {
        AlertDialog.Builder(this)
                .setTitle("Delete card")
                .setMessage("Do you want to delete this card?")
                .setIcon(R.drawable.outline_warning_24)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    loadingFL.visibility = View.VISIBLE
                    onMeetingApi.deleteCard(cardId).enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            loadingFL.visibility = View.GONE
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                val card = OnMeetingApiService.getInstance().cards.find { c -> c.cardId == cardId }
                                if (card != null) {
                                    OnMeetingApiService.getInstance().cards.remove(card)
                                    cards.remove(card)
                                    viewAdapter.notifyDataSetChanged()
                                }

                            }
                            loadingFL.visibility = View.GONE
                        }

                    })
                }
                .setNegativeButton(android.R.string.no, null).show()
        return true
    }
}
