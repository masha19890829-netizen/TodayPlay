package com.todayplay.app.auth

import android.annotation.SuppressLint
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.todayplay.app.BuildConfig
import com.todayplay.app.model.AccountProvider
import com.todayplay.app.model.AccountSession
import com.todayplay.app.model.AccountSignInResult
import java.util.UUID

class GoogleAccountGateway(context: Context) {
    private val signInContext = context
    private val appContext = context.applicationContext
    private val credentialManager = CredentialManager.create(appContext)

    val isConfigured: Boolean
        get() = BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()

    @SuppressLint("CredentialManagerSignInWithGoogle")
    suspend fun signIn(): AccountSignInResult {
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID.trim()
        if (clientId.isBlank()) {
            return AccountSignInResult(message = "Google sign-in needs GOOGLE_WEB_CLIENT_ID in release_config.properties.")
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(false)
            .setNonce(UUID.randomUUID().toString())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                context = signInContext,
                request = request,
            )
            val credential = result.credential
            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                AccountSignInResult(
                    session = AccountSession(
                        provider = AccountProvider.Google,
                        displayName = googleCredential.displayName ?: googleCredential.id,
                        email = googleCredential.id,
                        avatarUrl = googleCredential.profilePictureUri?.toString(),
                        backendVerified = false,
                        idTokenForBackend = googleCredential.idToken,
                    ),
                    message = "Signed in with Google profile. Server token verification is not connected yet.",
                )
            } else {
                AccountSignInResult(message = "Google sign-in returned an unsupported credential.")
            }
        } catch (_: GetCredentialCancellationException) {
            AccountSignInResult(message = "Google sign-in was cancelled.")
        } catch (_: NoCredentialException) {
            AccountSignInResult(message = "No Google credential is available on this device.")
        } catch (_: GoogleIdTokenParsingException) {
            AccountSignInResult(message = "Google credential could not be parsed.")
        } catch (error: GetCredentialException) {
            AccountSignInResult(message = error.message ?: "Google sign-in failed.")
        }
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }
}
