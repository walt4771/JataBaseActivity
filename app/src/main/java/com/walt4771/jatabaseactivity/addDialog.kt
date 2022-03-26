package com.walt4771.jatabaseactivity

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CustomDialog(context: Context)
{
    private val dialog = Dialog(context)

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_add)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val get_button = dialog.findViewById<Button>(R.id.getButton)
        val text_waitnum = dialog.findViewById<EditText>(R.id.text_waitnum)

        get_button.setOnClickListener {
            onClickListener.onClicked(text_waitnum.text.toString())
            dialog.dismiss()
        }
    }

    interface ButtonClickListener{
        fun onClicked(text_waitnum: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickListener(listener: ButtonClickListener){
        onClickListener = listener
    }

}