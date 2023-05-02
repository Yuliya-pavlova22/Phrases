package org.hyperskill.phrases

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import org.hyperskill.phrases.databinding.ActivityMainBinding

const val CHANNEL_ID = "org.hyperskill.phrases"

class Receiver() : BroadcastReceiver() {
    //val mainActivity: MainActivity
    var db: AppDatabase? = null

    override fun onReceive(context: Context, intent: Intent) {
        val intent = Intent(context, MainActivity::class.java)
   //      Получить все фразы из базы данных

        var randomPhrase: Phrase? = null

        Thread {
            randomPhrase =  db!!.getDao().get()
        }.run()


        // Настроить уведомление с использованием случайной фразы
        val pIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                //.setSmallIcon(R.drawable.photo)
                .setChannelId(CHANNEL_ID)
                .setContentTitle("Your phrase of the day")
                .setContentText("${randomPhrase?.phrase}")
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.notify(393939, notificationBuilder.build())
        }
    }


//функция создания канала уведомлений
@RequiresApi(Build.VERSION_CODES.O)
fun createNotificationChannel(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        CHANNEL_ID,
        "org.hyperskill.phrases",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "Channel for daily phrase notifications"
    }
    notificationManager.createNotificationChannel(channel)

}



class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDB(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        //заполнение recycleryview из базы данных
        db.getDao().getAll().asLiveData().observe(this) {
            var adapter = PhraseAdapter(it,binding, db, this)!!
            binding.recyclerView.adapter = adapter
        }

        binding.reminderTextView.setOnClickListener {
            //устанавливаем часы и сохраняем время
            var size = 0
            Thread {
                size = db.getDao().getCount()
            }.run()

            if (size == 0) {
                Toast.makeText(this, "No reminder set", Toast.LENGTH_SHORT).show()
            } else {
                val notifications = Notifications
                notifications.setReminder(this,db, binding)
            }
        }

        binding.addButton.setOnClickListener {
            AddPhrase().add(this, db)
        }
    }
}
