package tk.jonathancowling.rxjavatoy.business.events

interface LifecycleEventEmitter {
    fun emitCreate()
    fun emitStart()
    fun emitResume()
    fun emitPause()
    fun emitStop()
    fun emitDestroy()
    interface Factory {
        fun create(listener: LifecycleEventListener): LifecycleEventEmitter
    }
}