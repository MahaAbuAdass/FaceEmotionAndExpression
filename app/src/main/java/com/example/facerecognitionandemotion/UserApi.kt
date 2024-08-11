package com.example.facerecognitionandemotion

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi  {
    @Multipart

    @POST("/recognize")
    suspend fun userFaceResult(
        @Part  image: MultipartBody.Part
    ):UserFaceModel



    companion object{
        const val BASE_URL = "https://0ef0-109-237-194-215.ngrok-free.app"


    }

}