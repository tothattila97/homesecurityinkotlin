package production.toth.attila.homesecurityinkotlin.ui.activities

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import production.toth.attila.homesecurityinkotlin.ManagePermissions
import production.toth.attila.homesecurityinkotlin.R
import production.toth.attila.homesecurityinkotlin.ui.fragments.NavigationPagerAdapter

class TestActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var bottomNavigationView: BottomNavigationView
    var menuItem:MenuItem ?=null
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val list = listOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS, Manifest.permission.RECORD_AUDIO)
        managePermissions = ManagePermissions(this,list,PermissionsRequestCode)

        viewPager = findViewById(R.id.viewpager)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        viewPager.adapter = NavigationPagerAdapter(supportFragmentManager)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_one -> {viewPager.setCurrentItem(0); true}
                R.id.action_two -> {viewPager.setCurrentItem(1); true}
                R.id.action_three -> {viewPager.setCurrentItem(2); true}
                R.id.action_four -> {viewPager.setCurrentItem(3); true}
                else -> {
                    false
                }
            }
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if(menuItem != null){
                    menuItem?.isChecked = false
                }
                else{
                    bottomNavigationView.menu.getItem(0).isChecked = false
                }
                bottomNavigationView.getMenu().getItem(position).isChecked = true;
                menuItem = bottomNavigationView.getMenu().getItem(position);
            }
            override fun onPageSelected(position: Int) {

            }

        })
        managePermissions.checkPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequestCode ->{
                val isPermissionsGranted = managePermissions
                        .processPermissionsResult(requestCode,permissions,grantResults)

                if(isPermissionsGranted){
                    // Do the task now
                    toast("Permissions granted.")
                }else{
                    toast("Permissions denied.")
                }
                return
            }
        }
    }
    private fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
