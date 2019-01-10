package tk.jonathancowling.rxjavatoy.features.launcher

import tk.jonathancowling.rxjavatoy.business.events.IntEventEmitter
import tk.jonathancowling.rxjavatoy.business.events.IntEventListener

class DemoLauncherPresenter(view: DemoLauncherView, emitterFactory: IntEventEmitter.Factory) {
    init {
        emitterFactory.create(object: IntEventListener {
            override fun onEvent(eventId: Int): Boolean {
                return when (eventId) {
                    DemoLauncherActivity.MULTIPLE_REQUESTS -> {
                        view.launchMultiple()
                        true
                    }
                    DemoLauncherActivity.ONE_AFTER_THE_OTHER -> {
                        view.launchOneAfterTheOther()
                        true
                    }
                    DemoLauncherActivity.REACTIVE_VS_CALLBACKS -> {
                        view.launchReactiveVsCallbacks()
                        true
                    }
                    else -> false
                }
            }
        })
    }
}