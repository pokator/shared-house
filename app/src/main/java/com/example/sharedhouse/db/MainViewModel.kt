package com.example.sharedhouse.db

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sharedhouse.models.Apartment
import com.example.sharedhouse.models.PurchasedItem
import com.example.sharedhouse.models.UnpurchasedExpense
import com.google.firebase.auth.FirebaseAuth

class MainViewModel : ViewModel() {
    private var purchasedItems = MutableLiveData<List<PurchasedItem>>()
    private var unpurchasedItems = MutableLiveData<List<UnpurchasedExpense>>()
    private var curApartment = MutableLiveData<Apartment>()
    private var total = MutableLiveData<HashMap<String, Double>>()
    private var allApartments = MutableLiveData<List<Apartment>>()
    var allRoomates = MutableLiveData<HashMap<String, String>>()

    init {
        curApartment.observeForever { apartment ->
            getAllRoomates(){
                updatePurchasedItems()
            }
        }
    }

    fun observePurchasedItems() = purchasedItems
    fun observeUnpurchasedItems() = unpurchasedItems

    fun observeCurrentApartment() = curApartment

    fun observeAllApartments() = allApartments

    fun observeAllRoomates() = allRoomates

    fun observeTotal() = total

    //Get all purchased items
    fun updatePurchasedItems() {
        FirestoreService().dbFetchAllPurchasedExpenses(purchasedItems, curApartment.value!!.firestoreID) {}
    }

    //Get all unpurchased items
    fun updateUnpurchasedItems() {
        FirestoreService().dbFetchAllUnpurchasedExpenses(unpurchasedItems, curApartment.value!!.firestoreID)
    }

    //Adding a new item to the unpurchased expenses
    fun addUnpurchasedExpense(unpurchasedExpense: UnpurchasedExpense) {
        FirestoreService().dbAddUnpurchasedExpense(unpurchasedExpense, curApartment.value!!.firestoreID)
    }

    fun addNewApartment(apartmentId: String, password: String) {
        FirestoreService().dbAddNewApartment(apartmentId, FirebaseAuth.getInstance().currentUser!!, password) {
            updateCurrentApartment()
        }
    }

    fun addUserToExistingApartment(apartmentId: String) {
        FirestoreService().dbAddUserToExisitingApartment(FirebaseAuth.getInstance().currentUser!!,apartmentId) {
            updateCurrentApartment()
        }
    }

    fun updateCurrentApartment() {
        FirestoreService().dbGetUsersApartmentID(FirebaseAuth.getInstance().currentUser!!, curApartment) {
            val apartmentID = curApartment.value?.firestoreID ?: return@dbGetUsersApartmentID
            getAllRoomates(){}
        }
    }

    fun getAllApartments() {
        FirestoreService().dbGetAllAparments(allApartments)
    }

    fun getRoomatesWithoutObserving(): HashMap<String, String> {
        return allRoomates.value!!
    }

    fun getAllRoomates(callback: () -> Unit) {
        FirestoreService().dbGetAllRoomatesNames(allRoomates, curApartment.value!!.firestoreID)
        callback()
    }

    fun addPurchasedItem(unpurchasedExpense: UnpurchasedExpense, amount: Double, comment: String) {
        //Create our map of id to comment.
        var commentList = ArrayList<HashMap<String, String>>()
        var map = HashMap<String, String>()
        map["name"] = FirebaseAuth.getInstance().currentUser!!.displayName!!
        map["comment"] = comment
        if (comment.isNotEmpty()){
            commentList.add(map)
        }

        FirestoreService().doMoveFromUnpurchasedToPurchased(unpurchasedExpense, amount, commentList, curApartment.value!!.firestoreID, FirebaseAuth.getInstance().currentUser!!){
            val createdPurchaseItem = it
            val updatedApartment = curApartment.value?.apply {
                // Update fields in the apartment
                this.unpurchasedExpenses.remove(unpurchasedExpense)
                this.purchasedItems.add(createdPurchaseItem)
            }
            // Post the updated value to the MutableLiveData
            curApartment.postValue(updatedApartment!!)
        }

    }


    fun addCommentToPurchasedItem(purchasedItem: PurchasedItem, comment: String) {
        var map = HashMap<String, String>()
        map["name"] = FirebaseAuth.getInstance().currentUser!!.displayName!!
        map["comment"] = comment
        FirestoreService().dbAddCommentToPurchasedItem(purchasedItem, map, curApartment.value!!.firestoreID){
          updatePurchasedItems()
        }
    }


    fun addPurchasedExpense (purchasedItem: PurchasedItem) {
        FirestoreService().dbAddPurchasedExpense(purchasedItem, curApartment.value!!.firestoreID)
    }


    fun updateHasPaid(purchasedItem: PurchasedItem) {
        val map = purchasedItem.hasPaid
        map[FirebaseAuth.getInstance().currentUser!!.uid] = true
        purchasedItem.hasPaid = map

        FirestoreService().dbChangeHasPaidValue(purchasedItem, curApartment.value!!.firestoreID, ){
            updatePurchasedItems()
        }
    }


    fun calculateTotals() {
        var curUserId = FirebaseAuth.getInstance().currentUser!!.uid
        //Create map joining roommate ids to initial balance of 0
        var mapOfIdToOwed = HashMap<String, Double>()
        for (id in allRoomates.value!!.keys) {
            mapOfIdToOwed[id] = 0.0
        }
        for (purchasedItem in purchasedItems.value!!) {
            for (id in purchasedItem.hasPaid.keys) {
                if (purchasedItem.hasPaid[id] == false) {
                    //Only handling cases where someone owes something
                    if(purchasedItem.purchasedBy == curUserId) {
                        //The current user is owned by the specified user id
                        mapOfIdToOwed[id] = mapOfIdToOwed[id]!! - purchasedItem.price / purchasedItem.hasPaid.size
                    }
                    else if (id == curUserId) {
                        //The current user owes the purchaser
                        mapOfIdToOwed[purchasedItem.purchasedBy] = mapOfIdToOwed[purchasedItem.purchasedBy]!! + purchasedItem.price / purchasedItem.hasPaid.size
                    }

                }
            }
        }

        //Find net balance of the user.
        var netSum = 0.0
        for (id in mapOfIdToOwed.keys) {
            netSum += mapOfIdToOwed[id]!!
        }
        mapOfIdToOwed[curUserId] = netSum
        total.postValue(mapOfIdToOwed)
    }

    fun getItemMeta(position: Int) : UnpurchasedExpense {
        val item = unpurchasedItems.value?.get(position)
        return item!!
    }

    fun getApartmentMeta(position: Int) : Apartment {
        val apt = allApartments.value?.get(position)
        return apt!!
    }

    fun getPurchasedItemMeta(position: Int) : PurchasedItem {
        val item = purchasedItems.value?.get(position)
        return item!!
    }

}