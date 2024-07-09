package dev.nicoloakacat.numberninja

import android.os.Build
import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dev.nicoloakacat.numberninja.databinding.ActivityMainBinding
import dev.nicoloakacat.numberninja.db.UserStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()

    private fun askForPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    private suspend fun setUserIfLogged() {
        // check if the user logged in a previous session with the app
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            userViewModel.setUser(user)

            val uid: String = userViewModel.uid.value!!
            //TODO try catch errori
            val userDb = UserStorage.findOne(uid)
            if(userDb != null) userViewModel.setDataFromDB(userDb)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        askForPermissions()

        lifecycleScope.launch {
            setUserIfLogged()
        }

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)
    }
}