package org.d3if4080.AppPertamaRafif.catatan

import android.app.Application
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.d3if4080.AppPertamaRafif.R
import org.d3if4080.AppPertamaRafif.db.noteEntity
import org.w3c.dom.Text


class CatatanAdapter(private val notes : ArrayList<noteEntity> , private val listener: OnAdapterListener) : RecyclerView.Adapter<CatatanAdapter.NoteViewHolder> (){





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {


        return NoteViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_notes,parent,false)
)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val context = holder.itemView.context


        val animation = AnimationUtils.loadAnimation(holder.itemView.context,R.anim.decelerate)



        val note = notes[position]


      holder.a.text = note.teks
        holder.d.text = note.tanggalBikin


        //OnNormalClick or Singgle Click
        holder.a.setOnClickListener{
            listener.onClick(note)
        }

        holder.e.setOnClickListener {
            listener.onClick(note)
        }


        holder.c.setOnClickListener{
            listener.onShare(note)
        }


        listener.onDelete(note)

        //onTouchListener for Swipe


        holder.a.setOnDragListener { view, dragEvent ->
            listener.onDelete(note)

            true
        }

        holder.e.setOnDragListener { view, dragEvent ->
            listener.onDelete(note)

            true
        }



        //LongClickListener for pop-up
        holder.a.setOnLongClickListener{
            Toast.makeText(context, "Long Click Pressed", Toast.LENGTH_SHORT)
                .show()

            holder.e.startAnimation(animation)
            listener.onHapticTouch(note, holder.e)

            true
        }

        holder.e.setOnLongClickListener    {

            Toast.makeText(context, "Long Click Pressed", Toast.LENGTH_SHORT)
                .show()

            holder.e.startAnimation(animation)
            listener.onHapticTouch(note, holder.e)

            true

        }



        //unused

        /*holder.b.setOnClickListener{
       listener.onDelete(note)
   }
        */


    }

    override fun getItemCount() = notes.size


    class NoteViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
        val a = view.findViewById<TextView>(R.id.hasilsubmitcatatantextview)
        val b = view.findViewById<ImageView>(R.id.icon_delete)
       val c = view.findViewById<ImageView>(R.id.icon_share)
        val d = view.findViewById<TextView>(R.id.tanggaldibuat)
        val e = view.findViewById<ConstraintLayout>(R.id.medanSentuh)







    }




    fun setData(list: List<noteEntity>){
        notes.clear()
        notes.addAll(list)
        notifyDataSetChanged()
    }



    //bisa ini dibikin diluar class ini
    //dan ini fungsinya Untuk perantara aja, wadah fungsi untuk menjembatanai antara Activity/Fragment ke 'mata' ViewHoldernya RecyclerView
    //dan untuk apa isi fungsi, di Dideklarasikan di class Activity atau Fragment tadi tersebut.
    interface OnAdapterListener{
        fun onClick(note : noteEntity)
        fun onDelete(note : noteEntity)

        fun onShare(note : noteEntity)
        fun onUpdate(note : noteEntity)

        fun onHapticTouch(note : noteEntity, view: View)
    }



}

