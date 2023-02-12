package com.example.cloudcrud.crud.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cloudcrud.common.data_model.FirestoreDataModel
import com.example.cloudcrud.common.result_state.ResultState
import com.example.cloudcrud.crud.repository.CrudRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShowItemState(
    val dataShow: List<FirestoreDataModel> = emptyList(),
    val isLoadingShow: Boolean = false,
    val errorShow: String = ""
)

data class LoadState(
    var dataLoad: String = "",
    val isLoadingLoad: Boolean = false,
    var errorLoad: String = ""
)

@HiltViewModel
class CrudViewModel @Inject constructor(private val repository: CrudRepository) : ViewModel() {
    private val _stateShow: MutableState<ShowItemState> = mutableStateOf(ShowItemState())
    private val _stateLoad: MutableState<LoadState> = mutableStateOf(LoadState())

    val stateShow: State<ShowItemState> = _stateShow
    val stateLoad: State<LoadState> = _stateLoad

    var getTitle by mutableStateOf("")
    var getDescription by mutableStateOf("")
    var getId by mutableStateOf("")
    var dialogState by mutableStateOf(false)
    var openFor by mutableStateOf("")

    init {
        showData()
    }

    private fun showData() {
        viewModelScope.launch {
            repository.getDataListFromStore().collect {
                when (it) {
                    is ResultState.Failure -> _stateShow.value =
                        ShowItemState(errorShow = it.msg.message ?: "Error")
                    ResultState.Loading -> _stateShow.value = ShowItemState(isLoadingShow = true)
                    is ResultState.Success -> _stateShow.value = ShowItemState(dataShow = it.data)
                }
            }
        }
    }

    fun addItem() {
        viewModelScope.launch {
            repository.insertDataInStore(FirestoreDataModel.FirestoreItem(getTitle, getDescription))
                .collect {
                    when (it) {
                        is ResultState.Failure -> _stateLoad.value =
                            LoadState(errorLoad = it.msg.message ?: "Error")
                        ResultState.Loading -> _stateLoad.value = LoadState(isLoadingLoad = true)
                        is ResultState.Success -> _stateLoad.value = LoadState(dataLoad = it.data)
                    }
                }
        }
        showData()
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            repository.deleteDataFromStore(id)
                .collect {
                    when (it) {
                        is ResultState.Failure -> _stateLoad.value =
                            LoadState(errorLoad = it.msg.message ?: "Error")
                        ResultState.Loading -> _stateLoad.value = LoadState(isLoadingLoad = true)
                        is ResultState.Success -> _stateLoad.value = LoadState(dataLoad = it.data)
                    }
                }
        }
        showData()
    }

    fun updateItem() {
        viewModelScope.launch {
            repository.updateDataOfFirestore(
                FirestoreDataModel(
                    id = getId,
                    item = FirestoreDataModel.FirestoreItem(
                        title = getTitle,
                        description = getDescription
                    )
                )
            ).collect {
                when (it) {
                    is ResultState.Failure -> _stateLoad.value =
                        LoadState(errorLoad = it.msg.message ?: "Error")
                    ResultState.Loading -> _stateLoad.value = LoadState(isLoadingLoad = true)
                    is ResultState.Success -> _stateLoad.value = LoadState(dataLoad = it.data)
                }
            }
        }
        showData()
    }

    fun openDialog() {
        dialogState = true
    }

    fun closeDialog() {
        dialogState = false
    }

    fun clearAll() {
        getTitle = ""
        getDescription = ""
        getId = ""
        openFor = ""
    }
}