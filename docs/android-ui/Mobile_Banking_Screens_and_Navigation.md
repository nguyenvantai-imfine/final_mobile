# Đồ án Mobile Banking: Danh sách màn hình & Sơ đồ điều hướng

**Môn:** Mobile Apps Development, Đại học Tôn Đức Thắng
**Đề:** FinalProject 2026–2027
**Phạm vi tài liệu:** chỉ liệt kê màn hình và luồng điều hướng, không đặc tả UI/UX.

---

## 1. Phân tích đề: các module cần phủ

| # | Yêu cầu trong đề | Hệ quả về màn hình |
|---|---|---|
| 1 | Hai loại người dùng: Customer / Bank Officer | Hai luồng giao diện tách biệt sau khi đăng nhập |
| 2 | Bảo mật: Login, eKYC khuôn mặt, OTP 2FA cho mọi giao dịch | Màn Face Verify, màn OTP dùng chung, chặn giao dịch giá trị lớn bằng khuôn mặt |
| 3 | Quản lý tài khoản: checking, saving, mortgage | Mỗi loại một màn chi tiết riêng (đề yêu cầu "different view") |
| 4 | Giao dịch: nội bộ, liên ngân hàng, tích hợp VNPay/Stripe | Chuỗi: Nhập liệu → Xác nhận → (Face) → OTP → Kết quả |
| 5 | Tiện ích: điện, nước, nạp ĐT, vé máy bay, vé phim, khách sạn, e-commerce | 7 nhóm, mỗi nhóm một mini-flow |
| 6 | Bản đồ, chi nhánh gần nhất, đường đi bộ ngắn nhất | Map, danh sách chi nhánh, chi tiết, chỉ đường |
| 7 | AI Chatbot có link đến các tính năng | Màn Chat, deep-link sang các màn khác |

### Ràng buộc quan trọng rút ra từ đề
- **Customer** xem và chỉnh sửa thông tin của mình; **Officer** tạo tài khoản, sửa dữ liệu khách theo yêu cầu, và là người duy nhất sửa lãi suất tiết kiệm.
- **Mọi giao dịch** phải qua OTP; giao dịch giá trị cao phải xác thực thêm bằng khuôn mặt.
- Hệ thống phải **kiểm tra hợp lệ** (số dư, hạn mức...) trước khi lưu giao dịch.
- Mortgage hiển thị số tiền phải trả **mỗi tháng hoặc mỗi 2 tuần**.
- Saving hiển thị **lãi suất** và **lãi mỗi tháng**.

---

## 2. Danh sách màn hình

### A. Nhóm chung (Auth & eKYC)

| STT | Màn hình | Ghi chú |
|---|---|---|
| 1 | Splash | Kiểm tra trạng thái đăng nhập |
| 2 | Onboarding | Tuỳ chọn |
| 3 | Login | Dùng chung cho Customer và Officer |
| 4 | Quên mật khẩu | Nhập email/SĐT |
| 5 | Đặt lại mật khẩu | Sau khi xác thực OTP |
| 6 | Đổi mật khẩu lần đầu | Cho tài khoản do Officer cấp |
| 7 | OTP Verification | **Dùng chung** cho mọi giao dịch và quên MK |
| 8 | Face Verification | **Dùng chung**, cho giao dịch giá trị cao |

### B. Customer

#### B1. Trang chủ & Tài khoản

| STT | Màn hình | Ghi chú |
|---|---|---|
| 9 | Home / Dashboard | Tổng số dư, lối tắt chức năng |
| 10 | Danh sách tài khoản | Checking, Saving, Mortgage |
| 11 | Chi tiết TK Checking | Số dư |
| 12 | Chi tiết TK Saving | Số dư, lãi suất, lãi mỗi tháng |
| 13 | Chi tiết TK Mortgage | Số tiền trả mỗi tháng hoặc 2 tuần, lịch trả nợ |
| 14 | Lịch sử giao dịch | Có bộ lọc |
| 15 | Chi tiết giao dịch / Biên lai | |
| 16 | Nạp tiền (Deposit) | |
| 17 | Rút tiền (Withdraw) | |

#### B2. Giao dịch

| STT | Màn hình | Ghi chú |
|---|---|---|
| 18 | Chọn loại chuyển khoản | Nội bộ / Liên ngân hàng |
| 19 | Chuyển khoản nội bộ | Nhập thông tin |
| 20 | Chuyển khoản liên ngân hàng | Chọn ngân hàng + nhập thông tin |
| 21 | Xác nhận giao dịch | **Dùng chung** |
| 22 | Kết quả giao dịch | **Dùng chung**, thành công / thất bại |
| 23 | Cổng thanh toán VNPay / Stripe | WebView hoặc màn chọn phương thức |
| 24 | Danh sách người thụ hưởng | Tuỳ chọn |

