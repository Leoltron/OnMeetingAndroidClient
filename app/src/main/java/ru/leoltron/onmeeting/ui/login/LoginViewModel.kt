package ru.leoltron.onmeeting.ui.login

import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.leoltron.onmeeting.OnMeetingApplication
import ru.leoltron.onmeeting.R
import ru.leoltron.onmeeting.api.IOnMeetingApi
import ru.leoltron.onmeeting.api.model.ApiError
import ru.leoltron.onmeeting.api.model.UserModel

class LoginViewModel(private val onMeetingApi: IOnMeetingApi) : ViewModel() {

    val loginFormState = MutableLiveData<LoginFormState>()
    val loginResult = MutableLiveData<LoginResult>()

    fun loginOrRegister(username: String, password: String) {
        onMeetingApi.getUser(username).enqueue(object : SimpleCallback<UserModel>() {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) = when {
                response.code() == 404 -> onMeetingApi.register(username, password).enqueue(object : SimpleCallback<ResponseBody>() {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        login(username, password)
                    }
                })
                response.isSuccessful -> login(username, password)
                else -> reportUnknownError()
            }
        })
    }

    private fun reportUnknownError() {
        loginResult.value = LoginResult(R.string.login_failed)
    }

    private fun login(username: String, password: String) =
            onMeetingApi.login(username, password).enqueue(object : SimpleCallback<ResponseBody>() {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful)
                        loginResult.setValue(LoginResult(LoggedInUserView(username)))
                    else {
                        val text = if (response.code() == 401)
                            "Wrong username or password"
                        else
                            getResultFromBody(response.errorBody())
                        Toast.makeText(OnMeetingApplication.appContext, text, Toast.LENGTH_LONG).show()
                        loginResult.setValue(LoginResult(R.string.login_failed))
                    }
                }
            })

    private fun getResultFromBody(responseBody: ResponseBody?): String = if (responseBody == null) {
        "Login failed"
    } else Gson().fromJson(responseBody.charStream(), ApiError::class.java).message

    private abstract inner class SimpleCallback<T> : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            reportUnknownError()
        }
    }

    fun loginDataChanged(username: String, password: String) = if (!isUserNameValid(username))
        loginFormState.setValue(LoginFormState(R.string.invalid_username, null))
    else if (!isPasswordValid(password))
        loginFormState.setValue(LoginFormState(null, R.string.invalid_password))
    else
        loginFormState.setValue(LoginFormState(true))

    // A placeholder username validation check
    private fun isUserNameValid(username: String?): Boolean = when {
        username == null -> false
        username.contains("@") -> Patterns.EMAIL_ADDRESS.matcher(username).matches()
        else -> username.trim { it <= ' ' }.length > 4
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String?): Boolean =
            password != null && password.trim { it <= ' ' }.length > 5
}
