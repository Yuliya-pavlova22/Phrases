package org.hyperskill.phrases

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout

class AddPhrase {
    fun add(
        contextAlert: Context,
        db: AppDatabase,
        onAdd: (Phrase) -> Unit
    ) {
        val builder = AlertDialog.Builder(contextAlert)
        builder.setTitle("Add phrase")

        val editText = EditText(contextAlert)
        editText.id = R.id.editText
        editText.hint = "Phrase"

        val layout = LinearLayout(contextAlert)
        layout.orientation = LinearLayout.VERTICAL

        layout.addView(editText)

        builder.setView(layout)

        builder.setPositiveButton("ADD") { dialog, which ->
            val text1 = editText.text.toString()
            var item: Phrase = Phrase(0, text1)

            Thread {
                db.getDao().insertItem(item)
            }.start()

            onAdd(item)
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
