package com.example.travellog

import android.content.Intent
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

class DetailedViewActivity : ComponentActivity(), View.OnClickListener   {

    private lateinit var backBtn: ImageButton
    private lateinit var editBtn: ImageButton
    private lateinit var deleteBtn: ImageButton
    private lateinit var recordImage: ImageView
    private lateinit var recordTitle: TextView
    private lateinit var recordContinent: TextView
    private lateinit var recordCountry: TextView
    private lateinit var recordDate: TextView
    private lateinit var recordTime: TextView
    private lateinit var recordAdditionalInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed_view)

        backBtn = findViewById(R.id.dv_backBtn)
        editBtn = findViewById(R.id.dv_editBtn)
        deleteBtn = findViewById(R.id.dv_deleteBtn)

        backBtn.setOnClickListener(this)
        editBtn.setOnClickListener(this)

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