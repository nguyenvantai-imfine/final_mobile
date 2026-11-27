# MASTER_AGENT_INSTRUCTIONS.md
## TDT BANK MOBILE BANKING — SOVEREIGN ELITE

**Phiên bản:** 1.2
**Trạng thái:** **APPROVED — CLOSED** (Project Owner phê duyệt chính thức ngày 2026-11-27)
**Ngày:** 2026-11-27
**Vị trí lưu chính thức:** `docs/android-ui/MASTER_AGENT_INSTRUCTIONS.md` (Project Owner kiểm soát commit; Agent 00 quản lý nội dung và phiên bản)
**Người soạn:** Agent 00 — Master Project Coordinator
**Thẩm quyền phê duyệt cuối:** Project Owner
**Phạm vi:** Android UI (Native XML), nhánh `layout`

Ký hiệu nguồn: **[F01]** = FORM 01 gốc · **[PO-R1]** = Project Owner Decisions (Form 01 Revision) · **[PO-C1]** = Owner xác nhận 1–5 · **[PO-C2]** = Final Clarification & Closure · **[PO-F]** = Official Approval & Closure (Q-01 đến Q-04).

**Từ khi được phê duyệt, FORM 01 là bộ quy tắc điều phối nền tảng của dự án. Mọi FORM sau phải tuân thủ FORM 01.**

Phiên bản mới nhất được duyệt luôn có hiệu lực cao nhất. Tài liệu này không chứa thông số thiết kế nào do Agent 00 tự đặt ra.

---

## PHẦN A — QUY ĐỊNH ĐÃ CHỐT (CLOSED)

### A1. Phạm vi và công nghệ
| Mã | Quy định | Nguồn |
|---|---|---|
| C-01 | Chỉ phát triển giao diện Android Native XML. | F01 §03, PO-C2 |
| C-02 | Ngôn ngữ Java (Java 17). Không triển khai Java business logic; không đổi kiến trúc Java (package, repository, ViewModel, Firebase, navigation, business logic). | F01 §02–03, PO-R1 §7, PO-C2 |
| C-03 | **Light Mode only.** Baseline theme `Theme.Material3.Light.NoActionBar`. Không Dark Mode, không night-specific resources, không tự động chuyển theme. | F01 §06, PO-R1 §1, PO-C2 |
| C-04 | Hoãn lại: Java logic, Firebase auth, Firestore, API, Repository, ViewModel, navigation nghiệp vụ, tái cấu trúc database. Không mở rộng phạm vi khi chưa có phép của Project Owner. | F01 §03 |

### A2. Vai trò và quyền hạn
| Mã | Quy định | Nguồn |
|---|---|---|
| C-05 | Mô hình 4 agent: Agent 00 (điều phối) + Agent 01, 02, 03 (IDE Execution). Cả bốn theo cùng một chuẩn, không agent nào tự lập hệ thiết kế riêng. | F01 §04 |
| C-06 | **Agent 00** điều phối, review, quản lý tiêu chuẩn, đề xuất merge. **Không trực tiếp sửa code trong repository.** Không coi báo cáo "thành công" là hoàn thành; phải review output thực tế. | F01 §04, PO-R1 §6, PO-C2 §2.10 |
| C-07 | **IDE Agent** chỉ thực hiện công việc được phân công, theo đúng chuẩn chung. | F01 §04, PO-C2 §2.11 |
| C-08 | **Project Owner** có quyền phê duyệt cuối cùng: thiết kế, Design System, chính sách đặt tên, phân công agent, thay đổi phạm vi, nghiệm thu màn hình, quyết định cuối cùng. | F01 Final Authority, PO-C2 §2.12 |
| C-09 | Agent không được tự suy diễn thông số thiết kế chưa có tài liệu. Mọi thông số như vậy được đánh dấu **PENDING — WAITING FOR FORM 02**. | PO-C2 §3 |

