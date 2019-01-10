package tk.jonathancowling.rxjavatoy.features.multiplerequests

import tk.jonathancowling.rxjavatoy.business.events.*
import tk.jonathancowling.rxjavatoy.service.RxDemoService
import io.reactivex.disposables.CompositeDisposable

class MultipleRequestsPresenter(view: MultipleRequestsView, button: IntEventEmitter.Factory, lifecycle: LifecycleEventEmitter.Factory, service: RxDemoService) {

    private val disposables = CompositeDisposable()

    init {
        button.create(object : IntEventListener {
            override fun onEvent(eventId: Int): Boolean {
                return if (eventId == MultipleRequestsActivity.GO_BUTTON) {
                    disposables.add(service.getDocumentsAndProfile().subscribe(view::setString, view::setError))
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