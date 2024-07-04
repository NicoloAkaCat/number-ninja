package dev.nicoloakacat.numberninja.ui.profile

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.nicoloakacat.numberninja.databinding.FragmentProfileBinding
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dev.nicoloakacat.numberninja.R

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModels()

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
        val response = result.idpResponse
        val navController = findNavController()
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            profileViewModel.setUser(user)

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
                    profileViewModel.setUser(null)
                    findNavController().popBackStack(R.id.navigation_play, false)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(profileViewModel.user.value == null) {

            val user = FirebaseAuth.getInstance().currentUser
            if(user != null) {
                profileViewModel.setUser(user)
                return
            }

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel = profileViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val userName = user.displayName
                profileViewModel.setText("Welcome $userName")
            }
        }

        binding.logoutBtn.setOnClickListener { this.logOut() }

        return binding.root
    }
}