package tk.jonathancowling.rxjavatoy.features.reactivevscallbacks

import tk.jonathancowling.rxjavatoy.business.events.*
import tk.jonathancowling.rxjavatoy.network.Callback
import tk.jonathancowling.rxjavatoy.service.CallbackDemoService
import tk.jonathancowling.rxjavatoy.service.RxDemoService
import io.reactivex.disposables.CompositeDisposable

class ReactiveVsCallbacksPresenter(view: ReactiveVsCallbacksView,
                                   button: IntEventEmitter.Factory,
                                   lifecycle: LifecycleEventEmitter.Factory,
                                   rxService: RxDemoService,
                                   cbService: CallbackDemoService) {

    val disposables = CompositeDisposable()

    init {
        button.create(object : IntEventListener {
            override fun onEvent(eventId: Int): Boolean {
                return when (eventId) {
                    ReactiveVsCallbacksActivity.GO_REACTIVE_BUTTON -> {
                        rxService.requestWithCache().subscribe(view::setReactiveString, view::setReactiveError)
                        true
                    }
                    ReactiveVsCallbacksActivity.GO_CALLBACK_BUTTON  -> {
                        cbService.requestWithCache(object : Callback<String>{
                            override fun onSuccess(data: String) {
                                view.setCallbackString(data)
                            }

                            override fun onError(e: Throwable?) {
                                view.setCallbackError(e)
                            }
                        })
                        true
                    }
                    else -> false
                }
            }
        })

        lifecycle.create(object : SimpleLifecycleListener() {
            override fun onPause() {
                super.onPause()
                disposables.clear()
            }

            override fun onDestroy() {
                super.onDestroy()
                disposables.dispose()
            }
        })
    }
}
