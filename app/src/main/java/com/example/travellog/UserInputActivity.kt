package com.example.travellog

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar
import java.util.Locale
import java.util.UUID

abstract class UserInputActivity : ComponentActivity() {

    protected lateinit var imageField: ImageView
    protected lateinit var dateField: TextView
    protected lateinit var timeField: TextView
    protected val calendar = Calendar.getInstance()
    protected lateinit var photoUri: Uri
    lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }

    // Function to show DatePicker
    protected fun openDatePicker() {
        val datePickerDialog = DatePickerDialog(this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            dateField.text = formattedDate
        },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    // Function to show TimePicker
    protected fun openTimePicker() {
        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val selectedTime = Calendar.getInstance()
            val currentTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            if (selectedTime.timeInMillis > currentTime.timeInMillis) {
                Toast.makeText(this, "Selected time cannot be in the future", Toast.LENGTH_SHORT).show()
            } else {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = timeFormat.format(selectedTime.time)
                timeField.text = formattedTime
            }
        },
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        )
        timePickerDialog.show()
    }

    // Function to show image picker
    protected fun showImagePicker() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Select Option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> startCamera()
                1 -> startGallery()
            }
        }
        builder.show()
    }

    protected val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if(success) {
            Toast.makeText(this, "Photo captured successfully", Toast.LENGTH_SHORT).show()
            // Set the image URI to imageField
            val bitmap = uriToBitmap(photoUri)
            imageField.setImageURI(photoUri)
            imagePath = saveImageToInternalStorage(bitmap)
        }
    }

    protected val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
            // Set the image URI to the ImageView
            val bitmap = uriToBitmap(it)
            imageField.setImageURI(it)
            imagePath = saveImageToInternalStorage(bitmap)
        }
    }

    protected fun startCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "com.example.travellog.provider", photoFile)
        cameraLauncher.launch(photoUri)
    }

    protected fun startGallery() {
        galleryLauncher.launch("image/*")
    }

    protected fun createImageFile(): File {
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir)
    }

    protected fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
    }

    protected fun hasPermissions(): Boolean {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
        return allGranted
    }

    // Function to convert photo to Bitmap
    protected fun uriToBitmap(uri: Uri): Bitmap {
        return when {
            uri.scheme.equals("content") -> {
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            }
            else -> {
                BitmapFactory.decodeFile(uri.path)
            }
        }
    }

    // Function to save file to storage so file size is not limited
    protected fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    companion object {
        protected const val REQUEST_CODE_PERMISSIONS = 10
    }

}