# 📋 Kế hoạch phân công công việc: Quản lý Giờ làm việc và Tiền lương Nhân viên (Employee Working Hours and Salary Management)

## 🎯 Tổng quan dự án
Xây dựng ứng dụng Android quản lý giờ làm việc và tiền lương với 4 bảng dữ liệu chính:
1. **Account**: `id`, `username`, `password`, `role`
2. **Employee**: `id`, `code`, `fullname`, `email`, `phone`, `hourlyRate`, `accountId`
3. **WorkLog**: `id`, `employeeId`, `workDate`, `checkInTime`, `checkOutTime`, `totalHours`, `status`
4. **Salary**: `id`, `employeeId`, `month`, `year`, `totalHours`, `overtimeHours`, `totalSalary`, `status`

---

## 👥 Phân công nhiệm vụ cho nhóm 5 thành viên

### 👤 Thành viên 1: Database & Data Architecture
* **Nhiệm vụ:**
  - Thiết kế và khởi tạo Room Database (`Account`, `Employee`, `WorkLog`, `Salary`).
  - Viết các `Entities`, `DAOs`, và `AppDatabase` class.
  - Tạo dữ liệu mẫu khởi tạo (Seed data) cho tài khoản Admin và nhân viên mẫu.
  - Hỗ trợ các thành viên khác tích hợp truy vấn dữ liệu.

### 👤 Thành viên 2: Authentication & Session Management
* **Nhiệm vụ:**
  - Thiết kế màn hình Login (hỗ trợ phân quyền Admin và Employee).
  - Viết logic xác thực tài khoản từ Room Database.
  - Tích hợp **SharedPreferences** để lưu trữ phiên đăng nhập, thông tin tài khoản và tự động đăng nhập.
  - Xây dựng hệ thống điều hướng (Navigation) dựa trên `role` sau khi đăng nhập thành công.

### 👤 Thành viên 3: Employee Management Module (Admin)
* **Nhiệm vụ:**
  - Xây dựng giao diện Admin Dashboard.
  - Quản lý Nhân viên (CRUD): Thêm mới nhân viên, chỉnh sửa thông tin, cập nhật mức lương theo giờ (`hourlyRate`), xóa/vô hiệu hóa nhân viên.
  - Chức năng tìm kiếm nhân viên theo tên hoặc mã nhân viên (`code`).

### 👤 Thành viên 4: Attendance & WorkLog Module (Employee)
* **Nhiệm vụ:**
  - Xây dựng giao diện Employee Dashboard.
  - Chức năng **Check-in / Check-out** hàng ngày (ghi nhận `workDate`, `checkInTime`, `checkOutTime`).
  - Thuật toán tự động tính tổng giờ làm việc (`totalHours`).
  - **Data Validation:** Ngăn chặn check-in trùng lặp trong ngày, check-out không hợp lệ hoặc thiếu check-in.
  - Màn hình Lịch sử điểm danh (Attendance History) và tổng hợp giờ làm theo tháng.

### 👤 Thành viên 5: Salary Calculation & Version Control Master
* **Nhiệm vụ:**
  - Thuật toán tự động tính toán tiền lương hàng tháng (`totalSalary`) dựa trên tổng số giờ làm việc, giờ tăng ca (`overtimeHours`) và `hourlyRate`.
  - Màn hình xem bảng lương chi tiết dành cho Nhân viên.
  - Màn hình quản lý và tổng hợp bảng lương dành cho Quản trị viên (Admin).
  - **Version Control:** Quản lý Git Repository (`https://github.com/huynhhoang04/BaiThucHanhAndroid`), thiết lập nhánh, review Pull Request và xử lý conflict.

---

## 📅 Tiến độ thực hiện (Sprint Roadmap)
- **Giai đoạn 1 (Ngày 1-2):** Thiết kế CSDL (Room DB) & Cấu trúc dự án chung.
- **Giai đoạn 2 (Ngày 3-5):** Phát triển song song các module (Login/SharedPreferences, CRUD Nhân viên, Check-in/Check-out).
- **Giai đoạn 3 (Ngày 6-7):** Tích hợp module Tính lương (Salary Engine) & Lịch sử điểm danh.
- **Giai đoạn 4 (Ngày 8):** Kiểm thử toàn bộ hệ thống (Testing), fix bug, hoàn thiện tài liệu và bàn giao.
