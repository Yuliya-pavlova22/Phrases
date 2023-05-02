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
    lateinit var binding: ActivityMainBinding
    private lateinit var adapter: PhraseAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val db = AppDatabase.getDB(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        val myDataset: MutableList<Phrase> = db.getDao().getAll().toList().toMutableList();

        adapter = PhraseAdapter(myDataset, binding, db, this@MainActivity)
        binding.recyclerView.adapter = adapter

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
                val notifications = Notifications
                notifications.setReminder(this, db, binding)
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