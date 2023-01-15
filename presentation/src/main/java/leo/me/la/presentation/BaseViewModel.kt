package leo.me.la.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<VS : Any> : ViewModel() {

    protected abstract val _viewState: MutableStateFlow<VS>
    val viewState: StateFlow<VS> get () = _viewState

}
