package tk.jonathancowling.rxjavatoy.business.events

abstract class SimpleLifecycleListener : LifecycleEventListener {
    override fun onCreate() {}

    override fun onStart() {}

    override fun onResume() {}

    override fun onPause() {}

    override fun onStop() {}

    override fun onDestroy() {}
}