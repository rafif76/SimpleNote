package org.d3if4080.AppPertamaRafif.catatan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.contains
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.d3if4080.AppPertamaRafif.R
import org.d3if4080.AppPertamaRafif.databinding.ActivityNoteBinding
import org.d3if4080.AppPertamaRafif.db.noteDb
import org.d3if4080.AppPertamaRafif.db.noteEntity
import java.text.SimpleDateFormat
import java.util.*

class  NoteActivity : AppCompatActivity() {

    lateinit var binding: ActivityNoteBinding

    lateinit var noteAdapter: CatatanAdapter

    private val sdf = SimpleDateFormat("[dd-MM-yyyy] HH:mm:ss")


    val db by lazy { noteDb(this) }
//@color/cardview_light_background

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView()

        Toast.makeText(applicationContext, "Info : Pilih teks untuk mengedit", Toast.LENGTH_SHORT).show()


        binding.submitButton.setOnClickListener {

            val teks = binding.editTextTextMultiLine.text.toString()
            //  if (noHp.isEmpty() &&  noHp.length < 10 ) {
            if (teks.isEmpty()) {
                Toast.makeText(this, "Kosong teksnya", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else {
                clickSumbit()
                Toast.makeText(this, "Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                clearTextBox()

            }


        }

        binding.buttoncleardata.setOnClickListener {
            clickClearAll()
        }

        binding.imageButton2.setOnClickListener{
            startActivity(
                Intent(applicationContext,NoteFullDisplayActivity::class.java)
            )
            finish()
        }







    }
    

    fun clearTextBox() {
        val kosong = null
        binding.editTextTextMultiLine.text = kosong
    }

    fun clickSumbit() {

        val date= Date()
        val tanggals = sdf.format(date)

        CoroutineScope(Dispatchers.IO).launch {
            db.noteDao().insert(
                noteEntity(0, binding.editTextTextMultiLine.text.toString(),tanggals.toString(),tanggals.toString())
            )
            reloadNoteAgain()
        }

    }

    fun clickClearAll() {

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Yakin mau smua ?")
            setNegativeButton("Ngga") { dialogInterface, which -> dialogInterface.dismiss() }
            setPositiveButton("Yap") { dialogInterface, which ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().clearData()
                    reloadNoteAgain()
                }
            }

        }
        alertDialog.show()

    }


    fun backButton() {
        finish()
    }


    private fun recyclerView() {
        noteAdapter = CatatanAdapter(arrayListOf(), object : CatatanAdapter.OnAdapterListener {
            override fun onClick(note: noteEntity) {
                //Toast.makeText(applicationContext, note.teks, Toast.LENGTH_LONG).show()
                startActivity(
                        Intent(applicationContext,EditActivity::class.java)
                                .putExtra("intent_id" , note.id)
                                .putExtra("tanggal_bikin", note.tanggalBikin)
                )

            }

            override fun onDelete(note: noteEntity) {
               apusDialogTanya(note)
                }

            override fun onShare(note: noteEntity) {

                val teksDariDbase = note.teks

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "$teksDariDbase")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

            override fun onUpdate(note: noteEntity) {

            }


        })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = noteAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        reloadNoteAgain()


    }


    fun reloadNoteAgain() {
        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().getNotes()
            Log.d("NoteActivity", "dbrespone: $notes")
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }
    }

    private fun apusDialogTanya(note: noteEntity) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Konfirmasi")
            setMessage("Yakin mau hapus catatan ini ?")
            setNegativeButton("Ngga") { dialogInterface, which -> dialogInterface.dismiss() }
            setPositiveButton("Yap") { dialogInterface, which ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch {
                    db.noteDao().deleteData(note)
                    reloadNoteAgain()
                }
            }

        }
        alertDialog.show()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.opsi, menu)
        return super.onCreateOptionsMenu(menu)
    }


}



