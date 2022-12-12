package com.nasportfolio.network.mappers

import com.nasportfolio.network.models.TransformedResponse
import com.nasportfolio.network.utils.toJson
import okhttp3.Response

suspend fun Response.toTransformedResponse() = TransformedResponse(
    json = body?.toJson(),
    responseCode = code
)