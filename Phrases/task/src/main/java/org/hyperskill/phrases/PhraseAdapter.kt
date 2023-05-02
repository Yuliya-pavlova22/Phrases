package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import org.hyperskill.phrases.databinding.*


class PhraseAdapter(var myDataset: List<Phrase>,
                    private val binding2: ActivityMainBinding,
                    var db: AppDatabase,
                    private val context: Context
                    ): RecyclerView.Adapter<PhraseAdapter.PhraseHolder>(){
  //  var myPhrase = BankPhrase().generateList()


    class PhraseHolder(item : View): RecyclerView.ViewHolder(item) {
        val binding = ItemPhraseBinding.bind(item)
        fun bind(phrase: Phrase) = with(binding) {
            phraseTextView.text = phrase.phrase
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phrase, parent,false)
        return PhraseHolder(view)

    }

    override fun getItemCount(): Int {
        return  myDataset.size
    }


    override fun onBindViewHolder(holder: PhraseHolder, position: Int) {
        holder.bind(myDataset[position])

        var butDelete = holder.binding.deleteTextView

        butDelete.setOnClickListener {
            val pos = position
            if (pos >= 0 ){
                //   Toast.makeText(holder.itemView.context, "$pos", Toast.LENGTH_SHORT).show()

              //  val db = AppDatabase.getDB(holder.itemView.context as MainActivity)

                // Обработка введенных данных
                var item = myDataset[pos]
                //Создаем новый поток
                Thread{
                    db.getDao().deleteItem(item)
                }.start()

                val mutableList = myDataset.toMutableList() // Создаем новый изменяемый список
                mutableList.removeAt(pos) // Удаляем элемент из списка

                myDataset = mutableList.toList() // Обновляем список myDataset

                // проверяем на наличие уведомлений и отменяем уведомление
                if (myDataset?.isEmpty() == true) {


                        val intent = Intent(context, Receiver::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(
                            context,
                            1,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.cancel(pendingIntent)

                       val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancelAll()


                    binding2.reminderTextView.text = "No reminder set"
                }

                notifyDataSetChanged() // Обновляем RecyclerView

            }
            }
//
//
           }

        }






