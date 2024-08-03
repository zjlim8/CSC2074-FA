package com.example.travellog

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import java.io.File

class EditRecordActivity : UserInputActivity(), View.OnClickListener {

    private lateinit var record: RecordModel
    private lateinit var recordTitle: TextView
    private lateinit var recordContinent: TextView
    private lateinit var recordCountry: TextView
    private lateinit var recordAdditionalInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_record)

        // Initialising the elements in the activity
        imageField = findViewById(R.id.er_addImagePlaceholder)
        dateField = findViewById(R.id.er_visitDateTextField)
        timeField = findViewById(R.id.er_visitTimeTextField)
        recordTitle = findViewById(R.id.er_titleTextField)
        recordContinent = findViewById(R.id.er_continentTextField)
        recordCountry = findViewById(R.id.er_countryTextField)
        recordAdditionalInfo = findViewById(R.id.er_additionalInformationTextField)

        // Retrieving record from data sent over by previous activity
        record = (intent.getSerializableExtra("record") as? RecordModel)!!

        // Setting onClickListener for buttons
        val backBtn: ImageButton = findViewById(R.id.er_backBtn)
        val doneBtn: Button = findViewById(R.id.er_doneBtn)

        backBtn.setOnClickListener(this)
        doneBtn.setOnClickListener(this)
        imageField.setOnClickListener(this)
        dateField.setOnClickListener(this)
        timeField.setOnClickListener(this)

        val imageFile = File(record.imgPath)
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            imageField.setImageBitmap(bitmap)
        } else {
            imageField.setImageResource(R.drawable.addimageplaceholder)
        }

        imagePath = record.imgPath
        dateField.text = record.date
        timeField.text = record.time
        recordTitle.text = record.title
        recordContinent.text = record.continent
        recordCountry.text = record.country
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

        val editedTitle = recordTitle.text.toString()
        val editedContinent = recordContinent.text.toString()
        val editedCountry = recordCountry.text.toString()
        val editedDate = dateField.text.toString()
        val editedTime = timeField.text.toString()
        val editedAdditionalInfo = recordAdditionalInfo.text.toString()

        if(imagePath != null && editedTitle.isNotEmpty() && editedContinent.isNotEmpty() && editedCountry.isNotEmpty() && editedDate.isNotEmpty() && editedTime.isNotEmpty()) {
            record.imgPath = imagePath
            record.title = editedTitle
            record.continent = editedContinent
            record.country = editedCountry
            record.date = editedDate
            record.time = editedTime
            record.additionalInfo = editedAdditionalInfo

        val databaseHandler = DatabaseHandler(this)
        databaseHandler.updateRecord(record)
        } else {
            Toast.makeText(applicationContext, "Required fields are not filled!", Toast.LENGTH_SHORT).show()
        }


    }

}