### A3. Tài nguyên dùng chung
| Mã | Quy định | Nguồn |
|---|---|---|
| C-10 | Reuse trước; không tạo tài nguyên trùng; chỉ tạo khi tài nguyên hiện có không đáp ứng; mọi tài nguyên dùng chung được ghi vào Registry; cập nhật tài liệu sau mỗi thay đổi; không xóa tài nguyên dùng chung khi chưa kiểm tra tham chiếu và chưa được duyệt. | F01 §07 |
| C-11 | Không tạo Drawable trùng lặp về chức năng **và** thiết kế. | PO-C2 §2.8 |
| C-12 | **Semantic Design Tokens** là định hướng hệ màu chính thức. Thuộc tính theme Material3 phải ánh xạ về token chính thức. Không tạo nhiều resource cùng HEX và cùng ý nghĩa. Không xóa token cũ khi chưa kiểm tra tham chiếu. | PO-R1 §2, PO-C2 §2.6 |
| C-13 | Cấu trúc `values/` gồm: `colors.xml`, `dimens.xml`, `strings.xml`, `styles.xml`, `themes.xml`, `shapes.xml`. | PO-R1 §5 |
| C-14 | Đặt tên: tiếng Anh, lowercase snake_case, **semantic** (thể hiện chức năng), hạn chế hậu tố kích thước, không file trùng chức năng chỉ khác tên, không tên tiếng Việt, không hậu tố số, không đặt tên theo màn hình cho component dùng chung. Tài nguyên riêng một màn chỉ khi thiết kế thực sự độc nhất. | F01 §08, PO-R1 §4 |
| C-15 | Tài nguyên cũ phải được kiểm kê trước khi thay thế và được đánh dấu LEGACY; không xóa hoặc ghi đè hàng loạt khi chưa được phép. | PO-R1 §3, PO-C2 §2.7 |

### A4. Git và kiểm soát xung đột
| Mã | Quy định | Nguồn |
|---|---|---|
| C-16 | Mỗi IDE Agent làm việc trên branch/worktree riêng. Không cho nhiều agent cùng sửa một shared resource đồng thời. Mỗi tác vụ có quyền sở hữu file rõ ràng. Không giả định thay đổi ở workspace của agent này tự xuất hiện ở agent khác. | F01 §10, PO-R1 §6, PO-C2 §2.9 |
| C-17 | Cấu trúc nhánh chính thức: `layout` = nhánh tích hợp; `layout/agent-01` = Agent 01; `layout/agent-02` = Agent 02; `layout/agent-03` = Agent 03. **Agent 00 chỉ review và đề xuất merge; Project Owner quyết định merge vào `layout`.** Các nhánh `main`, `duc`, `huy`, `tai` được bỏ qua. | PO-C1 §4, PO-F Q-01 |

### A5. Quy trình, bàn giao, nghiệm thu
| Mã | Quy định | Nguồn |
|---|---|---|
| C-18 | Quy trình 9 bước cho mỗi màn hình: Receive → Analyze → Resource Check → Assign → Execute → Submit → Review → Approve → Update. Gói giao việc gồm: tên màn, file nguồn, file đầu ra dự kiến, chuẩn thiết kế liên quan, tài nguyên cần tái sử dụng, yêu cầu đặc biệt. | F01 §09 |
| C-19 | Mỗi bài nộp gồm: layout XML, drawable/vector mới, thay đổi tài nguyên, cập nhật Registry, danh sách tài nguyên tái sử dụng, danh sách file hiện có đã sửa, kết quả kiểm tra, vấn đề tồn đọng. Đường dẫn tương đối theo module Android. Không báo file chưa thật sự tạo; không tuyên bố đã tích hợp khi chưa tích hợp. | F01 §11 |
| C-20 | Màn hình không được duyệt chỉ vì file XML tồn tại. Kiểm tra: cú pháp XML, tham chiếu tài nguyên, constraint, id thiếu, nhất quán component, dùng đúng shared resource, độ khớp Stitch, responsive, cuộn, không bị cắt/chồng, biên dịch tài nguyên thành công. Đối chiếu hình ảnh bằng emulator/ảnh chụp khi có; nếu không có phải **báo rõ giới hạn**. | F01 §12 |
| C-21 | Giao tiếp: nhận việc → xác định nhiệm vụ, điều kiện thiếu, cách thực hiện, xung đột → báo hoàn thành kèm bằng chứng. Khi agent báo lỗi: tìm nguyên nhân thật, xét ảnh hưởng tới màn khác, kiểm tra shared resource, giao việc sửa, cập nhật tài liệu. Không giấu vấn đề; không âm thầm quyết định thiết kế lớn; thông tin mơ hồ thì hỏi, không tự bịa. | F01 §05, §14 |
| C-22 | Không chuyển đổi hàng loạt và không phát hành nhiệm vụ màn hình cho IDE Agent khi tiêu chuẩn chung chưa hoàn chỉnh. Không tạo Drawable tùy tiện trước khi Design System được chốt. | F01 §15, PO-C2 §4 |

