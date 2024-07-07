package dev.nicoloakacat.numberninja.ui.profile

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import dev.nicoloakacat.numberninja.databinding.FragmentProfileBinding
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.UserData
import dev.nicoloakacat.numberninja.UserStorage
import dev.nicoloakacat.numberninja.UserViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel = userViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        userViewModel.isUserLogged.observe(viewLifecycleOwner) {
            if(it){
                binding.notLoggedGroup.visibility = View.GONE
                binding.loggedGroup.visibility = View.VISIBLE
            }else {
                binding.notLoggedGroup.visibility = View.VISIBLE
                binding.loggedGroup.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileLoginButton.setOnClickListener {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_NumberNinja)
                .setIsSmartLockEnabled(false) //TODO togliere? non va
                .build()
            signInLauncher.launch(signInIntent)
        }
        binding.logoutBtn.setOnClickListener {
            logOut()
        }
    }

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

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
                        nationality = 0,
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
}