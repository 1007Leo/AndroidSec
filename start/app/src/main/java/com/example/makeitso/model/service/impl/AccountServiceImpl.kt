/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.makeitso.model.service.impl

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.makeitso.model.User
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.impl.StorageServiceImpl.Companion.TASK_COLLECTION
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class AccountServiceImpl @Inject constructor(
  private val auth: FirebaseAuth,
  private val firestore: FirebaseFirestore,
) : AccountService {

  override val currentUserId: String
    get() = auth.currentUser?.uid.orEmpty()

  override val hasUser: Boolean
    get() = auth.currentUser != null && !auth.currentUser!!.isAnonymous

  override val currentUser: Flow<User>
    get() = callbackFlow {
      val listener =
        FirebaseAuth.AuthStateListener { auth ->
          this.trySend(auth.currentUser?.let { User(it.uid, it.isAnonymous) } ?: User())
        }
      auth.addAuthStateListener(listener)
      awaitClose { auth.removeAuthStateListener(listener) }
    }

  override suspend fun authenticate(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).await()
    val newUser = User(userId = currentUserId, login = email, authMethod = MAIL_LOGIN_TYPE, anonymous = false)
    saveCurrentUserData(newUser)
  }

  override suspend fun googleSignIn(context: Context): Flow<Result<AuthResult>> {
    val firebaseAuth = FirebaseAuth.getInstance()
    return callbackFlow {
      try {
        // Initialize Credential Manager
        val credentialManager: CredentialManager = CredentialManager.create(context)

        // Generate a nonce (a random number used once)
        val ranNonce: String = UUID.randomUUID().toString()
        val bytes: ByteArray = ranNonce.toByteArray()
        val md: MessageDigest = MessageDigest.getInstance("SHA-1")
        val digest: ByteArray = md.digest(bytes)
        val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }

        // Set up Google ID option
        val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption
          .Builder("1073765350379-eneshftft24btihd41nhclge7dqtokpi.apps.googleusercontent.com")
          .setNonce(hashedNonce)
          .build()

        // Request credentials
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
          .addCredentialOption(signInWithGoogleOption)
          .build()

        // Get the credential result
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        // Check if the received credential is a valid Google ID Token
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdTokenCredential =
            GoogleIdTokenCredential.createFrom(credential.data)
          val authCredential =
            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
          val authResult = firebaseAuth.signInWithCredential(authCredential).await()
          trySend(Result.success(authResult))
          createUserData()
        } else {
          throw RuntimeException("Received an invalid credential type")
        }
      } catch (e: GetCredentialCancellationException) {
        trySend(Result.failure(Exception("Sign-in was canceled. Please try again.")))

      } catch (e: Exception) {
        trySend(Result.failure(e))
      }
      awaitClose { }
    }
  }

  override suspend fun sendRecoveryEmail(email: String) {
    auth.sendPasswordResetEmail(email).await()
  }

  override suspend fun linkAccount(email: String, password: String) {
    val credential = EmailAuthProvider.getCredential(email, password)
    auth.signInAnonymously().await()
    auth.currentUser!!.linkWithCredential(credential).await()
    createUserData()
  }

  override suspend fun createUserData() {
    val loginMethod: String =
      if (auth.currentUser?.email?.split("@")?.last() == "gmail.com") {
        GOOGLE_LOGIN_TYPE
      }
      else {
        MAIL_LOGIN_TYPE
      }

    val newUser = User(
      userId = currentUserId,
      name = auth.currentUser?.displayName?: "",
      authMethod = loginMethod,
      login = auth.currentUser?.email.toString(),
      anonymous = false)
    saveCurrentUserData(newUser)
  }

  override suspend fun createUserFromId(id: String) {

    val document = firestore.collection(USER_COLLECTION).whereEqualTo("userId", id).get().await()
    val user = document.first().toObject(User::class.java)
    saveCurrentUserData(user)
  }

  override suspend fun deleteAccount() {
    deleteCurrentUserTasks(currentUserId)
    deleteCurrentUserData(currentUserId)
    auth.currentUser!!.delete().await()
  }

  override suspend fun signOut() {
    if (auth.currentUser!!.isAnonymous) {
      auth.currentUser!!.delete()
    }
    auth.signOut()
  }

  override suspend fun getCurrentUserData(): User {
    val document = firestore.collection(USER_COLLECTION).whereEqualTo("userId", currentUserId).get().await()
    val user = document.first().toObject(User::class.java)
    return user
  }

  override suspend fun saveCurrentUserData(user: User) {
    val userWithUserId = user.copy(userId = currentUserId, anonymous = false)
    val cnt = firestore.collection(USER_COLLECTION).whereEqualTo("userId", currentUserId).get().await()
    if (cnt.size() == 0){
      firestore.collection(USER_COLLECTION).add(userWithUserId).await().id
    }
  }

  override suspend fun updateCurrentUserData(user: User) {
    val recordId = firestore.collection(USER_COLLECTION).whereEqualTo("userId", user.userId).get().await().first().id
    firestore.collection(USER_COLLECTION).document(recordId).set(user).await()
  }

  override suspend fun deleteCurrentUserData(id: String) {
    val documentId = firestore.collection(USER_COLLECTION).whereEqualTo("userId", id).get().await().first().id
    firestore.collection(USER_COLLECTION).document(documentId).delete().await()
  }

  override suspend fun deleteCurrentUserTasks(id: String) {
    val tasksIds = firestore.collection(TASK_COLLECTION).whereEqualTo("userId", id).get().await().documents.map { it.id }
    val batch = firestore.batch()
    for (taskId in tasksIds) {
      val taskRef = firestore.collection(TASK_COLLECTION).document(taskId)
      batch.delete(taskRef)
    }
    batch.commit().await()
  }

  companion object {
    private const val USER_COLLECTION = "users"
//    private const val LINK_ACCOUNT_TRACE = "linkAccount"
//    private const val SAVE_USER_DATA_TRACE = "saveUserData"
    const val MAIL_LOGIN_TYPE = "Logged in via Email"
    const val GOOGLE_LOGIN_TYPE = "Logged in via Gmail"
  }
}
