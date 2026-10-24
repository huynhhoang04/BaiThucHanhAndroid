package com.example.thuchanhandroid.ui.admin.salary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.local.AppDatabase
import com.example.thuchanhandroid.data.local.entity.EmployeeEntity
import com.example.thuchanhandroid.data.local.entity.SalaryEntity
import com.example.thuchanhandroid.data.repository.EmployeeRepositoryImpl
import com.example.thuchanhandroid.data.repository.SalaryRepositoryImpl
import com.example.thuchanhandroid.data.repository.WorkLogRepositoryImpl
import com.example.thuchanhandroid.utils.Constants
import com.example.thuchanhandroid.utils.DateTimeUtils
import com.example.thuchanhandroid.utils.SalaryCalculator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class AdminSalaryActivity : AppCompatActivity(), AdminSalaryContract.View {

    private lateinit var presenter: AdminSalaryContract.Presenter
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var btnGenerate: MaterialButton
    private lateinit var rvSalary: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: AdminSalaryAdapter

    private var selectedMonth = DateTimeUtils.currentMonth()
    private var selectedYear = DateTimeUtils.currentYear()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_salary)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerYear = findViewById(R.id.spinnerYear)
        btnGenerate = findViewById(R.id.btnGenerate)
        rvSalary = findViewById(R.id.rvSalary)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)

        setupSpinners()

        rvSalary.layoutManager = LinearLayoutManager(this)
        adapter = AdminSalaryAdapter(emptyList()) { salary ->
            presenter.markAsPaid(salary)
        }
        rvSalary.adapter = adapter

        val db = AppDatabase.getInstance(this)
        val salaryRepo = SalaryRepositoryImpl(db.salaryDao())
        val employeeRepo = EmployeeRepositoryImpl(db)
        val workLogRepo = WorkLogRepositoryImpl(db.workLogDao())

        presenter = AdminSalaryPresenter(salaryRepo, employeeRepo, workLogRepo)
        presenter.attachView(this)

        btnGenerate.setOnClickListener {
            presenter.generateAndUpsertSalaries(selectedMonth, selectedYear)
        }

        presenter.loadSalaries(selectedMonth, selectedYear)
    }

    private fun setupSpinners() {
        val months = (1..12).map { "Tháng $it" }
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter
        spinnerMonth.setSelection(selectedMonth - 1)

        val currentY = DateTimeUtils.currentYear()
        val years = ((currentY - 2)..(currentY + 1)).map { it.toString() }
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter
        spinnerYear.setSelection(years.indexOf(currentY.toString()))

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMonth = spinnerMonth.selectedItemPosition + 1
                selectedYear = years[spinnerYear.selectedItemPosition].toInt()
                presenter.loadSalaries(selectedMonth, selectedYear)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerMonth.onItemSelectedListener = listener
        spinnerYear.onItemSelectedListener = listener
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    override fun showSalaryList(salaries: List<Pair<SalaryEntity, EmployeeEntity>>) {
        tvEmpty.visibility = View.GONE
        rvSalary.visibility = View.VISIBLE
        adapter.updateList(salaries)
    }

    override fun showEmpty() {
        rvSalary.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private class AdminSalaryAdapter(
        private var list: List<Pair<SalaryEntity, EmployeeEntity>>,
        private val onMarkPaidClick: (SalaryEntity) -> Unit
    ) : RecyclerView.Adapter<AdminSalaryAdapter.ViewHolder>() {

        fun updateList(newList: List<Pair<SalaryEntity, EmployeeEntity>>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_salary, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (salary, employee) = list[position]
            holder.bind(salary, employee, onMarkPaidClick)
        }

        override fun getItemCount(): Int = list.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvMonthYear: TextView = itemView.findViewById(R.id.tvMonthYear)
            private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
            private val tvEmployeeInfo: TextView = itemView.findViewById(R.id.tvEmployeeInfo)
            private val tvHoursInfo: TextView = itemView.findViewById(R.id.tvHoursInfo)
            private val tvTotalSalary: TextView = itemView.findViewById(R.id.tvTotalSalary)
            private val btnMarkPaid: MaterialButton = itemView.findViewById(R.id.btnMarkPaid)

            fun bind(salary: SalaryEntity, employee: EmployeeEntity, onMarkPaidClick: (SalaryEntity) -> Unit) {
                tvMonthYear.text = "Tháng ${salary.month}/${salary.year}"
                val isPaid = salary.status == Constants.SalaryStatus.PAID
                tvStatus.text = if (isPaid) "ĐÃ THANH TOÁN" else "CHỜ THANH TOÁN"
                tvEmployeeInfo.text = "NV: ${employee.fullname} (${employee.code})"
                tvHoursInfo.text = "Tổng giờ: ${salary.totalHours}h (OT: ${salary.overtimeHours}h)"
                tvTotalSalary.text = SalaryCalculator.formatVnd(salary.totalSalary)

                if (isPaid) {
                    btnMarkPaid.visibility = View.GONE
                } else {
                    btnMarkPaid.visibility = View.VISIBLE
                    btnMarkPaid.setOnClickListener { onMarkPaidClick(salary) }
                }
            }
        }
    }
}
