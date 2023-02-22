package com.nancone.criminalintent.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.nancone.criminalintent.R
import java.util.*

const val ARG_BITMAP = "bitmap"
class CrimeImgDialogFragment : DialogFragment() {
    lateinit var imgBitmap: Bitmap
    lateinit var dialogImgView: ImageView
    lateinit var dialogBlankTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_img_dialog, container, false)
        dialogImgView = view.findViewById(R.id.crime_dialog_img_view) as ImageView
        dialogBlankTextView = view.findViewById(R.id.crime_blank_text_view) as TextView

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments?.getParcelable<Bitmap>(ARG_BITMAP) == null) {
            dialogImgView.visibility = View.GONE
            dialogImgView.setImageBitmap(null)
            dialogBlankTextView.visibility = View.VISIBLE
        }
        else
        {
            dialogImgView.visibility = View.VISIBLE
            dialogImgView.setImageBitmap(arguments?.getParcelable(ARG_BITMAP))
            dialogBlankTextView.visibility = View.GONE
        }
    }
    companion object {
        fun newInstance(bitmap: Bitmap?) : CrimeImgDialogFragment {
            val args = Bundle().apply {
                bitmap?.let {
                    putParcelable(ARG_BITMAP, it)
                }
            }
            return CrimeImgDialogFragment().apply {
                arguments = args
            }
        }
    }
}