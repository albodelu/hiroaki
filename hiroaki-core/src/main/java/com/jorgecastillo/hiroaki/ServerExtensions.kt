package com.jorgecastillo.hiroaki

import com.jorgecastillo.hiroaki.dispatcher.DispatcherRetainer
import com.jorgecastillo.hiroaki.matchers.matches
import com.jorgecastillo.hiroaki.models.Body
import com.jorgecastillo.hiroaki.models.PotentialRequestChain
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matcher
import retrofit2.Converter
import retrofit2.Retrofit

enum class Method {
    DELETE, GET, HEAD, POST, OPTIONS, PATCH, PUT, CONNECT, TRACE
}

private fun okHttpClient(
    loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY
): OkHttpClient =
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(loggingLevel))
                .build()

fun <T> MockWebServer.retrofitService(
    serviceClass: Class<T>,
    converterFactory: Converter.Factory,
    okHttpClient: OkHttpClient = okHttpClient()
): T {
    return Retrofit.Builder().baseUrl(this.url("/").toString())
            .client(okHttpClient)
            .addConverterFactory(converterFactory).build()
            .create(serviceClass)
}

fun MockWebServer.whenever(
    sentToPath: String,
    queryParams: QueryParams? = null,
    jsonBody: Body? = null,
    headers: Headers? = null,
    method: Method? = null
): PotentialRequestChain {
    this.setDispatcher(DispatcherRetainer.hiroakiDispatcher)
    return PotentialRequestChain(matches(
            sentToPath = sentToPath,
            method = method,
            queryParams = queryParams,
            jsonBody = jsonBody,
            headers = headers))
}

fun MockWebServer.whenever(method: Method, sentToPath: String): PotentialRequestChain {
    this.setDispatcher(DispatcherRetainer.hiroakiDispatcher)
    return PotentialRequestChain(matches(
            method = method,
            sentToPath = sentToPath))
}

fun MockWebServer.whenever(method: Method, sentToPath: String, params: QueryParams): PotentialRequestChain {
    this.setDispatcher(DispatcherRetainer.hiroakiDispatcher)
    return PotentialRequestChain(matches(
            method = method,
            sentToPath = sentToPath,
            queryParams = params))
}

fun MockWebServer.whenever(method: Method, sentToPath: String, jsonBody: Body): PotentialRequestChain {
    this.setDispatcher(DispatcherRetainer.hiroakiDispatcher)
    return PotentialRequestChain(matches(
            method = method,
            sentToPath = sentToPath,
            jsonBody = jsonBody))
}

fun MockWebServer.whenever(matcher: Matcher<RecordedRequest>): PotentialRequestChain {
    this.setDispatcher(DispatcherRetainer.hiroakiDispatcher)
    return PotentialRequestChain(matcher)
}
