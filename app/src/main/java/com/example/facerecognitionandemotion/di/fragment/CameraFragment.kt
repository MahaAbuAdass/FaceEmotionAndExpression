package com.example.facerecognitionandemotion.di.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.facerecognitionandemotion.FaceViewModel
import com.example.facerecognitionandemotion.Resource
import com.example.facerecognitionandemotion.databinding.CameraBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: CameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private lateinit var faceViewModel: FaceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        faceViewModel = ViewModelProvider(this).get(FaceViewModel::class.java)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.btnCapture.setOnClickListener { takePhoto() }
        binding.btnSwitchCamera.setOnClickListener { switchCamera() }
        binding.resetBtn.setOnClickListener {
            binding.result.text = "Click the submit button to analyze your expression."
        }

        observeViewModel()
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        faceViewModel.faceResult.observe(viewLifecycleOwner) { faceData ->
            when (faceData) {
                is Resource.Success -> {
                    binding.result.text = "${faceData.data?.emotion}\n${faceData.data?.name}\n${faceData.data?.time}"
                    Log.v("result for the face", "Success response: ${faceData.data.toString()}")
                }
                is Resource.Error -> {
                    binding.result.text = "Error: ${faceData.message}"
                    Log.v("result for the face", "Error response: ${faceData.message}")
                }
                is Resource.Loading -> {
                    Log.v("result for the face", "Loading response")
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Create Preview use case
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            // Create ImageCapture use case
            imageCapture = ImageCapture.Builder().build()

            try {
                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera() // Restart the camera with the new selector
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(requireContext().filesDir, "photo.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                sendImageToApi(photoFile)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraFragment", "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    private fun sendImageToApi(imageFile: File) {
        // Compress the image
        val compressedFile = compressImage(imageFile)

        // Convert File to RequestBody with the correct media type
        val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaType())

        // Create MultipartBody.Part with name "image"
        val body = MultipartBody.Part.createFormData("image", compressedFile.name, requestFile)

        // Pass MultipartBody.Part to ViewModel
        faceViewModel.getData(body)
    }

    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val outStream = ByteArrayOutputStream()

        // Compress the image to 80% of the original quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream)

        // Write the compressed data to a new file
        val compressedFile = File(requireContext().cacheDir, "compressed_photo.jpg")
        compressedFile.writeBytes(outStream.toByteArray())

        return compressedFile
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}
