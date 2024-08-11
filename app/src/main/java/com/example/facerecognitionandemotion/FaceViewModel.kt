package com.example.facerecognitionandemotion

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

@HiltViewModel
class FaceViewModel @Inject constructor(private val apiRepository: Repository) : ViewModel() {

    val faceResult = MutableLiveData<Resource<UserFaceModel>>()

    fun getData(faceImage: MultipartBody.Part) {
        viewModelScope.launch {
            faceResult.postValue(Resource.Loading(true))
            val response = apiRepository.getUserFaceResult(faceImage)

            when (response) {
                is Resource.Success -> {
                    faceResult.postValue(Resource.Success(response.data))
                    Log.v("result for the face", response.data.toString() ?: "")
                }
                is Resource.Error -> {
                    faceResult.postValue(Resource.Error(response.message))
                    Log.v("result for the face", response.message ?: "")
                }
                is Resource.Loading -> {
                    faceResult.postValue(Resource.Loading(true))
                }
            }
        }
    }
}
