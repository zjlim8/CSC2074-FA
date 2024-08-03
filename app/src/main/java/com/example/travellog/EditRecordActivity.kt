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
import com.example.travellog.AddLocationActivity.Companion.REQUEST_CODE_PERMISSIONS
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class EditRecordActivity : ComponentActivity(), View.OnClickListener {

    private lateinit var record: RecordModel
    private lateinit var recordImage: ImageView
    private lateinit var recordTitle: TextView
    private lateinit var recordContinent: TextView
    private lateinit var recordCountry: TextView
    private lateinit var recordDate: TextView
    private lateinit var recordTime: TextView
    private lateinit var recordAdditionalInfo: TextView
    private val calendar = Calendar.getInstance()
    private lateinit var imagePath: String
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_record)

        // Initialising the elements in the activity
        recordImage = findViewById(R.id.er_addImagePlaceholder)
        recordTitle = findViewById(R.id.er_titleTextField)
        recordContinent = findViewById(R.id.er_countryTextField)
        recordCountry = findViewById(R.id.er_continentTextField)
        recordDate = findViewById(R.id.er_visitDateTextField)
        recordTime = findViewById(R.id.er_visitTimeTextField)
        recordAdditionalInfo = findViewById(R.id.er_additionalInformationTextField)

        // Retrieving record from data sent over by previous activity
        record = (intent.getSerializableExtra("record") as? RecordModel)!!

        // Setting onClickListener for buttons
        val backBtn: ImageButton = findViewById(R.id.er_backBtn)
        val doneBtn: Button = findViewById(R.id.er_doneBtn)

        backBtn.setOnClickListener(this)
        doneBtn.setOnClickListener(this)
        recordImage.setOnClickListener(this)
        recordDate.setOnClickListener(this)
        recordTime.setOnClickListener(this)

        val imageFile = File(record.imgPath)
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            recordImage.setImageBitmap(bitmap)
        } else {
            recordImage.setImageResource(R.drawable.addimageplaceholder)
        }
        recordTitle.text = record.title
        recordContinent.text = record.continent
        recordCountry.text = record.country
        recordDate.text = record.date
        recordTime.text = record.time
        recordAdditionalInfo.text = record.additionalInfo

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.er_backBtn -> {
                val intent: Intent = Intent(this, DetailedViewActivity::class.java)
                intent.putExtra("record", record)
                startActivity(intent)
            }
            R.id.er_doneBtn -> {
                updateRecord()
                val intent: Intent = Intent(this, DetailedViewActivity::class.java)
                intent.putExtra("record", record)
                startActivity(intent)
            }
            R.id.er_visitDateTextField -> {
                openDatePicker()
            }
            R.id.er_visitTimeTextField -> {
                openTimePicker()
            }
            R.id.er_addImagePlaceholder -> {
                if(hasPermissions()) {
                    showImagePicker()
                } else {
                    requestPermissions()
                }
            }

        }
    }

    // Function to update the selected record
    private fun updateRecord() {

        record.title = recordTitle.text.toString()
        record.continent = recordContinent.text.toString()
        record.country = recordCountry.text.toString()
        record.date = recordDate.text.toString()
        record.time = recordTime.text.toString()
        record.additionalInfo = recordAdditionalInfo.text.toString()


        val databaseHandler = DatabaseHandler(this)
        databaseHandler.updateRecord(record)

    }

    // Function to show DatePicker
    private fun openDatePicker() {
        val datePickerDialog = DatePickerDialog(this, {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, monthOfYear, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            recordDate.text = formattedDate
        },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    // Function to show TimePicker
    private fun openTimePicker() {
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
                recordTime.text = formattedTime
            }
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
            recordImage.setImageURI(photoUri)
            imagePath = saveImageToInternalStorage(bitmap)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
            // Set the image URI to the ImageView
            val bitmap = uriToBitmap(it)
            recordImage.setImageURI(it)
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

}