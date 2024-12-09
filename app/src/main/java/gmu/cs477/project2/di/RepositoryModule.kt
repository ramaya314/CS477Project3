package gmu.cs477.project2.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import gmu.cs477.project2.ItemInventoryApplication
import gmu.cs477.project2.data.db.DatabaseHelper
import gmu.cs477.project2.data.db.repositories.sqlite.ItemRepository as SQLiteItemRepository
import gmu.cs477.project2.data.db.repositories.sqlite.OrderRepository as SQLiteOrderRepository

import gmu.cs477.project2.interfaces.IItemRepository
import gmu.cs477.project2.interfaces.IOrderRepository
import javax.inject.Singleton
import gmu.cs477.project2.BuildConfig

import gmu.cs477.project2.data.db.repositories.firebase.ItemRepository as FirebaseItemRepository
import gmu.cs477.project2.data.db.repositories.firebase.OrderRepository as FirebaseOrderRepository

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideItemRepository(dbHelper: DatabaseHelper): IItemRepository {

        return when (BuildConfig.DB_PROVIDER) {
            "Firebase" -> FirebaseItemRepository()
            else -> SQLiteItemRepository(dbHelper)
        }
    }

    @Provides
    @Singleton
    fun provideOrderRepository(dbHelper: DatabaseHelper): IOrderRepository {
        return when (BuildConfig.DB_PROVIDER) {
            "Firebase" -> FirebaseOrderRepository()
            else -> SQLiteOrderRepository(dbHelper)
        }
    }

    @Provides
    @Singleton
    fun provideDatabaseHelper(application: Application): DatabaseHelper {
        return DatabaseHelper(application)
    }

}