package com.example.sharedhouse

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedhouse.databinding.RowApartmentBinding
import com.example.sharedhouse.db.MainViewModel
import com.example.sharedhouse.models.Apartment

class ApartmentListAdapter(private val viewModel: MainViewModel, private val context: Context,
                           private val clickListener: (index : String)->Unit): ListAdapter<Apartment,
        ApartmentListAdapter.ViewHolder>(Diff()) {
   private var focusedRow: Int? = null

    class Diff : DiffUtil.ItemCallback<Apartment>() {
        // Item identity
        override fun areItemsTheSame(oldItem: Apartment, newItem: Apartment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: Apartment, newItem: Apartment): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.name == newItem.name
        }
    }



    inner class ViewHolder(private val rowBinding: RowApartmentBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {
        init {
            rowBinding.root.setOnTouchListener { _, _ ->
                focusedRow = adapterPosition
                notifyDataSetChangedDelay() // Notify adapter that data set has changed
                true // Return true to consume touch event
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(holder: ViewHolder, position: Int) {
            val apartment = viewModel.getApartmentMeta(position)
            val isFocusedRow = position == focusedRow
            holder.rowBinding.apartmentName.text = apartment.name

            rowBinding.root.setOnClickListener {

                clearFocusedRow()
            }

            if (isFocusedRow) {
                holder.rowBinding.passwordLayout.visibility = View.VISIBLE
                holder.rowBinding.joinButton.visibility = View.GONE
            } else {
                holder.rowBinding.passwordLayout.visibility = View.GONE
                holder.rowBinding.joinButton.visibility = View.VISIBLE
            }

            var buttonClicked = false
            rowBinding.joinButton.setOnClickListener {
                if (apartment.roomates.size > 4) {
                    Toast.makeText(context, "This apartment is full!", Toast.LENGTH_LONG)
                } else {
                    holder.rowBinding.passwordLayout.visibility = View.VISIBLE
                    holder.rowBinding.joinButton.visibility = View.GONE
                    holder.rowBinding.password.requestFocus()
                    val inputMethodManager = it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
                }
            }

            rowBinding.password.setOnFocusChangeListener { v, hasFocus ->
                if(!hasFocus && !buttonClicked) {
                    holder.rowBinding.passwordLayout.visibility = View.GONE
                    holder.rowBinding.joinButton.visibility = View.VISIBLE
                    val inputMethodManager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }

            rowBinding.confirmJoin.setOnClickListener {
                buttonClicked = true
                if(apartment.password == holder.rowBinding.password.text.toString()) {
                    clickListener(apartment.firestoreID)
                } else {
                    holder.rowBinding.password.error = "Invalid password!"
                    buttonClicked = false
                }
            }


        }

        private fun notifyDataSetChangedDelay() {
            rowBinding.root.postDelayed({
                notifyDataSetChanged()
            }, 50) // Adjust delay time as needed
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowApartmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    fun clearFocusedRow() {
        focusedRow = null
        notifyDataSetChanged()
    }

}