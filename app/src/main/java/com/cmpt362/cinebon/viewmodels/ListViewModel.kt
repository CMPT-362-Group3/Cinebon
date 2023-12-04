package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.ListEntity
import com.cmpt362.cinebon.data.repo.ListRepository
import kotlinx.coroutines.launch

class ListViewModel(private val listRepository: ListRepository) : ViewModel() {

    val userLists = listRepository.userLists
    val resolvedLists = listRepository.resolvedLists

    fun createList(list: ListEntity) {
        viewModelScope.launch {
            listRepository.createList(list)
        }
    }

    fun updateList(listId: String, updatedList: ListEntity) {
        viewModelScope.launch {
            listRepository.updateList(listId, updatedList)
        }
    }

    fun deleteList(listId: String) {
        viewModelScope.launch {
            listRepository.deleteList(listId)
        }
    }

    // TODO: i dont think i need this, can delete later but just in case
    fun startListRefreshWorker() {
        viewModelScope.launch {
            listRepository.startListRefreshWorker()
        }
    }
}