# 🌿 Project 90 (v1.0.0) — Kỷ Luật Thép 90 Ngày Lột Xác Hoàn Toàn

> **"Kỷ luật là cầu nối giữa mục tiêu và thành tựu."**  
> *Project 90* là giải pháp phần mềm theo dõi thói quen cực đoan (Hardcore Habit & Discipline Tracker) được xây dựng trên nền tảng **Android Hiện Đại (Kotlin & Jetpack Compose)**, giúp người dùng bứt phá thể chất, dinh dưỡng, tâm trí và tài chính trước tháng Hai.

---

## 📱 Điểm Nổi Bật Trong Phiên Bản v1.0.0 (Official Release)

### 1. 🛡️ Bảng Điều Khiển Thông Báo Đẩy Bám Sát 24/7 (Persistent Notification Dashboard)
- **Cố định vĩnh viễn trên Status Bar:** Chạy dưới dạng Foreground Service chuyên biệt (`specialUse`), thiết lập `FLAG_ONGOING_EVENT` và `FLAG_NO_CLEAR`. Không thể bị xóa hay vuốt tắt bởi thao tác thông thường hoặc nút "Clear All".
- **Tự động phục hồi:** Tích hợp `ACTION_REPOST` thông qua `deleteIntent` và cơ chế `START_STICKY`, tự động tái khởi động nếu hệ thống cố gắng giải phóng bộ nhớ.
- **Giao diện Full-Width Chuẩn Material 3:** Bố cục phẳng không khoảng trống thừa, gồm:
  - Header đếm ngược ngày và chuỗi lửa (Streak).
  - Thanh tiến độ động theo thời gian thực.
  - 4 ô chỉ số tóm tắt: Bước chân, Nước uống, Thể lực (Hít đất/Squat), Không đường (No Sugar).
  - Danh sách cảnh báo các mục còn dang dở trong ngày.
  - Phím tắt tương tác một chạm: `+250ml nước`, `+500 bước chân`, mở nhanh ứng dụng.

### 2. 🏃 Mô-đun Vận Động (Movement Tracker)
- Tích hợp cảm biến đếm bước chân phần cứng (`Sensor.TYPE_STEP_COUNTER`) với mục tiêu **10.000 bước/ngày**.
- Hỗ trợ thêm bước chân thủ công khi tập luyện máy chạy bộ hoặc không mang điện thoại.
- Danh sách kiểm tra hàng ngày: 50 hít đất, 100 squats, tắm nước lạnh.
- Mục tiêu hàng tuần: 2 buổi tập gym/thể lực cường độ cao, 1 trận đá bóng đối kháng.

### 3. 🥗 Mô-đun Dinh Dưỡng Kỷ Luật (No Sugar & Pure Water)
- Cam kết nhị phân: **Không nạp đường tinh luyện (No Sugar)**.
- Bộ đếm nước thông minh: Chia nhỏ mục tiêu **2.000 ml nước lọc/ngày** theo từng nấc 250ml.
- Tooltip dinh dưỡng: Gợi ý các nhóm thực phẩm lành mạnh (Trứng, ức gà, cá, rau họ cải, trái cây nguyên quả).

### 4. 🌙 Mô-đun Cai Nghiện Điện Thoại & Giấc Ngủ (Digital Wellbeing)
- Giới nghiêm Caffeine: Cảnh báo ngừng nạp caffeine sau **14:00** để bảo vệ thụ thể Adenosine.
- Khóa màn hình sau **22:00**: Hạn chế ánh sáng xanh kích thích não bộ.
- Nhắc nhở lên giường trước **22:30**: Đảm bảo chu kỳ 6.5 - 7.5 tiếng ngủ sâu (Deep Sleep) trước 5:00 AM.

### 5. ⏰ Báo Thức Chống Ngủ Nướng 5:00 AM (Hardcore CameraX AI)
- Chuông báo động đa tầng: Tích hợp các kiểu chuông kỷ luật (Còi Báo Động, Tiếng Chuông Gõ Quân Đội, Tiết Tấu Gấp Gáp).
- **Cơ chế tắt chuông bằng Camera (Computer Vision):** Chuông báo thức **chỉ tắt** khi người dùng thức dậy, bật camera và chụp đúng mục tiêu chỉ định:
  - 💧 *Cốc nước lọc* (uống ngay sau khi thức dậy).
  - 🚰 *Bồn rửa mặt / Phòng tắm* (rửa mặt nước lạnh tỉnh táo tức thì).

