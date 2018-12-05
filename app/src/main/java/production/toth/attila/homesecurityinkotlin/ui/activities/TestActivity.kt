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
import production.toth.attila.homesecurityinkotlin.ui.fragments.CameraFragment
import production.toth.attila.homesecurityinkotlin.ui.fragments.NavigationPagerAdapter

class TestActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    lateinit var bottomNavigationView: BottomNavigationView
    var menuItem:MenuItem ?=null
    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val list = listOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS, Manifest.permission.RECORD_AUDIO)
        managePermissions = ManagePermissions(this,list,permissionsRequestCode)

        viewPager = findViewById(R.id.viewpager)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        viewPager.adapter = NavigationPagerAdapter(supportFragmentManager)
        viewPager.offscreenPageLimit = 4

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_one -> {
                    viewPager.currentItem = 0; true}
                R.id.action_two -> {
                    viewPager.currentItem = 1; true}
                R.id.action_three -> {
                    viewPager.currentItem = 2; true}
                R.id.action_four -> {
                    viewPager.currentItem = 3; true}
                else -> {
                    false
                }
            }
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {/*required method overload */}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if(menuItem != null){
                    menuItem?.isChecked = false
                }
                else{
                    bottomNavigationView.menu.getItem(0).isChecked = false
                }
                bottomNavigationView.menu.getItem(position).isChecked = true
                menuItem = bottomNavigationView.menu.getItem(position)
            }
            override fun onPageSelected(position: Int) {/*required method overload*/}
        })
        managePermissions.checkPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            permissionsRequestCode ->{
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

    override fun onBackPressed() {
        super.onBackPressed()
        CameraFragment().releaseCamera()
        this.finishAffinity()
    }
}
