package leo.me.la.presentation

@Suppress("Unused", "MemberVisibilityCanBePrivate")
sealed class DataState<out T> {

    fun requireError(): Throwable = (this as Failure).error

    fun requireData(): T = (this as Success).data

    fun optData(): T? = (this as? Success)?.data

    override fun toString(): String {
        return when (this) {
            is Idle -> "Idle"
            is Loading -> "Loading"
            is Failure -> "Failure[error=$error]"
            is Success -> "Success[data=$data]"
        }
    }

    object Idle : DataState<Nothing>()

    object Loading : DataState<Nothing>()

    class Failure(t: Throwable? = null) : DataState<Nothing>() {

        val error = t ?: RuntimeException()

    }

    data class Success<T>(val data: T) : DataState<T>()

    companion object {
        val DataState<*>?.idle: Boolean get() = this is Idle
        val DataState<*>?.loading: Boolean get() = this is Loading
        val DataState<*>?.failed: Boolean get() = this is Failure
        val DataState<*>?.succeeded: Boolean get() = this is Success
    }

}
