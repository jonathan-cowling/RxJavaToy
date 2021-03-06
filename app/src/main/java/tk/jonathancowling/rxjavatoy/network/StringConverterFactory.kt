package tk.jonathancowling.rxjavatoy.network

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class StringConverterFactory: Converter.Factory() {
    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        if (String::class.java != type) {
            return null
        }

        return Converter<ResponseBody, String> { value -> value.string() }
    }
}