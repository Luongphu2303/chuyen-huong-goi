package com.anhphu.phoneprefix
import android.content.Context
import android.net.Uri
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle

class CallRedirectService : CallRedirectionService() {
    override fun onPlaceCall(handle: Uri, initialPhoneAccount: PhoneAccountHandle, allowInteractiveResponse: Boolean) {
        val prefs = getSharedPreferences("Config", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("ENABLED", false)
        val targetNumber = prefs.getString("TARGET_NUMBER", "") ?: ""
        if (isEnabled && targetNumber.isNotBlank()) {
            val redirectUri = Uri.fromParts("tel", targetNumber, null)
            redirectCall(redirectUri, initialPhoneAccount, false)
        } else {
            placeCallUnmodified()
        }
    }
}
