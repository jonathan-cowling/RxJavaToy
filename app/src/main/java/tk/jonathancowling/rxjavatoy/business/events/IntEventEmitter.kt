package tk.jonathancowling.rxjavatoy.business.events

interface IntEventEmitter {
    fun emitEvent(eventId: Int): Boolean

    interface Factory {
        fun create(listener: IntEventListener): IntEventEmitter
    }
}