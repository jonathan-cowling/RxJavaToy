package tk.jonathancowling.rxjavatoy.features.oneaftertheother

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import tk.jonathancowling.rxjavatoy.R
import tk.jonathancowling.rxjavatoy.business.events.*
import tk.jonathancowling.rxjavatoy.network.NetworkConfig
import tk.jonathancowling.rxjavatoy.service.RxDemoService
import tk.jonathancowling.rxjavatoy.service.mapper.ProfileMapper
import kotlinx.android.synthetic.main.activity_one_request_after_the_other.*

class OneRequestAfterTheOtherActivity : AppCompatActivity(), OneRequestAfterTheOtherView {

    override fun setString(string: String) {
        one_after_the_other_result.text = string
    }

    override fun setError(throwable: Throwable?) {
        one_after_the_other_result.text = getString(R.string.error_msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_request_after_the_other)
        title = "OneAfterTheOther"

        val buttonEmitter = object : IntEventEmitter.Factory {
            override fun create(listener: IntEventListener): IntEventEmitter {
                val emitter = ViewOnClickEmitter(listener)
                one_after_the_other_button.setOnClickListener(emitter)
                return emitter
            }
        }

        val lifecycleEmitter = object : LifecycleEventEmitter.Factory {
            override fun create(listener: LifecycleEventListener): LifecycleEventEmitter = AndroidLifecycleEventEmitter(listener)
        }

        OneRequestAfterTheOtherPresenter(this, buttonEmitter, lifecycleEmitter, RxDemoService(NetworkConfig(), ProfileMapper()))
    }

    companion object {
        const val GO_BUTTON = R.id.one_after_the_other_button
    }
}
