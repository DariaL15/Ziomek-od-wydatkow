package com.example.budgetbuddy

class ModelReminderItem (
    var name: String = "",
    var amount: Double = 0.0,
    var nextDate: String = "",
    var isChecked: Boolean = false,
    val repeatFrequency: String,
    var amountOfTransfers: Int = 0,
    var amountOfTransfersTemp: Int = 0,
    var endDayOfTransfers: String = "",
    var category: String = ""
)
{
    constructor() : this("", 0.0, "", false, "",0,0,"", "")
}