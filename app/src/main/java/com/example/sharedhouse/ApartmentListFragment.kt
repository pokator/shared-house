package com.example.sharedhouse

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedhouse.databinding.ApartmentViewBinding
import com.example.sharedhouse.db.MainViewModel

class ApartmentListFragment : Fragment() {
    companion object {
        val TAG : String = ApartmentListFragment::class.java.simpleName
    }

    private var _binding: ApartmentViewBinding? = null
    private val binding get() = _binding!!
    private val dataViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ApartmentViewBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val joinClickListener: (id: String) -> Unit = { id ->
            dataViewModel.addUserToExistingApartment(id)
            findNavController().popBackStack()
        }

        val adapter = ApartmentListAdapter(viewModel = dataViewModel, requireContext(), joinClickListener)
        val rv = binding.recyclerView
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        rv.addItemDecoration(itemDecor)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(rv.context)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        dataViewModel.getAllApartments()
        dataViewModel.observeAllApartments().observe (viewLifecycleOwner){
            adapter.submitList(it)
        }
        binding.buttonCreate.setOnClickListener {
            dataViewModel.addNewApartment(binding.newApartmentName.text.toString(), binding.newApartmentPassword.text.toString())
            findNavController().popBackStack()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}