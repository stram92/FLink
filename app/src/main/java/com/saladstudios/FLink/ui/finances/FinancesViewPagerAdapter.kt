package com.saladstudios.FLink.ui.finances

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class FinancesViewPagerAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getCount(): Int {
        return 4;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return FinancesOverviewFragment()
            1 -> return FinancesAbosFragment()
            2 -> return FinancesCategoriesFragment()
            3 -> return FinancesStatisticsFragment()
            else -> return FinancesOverviewFragment()
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