package com.example.snapgallery.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.snapgallery.R

class HiddenMultimedia : AppCompatActivity() {
    private var btn_fp: Button? = null
    private var btn_fppin: android.widget.Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_multimedia)

        //assign button reference from view
        btn_fp = findViewById<Button>(R.id.btn_fp);
        btn_fppin = findViewById<Button>(R.id.btn_fppin);

        //create new method to check whether support or not
        checkBioMetricSupported();

        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this@HiddenMultimedia,
            executor, object : AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                }

                // this method will automatically call when it is succeed verify fingerprint
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@HiddenMultimedia, MainActivity::class.java)
                    this@HiddenMultimedia.startActivity(intent)

                    finish()
                }

                // this method will automatically call when it is failed verify fingerprint
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    //attempt not regconized fingerprint
                    Toast.makeText(
                        applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        //perform action button only fingerprint
        btn_fp?.setOnClickListener { view ->

            // call method launch dialog fingerprint
            val promptInfo: BiometricPrompt.PromptInfo.Builder = dialogMetric()
            promptInfo.setNegativeButtonText("Cancel")

            //activate callback if it succeed
            biometricPrompt.authenticate(promptInfo.build())
        }

        //perform action button fingerprint with PIN code input
        btn_fppin?.setOnClickListener { view ->

            // call method launch dialog fingerprint
            val promptInfo: BiometricPrompt.PromptInfo.Builder = dialogMetric()
            promptInfo.setDeviceCredentialAllowed(true)

            //activate callback if it succeed
            biometricPrompt.authenticate(promptInfo.build())
        }
    }

    private fun dialogMetric(): PromptInfo.Builder {
        //Show prompt dialog
        return PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your biometric credential")
    }

    fun checkBioMetricSupported() {
        val manager: BiometricManager = BiometricManager.from(this)
        var info = ""
        when (manager.canAuthenticate(BIOMETRIC_WEAK or BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                info = "App can authenticate using biometrics."
                enableButton(true)
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                info = "No biometric features available on this device."
                enableButton(false)
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                info = "Biometric features are currently unavailable."
                enableButton(false)
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                info = "Need register at least one finger print"
                enableButton(false, true)
            }

            else -> {
                info = "Unknown cause"
                enableButton(false)
            }
        }

        //set message to text view so we can see what happen with sensor device
        val txinfo = findViewById<TextView>(R.id.tx_info)
        txinfo.text = info
    }

    fun enableButton(enable: Boolean) {
        //just enable or disable button
        btn_fp?.isEnabled = enable
        btn_fppin?.isEnabled = true
    }

    fun enableButton(enable: Boolean, enroll: Boolean) {
        enableButton(enable)
        if (!enroll) return
        // Prompts the user to create credentials that your app accepts.
        //Open settings to set credential fingerprint or PIN
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
        enrollIntent.putExtra(
            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        )
        startActivity(enrollIntent)
    }
}