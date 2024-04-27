package com.example.sharedhouse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sharedhouse.databinding.AddExpenseBinding
import com.example.sharedhouse.db.MainViewModel
import com.example.sharedhouse.models.PurchasedItem
import com.example.sharedhouse.models.UnpurchasedExpense
import com.google.firebase.auth.FirebaseAuth

class AddExpenseFragment : Fragment() {
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: AddExpenseBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private val sharedWith = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(tag, "in add expense fragment.")
        viewModel.updateCurrentApartment()
        var roommateCount = 0
        viewModel.observeCurrentApartment().observe(viewLifecycleOwner) {
            viewModel.getAllRoomates() {
                Log.d("ViewModel", "number of roommates: ${it.roomates.size}")
            }
            roommateCount = it.roomates.size
            var apt = it

            when (roommateCount) {
                0 -> {
                    binding.layoutMe.visibility = View.GONE
                    binding.layoutOne.visibility = View.GONE
                    binding.layoutTwo.visibility = View.GONE
                    binding.layoutThree.visibility = View.GONE
                    binding.layoutFour.visibility = View.GONE
                    binding.layoutOne.isEnabled = false
                    binding.layoutTwo.isEnabled = false
                    binding.layoutThree.isEnabled = false
                    binding.layoutFour.isEnabled = false
                    binding.layoutMe.isEnabled = false

                }

                1 -> {
                    binding.layoutMe.visibility = View.VISIBLE
                    binding.layoutMe.isEnabled = true
                    binding.layoutOne.visibility = View.INVISIBLE
                    binding.layoutTwo.visibility = View.GONE
                    binding.layoutThree.visibility = View.GONE
                    binding.layoutFour.visibility = View.GONE
                    binding.layoutOne.isEnabled = false
                    binding.layoutTwo.isEnabled = false
                    binding.layoutThree.isEnabled = false
                    binding.layoutFour.isEnabled = false
                    sharedWith.add(it.roomates[0])
                    viewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        binding.labelMe.text = it[apt.roomates[0]]
                    }
                }

                2 -> {
                    binding.layoutMe.visibility = View.GONE
                    binding.layoutMe.isEnabled = false
                    binding.layoutOne.visibility = View.VISIBLE
                    binding.layoutTwo.visibility = View.INVISIBLE
                    binding.layoutThree.visibility = View.GONE
                    binding.layoutFour.visibility = View.GONE
                    binding.layoutOne.isEnabled = true
                    binding.layoutTwo.isEnabled = false
                    binding.layoutThree.isEnabled = false
                    binding.layoutFour.isEnabled = false
                    sharedWith.add(it.roomates[0])
                    sharedWith.add(it.roomates[1])
                    viewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        binding.labelMe.text = it[apt.roomates[0]]
                        binding.labelTextView1.text = it[apt.roomates[1]]
                    }

                }

