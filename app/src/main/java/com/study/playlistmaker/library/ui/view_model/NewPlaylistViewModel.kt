package com.study.playlistmaker.library.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewPlaylistViewModel : ViewModel() {

    private val _isNameValid = MutableLiveData<Boolean>(false)
    val isButtonEnabled: LiveData<Boolean> = _isNameValid

    fun onNameInputTextChanged(text: String) {
        _isNameValid.value = text.isNotBlank()
    }
}
