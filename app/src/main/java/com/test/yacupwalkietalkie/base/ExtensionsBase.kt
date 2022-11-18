package com.test.yacupwalkietalkie.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

fun Toast(text: String) {
    android.widget.Toast.makeText(App.app, text, android.widget.Toast.LENGTH_LONG).show()
}

fun Boolean?.safe(): Boolean = this == true

fun <T> T.toSet() = setOf(this)

@SuppressLint("MissingPermission")
fun WifiP2pManager.connectToAddress(
    channel: WifiP2pManager.Channel,
    address: String,
    onError: () -> Unit = {},
    onSuccess: () -> Unit,
) {
    val config = WifiP2pConfig().apply {
        deviceAddress = address
    }
    connect(
        channel,
        config,
        object : WifiP2pManager.ActionListener {
            override fun onSuccess() = onSuccess.invoke()
            override fun onFailure(p0: Int) = onError.invoke()
        }
    )
}

fun AppCompatActivity.tryTurnWifiOn() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY).apply(::startActivity)
    } else {
        val wifiManager = application.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true
    }
}

fun makeOnBackgroundGlobal(action: suspend CoroutineScope.() -> Unit) = GlobalScope.launch(
    context = Dispatchers.IO,
    block = action
)

fun Socket.tryGetOps(): OutputStream? = try {
    getOutputStream()
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun Socket.tryGetIps(): InputStream? = try {
    getInputStream()
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun Socket.closeQuietly() {
    try {
        close()
    } catch (e: AssertionError) {
        throw e
    } catch (rethrown: RuntimeException) {
        if (rethrown.message == "bio == null") {
            // Conscrypt in Android 10 and 11 may throw closing an SSLSocket. This is safe to ignore.
            // https://issuetracker.google.com/issues/177450597
            return
        }
        throw rethrown
    } catch (_: Exception) {
    }
}