### A5+. Quyết định bổ sung của Project Owner [PO-F]
| Mã | Quy định | Nguồn |
|---|---|---|
| C-23 | **Java và View ID (Q-02).** Được phép xây dựng lại XML hoàn toàn theo Stitch. Giữ ID cũ nếu còn phù hợp; được tạo ID mới khi cần. **Không chỉnh sửa Java trong UI phase.** Mỗi bài bàn giao phải ghi rõ: danh sách ID cũ được giữ, ID mới, và các binding Java cần đồng bộ sau này. Java integration xử lý ở phase riêng. **Không hy sinh chất lượng thiết kế để duy trì cấu trúc XML cũ.** (C-23 điều chỉnh C-19, nhưng không nới C-02: Java vẫn đóng băng.) | PO-F Q-02 |
| C-24 | **Build và Visual QA (Q-03).** IDE Agent chịu trách nhiệm kiểm tra XML, resource và Gradle build khi môi trường cho phép, và nộp ảnh preview hoặc emulator để đối chiếu Stitch. Agent 00 review kết quả và báo cáo giới hạn kỹ thuật. Project Owner kiểm tra cuối trên Android Studio thực tế. | PO-F Q-03 |
| C-25 | **Phân loại lỗi build.** Báo cáo build phải tách: (a) lỗi toàn ứng dụng do layout/drawable cũ đang thiếu (đã biết, xem C-S2); (b) lỗi phát sinh từ màn hình hoặc tài nguyên mới. Chỉ (b) được tính vào bài nộp của IDE Agent. | PO-F Q-03 |
| C-26 | **Tài liệu (Q-04).** Tài liệu chính thức lưu trong `docs/android-ui/`. Project Owner kiểm soát commit tài liệu; Agent 00 quản lý nội dung và phiên bản. | PO-F Q-04 |
| C-27 | FORM 01 trạng thái **APPROVED — CLOSED**; là bộ quy tắc nền tảng, mọi FORM sau phải tuân thủ. | PO-F |

### A6. Giữ nguyên hiện trạng (Owner: "nếu ổn thì cứ giữ nguyên")
`app/src.zip`; 10 file `.idea/` đang được git theo dõi; việc không có `.gitattributes` — **không thay đổi** cho tới khi Project Owner chỉ đạo khác. `app/src/main/res.zip` chỉ để lưu trữ dữ liệu, **không dùng làm tham chiếu thiết kế** và không đưa vào Registry. [PO-C1]

---

## PHẦN B — CẤU HÌNH DỰ ÁN ĐÃ XÁC NHẬN [F01 §02]
Java 17 · Native Android Views · XML · View Binding bật · ConstraintLayout (chính), LinearLayout, FrameLayout (bổ sung) · Material Components 1.14.0 · ConstraintLayout 2.2.2 · AppCompat 1.8.0 · minSdk 24 · targetSdk 37 · compileSdk 37 · Light Mode only. Cấu hình build thực tế trong repo (đã đối chiếu) khớp các giá trị trên.

---

## PHẦN C — PENDING — WAITING FOR FORM 02 TRỞ ĐI
Các mục dưới đây **chưa có tài liệu chính thức**; không agent nào được tự quyết.

| Mục | Trạng thái |
|---|---|
| Color Palette: mã HEX chính thức của từng token | PENDING — WAITING FOR FORM 02 |
| Typography: font, thang cỡ chữ | PENDING — WAITING FOR FORM 02 |
| Spacing, Corner Radius, Elevation | PENDING — WAITING FOR FORM 02 |
| Buttons, Inputs, Cards, Bottom Sheets, Chips, Badges, Icons, Component States | PENDING — WAITING FOR FORM 02 |
| Cấu hình system bar cho Light | PENDING — WAITING FOR FORM 02 |
| Hợp nhất hệ màu hiện có (`colors.xml`) về Semantic Tokens (xem ghi chú C-S1) | PENDING — WAITING FOR FORM 02 |
| `ANDROID_DESIGN_SYSTEM.md` | PENDING — WAITING FOR FORM 02 |
| `HTML_TO_XML_CONVERSION_RULES.md` (gồm danh sách container được phép, chính sách chuỗi chữ `strings.xml`) | PENDING — WAITING FOR FORM tiếp theo |
| `OUTPUT_AND_QA_RULES.md` | PENDING — WAITING FOR FORM tiếp theo |
| `AGENT_COORDINATION.md` (chi tiết quy ước phối hợp; tên nhánh và người merge đã chốt ở C-17) | PENDING — WAITING FOR FORM tiếp theo |
| `DRAWABLE_RESOURCE_REGISTRY.md`, `SCREEN_CONVERSION_TRACKER.md` | PENDING — dựng khi có Design System và danh sách màn hình |
| Danh sách màn hình cần làm, HTML/CSS và ảnh tham chiếu Stitch | PENDING |

