package tk.jonathancowling.rxjavatoy.business.events

interface LifecycleEventListener {
    fun onCreate()
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
}