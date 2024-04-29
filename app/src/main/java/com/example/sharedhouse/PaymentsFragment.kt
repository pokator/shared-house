package com.example.sharedhouse

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sharedhouse.databinding.PaymentsFragmentBinding
import com.example.sharedhouse.db.MainViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.abs

class PaymentsFragment : Fragment() {
    companion object {
        val TAG : String = PaymentsFragment::class.java.simpleName
    }
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: PaymentsFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val dataViewModel: MainViewModel by activityViewModels()
    private val sharedWith = mutableListOf<String>()
    private val authViewModel: AuthViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PaymentsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        Log.d(TAG, "onCreateView ${viewModel.selected}")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addExpense.setOnClickListener {
            findNavController().navigate(R.id.addExpenseFragment)
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            dataViewModel.updatePurchasedItems()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        authViewModel.observeDisplayName().observe(viewLifecycleOwner) {
            binding.netBalanceLabel.text = "$it's Net Balance"
            if (it != "Uninitialized") {
                dataViewModel.updateCurrentApartment()
            }
        }

        dataViewModel.observePurchasedItems().observe(viewLifecycleOwner) {
            dataViewModel.calculateTotals()
        }


        dataViewModel.observeTotal().observe(viewLifecycleOwner) {
            val totalOwed = it[FirebaseAuth.getInstance().currentUser!!.uid]

            if (totalOwed!! > 0.0) {
                binding.netBalanceText.setTextColor(Color.rgb(139, 0, 0))
                binding.netBalanceText.text = "You owe $${String.format("%.2f", totalOwed)}"
            } else {
                binding.netBalanceText.setTextColor(Color.rgb( 1,  150,  32))
                binding.netBalanceText.text = "You are owed $${String.format("%.2f", abs(totalOwed))}"
            }
        }

        var roommateCount = 0
        dataViewModel.observeCurrentApartment().observe(viewLifecycleOwner) {
            dataViewModel.getAllRoomates(){}
            roommateCount = it.roomates.size - 1
            val apt = it
            //Conditionally displaying checkboxes
            when (roommateCount) {
                0 -> {
                    binding.layoutOne.visibility = View.GONE
                    binding.layoutTwo.visibility = View.GONE
                    binding.layoutThree.visibility = View.GONE
                    binding.layoutFour.visibility = View.GONE
                    binding.layoutOne.isEnabled = false
                    binding.layoutTwo.isEnabled = false
                    binding.layoutThree.isEnabled = false
                    binding.layoutFour.isEnabled = false
                }
                1 -> {
                    binding.layoutOne.visibility = View.VISIBLE
                    binding.layoutTwo.visibility = View.GONE
                    binding.layoutThree.visibility = View.GONE
                    binding.layoutFour.visibility = View.GONE
                    binding.layoutOne.isEnabled = true
                    binding.layoutTwo.isEnabled = false
                    binding.layoutThree.isEnabled = false
                    binding.layoutFour.isEnabled = false
                    sharedWith.add(it.roomates[0])
                    dataViewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        var tempApt = apt.roomates.toMutableList()
                        tempApt.remove(FirebaseAuth.getInstance().currentUser!!.uid)
                        binding.personOne.text = it[tempApt[0]]
                    }
                    dataViewModel.observeTotal().observe(viewLifecycleOwner) {
                        val totalOwed = it[apt.roomates[0]]
                        if (totalOwed!! > 0.0) {
                            binding.personOneBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personOneBalance.text = "You owe them $${String.format("%.2f", totalOwed)}"
                        } else {
                            binding.personOneBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personOneBalance.text = "They owe you $${String.format("%.2f", abs(totalOwed))}"
                        }

                    }
                }
                2 -> {
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
                    dataViewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        var tempApt = apt.roomates.toMutableList()
                        tempApt.remove(FirebaseAuth.getInstance().currentUser!!.uid)
                        binding.personOne.text = it[tempApt[0]]
                        binding.personTwo.text = it[tempApt[1]]
                    }
                    dataViewModel.observeTotal().observe(viewLifecycleOwner) {
                        val totalOwed = it[apt.roomates[0]]
                        if (totalOwed!! > 0.0) {
                            binding.personOneBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personOneBalance.text = "You owe them $${String.format("%.2f", totalOwed)}"
                        } else {
                            binding.personOneBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personOneBalance.text = "They owe you $${String.format("%.2f", abs(totalOwed))}"
                        }
                        val totalOwedTwo = it[apt.roomates[1]]
                        if (totalOwedTwo!! > 0.0) {
                            binding.personTwoBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personTwoBalance.text = "You owe them $${String.format("%.2f", totalOwedTwo)}"
                        } else {
                            binding.personTwoBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personTwoBalance.text = "They owe you $${String.format("%.2f", abs(totalOwedTwo))}"
                        }

                    }

                }

                3 -> {
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
                    dataViewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        var tempApt = apt.roomates.toMutableList()
                        tempApt.remove(FirebaseAuth.getInstance().currentUser!!.uid)
                        binding.personOne.text = it[tempApt[0]]
                        binding.personTwo.text = it[tempApt[1]]
                        binding.personThree.text = it[tempApt[2]]
                    }
                    dataViewModel.observeTotal().observe(viewLifecycleOwner) {
                        val totalOwed = it[apt.roomates[0]]
                        if (totalOwed!! > 0.0) {
                            binding.personOneBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personOneBalance.text = "You owe them $${String.format("%.2f", totalOwed)}"
                        } else {
                            binding.personOneBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personOneBalance.text = "They owe you $${String.format("%.2f", abs(totalOwed))}"
                        }
                        val totalOwedTwo = it[apt.roomates[1]]
                        if (totalOwedTwo!! > 0.0) {
                            binding.personTwoBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personTwoBalance.text = "You owe them $${String.format("%.2f", totalOwedTwo)}"
                        } else {
                            binding.personTwoBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personTwoBalance.text = "They owe you $${String.format("%.2f", abs(totalOwedTwo))}"
                        }
                        val totalOwedThree = it[apt.roomates[2]]
                        if (totalOwedThree!! > 0.0) {
                            binding.personThreeBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personThreeBalance.text = "You owe them $${String.format("%.2f", totalOwedThree)}"
                        } else {
                            binding.personThreeBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personThreeBalance.text = "They owe you $${String.format("%.2f", abs(totalOwedThree))}"
                        }

                    }
                }

