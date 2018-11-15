package production.toth.attila.homesecurityinkotlin.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import production.toth.attila.homesecurityinkotlin.R

class ProfileFragment: Fragment() {
    lateinit var genderPics: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView= inflater?.inflate(R.layout.fragment_profile, container, false)

        genderPics = rootView!!.findViewById(R.id.profile_image)
        Glide.with(this).load("https://us.123rf.com/450wm/triken/triken1608/triken160800029/61320775-male-avatar-profile-picture-default-user-avatar-guest-avatar-simply-human-head-vector-illustration-i.jpg?ver=6").into(genderPics)

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}