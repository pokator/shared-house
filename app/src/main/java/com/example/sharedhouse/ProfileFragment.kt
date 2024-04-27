package com.example.sharedhouse

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.sharedhouse.databinding.ProfileViewBinding
import com.example.sharedhouse.db.MainViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract

//Profile. Can sign out or join an apartment (if not in one)
class ProfileFragment : Fragment() {
    companion object {
        val TAG : String = ProfileFragment::class.java.simpleName
    }

    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            authViewModel.updateUser()
        }

    private var _binding: ProfileViewBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val dataViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfileViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // XXX Write me. Observe data to display in UI
        authViewModel.observeEmail().observe(viewLifecycleOwner) {
            binding.emailText.text = it
        }

        authViewModel.observeDisplayName().observe(viewLifecycleOwner) {
            binding.nameText.text = it
        }

        dataViewModel.observeCurrentApartment().observe(viewLifecycleOwner) {
            if (it.name != "") {
                binding.apartmentButton.text = it.name
                binding.apartmentButton.isClickable = false
            } else {
                binding.apartmentButton.text = "Join an apartment"
                binding.apartmentButton.isClickable = true
            }
        }

        binding.apartmentButton.setOnClickListener {
            findNavController().navigate(R.id.apartmentListFragment)
        }

        binding.logout.setOnClickListener {
            authViewModel.signOut()
            binding.apartmentButton.text = "Join an apartment"
            binding.apartmentButton.isClickable = true
            AuthInit(authViewModel, signInLauncher)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}