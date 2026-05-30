package com.example.mviimageeditor.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

class PermissionManager(
    private val context: Context,
    private val onPermissionResult: (Map<String, Boolean>) -> Unit,
) {
    private var launcher: ActivityResultLauncher<Array<String>>? =
        (context as? ComponentActivity)?.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            onPermissionResult(permissions)
        }

    fun requestPermission(permissions: List<String>) {
        launcher?.launch(
            permissions.toTypedArray(),
        )
    }

    private fun checkPermissionsFromManifest(): List<String> {
        val permissionNeedRequests = mutableListOf<String>()
        try {
            // Lấy thông tin về package của ứng dụng
            val packageInfo =
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_PERMISSIONS,
                )

            // Danh sách quyền được khai báo trong AndroidManifest.xml
            val requestedPermissions = packageInfo.requestedPermissions

            // Duyệt qua từng quyền để kiểm tra xem đã được cấp chưa
            requestedPermissions?.forEach { permission ->
                when (ContextCompat.checkSelfPermission(context, permission)) {
                    PackageManager.PERMISSION_GRANTED -> {
                    }

                    PackageManager.PERMISSION_DENIED -> {
                        permissionNeedRequests.add(permission)
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return permissionNeedRequests
    }
}

@Composable
fun PermissionRequester(
    permissions: List<String>,
    onPermissionResult: (Map<String, Boolean>) -> Unit,
) {
    val context = LocalContext.current

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            onPermissionResult(result)
        }

    LaunchedEffect(Unit) {
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
        }
        if (notGrantedPermissions.isNotEmpty()) {
            permissionLauncher.launch(notGrantedPermissions.toTypedArray())
        }
    }
}
