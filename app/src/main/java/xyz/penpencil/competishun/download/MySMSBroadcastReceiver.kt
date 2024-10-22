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
    private var otpReceiveListener: OTPReceiveListener? = null

    fun addListener(otpReceiveListener: OTPReceiveListener?) {
        this.otpReceiveListener = otpReceiveListener
    }

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
                            val pattern = Pattern.compile("(\\d{4})")
                            //   \d is for a digit
                            //   {} is the number of digits here 4.
                            val matcher = pattern.matcher(message)
                            var otp: String? = ""
                            if (matcher.find()) {
                                otp = matcher.group(0)
                                // 4 digit number
                                if (this.otpReceiveListener != null)
                                    otpReceiveListener!!.onOTPReceived(otp)
                                else {
                                    Log.e("dasdasjhdasjd", "onReceive: ")
                                }

                                Intent("otp-receiver").apply {
                                    putExtra("OTP", otp.toString())
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(this)
                                }
                            } else {
                                if (this.otpReceiveListener != null) otpReceiveListener!!.onOTPReceived(
                                    null
                                )
                            }
                        } else {
                            Log.e("dasdansbdnbasnd", "onReceive: ")
                        }
                    }

                    CommonStatusCodes.TIMEOUT -> if (this.otpReceiveListener != null) otpReceiveListener?.onOTPTimeOut()
                }
            }
        }
    }

    interface OTPReceiveListener {
        fun onOTPReceived(otp: String?)
        fun onOTPTimeOut()
    }
}