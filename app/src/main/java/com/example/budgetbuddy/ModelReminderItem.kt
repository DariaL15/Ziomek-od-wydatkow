package com.example.budgetbuddy

class ModelReminderItem (
    var name: String = "",
    var amount: Double = 0.0,
    var nextDate: String = "",
    var isChecked: Boolean = false,
    val repeatFrequency: String
)
{
    constructor() : this("", 0.0, "", false, "")
}