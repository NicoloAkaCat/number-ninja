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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.impl.WorkManagerImpl
import com.google.firebase.auth.FirebaseAuth
import dev.nicoloakacat.numberninja.databinding.ActivityMainBinding
import dev.nicoloakacat.numberninja.db.UserStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

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

    private fun runBackground() {
        val inputData = Data.Builder()
            .putString("uid", userViewModel.uid.value!!)
            .putInt("maxScore", userViewModel.maxScore.value!!)
            .putLong("nBetterPlayers", userViewModel.nBetterPlayers.value!!)

        val rankTrackerRequest: WorkRequest = OneTimeWorkRequestBuilder<RankTrackerWorker>()
            .setInputData(inputData.build())
            .build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueue(rankTrackerRequest)

        workManager.getWorkInfoByIdLiveData(rankTrackerRequest.id)
            .observe(this, Observer { workInfo ->
                if(workInfo != null && workInfo.state.isFinished) {
                    if(workInfo.state == WorkInfo.State.SUCCEEDED) {
                        val newBetterPlayersCount = workInfo.outputData.getLong("nBetterPlayers", Long.MAX_VALUE)

                        if(newBetterPlayersCount != Long.MAX_VALUE) {
                            userViewModel.setBetterPlayersCount(newBetterPlayersCount)
                            Log.d("COUNT", "New count: ${userViewModel.nBetterPlayers.value}")
                        }
                    }
                }
            })
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

            runBackground()
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