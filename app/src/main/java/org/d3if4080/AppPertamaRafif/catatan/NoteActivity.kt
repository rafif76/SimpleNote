package org.d3if4080.AppPertamaRafif.catatan

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.contains
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var popupWindow: PopupWindow
    lateinit var notenote : noteEntity


    private val sdf = SimpleDateFormat("[dd-MM-yyyy] HH:mm:ss")


    private lateinit var searchView: SearchView

    val db by lazy { noteDb(this) }
//@color/cardview_light_background

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerView()

        Toast.makeText(applicationContext, "Info : Pilih teks untuk mengedit", Toast.LENGTH_SHORT)
            .show()


        binding.submitButton.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this,R.anim.decelerate)

            binding.submitButton.startAnimation(animation)


            val teks = binding.editTextTextMultiLine.text.toString()
            //  if (noHp.isEmpty() &&  noHp.length < 10 ) {
            if (teks.isEmpty()) {
                Toast.makeText(this, "Kosong teksnya", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                clickSumbit()
                Toast.makeText(this, "Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                clearTextBox()

            }


        }

        binding.buttoncleardata.setOnClickListener {

            clickClearAll()
        }

        binding.arrowDown.setOnClickListener {

            val animation = AnimationUtils.loadAnimation(this,R.anim.decelerate)

            binding.arrowDown.startAnimation(animation)

            startActivity(
                Intent(applicationContext, NoteFullDisplayActivity::class.java)
            )
            finish()
        }


        searchWords()
        touchingItems()
    }


    fun clearTextBox() {
        val kosong = null
        binding.editTextTextMultiLine.text = kosong
    }

    fun clickSumbit() {

        val date = Date()
        val tanggals = sdf.format(date)

        CoroutineScope(Dispatchers.IO).launch {
            db.noteDao().insert(
                noteEntity(
                    0,
                    binding.editTextTextMultiLine.text.toString(),
                    tanggals.toString(),
                    tanggals.toString()
                )
            )
            reloadNoteAgain()
        }

    }

    fun clickClearAll() {
        val animation = AnimationUtils.loadAnimation(this,R.anim.decelerate)

        binding.buttoncleardata.startAnimation(animation)


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
                    Intent(applicationContext, EditActivity::class.java)
                        .putExtra("intent_id", note.id)
                        .putExtra("tanggal_bikin", note.tanggalBikin)
                )

            }

            override fun onDelete(note: noteEntity) {
                //noteAdapter.attachItemTouchToRecylerView(binding.recyclerView)

                //touchingItems(note)
                kehed(note);


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
                TODO("Not yet implemented")

            }

            override fun onHapticTouch(note: noteEntity, view: View) {
                longPressPopUp(note, view)
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

    /* Unused function, since Swipe function works
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

     */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.opsi, menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"

        // searchDatabase(searchQuery)

        CoroutineScope(Dispatchers.IO).launch {
            val notes = db.noteDao().searchDatabase(searchQuery)
            withContext(Dispatchers.Main) {
                noteAdapter.setData(notes)
            }
        }


    }

    private fun searchWords() {


        searchView = binding.cariCatatan
        // mau pake SearchView Itu sebenarnya opsional. kalo Langsung panggil bindingnya aja (binding.cariDataSearcview) Itu juga bisa.

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchDatabase(query)
                }

                return true
            }

            override fun onQueryTextSubmit(submit: String?): Boolean {
                if (submit != null) {
                    searchDatabase(submit)
                }
                return true

            }
        })
    }


    private fun kehed (note: noteEntity) : noteEntity{

        notenote = note

        return  notenote
    }
    private fun touchingItems() {

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            val background = ColorDrawable(Color.RED)


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val alertDialog = AlertDialog.Builder(this@NoteActivity)
                alertDialog.apply {
                    setTitle("Konfirmasi")
                    setMessage("Yakin mau hapus catatan ini ?")
                    setNegativeButton("Ngga") {
                            dialogInterface, which ->
                        dialogInterface.dismiss()
                        noteAdapter.notifyDataSetChanged()


                    }
                    setPositiveButton("Yap") { dialogInterface, which ->
                        dialogInterface.dismiss()
                        CoroutineScope(Dispatchers.IO).launch {
                            db.noteDao().deleteData(notenote)
                            reloadNoteAgain()
                        }
                    }

                }
                alertDialog.show()


            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // Menyusun background untuk swipe
                val itemView = viewHolder.itemView
                val backgroundCornerOffset = 20
                val iconMargin = (itemView.height - backgroundCornerOffset * 2) / 4
                val icon = ContextCompat.getDrawable(this@NoteActivity, R.drawable.ic_delete)
                val iconLeft: Int
                val iconRight: Int
                val iconTop = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight

                if (dX > 0) {
                    // Swipe ke kanan
                    background.setBounds(
                        itemView.left,
                        itemView.top,
                        dX.toInt() + backgroundCornerOffset,
                        itemView.bottom
                    )
                    iconLeft = itemView.left + iconMargin
                    iconRight = itemView.left + iconMargin + icon.intrinsicWidth
                } else {
                    // Swipe ke kiri
                    background.setBounds(
                        itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    iconRight = itemView.right - iconMargin
                }

                background.draw(c)
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                icon.draw(c)

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        })




        itemTouchHelper.attachToRecyclerView(binding.recyclerView)


    }

    private fun longPressPopUp(note : noteEntity, anchoreView : View){



        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView : View = layoutInflater.inflate(R.layout.popup_layout, null)

        popupWindow = PopupWindow(  popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true)




        popupWindow.showAtLocation(anchoreView, Gravity.CENTER, 0 ,0 )


        val popupTextView: TextView = popupView.findViewById(R.id.noteText_popUp)
        popupTextView.text = note.teks.toString()






    }


}






