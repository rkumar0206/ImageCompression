package com.example.gallermakerkotlin

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class sharedviewmodel : ViewModel() {

    var imageUri: MutableLiveData<Uri> = MutableLiveData()
    var imageBitmap: MutableLiveData<Bitmap> = MutableLiveData()

    fun setimageUri(uri: Uri) {

        imageUri.value = uri
    }

    fun setimageBitmap(bitmap: Bitmap) {

        imageBitmap.value = bitmap
    }

    fun getImageUri(): LiveData<Uri> {

        return imageUri
    }

    fun getImageBitmap(): LiveData<Bitmap> {
        return imageBitmap
    }

}