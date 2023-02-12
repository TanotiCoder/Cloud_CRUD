package com.example.cloudcrud.crud.repository

import com.example.cloudcrud.common.Utils.collectionName
import com.example.cloudcrud.common.data_model.FirestoreDataModel
import com.example.cloudcrud.common.result_state.ResultState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.IOException
import javax.inject.Inject


class CrudRepositoryImpl @Inject constructor(private val store: FirebaseFirestore) :
    CrudRepository {
    override fun getDataListFromStore(): Flow<ResultState<List<FirestoreDataModel>>> {
        return callbackFlow {
            try {
                trySend(ResultState.Loading)
                store.collection(collectionName).get()
                    .addOnSuccessListener {
                        val item = it.map { data ->
                            FirestoreDataModel(
                                item = FirestoreDataModel.FirestoreItem(
                                    title = data["title"] as String?,
                                    description = data["description"] as String?
                                ),
                                id = data.id
                            )
                        }
                        trySend(ResultState.Success(item))
                    }
                    .addOnFailureListener {
                        trySend(ResultState.Failure(it))
                    }
                awaitClose { close() }
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            } catch (e: IOException) {
                trySend(ResultState.Failure(e))
            }
        }
    }

    override fun insertDataInStore(firestoreItem: FirestoreDataModel.FirestoreItem): Flow<ResultState<String>> {
        return callbackFlow {
            try {
                trySend(ResultState.Loading)
                store.collection(collectionName).add(firestoreItem)
                    .addOnSuccessListener {
                        trySend(ResultState.Success(data = "Added Successfully...."))
                    }
                    .addOnFailureListener {
                        trySend(ResultState.Failure(it))
                    }
                awaitClose { close() }
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            } catch (e: IOException) {
                trySend(ResultState.Failure(e))
            }
        }
    }

    override fun deleteDataFromStore(id: String): Flow<ResultState<String>> {
        return callbackFlow {
            try {
                trySend(ResultState.Loading)
                store.collection(collectionName).document(id).delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            trySend(ResultState.Success("Deleted Successfully"))
                        }
                    }
                    .addOnFailureListener {
                        trySend(ResultState.Failure(it))
                    }
                awaitClose { close() }
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            } catch (e: IOException) {
                trySend(ResultState.Failure(e))
            }
        }
    }

    override fun updateDataOfFirestore(newItem: FirestoreDataModel): Flow<ResultState<String>> {
        return callbackFlow {
            try {
                trySend(ResultState.Loading)

                val map = HashMap<String, Any>()
                map["title"] = newItem.item?.title!!
                map["description"] = newItem.item.description!!
                store.collection(collectionName)
                    .document(newItem.id)
                    .update(map)
                    .addOnSuccessListener {
                        trySend(ResultState.Success("Update Successfully"))
                    }
                    .addOnFailureListener {
                        trySend(ResultState.Failure(it))
                    }
                awaitClose { close() }
            } catch (e: Exception) {
                trySend(ResultState.Failure(e))
            } catch (e: IOException) {
                trySend(ResultState.Failure(e))
            }
        }
    }
}