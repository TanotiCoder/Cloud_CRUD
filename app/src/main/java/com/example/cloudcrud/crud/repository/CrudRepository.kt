package com.example.cloudcrud.crud.repository

import com.example.cloudcrud.common.data_model.FirestoreDataModel
import com.example.cloudcrud.common.result_state.ResultState
import kotlinx.coroutines.flow.Flow

interface CrudRepository {
    fun getDataListFromStore(): Flow<ResultState<List<FirestoreDataModel>>>
    fun insertDataInStore(item: FirestoreDataModel.FirestoreItem): Flow<ResultState<String>>
    fun deleteDataFromStore(id: String): Flow<ResultState<String>>
    fun updateDataOfFirestore(newItem: FirestoreDataModel): Flow<ResultState<String>>
}