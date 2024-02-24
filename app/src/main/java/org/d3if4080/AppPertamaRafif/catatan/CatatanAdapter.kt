package org.d3if4080.AppPertamaRafif.catatan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
      holder.a.text = note.teks
        holder.d.text = note.tanggalBikin
        holder.a.setOnClickListener{
            listener.onClick(note)
        }
        holder.b.setOnClickListener{
            listener.onDelete(note)
        }
        holder.c.setOnClickListener{
            listener.onShare(note)
        }

    }

    override fun getItemCount() = notes.size


    class NoteViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
        val a = view.findViewById<TextView>(R.id.hasilsubmitcatatantextview)
        val b = view.findViewById<ImageView>(R.id.icon_delete)
       val c = view.findViewById<ImageView>(R.id.icon_share)
        val d = view.findViewById<TextView>(R.id.tanggaldibuat)



    }


    fun setData(list: List<noteEntity>){
        notes.clear()
        notes.addAll(list)
        notifyDataSetChanged()
    }


    interface OnAdapterListener{
        fun onClick(note : noteEntity)
        fun onDelete(note : noteEntity)
        fun onShare(note : noteEntity)
        fun onUpdate(note : noteEntity)

    }
}