package com.example.budgetbuddy
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

class ModelFixed(
    var documentId: String?= null,
    var name: String="",
    var amount: Double?=null,
    var repeatFrequency: String="",
    var category: String="",
    var beginDate: String="",
    var nextDate: String="",
    var amountOfTransfers: Int ?=null,
    var endDayOfTransfers: String="",
    var amountOfTransfersTemp: Int? =null,
    var whenStopFixedExpense: String="") {
    constructor():this("","",null,"", "", "", "", null, "", null, "")
}


