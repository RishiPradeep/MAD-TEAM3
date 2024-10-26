package com.example.mad_app.ui.home

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.mad_app.databinding.FragmentHomeBinding
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var plantImageView: ImageView
    private lateinit var resultTextView: TextView
    private var selectedImageUri: Uri? = null
    private lateinit var tfliteModel: Interpreter

    private val labelNames = arrayOf(
        "Apple scab", "Apple Black rot", "Apple Cedar apple rust", "Apple healthy",
        "Cherry Powdery mildew", "Cherry healthy", "Corn Cercospora leaf spot Gray leaf spot",
        "Corn Common rust", "Corn Northern Leaf Blight", "Corn healthy",
        "Grape Black rot", "Grape Esca", "Grape Leaf blight", "Grape healthy",
        "Peach Bacterial spot", "Peach healthy", "Pepper bell Bacterial spot",
        "Pepper bell healthy", "Potato Early blight", "Potato Late blight",
        "Potato healthy", "Strawberry Leaf scorch", "Strawberry healthy",
        "Tomato Bacterial spot", "Tomato Early blight", "Tomato Late blight",
        "Tomato Leaf Mold", "Tomato Septoria leaf spot", "Tomato Spider mites",
        "Tomato Target Spot", "Tomato Yellow Leaf Curl Virus", "Tomato mosaic virus", "Tomato healthy"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        plantImageView = binding.plantImage
        resultTextView = binding.resultText

        try {
            tfliteModel = Interpreter(loadModelFile("Leaf_Deases_model.tflite"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Model loading failed", Toast.LENGTH_SHORT).show()
        }

        val uploadButton: Button = binding.buttonUpload
        uploadButton.setOnClickListener {
            openImagePicker()
        }

        val scanButton: Button = binding.buttonScan
        scanButton.setOnClickListener {
            selectedImageUri?.let { uri -> scanImage(uri) }
        }

        return root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            plantImageView.setImageURI(selectedImageUri)
        }
    }

    private fun scanImage(imageUri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
        val result = processImageWithModel(bitmap)
        resultTextView.text = result
    }

    private fun processImageWithModel(bitmap: Bitmap): String {
        val inputSize = 150 // Adjust this according to your model's expected input size
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        // Create a 4D input array: [1, height, width, channels]
        val inputArray = Array(1) { Array(inputSize) { Array(inputSize) { FloatArray(3) } } }

        // Normalize the image
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixel = resizedBitmap.getPixel(j, i)
                inputArray[0][i][j][0] = ((pixel shr 16 and 0xFF) / 255.0f) // R
                inputArray[0][i][j][1] = ((pixel shr 8 and 0xFF) / 255.0f) // G
                inputArray[0][i][j][2] = ((pixel and 0xFF) / 255.0f) // B
            }
        }

        // Prepare the output array
        val outputArray = Array(1) { FloatArray(labelNames.size) }

        // Run inference
        tfliteModel.run(inputArray, outputArray)

        // Get the result from the output array
        val predictedClassIndex = outputArray[0].indices.maxByOrNull { outputArray[0][it] } ?: -1
        val confidence = outputArray[0][predictedClassIndex] * 100

        return if (confidence >= 80) {
            "Detected Disease: ${labelNames[predictedClassIndex]} with confidence: ${"%.2f".format(confidence)}%"
        } else {
            "Try Another Image"
        }
    }

    private fun loadModelFile(modelFile: String): MappedByteBuffer {
        val fileDescriptor = requireActivity().assets.openFd(modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
