package com.example.thuchanhandroid.ui.employee

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.local.AppDatabase
import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import com.example.thuchanhandroid.data.repository.EmployeeRepositoryImpl
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmployeeListActivity : AppCompatActivity(), EmployeeContract.View {

    private lateinit var presenter: EmployeeContract.Presenter
    private lateinit var adapter: EmployeeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_list)

        val database = AppDatabase.getInstance(this)
        val repository = EmployeeRepositoryImpl(database)
        presenter = EmployeePresenter(this, repository)

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val rvList: RecyclerView = findViewById(R.id.rvEmployeeList)
        val edtSearch: EditText = findViewById(R.id.edtSearchEmployee)
        val fabAdd: FloatingActionButton = findViewById(R.id.fabAddEmployee)

        btnBack.setOnClickListener {
            finish()
        }

        adapter = EmployeeAdapter(
            employees = emptyList(),
            onEditClick = { emp -> showAddOrEditDialog(emp) },
            onDeleteClick = { emp -> confirmDelete(emp) }
        )

        rvList.layoutManager = LinearLayoutManager(this)
        rvList.adapter = adapter

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.searchEmployees(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        fabAdd.setOnClickListener {
            showAddOrEditDialog(null)
        }

        presenter.loadEmployees()
    }

    override fun showEmployees(employees: List<EmployeeEntity>) {
        adapter.updateData(employees)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    private fun showAddOrEditDialog(employee: EmployeeEntity?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_employee, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        val tvTitle: TextView = dialogView.findViewById(R.id.tvDialogTitle)
        val edtCode: EditText = dialogView.findViewById(R.id.edtEmpCode)
        val edtName: EditText = dialogView.findViewById(R.id.edtEmpName)
        val edtEmail: EditText = dialogView.findViewById(R.id.edtEmpEmail)
        val edtPhone: EditText = dialogView.findViewById(R.id.edtEmpPhone)
        val edtHourlyRate: EditText = dialogView.findViewById(R.id.edtEmpHourlyRate)
        val layoutAccountInfo: View = dialogView.findViewById(R.id.layoutAccountInfo)
        val edtUsername: EditText = dialogView.findViewById(R.id.edtEmpUsername)
        val edtPassword: EditText = dialogView.findViewById(R.id.edtEmpPassword)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancelDialog)
        val btnSave: Button = dialogView.findViewById(R.id.btnSaveDialog)

        if (employee != null) {
            tvTitle.text = "Sửa Thông Tin Nhân Viên"
            edtCode.setText(employee.code)
            edtName.setText(employee.fullname)
            edtEmail.setText(employee.email ?: "")
            edtPhone.setText(employee.phone ?: "")
            edtHourlyRate.setText(employee.hourlyRate.toString())
            layoutAccountInfo.visibility = View.GONE
        } else {
            tvTitle.text = "Thêm Nhân Viên Mới"
            layoutAccountInfo.visibility = View.VISIBLE
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            if (employee == null) {
                presenter.addEmployee(
                    code = edtCode.text.toString(),
                    fullname = edtName.text.toString(),
                    email = edtEmail.text.toString(),
                    phone = edtPhone.text.toString(),
                    hourlyRateStr = edtHourlyRate.text.toString(),
                    username = edtUsername.text.toString(),
                    password = edtPassword.text.toString()
                )
            } else {
                presenter.updateEmployee(
                    currentEmployee = employee,
                    code = edtCode.text.toString(),
                    fullname = edtName.text.toString(),
                    email = edtEmail.text.toString(),
                    phone = edtPhone.text.toString(),
                    hourlyRateStr = edtHourlyRate.text.toString()
                )
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun confirmDelete(employee: EmployeeEntity) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa nhân viên ${employee.fullname}?")
            .setPositiveButton("Xóa") { _, _ ->
                presenter.deleteEmployee(employee)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}