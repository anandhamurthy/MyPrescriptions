package com.myprescriptions.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myprescriptions.models.Medicine
import com.myprescriptions.R

class MedicineAdapter(val userList: ArrayList<Medicine>) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_prescription_row, parent, false)
        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(medicine: Medicine) {
            val Name = itemView.findViewById(R.id.name) as TextView
            val Morning  = itemView.findViewById(R.id.morning) as TextView
            val Afternoon  = itemView.findViewById(R.id.afternoon) as TextView
            val Evening  = itemView.findViewById(R.id.evening) as TextView
            val Night  = itemView.findViewById(R.id.night) as TextView
            val Quantity  = itemView.findViewById(R.id.quantity) as TextView
            Name.text = medicine.name
            Morning.text = medicine.mor
            Afternoon.text = medicine.aft
            Evening.text = medicine.eve
            Night.text = medicine.nig
            Quantity.text = medicine.quantity
        }
    }
}