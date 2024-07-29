package com.example.travellog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        // Declaring variables that needs to have click listeners
        val addRecordButton: ImageButton = findViewById(R.id.addRecordBtn)

        // Setting click listeners
        addRecordButton.setOnClickListener(this)

        initialisingRecyclerView()

    }

    private fun initialisingRecyclerView() {

        val recyclerView: RecyclerView = findViewById(R.id.recordRecyclerView)
        val noRecordText: TextView = findViewById(R.id.noRecord)

        if (getRecordList().size > 0) {

            recyclerView.visibility = View.VISIBLE
            noRecordText.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            recyclerView.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val adapter = AdapterClass(this, getRecordList())
            // adapter instance is set to the recyclerview to inflate the items.
            recyclerView.adapter = adapter
        } else {

            recyclerView.visibility = View.GONE
            noRecordText.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.addRecordBtn -> {
                val intent: Intent = Intent(this, AddLocationActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Function to get the list of records from the database
    private fun getRecordList(): ArrayList<RecordModel> {
        // Creating instance of DatabaseHandler class
        val databaseHandler = DatabaseHandler(this)
        // Calling function in DatabaseHandler class to read data
        return databaseHandler.viewRecord()
    }


}
