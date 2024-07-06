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
import dev.nicoloakacat.numberninja.UserDB
import dev.nicoloakacat.numberninja.UserStorage
import dev.nicoloakacat.numberninja.UserViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val navController = findNavController()
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            userViewModel.setUser(user)

            val uid: String = userViewModel.uid.value!!
            val userDb = UserStorage.findOne(uid)

            if (userDb != null) {
                if(userViewModel.maxScore.value!! > userDb.maxScore!!) {
                    userDb.maxScore = userViewModel.maxScore.value
                    UserStorage.updateScore(userViewModel.maxScore.value!!, uid)
                }

                userViewModel.setDataFromDB(userDb)
            }
            else {
                val userToInsert = UserDB(
                    maxScore = userViewModel.maxScore.value!!,
                    nationality = 0,
                    name = userViewModel.displayName.value!!
                )

                UserStorage.createDocument(userToInsert, uid)
            }

            navController.run {
                popBackStack()
                navigate(R.id.navigation_profile)
            }
        } else {
            navController.popBackStack(R.id.navigation_play, false)
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
        }
    }

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
        binding.loginBtn.setOnClickListener {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false) //TODO togliere? non va
                .build()
            signInLauncher.launch(signInIntent)
        }
        binding.logoutBtn.setOnClickListener {
            logOut()
        }
    }
}