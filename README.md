# TDT Bank – Mobile Banking App (final_mobile)

Đồ án cuối kỳ môn **Mobile Apps Development** – ĐH Tôn Đức Thắng. Ứng dụng ngân hàng di động cho hai vai trò
**Customer** và **Officer**, viết bằng **Java + XML**, kiến trúc **MVVM**, dữ liệu trên **Firebase**.

## Công nghệ

- Android (minSdk 24), Java 17, ViewBinding, Material 3
- MVVM: `ui` → `viewmodel` → `repository` → Firebase
- Firebase Authentication, Cloud Firestore, Cloud Storage
- Chatbot: rule-based (mặc định) + Gemini API (tùy chọn, tự về rule-based khi lỗi)

## Chạy dự án

1. Mở thư mục trong Android Studio, chờ Gradle sync.
2. Đảm bảo `app/google-services.json` đúng Firebase project của nhóm.
3. (Tùy chọn) bật chatbot AI: thêm vào `local.properties` (file này đã bị `.gitignore`, **không commit**):

   ```properties
   GEMINI_API_KEY=your_key_here
   GEMINI_MODEL=gemini-2.5-flash
   ```

   Không có khóa thì chatbot dùng chế độ rule-based.
4. Triển khai Security Rules (cần [Firebase CLI](https://firebase.google.com/docs/cli)):

   ```bash
   firebase deploy --only firestore:rules,storage
   ```

   Rules nằm ở `firebase/firestore.rules` và `firebase/storage.rules`.
5. Chạy app. Để có dữ liệu demo: đăng nhập bằng tài khoản Officer → **Khách hàng** → menu ⋮ → **Nạp dữ liệu mẫu**.
   Tài khoản demo tạo ra: `khach.demo@tdtbank.vn` / `Demo@123456` (SĐT `0900000001`).

## Cấu trúc mã nguồn (`app/src/main/java/com/example/ck_mobile`)

| Gói | Nội dung |
| --- | --- |
| `ui/auth`, `ui/base` | Đăng nhập, `BaseSecureActivity` (kiểm tra vai trò và khóa tài khoản) |
| `ui/officer` | Danh sách / tạo / sửa / khóa khách hàng, lãi suất, đăng ký và duyệt eKYC |
| `ui/customer` | Lịch sử giao dịch, vé phim / khách sạn, chatbot, trạng thái eKYC |
| `viewmodel` | `CustomerListViewModel`, `TransactionHistoryViewModel`, `ChatViewModel` |
| `repository` | Mọi truy cập Firebase (`Customer`, `Rate`, `Kyc`, `Transaction`, `ServiceOrder`, `Chat`, `Seed`, `Audit`) |
| `chat` | `ChatEngine`, `RuleBasedChatEngine`, `GeminiChatEngine`, `HybridChatEngine`, `IntentMatcher` |
| `model` | Đối tượng dữ liệu Firestore |
| `utils` | `AuthSessionHelper`, `Constants`, `DeepLinkRouter`, `MoneyUtils`, `UiText`, `ImageUtils` |

Tài liệu thiết kế và sơ đồ (ERD, kiến trúc, use case, class diagram, luồng màn hình) nằm trong thư mục `docs/`.

## Tích hợp màn hình của thành viên khác vào chatbot

Nút deep link trong chat mở màn hình qua `DeepLinkRouter`. Khi có màn hình mới, đăng ký một lần
(ví dụ trong `Application` hoặc `CustomerMainActivity.onCreate`):

```java
DeepLinkRouter.register(ChatActions.TRANSFER, TransferActivity.class);
```

Các mã đích: `HISTORY, TRANSFER, DEPOSIT_WITHDRAW, BILL, TOPUP_PHONE, MOVIE, HOTEL, BRANCHES, ACCOUNTS, KYC`.

## Kiểm thử

```bash
./gradlew testDebugUnitTest   # IntentMatcherTest (thuần Java)
```

## Quy ước Git

Mỗi người một nhánh theo chức năng, mở Pull Request và review chéo trước khi gộp vào `main`.

## Tạo tài khoản Officer demo (làm 1 lần)

Rules chỉ cho Officer tạo khách hàng, nên Officer đầu tiên phải tạo thủ công trên Firebase Console:

1. **Authentication → Users → Add user**: nhập email (vd `officer@tdtbank.vn`) và mật khẩu, sao chép **User UID**.
2. **Firestore → collection `users` → Add document**, Document ID = UID vừa sao chép, các field (string):
   `fullName`, `email`, `role = OFFICER`, `status = ACTIVE`.
3. Đăng nhập app bằng tài khoản đó → màn hình Officer → menu ⋮ → **Nạp dữ liệu mẫu** (lãi suất, chi nhánh, khách demo `khach.demo@tdtbank.vn` / `Demo@123456`).
4. Triển khai rules: `firebase deploy --only firestore:rules,storage`.

## Chạy unit test và sinh sơ đồ

- Test: `./gradlew testDebugUnitTest` (IntentMatcher, ChatActions, Gemini parse, MoneyUtils, UiText).
- Sơ đồ lớp: `python3 docs/diagrams/generate_class_diagrams.py` (cần Graphviz `dot`), kết quả trong `docs/diagrams/` (có `class_all.png` toàn app).
