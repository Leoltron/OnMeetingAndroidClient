package ru.leoltron.onmeeting

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.leoltron.onmeeting.api.model.TagViewModel

class TagAdapter(private val tags: List<TagViewModel>, private val listener: IClickOnIdListener) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.tag_list_item, parent, false)
        return TagViewHolder(v)
    }

    override fun getItemCount(): Int = tags.size

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        holder.bgFL.background.setColor(Color.parseColor("#${tag.color}"))
        holder.textView.text = tag.name

        holder.bgFL.setOnClickListener { listener.onClicked(tag.id) }
    }

    class TagViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        var bgFL: FrameLayout = rootView.findViewById(R.id.tag_item_bg)
        var textView: TextView = rootView.findViewById(R.id.tag_item_name)
    }
}