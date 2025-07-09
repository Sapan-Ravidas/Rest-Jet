package com.sapan.restapp.services

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun get(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Response<ResponseBody>

    @POST
    suspend fun post(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Body body: RequestBody?
    ): Response<ResponseBody>

    @PUT
    suspend fun put(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Body body: RequestBody?
    ): Response<ResponseBody>

    @DELETE
    suspend fun delete(
        @Url url: String,
        @HeaderMap headers: Map<String, String>
    ): Response<ResponseBody>

    @PATCH
    suspend fun patch(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Body body: RequestBody?
    ): Response<ResponseBody>

    @Multipart
    @POST
    suspend fun uploadFile(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Part file: MultipartBody.Part
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST
    suspend fun postForm(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @FieldMap fields: Map<String, String>
    ): Response<ResponseBody>
}