package coffeecode.co.learnloader

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.app.Activity



class PermissionManager {
    companion object{
        fun check(activity: Activity, permission: String, requestCode: Int) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }
        }

        fun isGranted(activity: Activity, permission: String): Boolean {
            return ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

}