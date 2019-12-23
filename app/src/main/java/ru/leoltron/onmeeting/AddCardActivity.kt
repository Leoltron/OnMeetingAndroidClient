package ru.leoltron.onmeeting

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import org.joda.time.LocalDateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.leoltron.onmeeting.api.IOnMeetingApi
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.CardAddOrEditModel
import ru.leoltron.onmeeting.api.model.CardViewModel


class AddCardActivity : AppCompatActivity(), Callback<ResponseBody> {

    private lateinit var waitingFrame: FrameLayout

    private lateinit var titleEditText: EditText
    private lateinit var locationEditText: EditText

    private lateinit var startDateButton: Button
    private lateinit var startDateHolder: FormDateTimeHolder

    private lateinit var endDateButton: Button
    private lateinit var endDateHolder: FormDateTimeHolder

    private lateinit var participantsValue: TextView
    private lateinit var editParticipantsButton: Button

    private lateinit var tagsList: FlexboxLayout
    private lateinit var editTagsButton: Button

    private lateinit var submitButton: Button

    private lateinit var onMeetingApi: IOnMeetingApi

    private var editCardId: Int = -1
    private var isEditingMode: Boolean = false

    private var selectedParticipantIds: Set<Int> = emptySet()
    private var tagIds: Set<Int> = emptySet()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_add_card)

        waitingFrame = findViewById(R.id.cardAddWaitingFrame)

        titleEditText = findViewById(R.id.title_et)
        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) = if (titleEditText.length() < 3)
                titleEditText.error = getString(R.string.title_3_or_more)
            else titleEditText.error = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

        })
        locationEditText = findViewById(R.id.location_et)


        startDateButton = findViewById(R.id.start_date_button)
        startDateHolder = FormDateTimeHolder(startDateButton)
        startDateHolder.setOnClickListener(this::showDialog)

        endDateButton = findViewById(R.id.end_date_button)
        endDateHolder = FormDateTimeHolder(endDateButton)
        endDateHolder.setOnClickListener(this::showDialog)

        participantsValue = findViewById(R.id.participants_value_tv)
        editParticipantsButton = findViewById(R.id.participants_edit_button)
        editParticipantsButton.setOnClickListener {
            val intent = Intent(this, SelectParticipantsActivity::class.java)
                    .putExtra(idsSelectedKey, selectedParticipantIds.toIntArray())
            startActivityForResult(intent, selectParticipantsRequestId)
        }

        editTagsButton = findViewById(R.id.tags_edit_button)
        tagsList = findViewById(R.id.tags_value_fl)

        submitButton = findViewById(R.id.add_card_submit)
        submitButton.setOnClickListener {
            if (titleEditText.length() >= 3) {
                waitingFrame.visibility = View.VISIBLE
                submitButton.isEnabled = false

                val call = if (isEditingMode) onMeetingApi.editCard(editCardId, formToModel()) else onMeetingApi.addCard(formToModel())
                call.enqueue(this)
            }
        }

        onMeetingApi = OnMeetingApiService.getInstance().api

        val stringExtra = intent.getStringExtra("card")
        if (stringExtra != null) {
            modelToForm(stringExtra.toCardViewModel())
            isEditingMode = true
        }
    }

    private fun showDialog(holder: FormDateTimeHolder) {
        val now = LocalDateTime()
        DatePickerDialog(this, { _, y, m, d -> onDateSet(holder, y, m, d) }, now.year, now.monthOfYear - 1, now.dayOfMonth).show()
    }

    private fun onDateSet(holder: FormDateTimeHolder, year: Int, month: Int, day: Int) {
        val now = LocalDateTime()
        TimePickerDialog(this,
                { _, h, m -> holder.dateTime = LocalDateTime(year, month + 1, day, h, m) },
                now.hourOfDay,
                now.minuteOfHour,
                DateFormat.is24HourFormat(this)).show()
    }

    private fun formToModel(): CardAddOrEditModel {
        return CardAddOrEditModel(
                titleEditText.text.toString(),
                locationEditText.text.toString(),
                startDateHolder.dateTime?.toTimestamp(),
                endDateHolder.dateTime?.toTimestamp(),
                selectedParticipantIds.toList(),
                tagIds.toList())
    }

    private fun modelToForm(model: CardViewModel) {
        editCardId = model.cardId

        titleEditText.setText(model.title)
        locationEditText.setText(model.locationString ?: "")
        startDateHolder.dateTime = model.startDate?.toLocalDateTime()
        endDateHolder.dateTime = model.endDate?.toLocalDateTime()

        selectedParticipantIds = model.participants.map { u -> u.id }.toHashSet()
        updateParticipants()
        tagIds = model.tags.map { t -> t.id }.toHashSet()
        updateTagList()
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        Snackbar.make(submitButton, R.string.unexpected_error, Snackbar.LENGTH_LONG).show()
    }

    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        waitingFrame.visibility = View.GONE
        submitButton.isEnabled = true

        if (response.isSuccessful) {
            setResult(Activity.RESULT_OK)
            Snackbar.make(submitButton,
                    if (isEditingMode) R.string.successfully_edited
                    else R.string.successfully_added,
                    Snackbar.LENGTH_LONG).show()
            finish()
        }
    }

    private fun updateTagList() {

    }

    private fun updateParticipants() {
        participantsValue.text = OnMeetingApiService.getInstance().users
                .filter { p -> selectedParticipantIds.contains(p.id) }
                .map { p -> p.name }
                .sorted()
                .joinToString(", ")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == selectParticipantsRequestId) {
            val selectedIds = data?.getIntArrayExtra(idsSelectedKey)
            if (resultCode == Activity.RESULT_OK && selectedIds != null) {
                this.selectedParticipantIds = OnMeetingApiService.getInstance().users.map { u -> u.id }.intersect(selectedIds.asIterable()).toSet()
                updateParticipants()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val selectParticipantsRequestId = 0
        const val idsSelectedKey = "idsSelected"
    }
}
