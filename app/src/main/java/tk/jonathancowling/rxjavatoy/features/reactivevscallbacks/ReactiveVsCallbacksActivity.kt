package tk.jonathancowling.rxjavatoy.features.reactivevscallbacks

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import tk.jonathancowling.rxjavatoy.R
import tk.jonathancowling.rxjavatoy.business.events.*
import tk.jonathancowling.rxjavatoy.network.NetworkConfig
import tk.jonathancowling.rxjavatoy.service.CallbackDemoService
import tk.jonathancowling.rxjavatoy.service.RxDemoService
import tk.jonathancowling.rxjavatoy.service.mapper.ProfileMapper
import kotlinx.android.synthetic.main.activity_reactive_vs_callbacks.*

class ReactiveVsCallbacksActivity : AppCompatActivity(), ReactiveVsCallbacksView {
    override fun setReactiveString(string: String) {
        reactive_vs_callbacks_reactive_text.text = string
    }

    override fun setCallbackString(string: String) {
        reactive_vs_callbacks_callback_text.text = string
    }

    override fun setReactiveError(throwable: Throwable?) {
        reactive_vs_callbacks_reactive_text.text = getString(R.string.error_msg)
    }

    override fun setCallbackError(throwable: Throwable?) {
        reactive_vs_callbacks_callback_text.text = getString(R.string.error_msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reactive_vs_callbacks)
        title = "ReactiveVsCallbacks"

        val buttonEmitter = object : IntEventEmitter.Factory {
            override fun create(listener: IntEventListener): IntEventEmitter {
                val emitter = ViewOnClickEmitter(listener)
                reactive_vs_callbacks_reactive_button.setOnClickListener(emitter)
                reactive_vs_callbacks_callback_button.setOnClickListener(emitter)
                return emitter
            }
        }

        val lifecycleEmitter = object : LifecycleEventEmitter.Factory {
            override fun create(listener: LifecycleEventListener): LifecycleEventEmitter = AndroidLifecycleEventEmitter(listener)
        }

        val config = NetworkConfig()
        val profileMapper = ProfileMapper()
        ReactiveVsCallbacksPresenter(this,
            buttonEmitter,
            lifecycleEmitter,
            RxDemoService(config, profileMapper),
            CallbackDemoService(config, profileMapper))
    }

    companion object {
        const val GO_REACTIVE_BUTTON = R.id.reactive_vs_callbacks_reactive_button
        const val GO_CALLBACK_BUTTON = R.id.reactive_vs_callbacks_callback_button
    }
}
