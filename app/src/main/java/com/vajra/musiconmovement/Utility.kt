package com.vajra.musiconmovement

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat


/**
 * This Object is Created to keep the Main Class free from unnecessary methods which are not relevant to core functionality
 */
object Utility {
    /**
     * We show this custom dialog to alert user that please go to settings to enable camera permission
     */
    fun showMandatoryPermissionsNeedDialog(context: Context) {
        AlertDialog.Builder(context).apply {
            setCancelable(true)
            setMessage(context.getString(R.string.mandatory_permission_access_required))
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                intent.data = uri
                context.startActivity(intent)
            }
        }.show()
    }

    /**
     * We show this custom dialog to alert user denied camera permission
     */
    fun showPermissionDeniedDialog(
        context: Context,
        permissions: String,
        permissionRequestCode: Int
    ) {
        AlertDialog.Builder(context).apply {
            setCancelable(true)
            setMessage(context.getString(R.string.permission_camera_access_required))
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                ActivityCompat.requestPermissions(
                    context as MainActivity,
                    arrayOf(permissions),
                    permissionRequestCode
                )
            }
        }.show()
    }

    fun setCameraPermissionDeniedOnce(context: Context) {
        val myPrefs = "com.vajra.musiconmovement.MY_PREFERENCE_FILE"
        val sp: SharedPreferences = context.getSharedPreferences(myPrefs, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putBoolean("cameraPermissionDeniedOnce", true)
        editor.apply()
    }

    fun getCameraPermissionDeniedOnce(context: Context): Boolean {
        val myPrefs = "com.vajra.musiconmovement.MY_PREFERENCE_FILE"
        val sp: SharedPreferences = context.getSharedPreferences(myPrefs, Context.MODE_PRIVATE)
        return sp.getBoolean("cameraPermissionDeniedOnce", false)
    }
}