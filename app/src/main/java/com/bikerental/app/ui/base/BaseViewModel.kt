package com.bikerental.app.ui.base

import android.os.Messenger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikerental.app.ui.navigation.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


abstract class BaseViewModel(
    val navigator: Navigator
) : ViewModel() {

    companion object {
        const val TAG = "BaseViewModel"
    }

    protected fun launchFirebase(
        silent: Boolean = false,
//        error: (FirebaseErrorResponse) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (!silent) {
//            loader.start()
            viewModelScope.launch {
                try {
                    block()
                } catch (e: Throwable) {
                    if (e is CancellationException) return@launch
//                    val errorResponse = e.toFirebaseErrorResponse()
//                    handleFirebaseError(errorResponse)
//                    error(errorResponse)
//                    Logger.d(TAG, e)
//                    Logger.record(e)
                }
//                finally {
////                    loader.stop()
//                }
            }
        } else {
            viewModelScope.launch {
                try {
                    block()
                } catch (e: Throwable) {
                    if (e is CancellationException) return@launch
//                    val errorResponse = e.toFirebaseErrorResponse()
//                    error(errorResponse)
//                    Logger.d(TAG, e)
//                    Logger.record(e)
                }
            }
        }
    }
}