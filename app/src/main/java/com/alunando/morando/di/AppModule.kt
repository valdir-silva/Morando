package com.alunando.morando.di

import com.alunando.morando.BuildConfig
import com.alunando.morando.data.api.CosmosApiService
import com.alunando.morando.data.api.OpenFoodFactsApiService
import com.alunando.morando.data.api.ProductApiDataSource
import com.alunando.morando.data.datasource.CookingRemoteDataSource
import com.alunando.morando.data.datasource.InventoryRemoteDataSource
import com.alunando.morando.data.datasource.TasksRemoteDataSource
import com.alunando.morando.data.firebase.AuthManager
import com.alunando.morando.data.repository.CookingRepositoryImpl
import com.alunando.morando.data.repository.CookingRepositoryMock
import com.alunando.morando.data.repository.InventoryRepositoryImpl
import com.alunando.morando.data.repository.InventoryRepositoryMock
import com.alunando.morando.data.repository.ShoppingRepositoryImpl
import com.alunando.morando.data.repository.TasksRepositoryImpl
import com.alunando.morando.data.repository.TasksRepositoryMock
import com.alunando.morando.domain.repository.CookingRepository
import com.alunando.morando.domain.repository.InventoryRepository
import com.alunando.morando.domain.repository.ShoppingRepository
import com.alunando.morando.domain.repository.TasksRepository
import com.alunando.morando.domain.usecase.AddProductUseCase
import com.alunando.morando.domain.usecase.AddRecipeUseCase
import com.alunando.morando.domain.usecase.AddTaskUseCase
import com.alunando.morando.domain.usecase.CheckIngredientsAvailabilityUseCase
import com.alunando.morando.domain.usecase.DeleteProductUseCase
import com.alunando.morando.domain.usecase.DeleteRecipeUseCase
import com.alunando.morando.domain.usecase.DeleteTaskUseCase
import com.alunando.morando.domain.usecase.GenerateShoppingListUseCase
import com.alunando.morando.domain.usecase.GetProductByIdUseCase
import com.alunando.morando.domain.usecase.GetProductInfoFromBarcodeUseCase
import com.alunando.morando.domain.usecase.GetProductsNeedingReplenishmentUseCase
import com.alunando.morando.domain.usecase.GetProductsUseCase
import com.alunando.morando.domain.usecase.GetRecipeByIdUseCase
import com.alunando.morando.domain.usecase.GetRecipesUseCase
import com.alunando.morando.domain.usecase.GetShoppingItemsUseCase
import com.alunando.morando.domain.usecase.GetSubTasksUseCase
import com.alunando.morando.domain.usecase.GetTasksForDateRangeUseCase
import com.alunando.morando.domain.usecase.GetTasksForDateUseCase
import com.alunando.morando.domain.usecase.GetUserStovePreferenceUseCase
import com.alunando.morando.domain.usecase.MarkTaskCompleteUseCase
import com.alunando.morando.domain.usecase.SaveUserStovePreferenceUseCase
import com.alunando.morando.domain.usecase.UpdateProductUseCase
import com.alunando.morando.domain.usecase.UpdateRecipeUseCase
import com.alunando.morando.domain.usecase.UploadProductImageUseCase
import com.alunando.morando.domain.usecase.cooking.UploadRecipeImageUseCase
import com.alunando.morando.feature.barcode.presentation.BarcodeScannerViewModel
import com.alunando.morando.feature.contas.data.repository.ContasRepositoryMock
import com.alunando.morando.feature.contas.domain.repository.ContasRepository
import com.alunando.morando.feature.contas.domain.usecase.AddContaUseCase
import com.alunando.morando.feature.contas.domain.usecase.DeleteContaUseCase
import com.alunando.morando.feature.contas.domain.usecase.GetContasByMonthUseCase
import com.alunando.morando.feature.contas.domain.usecase.GetContasPendentesUseCase
import com.alunando.morando.feature.contas.domain.usecase.GetContasUseCase
import com.alunando.morando.feature.contas.domain.usecase.GetTotaisByMonthUseCase
import com.alunando.morando.feature.contas.domain.usecase.MarkContaPagaUseCase
import com.alunando.morando.feature.contas.domain.usecase.UpdateContaUseCase
import com.alunando.morando.feature.contas.presentation.ContasViewModel
import com.alunando.morando.feature.cooking.presentation.CookingViewModel
import com.alunando.morando.feature.inventory.presentation.InventoryViewModel
import com.alunando.morando.feature.tasks.presentation.TasksViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Módulo Koin com todas as dependências do app
 */
