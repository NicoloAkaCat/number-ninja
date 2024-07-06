package dev.nicoloakacat.numberninja

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


fun firestore(): FirebaseFirestore {
    return Firebase.firestore
}

object UserStorage {

    private const val COLLECTION_NAME: String = "users"

    fun createDocument(userData: UserData, uid: String) {
        runBlocking {
            try {
                firestore()
                    .collection(COLLECTION_NAME)
                    .document(uid)
                    .set(userData)
            }
            catch (e: Exception) {
                Log.e("CREATE_DOCUMENT", e.message ?: "An Error Occurred")
                throw e
            }
        }
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
                .addOnFailureListener {
                    Log.e("UPDATE_SCORE", "ERROR")
                }
        }.start()
    }

    fun findOne(uid: String): UserData? {
        return runBlocking {
            try {
                val doc = firestore()
                    .collection(COLLECTION_NAME)
                    .document(uid)

                return@runBlocking doc.get().await().toObject<UserData>()
            }
            catch (e: Exception) {
                Log.e("FIND_ONE", e.message ?: "An Error Occurred")
                throw e
            }
        }
    }
}
