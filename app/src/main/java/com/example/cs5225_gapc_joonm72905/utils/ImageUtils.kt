package com.example.cs5225_gapc_joonm72905.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    fun resizeImages(
        context: Context,
        uri: Uri,
        targetWidth: Int = 640,
        targetHeight: Int = 640,
    ): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun saveBitmapToTempFile(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val tempFile = File(context.cacheDir, "resized_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(tempFile)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            outputStream.flush()
            outputStream.close()

            Uri.fromFile(tempFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}