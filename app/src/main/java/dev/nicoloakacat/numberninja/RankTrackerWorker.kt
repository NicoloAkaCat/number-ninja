package dev.nicoloakacat.numberninja

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dev.nicoloakacat.numberninja.db.UserStorage

class RankTrackerWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {

        val currentBetterPlayersCount = inputData.getLong("nBetterPlayers", Long.MAX_VALUE)
        val uid = inputData.getString("uid")
        val maxScore = inputData.getInt("maxScore", 0)

        val newBetterPlayersCount = UserStorage.countBetterPlayersThan(maxScore)

        if(newBetterPlayersCount > currentBetterPlayersCount) {
            Log.d("WORKER", "Someone beat your high score :(")
            UserStorage.updateBetterPlayersCount(newBetterPlayersCount, uid!!)

            val notification = Notification(
                title = "Qualcuno ha battuto il tuo record :(",
                body = "Oh no! Qualcuno ha battuto il tuo record mentre eri offline...",
                channel = "default_channel",
                context = applicationContext,
                autoCancel = true
            )
            NotificationManager().sendNotification(notification)

            val returnData = Data.Builder()
                .putLong("nBetterPlayers", newBetterPlayersCount)
            return Result.success(returnData.build())
        }
        else if(newBetterPlayersCount < currentBetterPlayersCount) {
            Log.d("WORKER", "You beat someone score!")
            UserStorage.updateBetterPlayersCount(newBetterPlayersCount, uid!!)
        }
        else {
            Log.d("WORKER", "No updates")
        }

        return Result.success()
    }
}