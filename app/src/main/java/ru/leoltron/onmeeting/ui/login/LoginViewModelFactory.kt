package ru.leoltron.onmeeting.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import ru.leoltron.onmeeting.api.OnMeetingApiService

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(OnMeetingApiService.getInstance().api) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
