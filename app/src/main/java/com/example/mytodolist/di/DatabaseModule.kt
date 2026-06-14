package com.example.mytodolist.di

import android.content.Context
import androidx.room.Room
import com.example.mytodolist.data.db.AppDatabase
import com.example.mytodolist.data.db.dao.NotificationTimeDao
import com.example.mytodolist.data.db.dao.RoutineDao
import com.example.mytodolist.data.db.dao.TodoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "todolist.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideRoutineDao(db: AppDatabase): RoutineDao = db.routineDao()

    @Provides
    fun provideTodoDao(db: AppDatabase): TodoDao = db.todoDao()

    @Provides
    fun provideNotificationTimeDao(db: AppDatabase): NotificationTimeDao = db.notificationTimeDao()
}
