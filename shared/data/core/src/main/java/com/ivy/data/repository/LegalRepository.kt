package com.ivy.data.repository

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.datasource.LocalLegalDataSource
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LegalRepository @Inject constructor(
    private val localLegalDataSource: LocalLegalDataSource,
    private val dispatchers: DispatchersProvider
) {
    suspend fun isDisclaimerAccepted(): Boolean = true

    suspend fun setDisclaimerAccepted(
        accepted: Boolean
    ): Unit = withContext(dispatchers.io) {
        localLegalDataSource.setDisclaimerAccepted(accepted)
    }
}