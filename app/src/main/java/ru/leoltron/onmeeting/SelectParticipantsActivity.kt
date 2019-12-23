package ru.leoltron.onmeeting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.UserModel


class SelectParticipantsActivity : AppCompatActivity(), TextWatcher {
    private lateinit var participantsFilterET: EditText
    private lateinit var apiService: OnMeetingApiService
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var usersList: RecyclerView
    private lateinit var saveButton: Button
    private lateinit var participantsWaitingFL: FrameLayout

    private val users: MutableList<UserModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_participants)

        apiService = OnMeetingApiService.getInstance()

        participantsFilterET = findViewById(R.id.participantsFilterET)
        participantsFilterET.addTextChangedListener(this)

        saveButton = findViewById(R.id.select_participants_save)
        saveButton.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().putExtra(AddCardActivity.idsSelectedKey, usersAdapter.selectedIds.toIntArray()))
            finish()
        }

        usersList = findViewById(R.id.participantsSelectRecyclerView)
        usersList.layoutManager = LinearLayoutManager(this)
        usersAdapter = UsersAdapter(users)
        usersList.adapter = usersAdapter

        val selectedIds = intent.getIntArrayExtra(AddCardActivity.idsSelectedKey)
        if (selectedIds != null && selectedIds.isNotEmpty()) {
            usersAdapter.selectedIds.addAll(selectedIds.asIterable())
            usersAdapter.notifyDataSetChanged()
        }

        participantsWaitingFL = findViewById(R.id.participantsWaitingFrame)

        if (apiService.users.isNotEmpty()) {
            refreshList(apiService.users)
        } else {
            refreshList()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_participants, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_update_participants) {
            apiService.users.refresh { this.refreshList() }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshList() {
        participantsWaitingFL.visibility = View.VISIBLE
        saveButton.isEnabled = false
        apiService.users.refresh {
            this.refreshList(it)
            participantsWaitingFL.visibility = View.GONE
            saveButton.isEnabled = true
        }
    }

    private fun refreshList(sourceList: List<UserModel>) {
        val filterQuery = participantsFilterET.text.toString()
        users.clear()
        users.addAll(sourceList
                .filter { u -> filterQuery.isBlank() || u.name.contains(filterQuery, true) }
                .sortedBy { u -> u.name })
        usersAdapter.notifyDataSetChanged()
    }

    override fun afterTextChanged(editable: Editable?) {
        refreshList(apiService.users)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }
}
