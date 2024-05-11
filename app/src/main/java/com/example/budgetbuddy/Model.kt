package com.example.budgetbuddy

class Model(var documentId: String? = null, var notes: String ="", var amount: Double?=null , var date: String="", var collection: String="" )    {
    constructor():this("","",null,"", "")
}