---

## PHẦN D — GHI NHẬN HIỆN TRẠNG (chỉ là dữ kiện, không phải quyết định)
Quan sát trên nhánh `layout`, commit `4641efb` (2026-11-27 19:02):

- Project Owner chủ đích xóa trắng `layout/` và `drawable/` cũ để dựng lại giao diện từ Stitch (commit `bbase`); `values-night/themes.xml` đã bị loại; hai drawable launcher đã được khôi phục (`bupdate`).
- `values/` còn `colors.xml` (73 màu), `shapes.xml`, `strings.xml`, `themes.xml`; chưa có `dimens.xml`, `styles.xml`. Theme baseline là `Theme.Material3.Light.NoActionBar`.
- **C-S1:** `colors.xml` hiện gồm 3 nhóm tên màu (semantic, `tdt_` kiểu Material, không tiền tố) với một số giá trị HEX mâu thuẫn nhau. Đây là hiện trạng LEGACY, chưa được sửa; việc hợp nhất chờ Design System.
- **C-S2:** Java (không đổi) vẫn gọi 9 `R.layout.*`, `DialogLogoutConfirmBinding`, 3 drawable `bg_input_normal/error/locker` và 223 view id. Do layout đã bị xóa trắng, **dự án hiện chưa biên dịch được** — đây là trạng thái đã biết và được Owner chấp nhận.
- Môi trường của Agent 00 không có Android SDK/emulator và chưa liên kết GitHub: Agent 00 chỉ đọc được code đã push, không build, không chụp màn hình, không push.

---

## PHẦN E — MÂU THUẪN VÀ CHÍNH SÁCH: ĐÃ GIẢI QUYẾT
Bốn vấn đề do Agent 00 nêu trong v1.1 đã được Project Owner chốt ngày 2026-11-27. **Hiện không còn mâu thuẫn chính sách nào mở.**

| Mã | Vấn đề đã nêu | Quyết định | Quy định tương ứng |
|---|---|---|---|
| Q-01 | Mô hình nhánh và người merge | `layout` là nhánh tích hợp; `layout/agent-01`, `layout/agent-02`, `layout/agent-03`; Agent 00 chỉ review và đề xuất merge; Owner quyết định merge. | C-17 |
| Q-02 | Java đóng băng so với dựng lại UI từ đầu | Dựng lại XML hoàn toàn theo Stitch; giữ ID cũ nếu phù hợp, được tạo ID mới; không sửa Java; ghi nhận ID/binding cần đồng bộ trong báo cáo bàn giao; Java integration ở phase riêng. | C-23 |
| Q-03 | Ai build và đối chiếu hình ảnh | IDE Agent kiểm tra XML/resource/Gradle và nộp ảnh preview/emulator; Agent 00 review và báo giới hạn kỹ thuật; Owner kiểm tra cuối trên Android Studio; phân biệt lỗi build do layout cũ thiếu với lỗi từ màn hình mới. | C-24, C-25 |
| Q-04 | Nơi lưu tài liệu chuẩn | `docs/android-ui/`; Owner kiểm soát commit; Agent 00 quản lý nội dung và phiên bản. | C-26 |

**Đã kiểm tra, không phải mâu thuẫn:** (1) Quy tắc cấm xóa không phép (F01 §07) so với việc Owner tự xóa trắng: Owner có quyền tối thượng (C-08). (2) F01 §03 liệt kê tạo Drawable trong phạm vi nhưng F01 §15 cấm tạo sớm: chỉ khác thứ tự thực hiện (C-22). (3) Agent 00 "không sửa code" nhưng phải duy trì tài liệu: tài liệu không phải code, quyền ghi tài liệu nằm ngoài repo (xem Q-04).

---

## PHẦN F — NHẬT KÝ THAY ĐỔI
| Phiên bản | Nội dung |
|---|---|
| 1.0 | FORM 01 gốc. |
| 1.1 | Hợp nhất FORM 01 với Project Owner Decisions (Revision), xác nhận 1–5 và Final Clarification & Closure; tách mục đã chốt, mục PENDING và mục chờ quyết định. |
| 1.2 | Ghi nhận Official Approval & Closure: FORM 01 **APPROVED — CLOSED**; chốt Q-01 đến Q-04 (C-17, C-23 đến C-27); Phần E chuyển thành đã giải quyết; ghi vị trí lưu `docs/android-ui/`. |

**END OF MASTER_AGENT_INSTRUCTIONS.md v1.2**
