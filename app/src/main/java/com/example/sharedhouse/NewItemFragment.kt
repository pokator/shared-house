package com.example.sharedhouse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sharedhouse.databinding.NewItemBinding
import com.example.sharedhouse.db.MainViewModel
import com.example.sharedhouse.models.UnpurchasedExpense

// Creating a new item to add to unpurchased items.
class NewItemFragment : Fragment() {
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: NewItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val sharedWith = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var roommateCount = 0
        viewModel.observeCurrentApartment().observe(viewLifecycleOwner) {
            viewModel.getAllRoomates {
                Log.d("ViewModel", "number of roommates: ${it.roomates.size}")

            }
            roommateCount = it.roomates.size
            var apt = it

            //Conditionally displaying checkboxes
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
                    binding.layoutMe.isEnabled = true

                }

                1 -> {
                    binding.layoutMe.visibility = View.VISIBLE
                    binding.layoutMe.isEnabled = true
                    binding.layoutOne.visibility = View.GONE
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
                    binding.layoutMe.visibility = View.VISIBLE
                    binding.layoutMe.isEnabled = true
                    binding.layoutOne.visibility = View.VISIBLE
                    binding.layoutTwo.visibility = View.GONE
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
                    binding.layoutFour.visibility = View.GONE
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
                if (binding.itemTextField.text.isNotEmpty() && binding.quantityTextField.text.isNotEmpty()) {
                    if (binding.quantityTextField.text.toString().toInt() == 0) {
                        binding.quantityTextField.error = "Quantity must be greater than 0"
                        return@setOnClickListener
                    }
                    if (binding.meCheckbox.isChecked || binding.roommateCheckboxOne.isChecked
                        || binding.roommateCheckboxTwo.isChecked || binding.roommateCheckboxThree.isChecked
                        || binding.roommateCheckboxFour.isChecked
                    ) {
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

                        viewModel.addUnpurchasedExpense(
                            UnpurchasedExpense(
                                binding.itemTextField.text.toString(),
                                shareList,
                                binding.quantityTextField.text.toString().toInt(),
                            )
                        )

                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, "Must be shared with at least one person.", Toast.LENGTH_LONG)
                        return@setOnClickListener
                    }
                } else {
                    Toast.makeText(context, "Ensure that the item name and quantity are entered.", Toast.LENGTH_LONG)
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