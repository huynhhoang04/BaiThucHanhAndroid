## 🚀 Hướng Dẫn Quy Trình Làm Việc Nhóm (Team Workflow)

Vì dự án đang trong giai đoạn phát triển song song (Milestone 3, 4, 5...) và có 3 thành viên, chúng ta sẽ áp dụng **Git Flow cơ bản** để tránh việc code bị conflict hoặc ghi đè lên nhau.

### 1. Nhận Task (Assign Task)
- Mọi đầu việc sẽ được quản lý trên phần **Issues** của GitHub.
- Trước khi làm bất cứ tính năng gì, hãy vào phần Issues, tìm task tương ứng (hoặc tự tạo Issue mới nếu chưa có).
- Nhấn **Assign yourself** để thông báo cho 2 người còn lại biết là bạn đang làm phần này.

### 2. Tạo Branch (Nhánh mới)
- **TUYỆT ĐỐI KHÔNG** code trực tiếp và push thẳng lên nhánh `main`.
- Trước khi bắt đầu code task mới, hãy tạo một nhánh mới từ `main`:
```bash
git checkout main
git pull origin main # Cập nhật code mới nhất từ mọi người
git checkout -b feature/<tên-task-viết-liền-không-dấu>
# Ví dụ: git checkout -b feature/tracker-service
```

### 3. Commit Code
- Code xong một phần chức năng (hoặc hết ngày làm việc), hãy commit với thông điệp rõ ràng:
```bash
git add .
git commit -m "feat: Thêm chức năng đăng ký Peer trên Tracker (#IssueID)"
```

### 4. Push và Tạo Pull Request (PR)
- Push nhánh của bạn lên Github:
```bash
git push origin feature/<tên-task>
```
- Lên Github, tạo một **Pull Request (PR)** từ nhánh `feature/...` vào nhánh `main`.
- Nhờ 1 trong 2 người còn lại review code, nếu mọi thứ OK thì bấm **Merge** vào `main`.

### 5. Cập nhật code mới
- Trong lúc bạn đang làm, nếu có ai đó đã gộp code mới vào `main`, hãy kéo code mới đó về nhánh của bạn để tránh conflict sau này:
```bash
git checkout main
git pull origin main
git checkout feature/<tên-task-của-bạn>
git merge main
```
*Lưu ý: Nếu có conflict (đụng độ code), hãy bình tĩnh mở file bị conflict lên, xem xét phần code nào nên giữ, phần nào bỏ, lưu lại và commit.*

---
Chúc cả nhóm hoàn thành tốt dự án!
