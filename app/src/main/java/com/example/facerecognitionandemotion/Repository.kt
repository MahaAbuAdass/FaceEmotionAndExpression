package com.example.facerecognitionandemotion

import okhttp3.MultipartBody
import java.io.File

interface Repository {
    suspend fun getUserFaceResult(faceImage: MultipartBody.Part): Resource<UserFaceModel>
}
