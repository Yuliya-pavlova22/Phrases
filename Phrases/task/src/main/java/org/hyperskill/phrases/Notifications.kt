package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.hyperskill.phrases.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class Notifications() {

    companion object {
        var reminderTime: Date? = null
        const val CHANNEL_ID = "org.hyperskill.phrases"

        @RequiresApi(Build.VERSION_CODES.O)
        fun createNotificationChannel(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "org.hyperskill.phrases",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for daily phrase notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        fun setReminder(context: Context, db: AppDatabase, binding: ActivityMainBinding) {
            val cal = Calendar.getInstance()
            val timeSetListener =
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    cal.set(Calendar.MINUTE, minute)

                    val currentTime = Calendar.getInstance()
                    if (cal.timeInMillis < currentTime.timeInMillis) {
                        cal.add(Calendar.DAY_OF_YEAR, 1)
                    }

                    val receiver = Receiver()
                    receiver.db = db

                    val intent = Intent(context, MainActivity::class.java).apply {
                        action = "my_action"
                    }


                    val pendingIntent =
                        PendingIntent.getBroadcast(
                            context,
                            1,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                 //   настраиваем уведомления
                    val am: AlarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    am.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        cal.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )

                    context.registerReceiver(receiver, IntentFilter("my_action"))

                    reminderTime = cal.time
//                    Toast.makeText(context, "$reminderTime",Toast.LENGTH_SHORT).show()

                    binding.reminderTextView.text =
                        "Reminder set for ${SimpleDateFormat("HH:mm").format(reminderTime)}"
                }

            TimePickerDialog(
                context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        fun cancelReminder(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
            if (pendingIntent != null) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                reminderTime = null
                Toast.makeText(context, "Reminder cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        class Receiver() : BroadcastReceiver() {

            var db: AppDatabase? = null

            override fun onReceive(context: Context, intent: Intent) {
                val intent = Intent(context, MainActivity::class.java)

                // Получить все фразы из базы данных
                var randomPhrase: Phrase? = null

                Thread {
                    randomPhrase = db!!.getDao().get()
                }.run()

                if (randomPhrase != null) {
                    // Настроить уведомление с использованием случайной фразы
                    val pIntent = PendingIntent.getActivity(context, 0, intent, 0)
                    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
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


        }


    }


}
