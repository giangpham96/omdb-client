package leo.me.la.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel<VS : BaseViewState>(
    private val context: CoroutineContext = Dispatchers.Main
) : ViewModel(), CoroutineScope {
    protected var parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = context + parentJob

    protected val _viewStates: MutableLiveData<VS> = MutableLiveData()

    val viewStates: LiveData<VS>
        get() = _viewStates

    override fun onCleared() {
        parentJob.cancel()
        super.onCleared()
    }
}

interface BaseViewState
