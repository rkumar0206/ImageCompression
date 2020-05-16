package com.example.imagecompression

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

class BitmapUtility {

    fun getByteArrayFromBitmap(bitmap: Bitmap, quality: Int): ByteArray {

        var bytes: ByteArray? = null
        try {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            bytes = stream.toByteArray()
            stream.close()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bytes!!
    }

    fun getBitmapFromByteArray(byteArray: ByteArray): Bitmap {

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun convertByteArrayToString(bytes: ByteArray): String {

        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }


    fun convertBitmapToString(bitmap: Bitmap): String {

        val bytes = getByteArrayFromBitmap(bitmap, 100)

        return convertByteArrayToString(bytes)
    }


    fun convertBase64StringToBitmap(encodedString: String): Bitmap {

        val encodesBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return getBitmapFromByteArray(encodesBytes)

    }

    suspend fun convertUriToBitmap(uri: Uri, contentResolver: ContentResolver): Bitmap =
        withContext(Dispatchers.IO) {

            val mBitmap: Bitmap = if (Build.VERSION.SDK_INT < 28) {

                MediaStore.Images.Media.getBitmap(contentResolver, uri)

            } else {

                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        contentResolver,
                        uri
                    )
                )

            }
            return@withContext mBitmap
        }


}