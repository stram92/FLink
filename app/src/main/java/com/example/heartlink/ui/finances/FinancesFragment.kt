package com.example.heartlink.ui.finances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.heartlink.databinding.FragmentFinancesBinding

class FinancesFragment : Fragment() {

    private var _binding: FragmentFinancesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val financesViewModel =
            ViewModelProvider(this).get(FinancesViewModel::class.java)

        _binding = FragmentFinancesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textFinances
        financesViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}