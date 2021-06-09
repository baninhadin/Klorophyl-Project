package com.example.klorophyl.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.klorophyl.data.Injection
import com.example.klorophyl.data.ItemRepository

class ViewModelFactory private constructor(private val itemRepository: ItemRepository) : ViewModelProvider.NewInstanceFactory() {

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideItemRepository(context)).apply {
                    instance = this
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ListMapViewModel::class.java) -> {
                return ListMapViewModel(itemRepository) as T
            }

            modelClass.isAssignableFrom(ListUsersViewModel::class.java) -> {
                return ListUsersViewModel(itemRepository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                return ProfileViewModel(itemRepository) as T
            }

            modelClass.isAssignableFrom(ChallengeViewModel::class.java) -> {
                return ChallengeViewModel(itemRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }
}
