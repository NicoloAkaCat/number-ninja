package dev.nicoloakacat.numberninja.background

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dev.nicoloakacat.numberninja.R
import dev.nicoloakacat.numberninja.db.UserStorage

class RankTrackerWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {

        val currentBetterPlayersCount = inputData.getLong("nBetterPlayers", Long.MAX_VALUE)
        val uid = inputData.getString("uid")
        val maxScore = inputData.getInt("maxScore", 0)

        try{
            val newBetterPlayersCount = UserStorage.countBetterPlayersThan(maxScore)

            if(newBetterPlayersCount > currentBetterPlayersCount) {
                Log.d("WORKER", "Someone beat your high score :(")
                UserStorage.updateBetterPlayersCount(newBetterPlayersCount, uid!!)

                val notification = Notification(
                    title = applicationContext.getString(R.string.worker_notification_title),
                    body = applicationContext.getString(R.string.worker_notification_body),
                    channel = "default_channel",
                    context = applicationContext,
                    autoCancel = true
                )
                NotificationHandler.sendNotification(notification)

                return Result.success(
                    Data.Builder()
                        .putLong("nBetterPlayers", newBetterPlayersCount)
                        .build()
                )
            }
            else if(newBetterPlayersCount < currentBetterPlayersCount) {
                Log.d("WORKER", "You beat someone score!")
                UserStorage.updateBetterPlayersCount(newBetterPlayersCount, uid!!)
            }
            else {
                Log.d("WORKER", "No updates")
            }
        }catch (e: Exception){
            return Result.failure()
        }

        return Result.success()
    }
}