package com.example.cloudcrud.common.data_model

data class FirestoreDataModel(
    val item: FirestoreItem,
    val id: String
) {
    data class FirestoreItem(
        val title: String?,
        val description: String?
    )
}
