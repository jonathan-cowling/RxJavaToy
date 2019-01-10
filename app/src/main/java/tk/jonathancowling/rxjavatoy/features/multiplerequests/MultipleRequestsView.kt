package tk.jonathancowling.rxjavatoy.features.multiplerequests

interface MultipleRequestsView {
    fun setString(string: String)
    fun setError(err: Throwable)
}
