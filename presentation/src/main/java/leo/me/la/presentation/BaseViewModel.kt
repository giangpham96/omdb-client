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
    protected lateinit var parentJob: Job

    override val coroutineContext: CoroutineContext
        get() = context + parentJob

    protected val _viewStates: MutableLiveData<VS> = MutableLiveData()

    val viewStates: LiveData<VS>
        get() = _viewStates

    protected fun isParentJobInitialized() = ::parentJob.isInitialized

    override fun onCleared() {
        if (isParentJobInitialized())
            parentJob.cancel()
        super.onCleared()
    }
}

interface BaseViewState