                3 -> {
                    binding.layoutMe.visibility = View.VISIBLE
                    binding.layoutMe.isEnabled = true
                    binding.layoutOne.visibility = View.VISIBLE
                    binding.layoutTwo.visibility = View.VISIBLE
                    binding.layoutThree.visibility = View.GONE
                    binding.layoutFour.visibility = View.GONE
                    binding.layoutOne.isEnabled = true
                    binding.layoutTwo.isEnabled = true
                    binding.layoutThree.isEnabled = false
                    binding.layoutFour.isEnabled = false
                    sharedWith.add(it.roomates[0])
                    sharedWith.add(it.roomates[1])
                    sharedWith.add(it.roomates[2])
                    viewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        binding.labelMe.text = it[apt.roomates[0]]
                        binding.labelTextView1.text = it[apt.roomates[1]]
                        binding.labelTextView2.text = it[apt.roomates[2]]

                    }
                }

                4 -> {
                    binding.layoutMe.visibility = View.VISIBLE
                    binding.layoutMe.isEnabled = true
                    binding.layoutOne.visibility = View.VISIBLE
                    binding.layoutTwo.visibility = View.VISIBLE
                    binding.layoutThree.visibility = View.VISIBLE
                    binding.layoutFour.visibility = View.INVISIBLE
                    binding.layoutOne.isEnabled = true
                    binding.layoutTwo.isEnabled = true
                    binding.layoutThree.isEnabled = true
                    binding.layoutFour.isEnabled = false
                    sharedWith.add(it.roomates[0])
                    sharedWith.add(it.roomates[1])
                    sharedWith.add(it.roomates[2])
                    sharedWith.add(it.roomates[3])
                    viewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        binding.labelMe.text = it[apt.roomates[0]]
                        binding.labelTextView1.text = it[apt.roomates[1]]
                        binding.labelTextView2.text = it[apt.roomates[2]]
                        binding.labelTextView3.text = it[apt.roomates[3]]
                    }

                }

                5 -> {
                    binding.layoutMe.visibility = View.VISIBLE
                    binding.layoutMe.isEnabled = true
                    binding.layoutOne.visibility = View.VISIBLE
                    binding.layoutTwo.visibility = View.VISIBLE
                    binding.layoutThree.visibility = View.VISIBLE
                    binding.layoutFour.visibility = View.VISIBLE
                    binding.layoutOne.isEnabled = true
                    binding.layoutTwo.isEnabled = true
                    binding.layoutThree.isEnabled = true
                    binding.layoutFour.isEnabled = true
                    sharedWith.add(it.roomates[0])
                    sharedWith.add(it.roomates[1])
                    sharedWith.add(it.roomates[2])
                    sharedWith.add(it.roomates[3])
                    sharedWith.add(it.roomates[4])
                    viewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        binding.labelMe.text = it[apt.roomates[0]]
                        binding.labelTextView1.text = it[apt.roomates[1]]
                        binding.labelTextView2.text = it[apt.roomates[2]]
                        binding.labelTextView3.text = it[apt.roomates[3]]
                        binding.labelTextView4.text = it[apt.roomates[4]]
                    }
                }

                else -> println("Error")
            }

            binding.submitButton.setOnClickListener {
                if (binding.expenseText.text.isNotEmpty() && binding.amountText.text.isNotEmpty()) {
                    if (binding.meCheckbox.isChecked || binding.roommateCheckboxOne.isChecked
                        || binding.roommateCheckboxTwo.isChecked || binding.roommateCheckboxThree.isChecked
                        || binding.roommateCheckboxFour.isChecked
                    ) {
                        val title = binding.expenseText.text
                        var amount = binding.amountText.text.toString().toDouble()
                        if (binding.taxCheckbox.isChecked) {
                            amount *= 1.0625
                        }
                        // Valid setup of item details.
                        var shareList = mutableListOf<String>()
                        if (binding.meCheckbox.isChecked) {
                            shareList.add(sharedWith[0])
                        }
                        if (binding.roommateCheckboxOne.isChecked) {
                            shareList.add(sharedWith[1])
                        }
                        if (binding.roommateCheckboxTwo.isChecked) {
                            shareList.add(sharedWith[2])
                        }
                        if (binding.roommateCheckboxThree.isChecked) {
                            shareList.add(sharedWith[3])
                        }
                        if (binding.roommateCheckboxFour.isChecked) {
                            shareList.add(sharedWith[4])
                        }

                        val map = HashMap<String, Boolean>()
                        for (id in shareList){
                            map[id] = false
                        }

                        if (binding.meCheckbox.isChecked){
                            map[FirebaseAuth.getInstance().currentUser!!.uid] = true
                        }

                        val expenseToAdd = PurchasedItem(
                            purchasedBy = FirebaseAuth.getInstance().currentUser!!.uid,
                            name = title.toString(),
                            price = amount,
                            hasPaid = map,
                            quantity = 0
                        )

                        viewModel.addPurchasedExpense(expenseToAdd)
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, "Must be shared with at least one person.", Toast.LENGTH_LONG)
                        return@setOnClickListener
                    }
                } else {
                    Toast.makeText(context, "Ensure that the expense and amount are entered.", Toast.LENGTH_LONG)
                    return@setOnClickListener
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}