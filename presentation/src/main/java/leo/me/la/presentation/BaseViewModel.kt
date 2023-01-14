package leo.me.la.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<VS : Any> : ViewModel() {

    protected val _viewStates: MutableLiveData<VS> = MutableLiveData()

    val viewStates: LiveData<VS>
        get() = _viewStates
}
