package com.github.kutyrev.vocabulator.datasource.firebase.di

import com.github.kutyrev.vocabulator.datasource.firebase.CloudBase
import com.github.kutyrev.vocabulator.datasource.firebase.FireBaseSourceCloudBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class FirebaseModule {

    @Provides
    fun bindsTranslationDataSource(): CloudBase {
        return FireBaseSourceCloudBase()
    }
}
