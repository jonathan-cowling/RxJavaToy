package tk.jonathancowling.rxjavatoy.service

import tk.jonathancowling.rxjavatoy.network.DemoApi
import tk.jonathancowling.rxjavatoy.network.NetworkConfig
import tk.jonathancowling.rxjavatoy.service.mapper.ProfileMapper
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleSource
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import retrofit2.adapter.rxjava2.Result

class RxDemoService(private val config: NetworkConfig,
                    private val profileMapper: ProfileMapper) {

    private val api = config.retrofitInstance().create(DemoApi::class.java)
    private val cache = config.cache

    fun getDocumentsThenProfile(): Single<String> =  api.getDocuments()
                .compose(config.configureRequest())
                .map { config.extractResult(it) }
                .flatMap { docs -> SingleSource<String> { downstream ->
                        api.getProfile()
                                .compose(config.configureRequest())
                                .map{config.extractResult(it)}
                                .map(profileMapper::apply)
                                .subscribe({ profile ->
                                    downstream.onSuccess("%s | %d".format(docs, profile))
                                }, {
                                    downstream.onError(it)
                                })
                } }
                .compose(config.toMainThread())

    fun getDocumentsAndProfile(): Single<String> {
        // NOTE: making 2 requests may result in 2 errors (by design after zip emits one error it is closed)
        // normally this would crash the app, but the application calls RxJavaPlugins.setErrorHandler
        // see: https://medium.com/@bherbst/the-rxjava2-default-error-handler-e50e0cab6f9f
        val docsResponse = api.getDocuments()
            .compose(config.configureRequest())
            .map(config::extractResult)

        return api.getProfile()
            .compose(config.configureRequest())
            .map(config::extractResult)
            .map(profileMapper::apply)
            .zipWith(docsResponse, BiFunction<Int, String, String> { profile, docs ->
                "%s | %d".format(docs, profile)
            })
            .compose(config.toMainThread())
    }

    fun getDocuments(): Single<String> = api.getDocuments()
        .compose(config.configureRequest())
        .map {config.extractResult(it) }
        .compose(config.toMainThread())

    fun getProfile() = api.getProfile()
        .compose(config.configureRequest())
        .map { config.extractResult(it) }
        .map {
            profileMapper.apply(it)
        }
        .compose(config.toMainThread())

    fun requestWithCache(): Single<String> {

        val getProfile : SingleSource<Result<String>> = api.getProfile().compose(config.configureRequest())

        return cache.getDocuments()
            // cache, falling back to network
            .lift<Result<String>> {
                OnErrorReturnSingle(it, api.getDocuments().compose(config.configureRequest()))
             }
            // cache vs network race
            .map(config::extractResult)
            .doOnSuccess { config.cache.setDocuments(it) }
            .flatMap { docs ->
                Single.amb(listOf(cache.getProfile().lift { OnErrorReturnSingle(it, getProfile) },
                                  getProfile))
                    .map(config::extractResult)
                    .doOnSuccess { config.cache.setProfile(it) }
                    .map(profileMapper::apply)
                    .map { profile -> "%s | %d".format(docs, profile) }
            }
    }

    class OnErrorReturnSingle<T>(private val observer: SingleObserver<in T>, private val recoverWith: SingleSource<T>) :  SingleObserver<T> {
        override fun onSuccess(t: T) {
            observer.onSuccess(t)
        }

        override fun onSubscribe(d: Disposable) {}

        override fun onError(e: Throwable) {
            recoverWith.subscribe(observer)
        }
    }

}