package com.example.travellog

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddLocationActivity : ComponentActivity(), View.OnClickListener  {

    private lateinit var imageField: ImageView
    private lateinit var backBtn: ImageButton
    private lateinit var addBtn: Button
    private lateinit var photoUri: Uri
    private lateinit var dateField: TextView
    private lateinit var timeField: TextView
    private val calendar = Calendar.getInstance()
//    private var selectedImage: ByteArray? = null
    private lateinit var imagePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_location)

        imageField = findViewById(R.id.addImagePlaceholder)
        backBtn = findViewById(R.id.backBtn)
        addBtn = findViewById(R.id.addBtn)
        dateField = findViewById(R.id.visitDateTextField)
        timeField = findViewById(R.id.visitTimeTextField)

        imageField.setOnClickListener(this)
        backBtn.setOnClickListener(this)
        addBtn.setOnClickListener(this)
        dateField.setOnClickListener(this)
        timeField.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.backBtn -> {
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.addImagePlaceholder -> {
                if(hasPermissions()) {
                    showImagePicker()
                } else {
                    requestPermissions()
                }
            }
            R.id.visitDateTextField -> {
                openDatePicker()
            }
            R.id.visitTimeTextField -> {
                openTimePicker()
            }
            R.id.addBtn -> {
                val intent: Intent = Intent(this, MainActivity::class.java)
                addRecord()
                startActivity(intent)
                finish()
            }
        }
    }

    // Function to show DatePicker
    private fun openDatePicker() {
        val datePickerDialog = DatePickerDialog(this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            dateField.text = formattedDate
        },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Function to show TimePicker
    private fun openTimePicker() {
        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = timeFormat.format(selectedTime.time)
            timeField.text = formattedTime
        },
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        )
        timePickerDialog.show()
    }

    // Function to show image picker
    private fun showImagePicker() {
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

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if(success) {
            Toast.makeText(this, "Photo captured successfully", Toast.LENGTH_SHORT).show()
            // Set the image URI to imageField
            val bitmap = uriToBitmap(photoUri)
            imageField.setImageURI(photoUri)
            imagePath = saveImageToInternalStorage(bitmap)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
            // Set the image URI to the ImageView
            val bitmap = uriToBitmap(it)
            imageField.setImageURI(it)
            imagePath = saveImageToInternalStorage(bitmap)
        }
    }

    private fun startCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "com.example.travellog.provider", photoFile)
        cameraLauncher.launch(photoUri)
    }

    private fun startGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_PERMISSIONS)
    }

    private fun hasPermissions(): Boolean {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
        return allGranted
    }

//    // Function to convert photo to ByteArray so that it can be stored in database
//    private fun uriToByteArray(uri: Uri): ByteArray {
//        val inputStream: InputStream? = contentResolver.openInputStream(uri)
//        //return inputStream?.readBytes() ?: ByteArray(0)
//        val bitmap = BitmapFactory.decodeStream(inputStream)
//        inputStream?.close()
//
//        val outputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//        return outputStream.toByteArray()
//    }

    // Function to convert photo to Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap {
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
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
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

    // Function to add records into the database
    private fun addRecord() {
        val titleField: TextView = findViewById(R.id.titleTextField)
        val continentField: TextView = findViewById(R.id.continentTextField)
        val countryField: TextView = findViewById(R.id.countryTextField)
        val dateField: TextView = findViewById(R.id.visitDateTextField)
        val timeField: TextView = findViewById(R.id.visitTimeTextField)
        val additionalInfoField: TextView = findViewById(R.id.additionalInformationTextField)
        val databaseHandler = DatabaseHandler(this)

        // Converting user input to String
        val recordTitle = titleField.text.toString()
        val recordContinent = continentField.text.toString()
        val recordCountry = countryField.text.toString()
        val recordDate = dateField.text.toString()
        val recordTime = timeField.text.toString()
        val recordAdditionalInfo = additionalInfoField.text.toString()

        // Checks if all fields are empty except for additionalInfo
        if(::imagePath.isInitialized && recordTitle.isNotEmpty() && recordContinent.isNotEmpty() && recordCountry.isNotEmpty() && recordDate.isNotEmpty() && recordTime.isNotEmpty()) {
            val status = databaseHandler.addRecord(RecordModel(0, imagePath, recordTitle, recordContinent, recordCountry, recordDate, recordTime, recordAdditionalInfo))
            if(status > -1) {
                Toast.makeText(applicationContext, "Record Saved!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "Required Fields are not Filled!", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}

