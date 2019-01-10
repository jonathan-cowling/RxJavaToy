package tk.jonathancowling.rxjavatoy.features.reactivevscallbacks

interface ReactiveVsCallbacksView {
    fun setReactiveString(string: String)
    fun setCallbackString(string: String)
    fun setReactiveError(throwable: Throwable?)
    fun setCallbackError(throwable: Throwable?)
}
