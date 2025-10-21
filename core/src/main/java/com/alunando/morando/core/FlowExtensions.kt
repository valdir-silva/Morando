package com.alunando.morando.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Converte Flow para Result com estados de Loading e Error
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> =
    this
        .map<T, Result<T>> { Result.Success(it) }
        .onStart { emit(Result.Loading) }
        .catch { emit(Result.Error(it)) }

/**
 * Converte exceções do Flow em Result.Error
 */
fun <T> Flow<T>.catchAsResult(): Flow<Result<T>> =
    this
        .map<T, Result<T>> { Result.Success(it) }
        .catch { emit(Result.Error(it)) }
