package com.example.travellog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

class AddLocationActivity : UserInputActivity(), View.OnClickListener {

    private lateinit var backBtn: ImageButton
    private lateinit var addBtn: Button

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
                addRecord()
            }
        }
    }

    // Function to add records into the database
    private fun addRecord() {
        val titleField: TextView = findViewById(R.id.titleTextField)
        val continentField: TextView = findViewById(R.id.continentTextField)
        val countryField: TextView = findViewById(R.id.countryTextField)
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
        if(imagePath != null && recordTitle.isNotEmpty() && recordContinent.isNotEmpty() && recordCountry.isNotEmpty() && recordDate.isNotEmpty() && recordTime.isNotEmpty()) {
            val status = databaseHandler.addRecord(RecordModel(0, imagePath, recordTitle, recordContinent, recordCountry, recordDate, recordTime, recordAdditionalInfo))
            if(status > -1) {
                Toast.makeText(applicationContext, "Record Saved!", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Toast.makeText(applicationContext, "Required fields are not filled!", Toast.LENGTH_SHORT).show()
        }

    }

}

