package tk.jonathancowling.rxjavatoy.service

import tk.jonathancowling.rxjavatoy.network.Callback
import tk.jonathancowling.rxjavatoy.network.DemoApi
import tk.jonathancowling.rxjavatoy.network.NetworkConfig
import tk.jonathancowling.rxjavatoy.service.mapper.ProfileMapper

class CallbackDemoService(private val config: NetworkConfig, private val profileMapper: ProfileMapper){

    private val api = config.retrofitInstance().create(DemoApi::class.java)

    private val cache = config.cache

    fun getDocumentsCallback(next: Callback<String>) = api.getDocumentsCallback()
        .enqueue(config.extractResult(next))

    fun getProfileCallback(next: Callback<Int>) {

        val stringToInt = object : Callback<String> {

            override fun onSuccess(data: String) {

                next.onSuccess(profileMapper.apply(data))
            }

            override fun onError(e: Throwable?) {

                next.onError(e)
            }
        }

        api.getProfileCallback()
            .enqueue(config.extractResult(stringToInt))
    }

    fun requestWithCache(next: Callback<String>) {

        val fastestProfile = object : Callback<Array<String>> {

            var timesOnSuccessInvoked = 0
            var timesOnErrorInvoked = 0
            val allowedErrors = 1
            val allowedSuccesses = 1

            override fun onSuccess(data: Array<String>) {
                if (timesOnSuccessInvoked < allowedSuccesses) {

                    cache.setProfile(data[1])

                    next.onSuccess("%s | %d".format(data[0], profileMapper.apply(data[1])))
                }

                timesOnSuccessInvoked++
            }

            override fun onError(e: Throwable?) {

                if (timesOnErrorInvoked >= allowedErrors) {

                    next.onError(e)
                }

                timesOnErrorInvoked++
            }
        }

        val launchFastestProfile = object : Callback<String> {
            override fun onSuccess(data: String) {

                cache.getProfileCallback().enqueue(config.extractResult(createProfileCallback(data, fastestProfile)))
                api.getProfileCallback().enqueue(config.extractResult(createProfileCallback(data, fastestProfile)))
            }

            override fun onError(e: Throwable?) {
                next.onError(e)
            }

        }

        val fallbackToApi = object : Callback<String> {
            override fun onSuccess(data: String) {

                cache.setDocuments(data)
                launchFastestProfile.onSuccess(data)
            }

            override fun onError(e: Throwable?) {

                api.getDocumentsCallback()
                    .enqueue(config.extractResult(launchFastestProfile))
            }
        }

        cache.getDocumentsCallback().enqueue(config.extractResult(fallbackToApi))
    }

    private fun createProfileCallback(docsData: String, next: Callback<Array<String>>) = object : Callback<String> {
            override fun onSuccess(data: String) {
                next.onSuccess(Array(2 ) {
                    when (it){
                        0 -> docsData
                        1 -> data
                        else -> throw IndexOutOfBoundsException()
                    }
                })
            }

            override fun onError(e: Throwable?) {
                next.onError(e)
            }
        }

}