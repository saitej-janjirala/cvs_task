package com.saitejajanjirala.cvs_task.ui.detail

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saitejajanjirala.cvs_task.domain.repo.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailScreenViewModel @Inject constructor(private val searchRepository:SearchRepository) : ViewModel() {

    private val _shareUri = mutableStateOf<Uri?>(null)
    val shareUri: State<Uri?> = _shareUri

    fun getUri(url:String){
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.getFileUri(url).collect{
                _shareUri.value = it
            }
        }
    }


}