#### B3. Tiện ích

| STT | Màn hình | Ghi chú |
|---|---|---|
| 25 | Menu Tiện ích | |
| 26 | Thanh toán hoá đơn điện | |
| 27 | Thanh toán hoá đơn nước | |
| 28 | Nạp tiền điện thoại | |
| 29 | Vé máy bay | Tìm chuyến → Danh sách chuyến → Nhập hành khách → Xác nhận |
| 30 | Vé phim | Danh sách phim → Chọn rạp/suất → Chọn ghế → Xác nhận |
| 31 | Khách sạn | Tìm kiếm → Danh sách → Chi tiết → Xác nhận |
| 32 | E-commerce | Danh sách sản phẩm → Giỏ hàng → Checkout |
| 33 | Kết quả thanh toán tiện ích | **Dùng chung** |

#### B4. Bản đồ

| STT | Màn hình | Ghi chú |
|---|---|---|
| 34 | Bản đồ | Vị trí người dùng + chi nhánh gần |
| 35 | Danh sách chi nhánh / ATM | |
| 36 | Chi tiết chi nhánh | |
| 37 | Chỉ đường đi bộ | Đường ngắn nhất đến chi nhánh gần nhất |

#### B5. Chatbot

| STT | Màn hình | Ghi chú |
|---|---|---|
| 38 | Chatbot | Trả lời kèm link đến chức năng liên quan |

#### B6. Cá nhân

| STT | Màn hình | Ghi chú |
|---|---|---|
| 39 | Hồ sơ cá nhân | |
| 40 | Chỉnh sửa thông tin | |
| 41 | Đổi mật khẩu | |
| 42 | Cài đặt bảo mật | Trạng thái eKYC, đăng ký lại khuôn mặt |
| 43 | Thông báo | Tuỳ chọn |

### C. Bank Officer

| STT | Màn hình | Ghi chú |
|---|---|---|
| 44 | Officer Dashboard | |
| 45 | Danh sách khách hàng | Có tìm kiếm |
| 46 | Tạo khách hàng: nhập thông tin | |
| 47 | Tạo khách hàng: thu thập khuôn mặt | eKYC |
| 48 | Tạo tài khoản ngân hàng cho khách | Chọn loại checking / saving / mortgage |
| 49 | Kết quả tạo tài khoản | Thông tin đăng nhập cấp cho khách |
| 50 | Chi tiết khách hàng | |
| 51 | Sửa thông tin khách hàng | |
| 52 | Danh sách tài khoản của khách | Xem, khoá/mở (khoá/mở là tuỳ chọn) |
| 53 | Xem giao dịch của khách | |
| 54 | Quản lý lãi suất tiết kiệm | Danh sách |
| 55 | Sửa lãi suất | |
| 56 | Hồ sơ Officer / Đổi mật khẩu | |

**Tổng cộng: 56 màn hình** (đã gồm các màn tuỳ chọn và các màn dùng chung chỉ tính một lần).

---

## 3. Sơ đồ điều hướng tổng thể (Prototype Navigation Flow)

### 3.1. Luồng gốc

```
Splash → (đã đăng nhập?)
   ├─ Chưa → Onboarding → Login
   │            ├─ Quên MK → Nhập email/SĐT → OTP → Đặt lại MK → Login
   │            └─ Đăng nhập thành công → kiểm tra role
   │                  ├─ CUSTOMER → (lần đầu? Đổi MK) → Customer Main
   │                  └─ OFFICER  → Officer Main
   └─ Rồi → kiểm tra role → Main tương ứng
```

### 3.2. Customer Main

**Bottom Navigation:** Home | Tài khoản | Tiện ích | Bản đồ | Cá nhân
**Chatbot:** nút nổi (floating button), hiện ở mọi tab.

