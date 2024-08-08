package com.example.facerecognitionandemotion

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface UserApi  {
    @Multipart

    @POST("/recognize")
    suspend fun userFaceResult(
        @Part image: MultipartBody.Part
    ):UserFaceModel



    companion object{
        const val BASE_URL = "https://5543-109-237-194-215.ngrok-free.app"
    }



}