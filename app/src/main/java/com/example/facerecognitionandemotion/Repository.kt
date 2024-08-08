package com.example.facerecognitionandemotion

import java.io.File

interface Repository  {
    suspend fun getUserFaceResult(faceImage : File): Resource<UserFaceModel>

}