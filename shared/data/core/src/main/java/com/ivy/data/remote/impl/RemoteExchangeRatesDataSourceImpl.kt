package com.ivy.data.remote.impl

import arrow.core.Either
import arrow.core.left
import com.ivy.data.remote.RemoteExchangeRatesDataSource
import com.ivy.data.remote.responses.ExchangeRatesResponse
import io.ktor.client.HttpClient
import javax.inject.Inject

class RemoteExchangeRatesDataSourceImpl @Inject constructor(
    @Suppress("UnusedPrivateMember")
    private val ktorClient: dagger.Lazy<HttpClient>,
) : RemoteExchangeRatesDataSource {

    override suspend fun fetchEurExchangeRates(): Either<String, ExchangeRatesResponse> {
        // App is configured for 100% offline use.
        // Remote exchange rate syncing is disabled to guarantee zero internet calls.
        return "Offline mode: remote currency sync is disabled".left()
    }
}
