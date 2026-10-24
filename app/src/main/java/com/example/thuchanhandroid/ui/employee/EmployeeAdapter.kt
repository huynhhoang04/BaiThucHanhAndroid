package com.example.thuchanhandroid.ui.employee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import java.text.NumberFormat
import java.util.Locale

class EmployeeAdapter(
    private var employees: List<EmployeeEntity>,
    private val onEditClick: (EmployeeEntity) -> Unit,
    private val onDeleteClick: (EmployeeEntity) -> Unit
) : RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCode: TextView = itemView.findViewById(R.id.tvEmployeeCode)
        val tvName: TextView = itemView.findViewById(R.id.tvEmployeeName)
        val tvRate: TextView = itemView.findViewById(R.id.tvHourlyRate)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_employee, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val emp = employees[position]
        holder.tvCode.text = emp.code
        holder.tvName.text = emp.fullname

        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.tvRate.text = "Lương: ${formatter.format(emp.hourlyRate)}/h"

        holder.btnEdit.setOnClickListener { onEditClick(emp) }
        holder.btnDelete.setOnClickListener { onDeleteClick(emp) }
    }

    override fun getItemCount(): Int = employees.size

    fun updateData(newList: List<EmployeeEntity>) {
        employees = newList
        notifyDataSetChanged()
    }
}