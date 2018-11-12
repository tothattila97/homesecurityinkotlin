package production.toth.attila.homesecurityinkotlin.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.hdodenhof.circleimageview.CircleImageView
import production.toth.attila.homesecurityinkotlin.R

class ProfileFragment: Fragment() {
    lateinit var genderPics: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView= inflater?.inflate(R.layout.fragment_profile, container, false);

        //genderPics = rootView!!.findViewById(R.id.profile_image)
        //Picasso.get().load("https://questortech.com/wp-content/uploads/2018/07/placeholder-man-300x300.png").into(genderPics)

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}