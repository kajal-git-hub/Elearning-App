package xyz.penpencil.competishun.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern


@AndroidEntryPoint
class MySMSBroadcastReceiver : BroadcastReceiver() {

    // Listener for receiving OTP
    private var otpReceiveListener: OTPReceiveListener? = null

    // Function to add listener
    fun addListener(otpReceiveListener: OTPReceiveListener?) {
        this.otpReceiveListener = otpReceiveListener
    }

    // This function will be called when an SMS is retrieved
    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            if (extras != null) {
                val status = extras[SmsRetriever.EXTRA_STATUS] as Status?
                if (status != null) when (status.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents
                        val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                        if (message != null) {
                            val pattern = Pattern.compile("(\\d{4})") // Regex to capture 4-digit OTP
                            val matcher = pattern.matcher(message)
                            var otp: String? = ""
                            if (matcher.find()) {
                                otp = matcher.group(0)
                                otpReceiveListener?.onOTPReceived(otp) ?: Log.e("Receiver", "No listener attached")

                                // Broadcast OTP locally within the app
                                Intent("otp-receiver").apply {
                                    putExtra("OTP", otp.toString())
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(this)
                                }
                            } else {
                                otpReceiveListener?.onOTPReceived(null)
                            }
                        } else {
                            Log.e("Receiver", "Message is null")
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        otpReceiveListener?.onOTPTimeOut()
                    }
                }
            }
        }
    }

    // Listener interface to handle OTP events
    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
    }
}