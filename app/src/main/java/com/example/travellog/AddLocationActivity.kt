package com.example.travellog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class AddLocationActivity : ComponentActivity(), View.OnClickListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_location)

        val imageField: ImageView = findViewById(R.id.addImagePlaceholder)
        val backBtn: ImageButton = findViewById(R.id.backBtn)
        val addBtn: Button = findViewById(R.id.addBtn)

        imageField.setOnClickListener(this)
        backBtn.setOnClickListener(this)
        addBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.addImagePlaceholder -> {
                val intent: Intent = Intent(this, CapturePhotoActivity::class.java)
                startActivity(intent)
            }
            R.id.backBtn -> {
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.addBtn -> {
                val intent: Intent = Intent(this, MainActivity::class.java)
                //addRecord()
                startActivity(intent)
                finish()
            }
        }
    }

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
}

