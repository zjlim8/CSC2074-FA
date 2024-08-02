package com.example.travellog

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.travellog.ui.theme.TravelLogTheme
import org.w3c.dom.Text
import java.io.File

class DetailedViewActivity : ComponentActivity(), View.OnClickListener   {

    private lateinit var backBtn: ImageButton
    private lateinit var editBtn: ImageButton
    private lateinit var deleteBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed_view)

        backBtn = findViewById(R.id.dv_backBtn)
        editBtn = findViewById(R.id.dv_editBtn)
        deleteBtn = findViewById(R.id.dv_deleteBtn)

        backBtn.setOnClickListener(this)
        editBtn.setOnClickListener(this)

        val record: RecordModel? = intent.getSerializableExtra("record") as? RecordModel



        if(record != null) {
            val recordImage: ImageView = findViewById(R.id.recordImg)
            val recordTitle: TextView = findViewById(R.id.recordTitle)
            val recordContinent: TextView = findViewById(R.id.recordCountry)
            val recordCountry: TextView = findViewById(R.id.recordContinent)
            val recordDate: TextView = findViewById(R.id.recordDate)
            val recordTime: TextView = findViewById(R.id.recordTime)
            val recordAdditionalInfo: TextView = findViewById(R.id.recordAdditionalInfo)

            val imageFile = File(record!!.imgPath)
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

    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.dv_backBtn -> {
                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.dv_editBtn -> {

            }
            R.id.dv_deleteBtn -> {

            }
        }
    }
}