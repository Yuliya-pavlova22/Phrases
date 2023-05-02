package org.hyperskill.phrases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.phrases.databinding.*

class PhraseAdapter(
    var myDataset: MutableList<Phrase>,
    private val binding2: ActivityMainBinding,
    var db: AppDatabase,
    private val context: Context
) : RecyclerView.Adapter<PhraseAdapter.PhraseHolder>() {

    class PhraseHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemPhraseBinding.bind(item)
        fun bind(phrase: Phrase) = with(binding) {
            phraseTextView.text = phrase.phrase
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phrase, parent, false)
        return PhraseHolder(view)
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun onBindViewHolder(holder: PhraseHolder, position: Int) {
        holder.bind(myDataset[position])

        var butDelete = holder.binding.deleteTextView

        butDelete.setOnClickListener {
            val pos = position
            if (pos >= 0) {
                var item = myDataset[pos]

                Thread {
                    db.getDao().deleteItem(item)
                }.start()

                myDataset.remove(item)
            }
            notifyDataSetChanged() // Обновляем RecyclerView

            // проверяем на наличие уведомлений и отменяем уведомление
            if (myDataset?.isEmpty()) {
                Notifications.cancelReminder(context)
                binding2.reminderTextView.text = "No reminder set"
            }
        }
    }
}
