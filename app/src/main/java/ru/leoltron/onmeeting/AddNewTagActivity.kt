package ru.leoltron.onmeeting

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.madrapps.pikolo.ColorPicker
import com.madrapps.pikolo.listeners.OnColorSelectionListener
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.leoltron.onmeeting.api.IOnMeetingApi
import ru.leoltron.onmeeting.api.OnMeetingApiService
import ru.leoltron.onmeeting.api.model.TagModel


class AddNewTagActivity : AppCompatActivity(), OnColorSelectionListener {

    private lateinit var titleEditText: EditText
    private lateinit var colorPicker: ColorPicker
    private lateinit var submitButton: Button

    private lateinit var newTagAddWaitingFrame: FrameLayout

    private lateinit var onMeetingApi: IOnMeetingApi

    private var colorString: String = "AAAAAA"

    private fun title(): String = titleEditText.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_tag)

        titleEditText = findViewById(R.id.titleEditText)
        titleEditText.addTextChangedListener { t -> validateTitle(t.toString()) }

        colorPicker = findViewById(R.id.colorPicker)
        colorPicker.setColorSelectionListener(this)
        submitButton = findViewById(R.id.add_new_tag_submit)
        submitButton.setOnClickListener { submit() }

        onMeetingApi = OnMeetingApiService.getInstance().api

        newTagAddWaitingFrame = findViewById(R.id.newTagAddWaitingFrame)
    }

    override fun onColorSelected(color: Int) {
        colorString = java.lang.String.format("%06X", 0xFFFFFF and color)
    }

    override fun onColorSelectionEnd(color: Int) {
    }

    override fun onColorSelectionStart(color: Int) {
    }

    private fun submit() {
        if (validateTitle(titleEditText.text.toString())) {
            newTagAddWaitingFrame.visibility = View.VISIBLE
            submitButton.isEnabled = false

            onMeetingApi.addTag(toModel()).enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(this@AddNewTagActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show()
                    newTagAddWaitingFrame.visibility = View.GONE
                    submitButton.isEnabled = true
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    newTagAddWaitingFrame.visibility = View.GONE
                    submitButton.isEnabled = true
                    when {
                        response.isSuccessful -> {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        response.code() == 400 -> titleEditText.error = getString(R.string.tag_already_exists)
                        else -> Toast.makeText(this@AddNewTagActivity, R.string.unexpected_error, Toast.LENGTH_LONG).show()

                    }
                }
            })
        }
    }

    private fun validateTitle(s: String): Boolean = when {
        s.isBlank() -> {
            titleEditText.error = getString(R.string.cannot_be_empty)
            false
        }
        OnMeetingApiService.getInstance().tags.map { it.name }.contains(s) -> {
            titleEditText.error = getString(R.string.tag_already_exists)
            false
        }
        else -> {
            titleEditText.error = null
            true
        }
    }

    private fun toModel(): TagModel {
        return TagModel(title(), colorString)
    }
}
