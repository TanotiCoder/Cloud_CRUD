package com.example.cloudcrud.common

import com.example.cloudcrud.crud.repository.CrudRepository
import com.example.cloudcrud.crud.repository.CrudRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun createFirestore(): FirebaseFirestore = Firebase.firestore
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideCrudRepo(crudRepositoryImpl: CrudRepositoryImpl):CrudRepository
}
