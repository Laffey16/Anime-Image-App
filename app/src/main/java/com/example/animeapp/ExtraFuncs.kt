package com.example.animeapp

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID


var isReadPermissionGranted = false
var isWritePermissionGranted = false


object ExtraFuncs : AppCompatActivity() {

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }


    fun downloadImage(context: Context, iv: ImageView) {

        requestPerms(context = context)

        val bmp = iv.drawable.toBitmap()
        lifecycleScope.launch {

            if (!isWritePermissionGranted) {

                displayMessage(context, "Write permission not granted.")

            } else {
                if (saveImage(UUID.randomUUID().toString(), bmp, context)) {

                    displayMessage(context, "Image saved successfully.")
                } else {

                    displayMessage(context, "Failed to save image.")
                }
            }


        }

    }


    private fun requestPerms(context: Context) {
        val isReadPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val isWritePermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val minSdkLevel = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        isReadPermissionGranted = isReadPermission
        isWritePermissionGranted = isWritePermission || minSdkLevel

        val permissionRequest = mutableListOf<String>()
        if (!isWritePermissionGranted) {

            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }
        if (!isReadPermissionGranted) {

            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private fun saveImage(name: String, bmp: Bitmap?, context: Context): Boolean {

        val imageCollection: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {

            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (bmp != null) {
                put(MediaStore.Images.Media.WIDTH, bmp.width)
                put(MediaStore.Images.Media.HEIGHT, bmp.height)
            }

        }

        return try {
            context.contentResolver.insert(imageCollection, contentValues)?.also {

                context.contentResolver.openOutputStream(it).use { outputStream ->

                    if (bmp != null) {

                        if (!bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {

                            throw IOException("Failed to save Bitmap")
                        }
                    }
                }

            } ?: throw IOException("Failed to create Media Store entry")
            true
        } catch (e: IOException) {

            e.printStackTrace()
            false
        }

    }
}






