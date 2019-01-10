package tk.jonathancowling.rxjavatoy.business.events

interface IntEventListener {
    fun onEvent(eventId: Int): Boolean
}