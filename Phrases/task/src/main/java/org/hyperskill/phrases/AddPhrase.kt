package org.hyperskill.phrases

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout

class AddPhrase {
    fun add(contextAlert: Context, db: AppDatabase){
        val builder = AlertDialog.Builder(contextAlert)
        builder.setTitle("Add phrase")

        val editText = EditText(contextAlert)

        editText.hint = "Phrase"

        val layout = LinearLayout(contextAlert)
        layout.orientation = LinearLayout.VERTICAL

        layout.addView(editText)

        builder.setView(layout)

        builder.setPositiveButton("ADD") { dialog, which ->
            val text1 = editText.text.toString()

            // Обработка введенных данных
            var item = Phrase(null,
                text1)
            //Создаем новый поток
            Thread{
                db.getDao().insertItem(item)
            }.start()

        }


        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }

}