package com.example.facerecognitionandemotion

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userApi: UserApi) : Repository {
    override suspend fun getUserFaceResult(faceImage: File): Resource<UserFaceModel> {
        return try {
            val requestFile = faceImage.asRequestBody("image/jpeg".toMediaType())
            val body = MultipartBody.Part.createFormData("file", faceImage.name, requestFile)
            val result = userApi.userFaceResult(body)
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message)
        }
    }
}



