package com.example.sharedhouse.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

//Defines the apartment as per Firebase
data class Apartment (
    var name: String = "",
    var roomates: List<String> = emptyList(),
    var password: String = "",
    @ServerTimestamp
    val timeStamp: Timestamp? = null,
    @DocumentId
    var firestoreID: String = "",
    var unpurchasedExpenses: MutableList<UnpurchasedExpense> = emptyList<UnpurchasedExpense>().toMutableList(), // Not a Firestore field
    var purchasedItems: MutableList<PurchasedItem> = emptyList<PurchasedItem>().toMutableList()// Not a Firestore field
)