### 6. 💰 Mô-đun Quản Lý Tài Chính (Pay Yourself First)
- **Tự động trích 10% tiết kiệm:** Khóa ngay 10% thu nhập vào Quỹ Tiết Kiệm Bất Khả Xâm Phạm trước khi chi tiêu.
- **Tự động bắt biến động số dư ngân hàng:** Ứng dụng tích hợp `NotificationListenerService` đọc và bóc tách thông báo nhận tiền/chi tiêu từ **TNEX, MB Bank, Vietcombank, Techcombank**, tự động cập nhật vào sổ chi tiêu.

### 7. 📖 Mô-đun Phát Triển Bản Thân (Read & Write)
- Đồng hồ đếm ngược 20 phút đọc sách tập trung.
- Nhật ký tối 3 dòng cô đọng: (1) Việc đã hoàn thành tốt - (2) Bài học kinh nghiệm - (3) Mục tiêu hành động ngày mai.

---

## 🎨 Thiết Kế Giao Diện: Eco-Botanical Aesthetic
- **Bảng màu chủ đạo:** Xanh lục rừng già (`#142C1B`, `#1B4324`, `#2E7D32`), xanh ngọc lục bảo (`#48BB78`), màu ngọc bích tuyết (`#E8F5E9`, `#F0FFF4`), vàng hổ phách chuỗi lửa (`#FFB800`, `#FFA726`).
- **Logo tối giản:** Biểu tượng số **90** cách điệu với đường nét hình học hiện đại và lá vàng kỷ luật trên nền xanh rừng đậm.

---

## 🏗️ Kiến Trúc Kỹ Thuật (Architecture & Tech Stack)

```
app/src/main/java/com/example/
├── camera/                  # Phân loại hình ảnh CameraX tắt báo thức
├── data/
│   ├── local/
│   │   ├── dao/             # Room DAOs (User, Habit, DailyLog, Finance)
│   │   ├── database/        # Room Database (Project90Database)
│   │   └── entity/          # Room Entities
│   └── repository/          # Repository Pattern trung gian quản lý dữ liệu
├── sensor/                  # Quản lý cảm biến phần cứng (Pedometer)
├── service/
│   ├── PersistentHabitNotificationService.kt  # Foreground Service 24/7
│   ├── HabitNotificationHelper.kt             # Tạo & Cập nhật Dashboard Notification
│   ├── BankNotificationListenerService.kt     # Đọc thông báo ngân hàng tự động
│   ├── NotificationActionReceiver.kt          # Xử lý nút bấm nhanh trên thông báo
│   ├── AlarmSoundManager.kt                   # Bộ máy phát chuông báo thức Hardcore
│   └── BootReceiver.kt                        # Khôi phục dịch vụ khi khởi động lại máy
├── ui/
│   ├── components/          # Thanh tiến độ vòng tròn 90 ngày, Thẻ thói quen, BottomNav
│   ├── screens/             # 6 Màn hình tính năng chính
│   ├── theme/               # Theme Eco-Botanical, M3 ColorScheme, Typography
│   └── viewmodel/           # Project90ViewModel (StateFlow & Coroutines)
└── MainActivity.kt          # Entry Point & Điều hướng Navigation Compose
```

- **Ngôn ngữ:** 100% Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Local Database:** Room Database với KSP
- **Asynchronous:** Kotlin Coroutines & StateFlow / SharedFlow
- **Camera & AI:** CameraX ImageAnalysis + Grayscale / Edge / Luminance Classification
- **Background Service:** Android Foreground Service (Special Use) + NotificationListenerService

---

## 🚀 Hướng Dẫn Build & Cài Đặt File APK

### Cách 1: Xuất file APK trực tiếp trong AI Studio
1. Mở thanh công cụ phía trên hoặc menu **Settings / Export** trong giao diện AI Studio.
2. Chọn **"Export APK"** hoặc **"Download APK"**.
3. Chuyển file APK sang điện thoại Android và tiến hành cài đặt.

### Cách 2: Build thủ công bằng Gradle CLI
Chạy lệnh trong terminal gốc của dự án:
```bash
gradle assembleDebug
```
File APK hoàn chỉnh sẽ được tạo tại đường dẫn:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📋 Giấy Phép & Tuyên Bố
- Phát triển bởi Đội ngũ Kỹ thuật Project 90.
- Phiên bản: `v1.0.0` (Build 1).
- Tương thích: Android 7.0 (API 24) trở lên, tối ưu hóa cho Android 14 & 15.
