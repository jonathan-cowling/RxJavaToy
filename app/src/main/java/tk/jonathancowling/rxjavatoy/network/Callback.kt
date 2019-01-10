package tk.jonathancowling.rxjavatoy.network

interface Callback<T> {
    fun onSuccess(data: T)
    fun onError(e: Throwable?)
}