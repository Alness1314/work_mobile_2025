package com.susess.cv360.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.susess.cv360.helpers.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    // Navegación a segunda pantalla
    private val _navigateToSecondEvent = MutableLiveData<Event<Unit>>()
    val navigateToSecondEvent: LiveData<Event<Unit>> = _navigateToSecondEvent

    fun onNavigateToSecond(){
        _navigateToSecondEvent.value = Event(Unit)
    }
}