                4 -> {
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
                    dataViewModel.observeAllRoomates().observe(viewLifecycleOwner) {
                        var tempApt = apt.roomates.toMutableList()
                        tempApt.remove(FirebaseAuth.getInstance().currentUser!!.uid)
                        binding.personOne.text = it[tempApt[0]]
                        binding.personTwo.text = it[tempApt[1]]
                        binding.personThree.text = it[tempApt[2]]
                        binding.personFour.text = it[tempApt[3]]


                    }
                    dataViewModel.observeTotal().observe(viewLifecycleOwner) {
                        val totalOwed = it[apt.roomates[0]]
                        if (totalOwed!! > 0.0) {
                            binding.personOneBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personOneBalance.text = "You owe them $${String.format("%.2f", totalOwed)}"
                        } else {
                            binding.personOneBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personOneBalance.text = "They owe you $${String.format("%.2f", abs(totalOwed))}"
                        }
                        val totalOwedTwo = it[apt.roomates[1]]
                        if (totalOwedTwo!! > 0.0) {
                            binding.personTwoBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personTwoBalance.text = "You owe them $${String.format("%.2f", totalOwedTwo)}"
                        } else {
                            binding.personTwoBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personTwoBalance.text = "They owe you $${String.format("%.2f", abs(totalOwedTwo))}"
                        }
                        val totalOwedThree = it[apt.roomates[2]]
                        if (totalOwedThree!! > 0.0) {
                            binding.personThreeBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personThreeBalance.text = "You owe them $${String.format("%.2f", totalOwedThree)}"
                        } else {
                            binding.personThreeBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personThreeBalance.text = "They owe you $${String.format("%.2f", abs(totalOwedThree))}"
                        }
                        val totalOwedFour = it[apt.roomates[3]]
                        if (totalOwedFour!! > 0.0) {
                            binding.personFourBalance.setTextColor(Color.rgb(139, 0, 0))
                            binding.personFourBalance.text = "You owe them $${String.format("%.2f", totalOwedFour)}"
                        } else {
                            binding.personFourBalance.setTextColor(Color.rgb( 1,  150,  32))
                            binding.personFourBalance.text = "They owe you $${String.format("%.2f", abs(totalOwedFour))}"
                        }

                    }

                }
                else -> println("Error")
            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}