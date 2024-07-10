package dev.nicoloakacat.numberninja.db

import android.util.Log
import com.firebase.ui.auth.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import dev.nicoloakacat.numberninja.Nationality
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


fun firestore(): FirebaseFirestore {
    return Firebase.firestore
}

object UserStorage {

    private const val COLLECTION_NAME: String = "users"

    suspend fun createDocument(userData: UserData, uid: String) {
        try {
            firestore()
                .collection(COLLECTION_NAME)
                .document(uid)
                .set(userData)
                .await()
        }
        catch (e: Exception) {
            Log.e("CREATE_DOCUMENT", e.message ?: "An Error Occurred")
            throw e
        }
    }

    fun updateBetterPlayersCount(count: Long, uid: String) {
        Thread {
            firestore()
                .collection(COLLECTION_NAME)
                .document(uid)
                .update("nBetterPlayers", count)
                .addOnSuccessListener {
                    Log.d("UPDATE_BETTER_PLAYERS", "SUCCESS: $count")
                }
                .addOnFailureListener { e ->
                    Log.d("UPDATE_BETTER_PLAYERS", "ERROR: ${e.message}")
                }
        }.start()
    }

    fun updateScore(newScore: Int, uid: String) {
        Thread {
            firestore()
                .collection(COLLECTION_NAME)
                .document(uid)
                .update("maxScore", newScore)
                .addOnSuccessListener {
                    Log.e("UPDATE_SCORE", "SUCCESS: $newScore")
                }
                .addOnFailureListener { e ->
                    Log.e("UPDATE_SCORE", "ERROR: ${e.message}")
                }
        }.start()
    }

    suspend fun updateData(newName: String, newNationality: String, uid: String) {
        try{
            firestore()
                .collection(COLLECTION_NAME)
                .document(uid)
                .update("name", newName, "nationality", newNationality.replace(" ", "_"))
                .await()
        }catch (e: Exception){
            Log.e("UPDATE_DATA", e.message ?: "An Error Occurred")
            throw e
        }
    }

    suspend fun findOne(uid: String): UserData? {
        try {
            val doc = firestore()
                .collection(COLLECTION_NAME)
                .document(uid)

            return doc.get().await().toObject<UserData>()
        }
        catch (e: Exception) {
            Log.e("FIND_ONE", e.message ?: "An Error Occurred")
            throw e
        }
    }

    suspend fun findAll(
        orderBy: String = "maxScore",
        direction: Query.Direction = Query.Direction.DESCENDING,
        limit: Long = 0,
        offset: Int = 0
    ): MutableList<UserData> {
        val users = mutableListOf<UserData>()
        try {
            var doc = firestore()
                .collection(COLLECTION_NAME)
                .orderBy(orderBy, direction)

            if(limit > 0) doc = doc.limit(limit)
            if(offset > 0) doc = doc.startAfter(offset)

            val fetchedUsers = doc.get().await()
            for(user in fetchedUsers) {
                //Log.d("USER", user.toString())
                users.add(user.toObject<UserData>())
            }
            return users
        }
        catch (e: Exception) {
            Log.e("FIND_ALL", e.message ?: "An Error Occurred")
            throw e
        }
    }

    suspend fun countBetterPlayersThan(maxScore: Int): Long {
        try {
            val doc = firestore()
                .collection(COLLECTION_NAME)
                .whereGreaterThan("maxScore", maxScore)

            return doc.count().get(AggregateSource.SERVER).await().count
        }catch (e: Exception){
            Log.e("COUNT_BETTER_PLAYERS_THAN", e.message ?: "An Error Occurred")
            throw e
        }
    }
}
