// Nạp dữ liệu tham chiếu (config, banks, rates, branches) và tạo Officer đầu tiên.
// Cách dùng (PowerShell, trong thư mục firebase-seed):
//   node seed.js --email officer@tdtbank.vn --password "Matkhau@123" --name "Nhan Vien 1"
//   node seed.js --no-rates      (bỏ qua lãi suất nếu code app chưa cập nhật)
// Chạy lại nhiều lần an toàn: dùng id cố định, ghi đè.
const admin = require('firebase-admin');
const path = require('path');

const args = process.argv.slice(2);
const opt = (name) => { const i = args.indexOf('--' + name); return i >= 0 ? args[i + 1] : undefined; };
const flag = (name) => args.includes('--' + name);

let key;
try { key = require(path.join(__dirname, 'serviceAccountKey.json')); }
catch (e) { console.error('Không thấy serviceAccountKey.json trong thư mục này. Xem hướng dẫn Bước 5.'); process.exit(1); }

admin.initializeApp({ credential: admin.credential.cert(key) });
const db = admin.firestore();
const ts = admin.firestore.FieldValue.serverTimestamp();

const BRANCHES = [
  ['TDT Bank - Chi nhánh Quận 1', '12 Nguyễn Huệ, Quận 1', 10.7743, 106.7030],
  ['TDT Bank - Chi nhánh Quận 3', '88 Võ Văn Tần, Quận 3', 10.7786, 106.6907],
  ['TDT Bank - Chi nhánh Quận 7', '19 Nguyễn Hữu Thọ, Quận 7', 10.7295, 106.7218],
  ['TDT Bank - Chi nhánh Bình Thạnh', '45 Điện Biên Phủ, Bình Thạnh', 10.8012, 106.7109],
  ['TDT Bank - Chi nhánh Tân Bình', '210 Hoàng Văn Thụ, Tân Bình', 10.7993, 106.6615],
  ['TDT Bank - Chi nhánh Thủ Đức', '1 Võ Văn Ngân, Thủ Đức', 10.8509, 106.7717],
  ['TDT Bank - Chi nhánh Gò Vấp', '72 Quang Trung, Gò Vấp', 10.8346, 106.6633],
  ['TDT Bank - Chi nhánh Quận 5', '301 An Dương Vương, Quận 5', 10.7599, 106.6676],
];
const RATES = [[1, 3.0], [3, 4.5], [6, 5.5], [12, 6.0], [24, 6.2]];
const BANKS = [['VCB', 'Vietcombank'], ['BIDV', 'BIDV'], ['CTG', 'VietinBank'], ['TCB', 'Techcombank'],
  ['MBB', 'MB Bank'], ['ACB', 'ACB'], ['VPB', 'VPBank'], ['STB', 'Sacombank']];

async function main() {
  const batch = db.batch();

  batch.set(db.doc('config/limits'), { highValueThreshold: 10000000, dailyLimit: 500000000, maxOtpAttempts: 3 });

  BANKS.forEach(([code, name]) => batch.set(db.doc(`banks/${code}`), { name, fee: 7000, active: true }));

  BRANCHES.forEach((b, i) => batch.set(db.doc(`branches/branch_${String(i + 1).padStart(2, '0')}`),
    { name: b[0], address: b[1], lat: b[2], lng: b[3], phone: '1900 1234', openHours: '08:00 - 17:00', active: true }));

  if (!flag('no-rates')) {
    RATES.forEach(([m, r]) => batch.set(db.doc(`rates/SAVING_${m}M`),
      { productType: 'SAVING', termMonths: m, annualRate: r, updatedAt: ts }));
  }
  await batch.commit();
  console.log('OK: config, banks, branches' + (flag('no-rates') ? '' : ', rates') + ' đã được nạp.');

  const email = opt('email'), password = opt('password');
  if (email && password) {
    let user;
    try { user = await admin.auth().getUserByEmail(email); console.log('Officer đã tồn tại trong Authentication, dùng lại.'); }
    catch (e) { user = await admin.auth().createUser({ email, password, displayName: opt('name') || 'Officer' }); console.log('Đã tạo tài khoản Authentication:', email); }
    await db.doc(`users/${user.uid}`).set({
      fullName: opt('name') || 'Officer', email, phone: '', address: '',
      role: 'OFFICER', status: 'ACTIVE', kycStatus: 'NONE', createdAt: ts,
    });
    console.log('OK: users/' + user.uid + ' (role = OFFICER). Đăng nhập app bằng email + mật khẩu vừa nhập.');
  } else {
    console.log('Bỏ qua tạo Officer (không có --email và --password).');
  }
}
main().then(() => process.exit(0)).catch((e) => { console.error('LỖI:', e.message); process.exit(1); });
