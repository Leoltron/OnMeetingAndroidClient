package ru.leoltron.onmeeting

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import ru.leoltron.onmeeting.api.model.UserModel


class UsersAdapter(private val participants: List<UserModel>) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    val selectedIds: MutableSet<Int> = mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.participants_select_item, parent, false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val model = participants[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) selectedIds.add(model.id) else selectedIds.remove(model.id)
        }
        holder.checkBox.isChecked = selectedIds.contains(model.id)
        holder.checkBox.text = model.name
    }

    override fun getItemCount(): Int = participants.size

    class UserViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        var checkBox: CheckBox = rootView.findViewById(R.id.participantItemCheckBox)
    }
}

