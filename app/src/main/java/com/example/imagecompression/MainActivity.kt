package com.example.imagecompression

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.gallermakerkotlin.EditProfileDialog
import com.example.gallermakerkotlin.sharedviewmodel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), View.OnClickListener {


    companion object {
        const val REQUEST_CODE = 122

    }

    //private var account: GoogleSignInAccount? = null
    //private lateinit var auth: FirebaseAuth
    //lateinit var googleSignInClient: GoogleSignInClient
    //lateinit var navController: NavController

    private var permissionGranted = false



    private lateinit var viewModel: sharedviewmodel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        show_profile_image.setOnClickListener(this)
        add_profile_image.setOnClickListener(this)



        viewModel = ViewModelProvider(this).get(sharedviewmodel::class.java)

        viewModel.getImageUri().observe(this,
            Observer<Uri> { imageUri ->

                val mSelectedtImageUri = imageUri

                CoroutineScope(IO).launch {
                    set_compressedImage_of_type_Uri(imageUri = mSelectedtImageUri, quality = 100)
                }
            })

        viewModel.getImageBitmap().observe(this,
            Observer<Bitmap> { bitmap ->

                val mSelectedImageBitmap = bitmap
                show_hide_Progressbar()
                val bytes = BitmapUtility().getByteArrayFromBitmap(mSelectedImageBitmap, 100)

                //val compressed_Image = BitmapUtility().getBitmapFromByteArray(bytes)  /*getCompressedImage_from_Bitmap(mSelectedImageBitmap!!, 100)*/

                Glide.with(this)
                    .load(bytes)
                    .into(show_profile_image)

                show_hide_Progressbar()
            })


    }


    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.show_profile_image -> openDialog()
            R.id.add_profile_image -> openDialog()
        }

    }

    private fun openDialog() {

        verifyPermissions()

        val dialog = EditProfileDialog()
        dialog.show(supportFragmentManager, "selectProfileDialog")

    }


    private fun verifyPermissions() {
        var permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )

        if (ContextCompat.checkSelfPermission(
                this,
                permissions[0]
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                permissions[1]
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                permissions[2]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {

            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                permissionGranted = true
            }
        }
    }


    fun show_hide_Progressbar() {

        GlobalScope.launch(Main) {

            if (progressBar.visibility == View.VISIBLE) {

                progressBar.visibility = View.INVISIBLE
            } else {
                progressBar.visibility = View.VISIBLE
            }

        }

    }

    fun showToast(message: String) {

        GlobalScope.launch(Main) {

            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun setImage(image: Bitmap) {

        Glide.with(this)
            .load(image)
            .into(show_profile_image)
    }

    /**
     *  Below set of functions are used for compressing images which are in Uri or Bitmap format
     */

    suspend fun set_compressedImage_of_type_Uri(imageUri: Uri?, quality: Int) {

        if (imageUri != null) {

            show_hide_Progressbar()
            val result: Bitmap? = BitmapUtility().convertUriToBitmap(
                imageUri,
                contentResolver
            )   /*convertUriToBitmap(imageUri, contentResolver)*/

            if (result != null) {
                doingSomethinginMainThread(result, quality)
            } else {

                showToast("Unable to convert the uri to bitmap")
            }

        }
    }



    private suspend fun doingSomethinginMainThread(bitmap: Bitmap, quality: Int) {

        withContext(Main) {
            val imagebyteArray = BitmapUtility().getByteArrayFromBitmap(
                bitmap,
                quality
            )  /*getBytesFromBitmap(bitmap, quality)*/
            val b_bitmap =
                BitmapUtility().getBitmapFromByteArray(imagebyteArray)   /*BitmapFactory.decodeByteArray(imagebyteArray, 0, imagebyteArray!!.size)*/

            setImage(b_bitmap)
            show_hide_Progressbar()
        }

    }

}
