package com.example.thuchanhandroid.ui.employee.salary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.thuchanhandroid.R
import com.example.thuchanhandroid.data.local.AppDatabase
import com.example.thuchanhandroid.data.local.entity.SalaryEntity
import com.example.thuchanhandroid.data.pref.SessionManager
import com.example.thuchanhandroid.data.repository.EmployeeRepositoryImpl
import com.example.thuchanhandroid.data.repository.SalaryRepositoryImpl
import com.example.thuchanhandroid.data.repository.WorkLogRepositoryImpl
import com.example.thuchanhandroid.utils.Constants
import com.example.thuchanhandroid.utils.SalaryCalculator
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MySalaryActivity : AppCompatActivity(), MySalaryContract.View {

    private lateinit var presenter: MySalaryContract.Presenter
    private lateinit var rvSalary: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: SalaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_salary)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        rvSalary = findViewById(R.id.rvSalary)
        progressBar = findViewById(R.id.progressBar)
        tvEmpty = findViewById(R.id.tvEmpty)

        rvSalary.layoutManager = LinearLayoutManager(this)
        adapter = SalaryAdapter(emptyList())
        rvSalary.adapter = adapter

        val db = AppDatabase.getInstance(this)
        val salaryRepo = SalaryRepositoryImpl(db.salaryDao())
        val employeeRepo = EmployeeRepositoryImpl(db)
        val workLogRepo = WorkLogRepositoryImpl(db.workLogDao())

        presenter = MySalaryPresenter(salaryRepo, employeeRepo, workLogRepo)
        presenter.attachView(this)

        val session = SessionManager(this)
        CoroutineScope(Dispatchers.Main).launch {
            val account = withContext(Dispatchers.IO) {
                session.username?.let { db.accountDao().findByUsername(it) }
            }
            if (account != null) {
                val employee = withContext(Dispatchers.IO) {
                    db.employeeDao().findByAccountId(account.id)
                }
                if (employee != null) {
                    presenter.loadSalaries(employee.id)
                } else {
                    showError("Không tìm thấy thông tin nhân viên")
                }
            } else {
                showError("Phiên đăng nhập hết hạn")
            }
        }
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

    override fun showSalaryList(salaries: List<SalaryEntity>) {
        tvEmpty.visibility = View.GONE
        rvSalary.visibility = View.VISIBLE
        adapter.updateList(salaries)
    }

    override fun showEmpty() {
        rvSalary.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private class SalaryAdapter(
        private var list: List<SalaryEntity>
    ) : RecyclerView.Adapter<SalaryAdapter.ViewHolder>() {

        fun updateList(newList: List<SalaryEntity>) {
            list = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_salary, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int = list.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvMonthYear: TextView = itemView.findViewById(R.id.tvMonthYear)
            private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
            private val tvEmployeeInfo: TextView = itemView.findViewById(R.id.tvEmployeeInfo)
            private val tvHoursInfo: TextView = itemView.findViewById(R.id.tvHoursInfo)
            private val tvTotalSalary: TextView = itemView.findViewById(R.id.tvTotalSalary)

            fun bind(item: SalaryEntity) {
                tvMonthYear.text = "Tháng ${item.month}/${item.year}"
                tvStatus.text = if (item.status == Constants.SalaryStatus.PAID) "ĐÃ THANH TOÁN" else "CHỜ THANH TOÁN"
                tvEmployeeInfo.visibility = View.GONE
                tvHoursInfo.text = "Tổng giờ làm: ${item.totalHours}h (OT: ${item.overtimeHours}h)"
                tvTotalSalary.text = SalaryCalculator.formatVnd(item.totalSalary)
            }
        }
    }
}
