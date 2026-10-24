# 📊 Chi tiết Breakdown & Workflow: Module 4 (Attendance & WorkLog)

Tài liệu này cung cấp bức tranh toàn cảnh về **Module 4 (Chấm công & Nhật ký làm việc)** tích hợp trên nền tảng baseline của nhóm, bao gồm cấu trúc file, sơ đồ workflow bằng Mermaid, và giải thích chi tiết luồng chạy qua code.

---

## 📂 1. Danh sách các File liên quan đến Module 4

| Tên File | Đường dẫn | Vai trò & Chức năng chính |
| :--- | :--- | :--- |
| **`AttendanceViewModel.kt`** | `ui/employee/AttendanceViewModel.kt` | Quản lý State, kết nối `WorkLogRepository` để thực hiện Check-in, Check-out, tải lịch sử tháng và tổng giờ làm. |
| **`WorkLogAdapter.kt`** | `ui/employee/WorkLogAdapter.kt` | Adapter hiển thị danh sách nhật ký chấm công (`WorkLogEntity`) lên `RecyclerView`. |
| **`EmployeeDashboardActivity.kt`** | `ui/employee/EmployeeDashboardActivity.kt` | Màn hình giao diện Dashboard của Nhân viên, gắn sự kiện nút Check-in/Check-out và quan sát dữ liệu từ ViewModel. |
| **`activity_employee_dashboard.xml`** | `res/layout/activity_employee_dashboard.xml` | Layout XML chứa các nút bấm Check-In, Check-Out, hiển thị tổng giờ tháng và danh sách `RecyclerView`. |
| **`item_work_log.xml`** | `res/layout/item_work_log.xml` | Layout XML cho từng dòng item trong lịch sử chấm công (Ngày, Giờ vào, Giờ ra, Tổng giờ, Trạng thái). |
| **`WorkLogRepository.kt`** *(Baseline)* | `data/repository/WorkLogRepository.kt` | Chứa logic nghiệp vụ lõi (validate check-in trùng lặp, tính giờ làm giữa 2 mốc thời gian qua `DateTimeUtils`). |
| **`SessionManager.kt`** *(Baseline)* | `data/pref/SessionManager.kt` | Quản lý SharedPreferences để lấy `username` của nhân viên đang đăng nhập. |

---

## 🗺️ 2. Sơ đồ Workflow (Mermaid Diagram)

Dưới đây là biểu đồ minh họa luồng hoạt động khi nhân viên thực hiện Check-In, Check-Out và xem lịch sử trên ứng dụng:

```mermaid
sequenceDiagram
    autonumber
    actor User as Nhân viên
    participant UI as EmployeeDashboardActivity
    participant VM as AttendanceViewModel
    participant Repo as WorkLogRepositoryImpl
    participant DB as Room Database (DAO)

    Note over UI, DB: 1. Khởi động màn hình & Lấy thông tin nhân viên
    UI->>VM: initEmployee(username)
    VM->>DB: Truy vấn AccountDao & EmployeeDao lấy employeeId
    VM->>Repo: getHistory() & monthlyTotalHours()
    Repo->>DB: Lấy danh sách work_logs tháng hiện tại
    DB-->>UI: Cập nhật historyList & monthlyHours lên RecyclerView

    Note over UI, DB: 2. Luồng Check-In (Chấm công vào)
    User->>UI: Nhấn nút Check-In
    UI->>VM: checkIn()
    VM->>Repo: checkIn(employeeId)
    Repo->>DB: Kiểm tra workLogDao.findByEmployeeAndDate(today)
    alt Đã check-in trong ngày
        DB-->>Repo: Trả về bản ghi tồn tại
        Repo-->>VM: Result.failure("Bạn đã check-in hôm nay")
        VM-->>UI: Toast báo lỗi
    else Chưa check-in
        DB-->>Repo: null
        Repo->>DB: Insert WorkLog(status = WORKING, checkInTime = now)
        Repo-->>VM: Result.success(workLog)
        VM-->>UI: Cập nhật UI & Tải lại dữ liệu tháng
    end

    Note over UI, DB: 3. Luồng Check-Out (Chấm công ra)
    User->>UI: Nhấn nút Check-Out
    UI->>VM: checkOut()
    VM->>Repo: checkOut(employeeId)
    Repo->>DB: Tìm work log ngày hôm nay
    alt Chưa check-in
        DB-->>Repo: null
        Repo-->>VM: Result.failure("Chưa check-in hôm nay")
        VM-->>UI: Toast báo lỗi
    else Đã check-out rồi
        Repo-->>VM: Result.failure("Đã check-out hôm nay")
    else Hợp lệ để Check-Out
        Repo->>Repo: DateTimeUtils.hoursBetween(checkIn, checkOut)
        Repo->>DB: Update WorkLog(status = COMPLETED, checkOutTime, totalHours)
        Repo-->>VM: Result.success(completedLog)
        VM-->>UI: Cập nhật tổng giờ làm & danh sách lịch sử
    end
