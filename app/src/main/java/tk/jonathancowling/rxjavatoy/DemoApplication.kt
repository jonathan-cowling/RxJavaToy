package tk.jonathancowling.rxjavatoy

import android.app.Application
import android.util.Log
import io.reactivex.plugins.RxJavaPlugins

class DemoApplication : Application() {
    init {
        RxJavaPlugins.setErrorHandler {
            Log.e(this::class.java.simpleName, "Uncaught RxJavaException\n$it")
        }
    }
}