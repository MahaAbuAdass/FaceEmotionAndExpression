package com.example.facerecognitionandemotion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class FaceViewModel @Inject constructor(private val apiRepository: Repository) : ViewModel() {

    val faceResult = MutableLiveData<Resource<UserFaceModel>>()

    private val _userData = MutableLiveData<UserFaceModel>()
    val userData : MutableLiveData<UserFaceModel>  = _userData


   fun getData(faceImage: File){
       viewModelScope.launch{
           val response = apiRepository.getUserFaceResult(faceImage)

           faceResult.postValue(Resource.Loading(true))

           when(response){
               is Resource.Success ->{
                   faceResult.postValue(Resource.Success(response.data))
               }
               is Resource.Error -> {
                   faceResult.postValue((Resource.Error(response.message)))
               }
               else ->{
                   faceResult.postValue(Resource.Loading(false))
               }

           }
       }
   }

}