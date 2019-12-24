package ru.leoltron.onmeeting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_tag.*
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.TagViewModel

class AddTagActivity : AppCompatActivity(), IClickOnIdListener {
    private lateinit var tagListLoadingFrame: FrameLayout
    private lateinit var tagListRecyclerView: RecyclerView

    private lateinit var apiService: OnMeetingApiService
    private lateinit var tagIdsToIgnore: Set<Int>
    private val tagList: MutableList<TagViewModel> = ArrayList()
    private lateinit var tagAdapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tag)
        setSupportActionBar(toolbar)

        setResult(Activity.RESULT_CANCELED)

        apiService = OnMeetingApiService.getInstance()

        tagListRecyclerView = findViewById(R.id.add_tag_rv)
        tagListRecyclerView.layoutManager = LinearLayoutManager(this)
        tagAdapter = TagAdapter(tagList, this)
        tagListRecyclerView.adapter = tagAdapter

        tagListLoadingFrame = findViewById(R.id.tagListLoadingFrame)

        fab.setOnClickListener {
            startActivityForResult(Intent(this, AddNewTagActivity::class.java), 0)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tagIdsToIgnore = intent.getIntArrayExtra("selectedIds")?.toHashSet() ?: emptySet()
        if (apiService.tags.isEmpty()) {
            updateTags()
        } else {
            updateTags(apiService.tags)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            updateTags()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_tags, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_update_tags) {
            updateTags()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateTags() {
        tagListLoadingFrame.visibility = View.VISIBLE
        apiService.tags.refresh { t ->
            updateTags(t)
            tagListLoadingFrame.visibility = View.GONE
        }
    }

    private fun updateTags(source: List<TagViewModel>) {
        tagList.clear()
        tagList.addAll(source.filter { t -> !tagIdsToIgnore.contains(t.id) }.sortedBy { t -> t.name })
        tagAdapter.notifyDataSetChanged()
    }

    override fun onClicked(id: Int) {
        setResult(Activity.RESULT_OK, Intent().putExtra("selectedId", id))
        finish()
    }

}
