package com.walt4771.a080callcheckin

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText

class CustomDialog(context: Context)
{
    private val dialog = Dialog(context)

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_add)
        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val textedit_addname = dialog.findViewById<EditText>(R.id.textedit_addname)
        val textedit_addcontact = dialog.findViewById<EditText>(R.id.textedit_addcontact)
        val cancel_button = dialog.findViewById<Button>(R.id.cancel_button)
        val confirm_button = dialog.findViewById<Button>(R.id.finish_button)

        cancel_button.setOnClickListener {
            dialog.dismiss()
        }

        confirm_button.setOnClickListener {
            onClickListener.onClicked(textedit_addname.text.toString(),textedit_addcontact.text.toString())
            dialog.dismiss()
        }
    }

    interface ButtonClickListener{
        fun onClicked(addName: String, addContact: String)
    }

    private lateinit var onClickListener: ButtonClickListener

    fun setOnClickListener(listener: ButtonClickListener){
        onClickListener = listener
    }




}