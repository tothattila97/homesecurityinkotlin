package production.toth.attila.homesecurityinkotlin.ui.fragments

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class NavigationPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
    companion object {
        const val NUM_PAGES = 4;
    }

    override fun getCount(): Int {
        return NUM_PAGES
    }

    override fun getItem(position: Int): Fragment {
       when(position){
           0 -> return ProfileFragment()
           1 -> return SettingsFragment()
           2 -> return CameraFragment()
           3 -> return AboutFragment()
       }
        return ProfileFragment()
    }

}