package org.d3if4080.AppPertamaRafif.catatan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.room.Ignore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if4080.AppPertamaRafif.R
import org.d3if4080.AppPertamaRafif.databinding.ActivityEditBinding
import org.d3if4080.AppPertamaRafif.db.noteDb
import org.d3if4080.AppPertamaRafif.db.noteEntity
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    lateinit var noteAdapter: CatatanAdapter

    private val sdf = SimpleDateFormat("[dd-MM-yyyy] HH:mm:ss")



    val db by lazy { noteDb(this) }
    private var noteId: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        noteId = intent.getIntExtra("intent_id", 0)
        //tanggalIntent = intent.getStringExtra("tanggal_bikin").toString()
        //Toast.makeText(this, noteId.toString(), Toast.LENGTH_SHORT).show()
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataNote()


        binding.backbuttonUpdate.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.apply {
                setTitle("Konfirmasi")
                setMessage("Kembali tanpa menyimpan?")
                setNegativeButton("Ngga") { dialogInterface, which -> dialogInterface.dismiss() }
                setPositiveButton("Yap") { dialogInterface, which ->
                    dialogInterface.dismiss()
                    finish()
                }

            }
            alertDialog.show()

        }


        binding.updateButton.setOnClickListener {
            UpdateCatatan()
        }

    }

    fun UpdateCatatan() {

        val date= Date()
        val tanggals = sdf.format(date)

        noteId = intent.getIntExtra("intent_id", 0)
        val tanggalIntent = intent.getStringExtra("tanggal_bikin")

        CoroutineScope(Dispatchers.IO).launch {

            db.noteDao().updateData(
                    noteEntity(noteId, binding.EditField.text.toString(),tanggals.toString(),tanggalIntent.toString())
            )
            finish()
        }
    }

    fun getDataNote() {

        val a = R.string.tanggal_di_update

        noteId = intent.getIntExtra("intent_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNote(noteId)[0]
            binding.EditField.setText( notes.teks)
            binding.dateLastUpdated.setText("Last updated \n" +  notes.date)


        }
    }

  /*  fun reloadNoteAgain2() {
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNotes()
            Log.d("NoteActivity", "dbrespone: $notes")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }


        }

   */


    }




