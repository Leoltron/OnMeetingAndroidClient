package ru.leoltron.onmeeting

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import ru.leoltron.onmeeting.MainActivity.Companion.EDIT_CARD_REQ_CODE
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.CardViewModel
import ru.leoltron.onmeeting.api.model.TagViewModel


class CardAdapter(private val cards: List<CardViewModel>, private val activity: MainActivity) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_list_item, parent, false)
        return CardViewHolder(v, activity)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) =
            holder.bind(cards[position])

    override fun getItemCount(): Int = cards.size

    class CardViewHolder(private val rootView: View, private val activity: MainActivity) : RecyclerView.ViewHolder(rootView) {
        private var participantsLL: LinearLayout = rootView.findViewById(R.id.participantsLL)

        private var cardNameView: TextView = rootView.findViewById(R.id.cardName)
        private var creatorNameView: TextView = rootView.findViewById(R.id.creatorName)
        private var locationView: TextView = rootView.findViewById(R.id.location)
        private var startDateView: TextView = rootView.findViewById(R.id.startDate)
        private var tagList: FlexboxLayout = rootView.findViewById(R.id.tagList)
        private var participants: TextView = rootView.findViewById(R.id.participants)

        fun bind(cardViewModel: CardViewModel) {
            cardNameView.text = cardViewModel.title
            creatorNameView.text = cardViewModel.username
            locationView.setTextAndHideIfBlank(cardViewModel.locationString)
            val startDateString = cardViewModel.startDate?.toLocalDateTime()?.toString("dd.MM.yyyy HH:mm")
            val endDateString = cardViewModel.startDate?.toLocalDateTime()?.toString("dd.MM.yyyy HH:mm")
            if (!(startDateString.isNullOrBlank() && endDateString.isNullOrBlank()))
                startDateView.setTextAndHideIfBlank("$startDateString to $endDateString")

            if (cardViewModel.participants.isEmpty()) {
                participantsLL.visibility = View.GONE
            } else {
                participants.text = cardViewModel.participants.map { it.name }.sorted().joinToString(", ")
                participantsLL.visibility = View.VISIBLE
            }

            tagList.removeAllViews()

            cardViewModel.tags.sortedBy { t -> t.id }.map { tag -> tagToView(tag) }.forEach { v -> tagList.addView(v) }

            if (cardViewModel.username == OnMeetingApiService.getInstance().currentUsername)
                rootView.setOnClickListener { cardViewModel.startEditActivity(activity, EDIT_CARD_REQ_CODE) }
        }

        private fun tagToView(tag: TagViewModel): View {
            val v = FrameLayout(tagList.context)
            v.setPadding(0, 0, 5, 10)
            val tv = TextView(ContextThemeWrapper(tagList.context, R.style.tag))

            setColor(tv.background, Color.parseColor("#${tag.color}"))
            tv.text = tag.name
            v.addView(tv)
            return v
        }

        private fun setColor(background: Drawable?, color: Int) {
            when (background) {
                is ShapeDrawable -> background.paint.color = color
                is GradientDrawable -> background.setColor(color)
                is ColorDrawable -> background.color = color
            }
        }
    }
}

