package ru.leoltron.onmeeting

import android.graphics.Color
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.google.android.flexbox.FlexboxLayout
import androidx.recyclerview.widget.RecyclerView
import ru.leoltron.onmeeting.api.model.CardViewModel
import ru.leoltron.onmeeting.api.model.TagViewModel

class CardAdapter(private val cards: List<CardViewModel>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_list_item, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) =
            holder.bind(cards[position])

    override fun getItemCount(): Int = cards.size

    class CardViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        var cardNameView: TextView = rootView.findViewById(R.id.cardName)
        var creatorNameView: TextView = rootView.findViewById(R.id.creatorName)
        var locationView: TextView = rootView.findViewById(R.id.location)
        var startDateView: TextView = rootView.findViewById(R.id.startDate)
        var tagList: FlexboxLayout = rootView.findViewById(R.id.tagList)
        var participants: TextView = rootView.findViewById(R.id.participants)

        fun bind(cardViewModel: CardViewModel) {
            cardNameView.text = cardViewModel.title
            creatorNameView.text = cardViewModel.username
            locationView.text = cardViewModel.locationString
            startDateView.text = cardViewModel.startDate?.toLocaleString()
            participants.text = cardViewModel.participants.joinToString(", ") { it.name }

            tagList.removeAllViews()

            cardViewModel.tags.map { tag -> tagToTextView(tag) }.forEach { tv -> tagList.addView(tv) }
        }

        private fun tagToTextView(tag: TagViewModel): TextView {
            val tv = TextView(ContextThemeWrapper(tagList.context, R.style.tag))
            tv.setTextColor(Color.parseColor("#${tag.color}"))
            tv.text = tag.name
            return tv
        }
    }
}

