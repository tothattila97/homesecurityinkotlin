package production.toth.attila.homesecurityinkotlin.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import production.toth.attila.homesecurityinkotlin.R
import production.toth.attila.homesecurityinkotlin.models.Gender
import production.toth.attila.homesecurityinkotlin.models.UserProfileModel
import production.toth.attila.homesecurityinkotlin.network.IHttpCallback
import production.toth.attila.homesecurityinkotlin.network.RetrofitNetworkService

class ProfileFragment: Fragment() {
    lateinit var genderPics: CircleImageView
    lateinit var profileName: TextView
    lateinit var profileEmail: TextView
    lateinit var profilePhoneNumber: TextView
    lateinit var profileBirthDate: TextView
    lateinit var profileGender: TextView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView= inflater?.inflate(R.layout.fragment_profile, container, false)

        genderPics = rootView!!.findViewById(R.id.profile_image)

        profileName = rootView.findViewById(R.id.profile_name)
        profileEmail = rootView.findViewById(R.id.profile_email)
        profilePhoneNumber = rootView.findViewById(R.id.profile_phone_number)
        profileBirthDate = rootView.findViewById(R.id.profile_birth_date)
        profileGender = rootView.findViewById(R.id.profile_gender)

        RetrofitNetworkService(context).profile(object : IHttpCallback {
            override fun getIsSucceeded(succeeded: Boolean) {}
            override fun getUserProfile(userProfile: UserProfileModel?) {
                profileName.text = "${userProfile?.firstName} ${userProfile?.lastName}"
                profileEmail.text = userProfile?.email
                profilePhoneNumber.text = userProfile?.phoneNumber
                profileBirthDate.text = userProfile?.dateOfBirth.toString()
                profileGender.text = userProfile?.gender?.name

                if(userProfile?.gender == Gender.Man)
                    Glide.with(context).load("https://us.123rf.com/450wm/triken/triken1608/triken160800029/61320775-male-avatar-profile-picture-default-user-avatar-guest-avatar-simply-human-head-vector-illustration-i.jpg?ver=6").into(genderPics)
                else if (userProfile?.gender == Gender.Woman)
                    Glide.with(context).load("https://www.healthline.com/hlcmsresource/images/medical-reviewer/placeholder-woman.png").into(genderPics)
                //else
                //genderPics.contentDescription = userProfileModel!!.firstName[0] as CharSequence

            }
        })
        return rootView
    }

}