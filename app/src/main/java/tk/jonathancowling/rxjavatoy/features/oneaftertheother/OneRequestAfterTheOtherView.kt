package tk.jonathancowling.rxjavatoy.features.oneaftertheother

interface OneRequestAfterTheOtherView {
    fun setString(string: String)
    fun setError(throwable: Throwable?)
}
