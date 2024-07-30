package com.example.travellog

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import java.io.File

class AddLocationActivity : ComponentActivity(), View.OnClickListener  {

    private lateinit var imageField: ImageView
    private lateinit var backBtn: ImageButton
    private lateinit var addBtn: Button
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_location)

        imageField = findViewById(R.id.addImagePlaceholder)
        backBtn = findViewById(R.id.backBtn)
        addBtn = findViewById(R.id.addBtn)

        imageField.setOnClickListener(this)
        backBtn.setOnClickListener(this)
        addBtn.setOnClickListener(this)

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
            R.id.addBtn -> {
                val intent: Intent = Intent(this, MainActivity::class.java)
                //addRecord()
                startActivity(intent)
                finish()
            }
        }
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
            imageField.setImageURI(photoUri)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
            // Set the image URI to the ImageView
            imageField.setImageURI(it)
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

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permissions Required")
            .setMessage("Camera permissions are required to take photos. Please grant the permissions.")
            .setPositiveButton("OK") { dialog, _ ->
                requestPermissions()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if(hasPermissions()) {
//                showImagePicker()
//            } else {
//                Toast.makeText(this, "Permission not granted.", Toast.LENGTH_SHORT).show()
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                    showPermissionRationaleDialog()
//                } else {
//                    Toast.makeText(this, "Permissions were denied permanently.", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }

    // Function to add records into the database
    private fun addRecord(view: View) {
        //val imageField: ImageView = findViewById(R.id.addImagePlaceholder)
        val titleField: TextView = findViewById(R.id.titleTextField)
        val continentField: TextView = findViewById(R.id.continentTextField)
        val countryField: TextView = findViewById(R.id.countryTextField)
        val dateField: TextView = findViewById(R.id.visitDateTextField)
        val timeField: TextView = findViewById(R.id.visitTimeTextField)
        val additionalInfoField: TextView = findViewById(R.id.additionalInformationTextField)
        val databaseHandler = DatabaseHandler(this)

        // Converting user input to String
        // imageField conversion here
        val recordTitle = titleField.text.toString()
        val recordContinent = continentField.text.toString()
        val recordCountry = countryField.text.toString()
        val recordDate = dateField.text.toString()
        val recordTime = timeField.text.toString()
        val recordAdditionalInfo = additionalInfoField.text.toString()

        // Checks if all fields are empty except for additionalInfo
        if(/* image missing */recordTitle.isNotEmpty() && recordContinent.isNotEmpty() && recordCountry.isNotEmpty() && recordDate.isNotEmpty() && recordTime.isNotEmpty()) {
            val status = databaseHandler.addRecord(RecordModel(0, 0, recordTitle, recordContinent, recordCountry, recordDate, recordTime, recordAdditionalInfo))
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

