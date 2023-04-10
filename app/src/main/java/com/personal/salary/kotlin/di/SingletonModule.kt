package com.personal.salary.kotlin.di

import android.content.Context
import com.personal.salary.kotlin.app.AppApplication
import com.personal.salary.kotlin.viewmodel.app.AppViewModel
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    fun provideGson(): Gson = GsonUtils.getGson()

    @Provides
    fun provideApplication(@ApplicationContext context: Context): AppApplication = context.applicationContext as AppApplication

    @Provides
    fun provideAppViewModel(): AppViewModel = AppViewModel.getAppViewModel()
}