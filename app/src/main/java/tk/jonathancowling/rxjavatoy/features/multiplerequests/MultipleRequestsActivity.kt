package tk.jonathancowling.rxjavatoy.features.multiplerequests

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import tk.jonathancowling.rxjavatoy.R
import tk.jonathancowling.rxjavatoy.business.events.*
import tk.jonathancowling.rxjavatoy.network.NetworkConfig
import tk.jonathancowling.rxjavatoy.service.RxDemoService
import tk.jonathancowling.rxjavatoy.service.mapper.ProfileMapper
import kotlinx.android.synthetic.main.activity_multiple_requests.*

class MultipleRequestsActivity : AppCompatActivity(), MultipleRequestsView {
    override fun setString(string: String) {
        multiple_requests_result.text = string
    }

    override fun setError(err: Throwable) {
        multiple_requests_result.text = getString(R.string.error_msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_requests)
        title = "MultipleRequests"

        val buttonEmitter = object : IntEventEmitter.Factory {
            override fun create(listener: IntEventListener): IntEventEmitter {
                val emitter = ViewOnClickEmitter(listener)
                multiple_requests_button.setOnClickListener(emitter)
                return emitter
            }
        }

        val lifecycleEmitter = object : LifecycleEventEmitter.Factory {
            override fun create(listener: LifecycleEventListener): LifecycleEventEmitter = AndroidLifecycleEventEmitter(listener)
        }

        MultipleRequestsPresenter(this, buttonEmitter, lifecycleEmitter, RxDemoService(NetworkConfig(), ProfileMapper()))
    }

    companion object {
        const val GO_BUTTON = R.id.multiple_requests_button
    }
}
