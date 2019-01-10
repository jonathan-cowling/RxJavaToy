package tk.jonathancowling.rxjavatoy.business.events

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

class AndroidLifecycleEventEmitter(private val listener: LifecycleEventListener) : LifecycleEventEmitter, LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    override fun emitCreate() = listener.onCreate()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun emitStart() = listener.onStart()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun emitResume() = listener.onResume()

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun emitPause() = listener.onPause()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun emitStop() = listener.onStop()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun emitDestroy() = listener.onDestroy()
}
