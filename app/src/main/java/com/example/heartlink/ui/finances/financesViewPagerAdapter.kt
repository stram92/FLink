package com.example.heartlink.ui.finances

import android.content.Context
import android.content.res.Resources
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.heartlink.R

class financesViewPagerAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm){
    override fun getCount(): Int {
        return 4;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return financesOverviewFragment()
            1 -> return financesAbosFragment()
            2 -> return financesCategoriesFragment()
            3 -> return financesStatisticsFragment()
            else -> return financesOverviewFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> return "Übersicht"
            1 -> return "Abos & Verträge"
            2 -> return "Kategorien"
            3 -> return "Statistiken"
        }

        return super.getPageTitle(position)
    }


}