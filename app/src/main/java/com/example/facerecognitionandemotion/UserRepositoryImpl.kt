package com.example.facerecognitionandemotion

import android.util.Log
import okhttp3.MultipartBody
import retrofit2.HttpException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userApi: UserApi) : Repository {

    override suspend fun getUserFaceResult(faceImage: MultipartBody.Part): Resource<UserFaceModel> {
        return try {
            val result = userApi.userFaceResult(faceImage)
            Log.v("success for the api", result.toString())
            Resource.Success(result)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
            Log.v("error for the api", errorBody)
            Resource.Error(errorBody)
        } catch (e: Exception) {
            Log.v("exception for the api", e.toString())
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
}
