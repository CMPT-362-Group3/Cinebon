package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.repo.ListRepository
import kotlinx.coroutines.launch

class NewListViewModel : ViewModel() {

    private val listRepository = ListRepository.getInstance()
    val userLists = listRepository.resolvedLists

    fun createDefaultList() {
        viewModelScope.launch {
            listRepository.createDefaultList()
        }
    }

    fun createEmptyNewList() {
        viewModelScope.launch {
            listRepository.createEmptyNewList()
        }
    }
}