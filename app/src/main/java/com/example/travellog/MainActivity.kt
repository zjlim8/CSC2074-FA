package com.example.travellog

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity(), View.OnClickListener, AdapterClass.OnItemClickListener {

    private lateinit var sqlQuery: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()

        val filterSelector: Spinner = findViewById(R.id.filterSelector)
        val filterItems = listOf("All", "Africa", "Asia", "Australia", "Antarctica", "Europe", "North America", "South America")
        val filterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterItems)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSelector.adapter = filterAdapter

        filterSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = filterItems[position]
                initialisingRecyclerView(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nothing is done
            }
        }

        // Declaring variables that needs to have click listeners
        val addRecordButton: ImageButton = findViewById(R.id.addRecordBtn)

        // Setting click listeners
        addRecordButton.setOnClickListener(this)

        initialisingRecyclerView("All")

    }

    private fun initialisingRecyclerView(selectedFilter: String) {

        val recyclerView: RecyclerView = findViewById(R.id.recordRecyclerView)
        val noRecordText: TextView = findViewById(R.id.noRecord)
        sqlQuery = when (selectedFilter) {
            "All" -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME} ORDER BY _id DESC"
            "Asia" -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME} WHERE continent='Asia' ORDER BY _id DESC"
            "Africa" -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME} WHERE continent='Africa' ORDER BY _id DESC"
            "Australia" -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME} WHERE continent='Australia' ORDER BY _id DESC"
            "Antarctica" -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME} WHERE continent='Antarctica' ORDER BY _id DESC"
            "Europe" -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME} WHERE continent='Europe' ORDER BY _id DESC"
            "North America" -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME} WHERE continent='North America' ORDER BY _id DESC"
            "South America" -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME} WHERE continent='South America' ORDER BY _id DESC"
            else -> "SELECT * FROM ${DatabaseHandler.TABLE_NAME}"
        }

        if (getRecordList(sqlQuery).size > 0) {

            recyclerView.visibility = View.VISIBLE
            noRecordText.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            recyclerView.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val adapter = AdapterClass(this, getRecordList(sqlQuery), this)
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
    private fun getRecordList(sqlQuery: String): ArrayList<RecordModel> {
        // Creating instance of DatabaseHandler class
        val databaseHandler = DatabaseHandler(this)
        // Calling function in DatabaseHandler class to read data
        return databaseHandler.viewRecord(sqlQuery)
    }

    override fun onItemClick(position: Int) {
        val clickedItem = getRecordList(sqlQuery)[position]
        val intent: Intent = Intent(this, DetailedViewActivity::class.java)
        intent.putExtra("record", clickedItem)
        startActivity(intent)
    }


}
