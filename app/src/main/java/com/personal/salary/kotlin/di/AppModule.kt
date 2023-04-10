package com.personal.salary.kotlin.di

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object AppModule {

    /*@ActivityScoped
    @Provides
    fun provideMultiOperationHelper(@ActivityContext ctx: Context) = MultiOperationHelper(ctx as Activity)
    */
}