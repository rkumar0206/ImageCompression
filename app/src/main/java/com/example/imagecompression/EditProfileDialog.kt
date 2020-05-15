package com.example.gallermakerkotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.imagecompression.R

import kotlinx.android.synthetic.main.fragment_edit_profile_dialog.*


class EditProfileDialog : DialogFragment() {

    companion object {
        const val PICK_PROFILE_IMAGE_REQUEST_CODE = 123
        const val TAKE_PROFILE_IMAGE_REQUEST_CODE = 124
    }

    private lateinit var viewModel: sharedviewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setOnShowListener {

            dialog?.setTitle("Choose Option")
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        choose_from_file.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, PICK_PROFILE_IMAGE_REQUEST_CODE)

        }

        choose_from_camera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, TAKE_PROFILE_IMAGE_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == PICK_PROFILE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {

                val uri = data.data
                //send the uri to show_profile_fragment and dismiss the dialog

                uri?.let { uri ->

                    viewModel.setimageUri(uri)

                    dialog?.dismiss()

                }

            }
        } else if (requestCode == TAKE_PROFILE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {

                val bitmap: Bitmap = data.extras?.get("data") as Bitmap

                //send the bitmap to show_profile_fragment and dismiss the dialog
                viewModel.setimageBitmap(bitmap)
                //mListener.getImageBitmap(bitmap)
                dialog?.dismiss()
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(sharedviewmodel::class.java)

    }

}
