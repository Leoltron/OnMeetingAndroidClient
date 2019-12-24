package ru.leoltron.onmeeting.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.leoltron.onmeeting.MainActivity
import ru.leoltron.onmeeting.R
import ru.leoltron.onmeeting.api.OnMeetingApiService

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login)
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loading)

        loginViewModel.loginFormState.observe(this, Observer { loginFormState ->
            if (loginFormState == null) {
                return@Observer
            }
            loginButton.isEnabled = loginFormState.isDataValid
            if (loginFormState.usernameError != null) {
                usernameEditText.error = getString(loginFormState.usernameError!!)
            }
            if (loginFormState.passwordError != null) {
                passwordEditText.error = getString(loginFormState.passwordError!!)
            }
        })

        loginViewModel.loginResult.observe(this, Observer { loginResult ->
            if (loginResult == null) {
                return@Observer
            }
            loadingProgressBar.visibility = View.GONE
            formEnabled = true
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success!!)
            }
            setResult(Activity.RESULT_OK)

            //Complete and destroy loginOrRegister activity once successful
            // finish();
        })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(usernameEditText.text.toString(),
                        passwordEditText.text.toString())
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginOrRegister(usernameEditText, passwordEditText)
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            formEnabled = false
            loginOrRegister(usernameEditText, passwordEditText)
        }

        tryRestoreLoginData()
    }

    private fun tryRestoreLoginData() {
        val pref = getPreferences(Context.MODE_PRIVATE)
        val username = pref.getString("username", "")
        val password = pref.getString("password", "")
        if (username.isNullOrBlank() || password.isNullOrBlank())
            return

        usernameEditText.setText(username, TextView.BufferType.EDITABLE)
        passwordEditText.setText(password, TextView.BufferType.EDITABLE)
    }

    private fun saveLoginData(username: String, password: String) =
            getPreferences(Context.MODE_PRIVATE).edit()
                    .putString("username", username)
                    .putString("password", password)
                    .apply()

    private var formEnabled: Boolean
        get() = loginButton.isEnabled
        set(value) {

            passwordEditText.isEnabled = value
            usernameEditText.isEnabled = value
            loginButton.isEnabled = value
        }

    private fun loginOrRegister(usernameEditText: EditText, passwordEditText: EditText) {
        loginViewModel.loginOrRegister(usernameEditText.text.toString(),
                passwordEditText.text.toString())
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        saveLoginData(usernameEditText.text.toString(), passwordEditText.text.toString())
        val instance = OnMeetingApiService.getInstance()
        instance.refreshAll()

        instance.currentUsername = model.displayName

        val welcome = getString(R.string.welcome) + " " + model.displayName
        val intent = Intent(this, MainActivity::class.java)
        Toast.makeText(applicationContext, welcome, Toast.LENGTH_LONG).show()
        startActivity(intent)
    }

    private fun showLoginFailed(@StringRes errorString: Int?) {
        Toast.makeText(applicationContext, errorString!!, Toast.LENGTH_SHORT).show()
    }
}
