package com.example.travellog

data class RecordModel(val id: Int, var image: ByteArray, var title: String, var continent: String, var country: String, var date: String, var time: String, var additionalInfo: String) {

}