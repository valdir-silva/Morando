package com.alunando.morando.di

import com.alunando.morando.data.datasource.TasksRemoteDataSource
import com.alunando.morando.data.firebase.AuthManager
import com.alunando.morando.data.repository.InventoryRepositoryImpl
import com.alunando.morando.data.repository.ShoppingRepositoryImpl
import com.alunando.morando.data.repository.TasksRepositoryImpl
import com.alunando.morando.domain.repository.InventoryRepository
import com.alunando.morando.domain.repository.ShoppingRepository
import com.alunando.morando.domain.repository.TasksRepository
import com.alunando.morando.domain.usecase.AddProductUseCase
import com.alunando.morando.domain.usecase.AddTaskUseCase
import com.alunando.morando.domain.usecase.GenerateShoppingListUseCase
import com.alunando.morando.domain.usecase.GetDailyTasksUseCase
import com.alunando.morando.domain.usecase.GetProductsUseCase
import com.alunando.morando.domain.usecase.GetShoppingItemsUseCase
import com.alunando.morando.domain.usecase.GetWeeklyTasksUseCase
import com.alunando.morando.domain.usecase.MarkTaskCompleteUseCase
import com.alunando.morando.feature.barcode.presentation.BarcodeScannerViewModel
import com.alunando.morando.feature.tasks.presentation.TasksViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo Koin com todas as dependências do app
 */
val appModule = module {
    
    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    // Auth
    single { AuthManager(get()) }

    // Data Sources
    single { TasksRemoteDataSource(get(), get()) }

    // Repositories
    single<TasksRepository> { TasksRepositoryImpl(get()) }
    single<InventoryRepository> { InventoryRepositoryImpl() }
    single<ShoppingRepository> { ShoppingRepositoryImpl() }

    // Use Cases - Tasks
    factory { GetDailyTasksUseCase(get()) }
    factory { GetWeeklyTasksUseCase(get()) }
    factory { MarkTaskCompleteUseCase(get()) }
    factory { AddTaskUseCase(get()) }

    // Use Cases - Inventory
    factory { GetProductsUseCase(get()) }
    factory { AddProductUseCase(get()) }

    // Use Cases - Shopping
    factory { GetShoppingItemsUseCase(get()) }
    factory { GenerateShoppingListUseCase(get(), get()) }

    // ViewModels
    viewModel { TasksViewModel(get(), get(), get()) }
    viewModel { BarcodeScannerViewModel() }
}

