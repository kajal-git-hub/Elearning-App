package xyz.penpencil.competishun.utils



import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.ArrayList
import java.util.Arrays

class AppSignatureHashHelper(context: Context) : ContextWrapper(context) {

    companion object {
        const val TAG = "AppSignatureHashHelper"
        private const val HASH_TYPE = "SHA-256"
        const val NUM_HASHED_BYTES = 9
        const val NUM_BASE64_CHAR = 11
    }

    /**
     * Get all the app signatures for the current package
     *
     * @return List of app signature hashes
     */
    fun getAppSignatures(): ArrayList<String> {
        val appSignaturesHashs = ArrayList<String>()

        try {
            // Get all package details
            val packageName = packageName
            val packageManager = packageManager
            val signatures: Array<Signature> =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures

            for (signature in signatures) {
                val hash = hash(packageName, signature.toCharsString())
                hash?.let {
                    appSignaturesHashs.add(it)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Package not found", e)
        }
        return appSignaturesHashs
    }

    @TargetApi(19)
    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        return try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

            base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "No Such Algorithm Exception", e)
            null
        }
    }
}


