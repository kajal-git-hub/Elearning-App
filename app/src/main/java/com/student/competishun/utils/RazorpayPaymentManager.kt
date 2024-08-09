import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.razorpay.Checkout
import com.razorpay.CheckoutActivity
import com.student.competishun.utils.SharedPreferencesManager
import org.json.JSONObject

class RazorpayPaymentManager(
    private val activity: Activity,
    private val key: String,
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val onPaymentSuccess: (String?) -> Unit,
    private val onPaymentError: (String?) -> Unit
) {

    fun startPayment(
        orderId: String,
        amount: Double,
        currency: String,
        launcher: ActivityResultLauncher<Intent>
    ) {
        val checkout = Checkout()
        checkout.setKeyID(key)

        try {
            val options = JSONObject().apply {
                put("name", "Competishun")
                put("description", "Order ID: $orderId")
                put("currency", currency)
                put("amount", (amount * 100).toInt()) // Amount in paise

                val prefill = JSONObject().apply {
                    put("email", "kajal@antino.com") // Replace with actual email
                    put("contact", sharedPreferencesManager.mobileNo) // Replace with actual contact
                }
                put("prefill", prefill)
            }
Log.e("getoptions",options.toString())
            // Create an intent for Razorpay checkout
            val checkoutIntent = Intent(activity, CheckoutActivity::class.java).apply {
                putExtra("options", options.toString())
            }

            launcher.launch(checkoutIntent)
        } catch (e: Exception) {
            Log.e("Razorpay", "Error in starting Razorpay Payment", e)
            onPaymentError(e.message)
        }
    }

    fun handleActivityResult(
        resultCode: Int,
        data: Intent?,
        onPaymentSuccess: (String?) -> Unit,
        onPaymentError: (String?) -> Unit
    ) {
        if (resultCode == Activity.RESULT_OK) {
            val response = data?.getStringExtra("response")
            if (response != null) {
                // Handle successful payment
                onPaymentSuccess(response)
            } else {
                // Handle failed payment
                onPaymentError("Payment failed without response")
            }
        } else {
            onPaymentError("Payment cancelled or failed")
        }
    }
}
