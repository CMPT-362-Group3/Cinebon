package com.cmpt362.cinebon.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cmpt362.cinebon.data.entity.ResolvedListEntity
import com.cmpt362.cinebon.data.repo.ListRepository
import com.cmpt362.cinebon.data.repo.ListRepository.Companion.DEFAULT_LIST_NAME
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class IndividualListViewModel(listId: String) : ViewModel() {

    private val listRepository = ListRepository.getInstance()

    private val _currentList = MutableStateFlow(
        listRepository.getResolvedListById(listId)
    )

    val currentList: StateFlow<ResolvedListEntity?> = _currentList

    // When the VM initializes, track the list with the given id
    // And keep it plugged to the UI whenever resolved lists update
    init {
        viewModelScope.launch {
            listRepository.resolvedLists.collectLatest {
                _currentList.value = listRepository.getResolvedExternalListById(listId)
            }
        }
    }

    // Method to update the shown list's name
    // We do a little verification to only update if user is owner
    fun updateListName(name: String) {
        if (currentList.value?.isSelf == false || name == DEFAULT_LIST_NAME) {
            return
        }

        viewModelScope.launch {
            listRepository.updateListName(currentList.value!!.listId, name)
        }
    }

    fun addMovieToList(movieId: Int) {
        viewModelScope.launch {
            listRepository.addMovieToList(currentList.value!!.listId, movieId)
        }
    }

    fun removeMovieFromList(movieId: Int) {
        viewModelScope.launch {
            listRepository.deleteMovieFromList(currentList.value!!.listId, movieId)
        }
    }

    fun deleteList(list: ResolvedListEntity?) {
        if (list == null) return

        viewModelScope.launch {
            listRepository.deleteList(list)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val listId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IndividualListViewModel(listId) as T
        }
    }
}