package tk.jonathancowling.rxjavatoy.business.events

import android.view.View

class ViewOnClickEmitter(private val listener: IntEventListener): View.OnClickListener, IntEventEmitter {
    override fun onClick(v: View?) {
        emitEvent(v?.id ?: -1)
    }

    override fun emitEvent(eventId: Int): Boolean {
        return listener.onEvent(eventId)
    }

}