@Suppress("MagicNumber")
val appModule =
    module {

        // Firebase (apenas quando não for MOCK)
        single { FirebaseAuth.getInstance() }
        single { FirebaseFirestore.getInstance() }
        single { FirebaseStorage.getInstance() }

        // Auth
        single { AuthManager(get()) }

        // HTTP Client
        single {
            OkHttpClient
                .Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        // Moshi
        single {
            Moshi
                .Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }

        // API Services
        single {
            Retrofit
                .Builder()
                .baseUrl("https://api.cosmos.bluesoft.com.br/")
                .client(get())
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .build()
                .create(CosmosApiService::class.java)
        }

        single {
            Retrofit
                .Builder()
                .baseUrl("https://world.openfoodfacts.org/")
                .client(get())
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .build()
                .create(OpenFoodFactsApiService::class.java)
        }

        // API Data Source (real ou mock)
        single {
            if (BuildConfig.BACKEND_TYPE == "MOCK") {
                null // Mock não usa API externa
            } else {
                ProductApiDataSource(get(), get())
            }
        }

        // Data Sources (apenas quando não for MOCK)
        single { TasksRemoteDataSource(get(), get()) }
        single { InventoryRemoteDataSource(get(), get(), get(), get()) }
        single { CookingRemoteDataSource(get(), get(), get()) }

        // Repositories - usa implementação mock ou real baseado no BuildConfig
        single<TasksRepository> {
            if (BuildConfig.BACKEND_TYPE == "MOCK") {
                TasksRepositoryMock()
            } else {
                TasksRepositoryImpl(get())
            }
        }
        single<InventoryRepository> {
            if (BuildConfig.BACKEND_TYPE == "MOCK") {
                InventoryRepositoryMock()
            } else {
                InventoryRepositoryImpl(get())
            }
        }
        single<ShoppingRepository> { ShoppingRepositoryImpl() }
        single<CookingRepository> {
            if (BuildConfig.BACKEND_TYPE == "MOCK") {
                CookingRepositoryMock(get())
            } else {
                CookingRepositoryImpl(get())
            }
        }
        single<ContasRepository> { ContasRepositoryMock() }

        // Use Cases - Tasks
        factory { GetTasksForDateUseCase(get()) }
        factory { GetTasksForDateRangeUseCase(get()) }
        factory { GetSubTasksUseCase(get()) }
        factory { MarkTaskCompleteUseCase(get()) }
        factory { AddTaskUseCase(get()) }
        factory { DeleteTaskUseCase(get()) }

        // Use Cases - Inventory
        factory { GetProductsUseCase(get()) }
        factory { AddProductUseCase(get()) }
        factory { UpdateProductUseCase(get()) }
        factory { DeleteProductUseCase(get()) }
        factory { GetProductByIdUseCase(get()) }
        factory { GetProductsNeedingReplenishmentUseCase(get()) }
        factory { UploadProductImageUseCase(get()) }
        factory { GetProductInfoFromBarcodeUseCase(get()) }

        // Use Cases - Shopping
        factory { GetShoppingItemsUseCase(get()) }
        factory { GenerateShoppingListUseCase(get(), get()) }

        // Use Cases - Cooking
        factory { GetRecipesUseCase(get()) }
        factory { GetRecipeByIdUseCase(get()) }
        factory { AddRecipeUseCase(get()) }
        factory { UpdateRecipeUseCase(get()) }
        factory { DeleteRecipeUseCase(get()) }
        factory { CheckIngredientsAvailabilityUseCase(get()) }
        factory { GetUserStovePreferenceUseCase(get()) }
        factory { SaveUserStovePreferenceUseCase(get()) }
        factory { UploadRecipeImageUseCase(get()) }

        // Use Cases - Contas
        factory { GetContasUseCase(get()) }
        factory { GetContasByMonthUseCase(get()) }
        factory { GetContasPendentesUseCase(get()) }
        factory { AddContaUseCase(get()) }
        factory { UpdateContaUseCase(get()) }
        factory { DeleteContaUseCase(get()) }
        factory { MarkContaPagaUseCase(get()) }
        factory { GetTotaisByMonthUseCase(get()) }

        // ViewModels
        viewModel { TasksViewModel(get(), get(), get(), get(), get(), get(), get()) }
        viewModel { InventoryViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { BarcodeScannerViewModel() }
        viewModel { CookingViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
        viewModel { ContasViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { com.alunando.morando.ui.login.LoginViewModel(get()) }
    }
