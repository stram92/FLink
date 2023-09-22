package com.saladstudios.FLink.ui.finances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.saladstudios.FLink.R
import com.saladstudios.FLink.databinding.FragmentFinancesBinding

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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager>(R.id.finances_view_pager)
        viewPager.adapter = FinancesViewPagerAdapter(childFragmentManager)

        val tabLayout = view.findViewById<TabLayout>(R.id.finances_top_navigation)
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}