```
HOME
 ├─ Lối tắt: Chuyển khoản → [Luồng giao dịch]
 ├─ Lối tắt: Nạp / Rút → Deposit / Withdraw → [Luồng giao dịch]
 ├─ Lối tắt: Tiện ích → Tab Tiện ích
 ├─ Thông báo
 └─ Danh sách TK → Chi tiết (Checking | Saving | Mortgage)

TÀI KHOẢN
 └─ Danh sách TK → Chi tiết theo loại
        ├─ Lịch sử giao dịch → Chi tiết giao dịch
        └─ Nạp / Rút

TIỆN ÍCH
 └─ Menu → Điện | Nước | Nạp ĐT | Vé máy bay | Vé phim | Khách sạn | E-commerce
        └─ mỗi mục: Nhập/Chọn → Xác nhận → [Luồng giao dịch: bước xác thực] → Kết quả

BẢN ĐỒ
 └─ Map ⇄ Danh sách chi nhánh → Chi tiết chi nhánh → Chỉ đường đi bộ

CÁ NHÂN
 └─ Hồ sơ → Sửa thông tin | Đổi MK | Bảo mật (eKYC) | Đăng xuất → Login

CHATBOT (nút nổi, mọi tab)
 └─ Chat → link sang: Nạp ĐT | Thanh toán điện/nước | Lịch sử giao dịch | Chuyển khoản
```

### 3.3. Luồng giao dịch (dùng chung)

Áp dụng cho: chuyển khoản, nạp/rút tiền, mọi tiện ích có thanh toán.

```
Nhập thông tin
   → Xác nhận giao dịch
   → Hệ thống kiểm tra hợp lệ (số dư, hạn mức)
        ├─ Không hợp lệ → Kết quả: Thất bại → Thử lại / Về Home
        └─ Hợp lệ → Giá trị cao?
               ├─ Có   → Face Verification → OTP
               └─ Không → OTP
   → Kết quả: Thành công → Biên lai → Về Home / Lịch sử giao dịch
              Thất bại   → Thử lại / Về Home

(Nạp tiền qua VNPay/Stripe: Xác nhận → Cổng thanh toán → Kết quả)
```

### 3.4. Officer Main

**Bottom Navigation:** Dashboard | Khách hàng | Lãi suất | Cá nhân

```
DASHBOARD
 └─ Lối tắt: Tạo khách hàng | Danh sách KH | Lãi suất

KHÁCH HÀNG
 ├─ Danh sách (tìm kiếm) → Chi tiết khách hàng
 │      ├─ Sửa thông tin
 │      ├─ Danh sách TK → Xem giao dịch
 │      └─ Đăng ký/cập nhật khuôn mặt (eKYC)
 └─ Tạo khách hàng mới:
        Nhập thông tin → Thu thập khuôn mặt → Chọn loại TK
        → Kết quả (thông tin đăng nhập) → Chi tiết khách hàng

LÃI SUẤT
 └─ Danh sách → Sửa lãi suất → Xác nhận → Danh sách

CÁ NHÂN
 └─ Hồ sơ | Đổi MK | Đăng xuất → Login
```

---

## 4. Ma trận màn hình dùng chung

| Màn hình | Được gọi từ |
|---|---|
| OTP Verification | Quên MK, chuyển khoản, nạp/rút, mọi tiện ích |
| Face Verification | Giao dịch giá trị cao; Officer thu thập khuôn mặt; Customer đăng ký lại khuôn mặt |
| Xác nhận giao dịch | Chuyển khoản, nạp/rút, tiện ích |
| Kết quả giao dịch | Chuyển khoản, nạp/rút, tiện ích, VNPay/Stripe |
| Chi tiết giao dịch / Biên lai | Lịch sử giao dịch, Kết quả giao dịch, Officer xem giao dịch khách |
| Lịch sử giao dịch | Chi tiết TK, Chatbot, Officer xem giao dịch khách |

---

## 5. Phân loại ưu tiên

**Bắt buộc theo đề (làm trước):**
Login, OTP, Face Verify, 3 loại chi tiết tài khoản, lịch sử giao dịch, nạp/rút, chuyển khoản (nội bộ + liên ngân hàng), VNPay/Stripe, các tiện ích, bản đồ + đường đi, chatbot, toàn bộ luồng Officer.

**Tuỳ chọn (bỏ được nếu thiếu thời gian):**
Onboarding, Danh sách người thụ hưởng, Thông báo, chức năng khoá/mở tài khoản.

---

## 6. Lưu ý khi thiết kế Prototype

- OTP, Face Verify, Xác nhận và Kết quả giao dịch chỉ cần thiết kế **một bản**, tái sử dụng ở mọi luồng.
- Các tiện ích phức tạp (vé máy bay, vé phim, khách sạn, e-commerce) có thể làm gọn **2–3 bước** mỗi loại.
- Prototype nên nối dây đủ **hai nhánh** (Customer và Officer) và cả đường **thất bại** của giao dịch để thể hiện việc "verify trước khi lưu".
- Chatbot cần có ít nhất các link: nạp ĐT, hoá đơn điện/nước, lịch sử giao dịch, chuyển khoản.
