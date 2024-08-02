package com.example.travellog

import java.io.Serializable

data class RecordModel(val id: Int, var imgPath: String, var title: String, var continent: String, var country: String, var date: String, var time: String, var additionalInfo: String) : Serializable