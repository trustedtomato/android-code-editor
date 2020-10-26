package com.example.codeeditor

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

typealias PermissionCallback = (List<Pair<String, Boolean>>) -> Unit
typealias IntentCallback = (Pair<Int, Intent?>) -> Unit

open class RequesterActivity : AppCompatActivity() {
    private val permissionRequests: MutableMap<Int, PermissionCallback> = mutableMapOf()
    private var lastPermissionRequestCode = -1

    fun requestPermissions(permissions: Array<out String>, callback: PermissionCallback) {
        lastPermissionRequestCode += 1
        permissionRequests[lastPermissionRequestCode] = callback
        ActivityCompat.requestPermissions(this, permissions, lastPermissionRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val results = permissions.mapIndexed { index, permission ->
            Pair(
                permission,
                grantResults.size > index && grantResults[index] == PackageManager.PERMISSION_GRANTED
            )
        }
        permissionRequests[requestCode]?.invoke(results)
    }

    private val intentRequests: MutableMap<Int, IntentCallback> = mutableMapOf()
    private var lastIntentRequestCode = -1

    fun requestIntent(intent: Intent, callback: IntentCallback) {
        lastIntentRequestCode += 1
        intentRequests[lastIntentRequestCode] = callback
        startActivityForResult(intent, lastIntentRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        intentRequests[requestCode]?.invoke(Pair(resultCode, data))
    }
}