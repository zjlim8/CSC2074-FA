package com.example.travellog

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import java.io.File

class DetailedViewActivity : ComponentActivity(), View.OnClickListener   {

    private lateinit var backBtn: ImageButton
    private lateinit var editBtn: ImageButton
    private lateinit var deleteBtn: ImageButton
    private lateinit var record: RecordModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed_view)

        backBtn = findViewById(R.id.dv_backBtn)
        editBtn = findViewById(R.id.dv_editBtn)
        deleteBtn = findViewById(R.id.dv_deleteBtn)

        backBtn.setOnClickListener(this)
        editBtn.setOnClickListener(this)
        deleteBtn.setOnClickListener(this)

        record = (intent.getSerializableExtra("record") as? RecordModel)!!

        val recordImage: ImageView = findViewById(R.id.recordImg)
        val recordTitle: TextView = findViewById(R.id.recordTitle)
        val recordContinent: TextView = findViewById(R.id.recordCountry)
        val recordCountry: TextView = findViewById(R.id.recordContinent)
        val recordDate: TextView = findViewById(R.id.recordDate)
        val recordTime: TextView = findViewById(R.id.recordTime)
        val recordAdditionalInfo: TextView = findViewById(R.id.recordAdditionalInfo)

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
            R.id.dv_backBtn -> {
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.dv_editBtn -> {
                val intent: Intent = Intent(this, EditRecordActivity::class.java)
                intent.putExtra("record", record)
                startActivity(intent)
            }
            R.id.dv_deleteBtn -> {
                deleteRecord()
            }
        }
    }

    private fun deleteRecord() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this record?").setCancelable(false).
        setPositiveButton("Yes") { dialog, id ->
            val databaseHandler = DatabaseHandler(this)
            databaseHandler.deleteRecord(record)
            val intent: Intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        .setNegativeButton("No") { dialog, id ->
            dialog.dismiss()
        }
        val confirmation: AlertDialog = builder.create()
        confirmation.show()
    }

}