package tk.jonathancowling.rxjavatoy.features.oneaftertheother

import tk.jonathancowling.rxjavatoy.business.events.IntEventEmitter
import tk.jonathancowling.rxjavatoy.business.events.IntEventListener
import tk.jonathancowling.rxjavatoy.business.events.LifecycleEventEmitter
import tk.jonathancowling.rxjavatoy.business.events.SimpleLifecycleListener
import tk.jonathancowling.rxjavatoy.service.RxDemoService
import io.reactivex.disposables.CompositeDisposable

class OneRequestAfterTheOtherPresenter(view: OneRequestAfterTheOtherView, button: IntEventEmitter.Factory, lifecycle: LifecycleEventEmitter.Factory, service: RxDemoService) {

    private val disposables = CompositeDisposable()

    init {
        button.create(object : IntEventListener {
            override fun onEvent(eventId: Int): Boolean {
                return if (eventId == OneRequestAfterTheOtherActivity.GO_BUTTON) {
                    disposables.add(service.getDocumentsThenProfile()
                            .subscribe(view::setString, view::setError))
                    true
                } else {
                    false
                }
            }
        })

        lifecycle.create(object: SimpleLifecycleListener(){
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