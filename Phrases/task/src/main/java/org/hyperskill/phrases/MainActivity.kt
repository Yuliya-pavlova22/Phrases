package org.hyperskill.phrases

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.hyperskill.phrases.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDB(this)
        val myDataset= db.getDao().getAll().toList().toMutableList();
        val adapter = PhraseAdapter(myDataset, binding, db, this@MainActivity)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        Notifications.createNotificationChannel(this)

        binding.reminderTextView.setOnClickListener {
            //устанавливаем часы и сохраняем время
            var size = 0
            Thread {
                size = db.getDao().getCount()
            }.run()

            if (size == 0) {
                Toast.makeText(this, "No reminder set", Toast.LENGTH_SHORT).show()
            } else {
                Notifications.setReminder(this, db, binding)
            }
        }

        binding.addButton.setOnClickListener {
            AddPhrase().add(this, db) {
                myDataset.add(it)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
