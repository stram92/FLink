package com.example.heartlink.ui.cooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.heartlink.databinding.FragmentCookingBinding

class CookingFragment : Fragment() {

    private var _binding: FragmentCookingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cookingViewModel =
            ViewModelProvider(this).get(ListsViewModel::class.java)

        _binding = FragmentCookingBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textCooking
        cookingViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}