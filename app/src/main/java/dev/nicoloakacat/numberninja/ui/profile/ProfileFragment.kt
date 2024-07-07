package dev.nicoloakacat.numberninja.ui.profile

import android.app.Activity.RESULT_OK
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dev.nicoloakacat.numberninja.databinding.FragmentProfileBinding
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dev.nicoloakacat.numberninja.Nationality
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.db.UserData
import dev.nicoloakacat.numberninja.db.UserStorage
import dev.nicoloakacat.numberninja.UserViewModel
import dev.nicoloakacat.numberninja.getFlagUri
import dev.nicoloakacat.numberninja.hide
import dev.nicoloakacat.numberninja.show

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val nations = Nationality.entries.map { n -> n.toString().replace("_", " ") }
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel = userViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        userViewModel.isUserLogged.observe(viewLifecycleOwner) {logged ->
            if(logged){
                hide(binding.notLoggedGroup)
                show(binding.loggedGroup)
                // setting list of nationalities
                val nationalityAdapter = ArrayAdapter(requireContext(), R.layout.item_nationality, nations)
                binding.profileCardNationality.setAdapter(nationalityAdapter)
                setFlagIcon(userViewModel.nationality.value!!)
            }else {
                show(binding.notLoggedGroup)
                hide(binding.loggedGroup)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // changing flag icon when user selects another nationality!
        binding.profileCardNationality.setOnItemClickListener{ _, _, position: Int, _ ->
            val nationalitySelected = nations[position]
            setFlagIcon(nationalitySelected)
        }

        // login
        binding.profileLoginButton.setOnClickListener {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_NumberNinja)
                .setIsSmartLockEnabled(false) //TODO togliere? non va
                .build()
            signInLauncher.launch(signInIntent)
        }

        // logout
        binding.profileLogoutButton.setOnClickListener {
            logOut()
        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val navController = findNavController()
        when(result.resultCode){
            RESULT_OK -> {
                val user = FirebaseAuth.getInstance().currentUser
                userViewModel.setUser(user)

                val uid: String = userViewModel.uid.value!!
                //TODO try catch errori
                val userDataFromDB = UserStorage.findOne(uid)

                // sync data with the DB
                val localMaxScore = userViewModel.maxScore.value!!
                if (userDataFromDB != null) {
                    if(localMaxScore > userDataFromDB.maxScore!!) {
                        userDataFromDB.maxScore = userViewModel.maxScore.value
                        UserStorage.updateScore(localMaxScore, uid)
                    }
                    userViewModel.setDataFromDB(userDataFromDB)
                }
                else {
                    val userToInsert = UserData(
                        maxScore = localMaxScore,
                        nationality = "Unknown",
                        name = userViewModel.displayName.value!!
                    )
                    UserStorage.createDocument(userToInsert, uid)
                }

                navController.run {
                    popBackStack()
                    navigate(R.id.navigation_profile)
                }
            }

            else -> navController.popBackStack(R.id.navigation_play, false)
        }
    }

    private fun logOut() {
        context?.let {
            AuthUI.getInstance()
                .signOut(it)
                .addOnCompleteListener {
                    userViewModel.setUser(null)
                    findNavController().popBackStack(R.id.navigation_profile, false)
                }
                .addOnFailureListener{
                    //TODO errori
                }
        }
    }

    private fun setFlagIcon(nationality: String) {
        try{
            binding.profileCardNationalityBox.startIconDrawable = ResourcesCompat.getDrawable(
                resources,
                resources.getIdentifier(getFlagUri(nationality), null, null),
                null
            )
        }catch(e: NotFoundException){
            Log.w("FLAG_DRAWABLE", "Flag drawable not found")
            binding.profileCardNationalityBox.setStartIconDrawable(R.drawable.flag_unknown)
        }
    }
}