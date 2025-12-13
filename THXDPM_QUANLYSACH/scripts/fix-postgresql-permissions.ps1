# Script PowerShell để fix lỗi permission PostgreSQL

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Fix PostgreSQL Permissions" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Yêu cầu thông tin
$username = Read-Host "Nhập username PostgreSQL (mặc định: postgres)"
if ([string]::IsNullOrWhiteSpace($username)) {
    $username = "postgres"
}

$password = Read-Host "Nhập password PostgreSQL" -AsSecureString
$passwordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($password)
)

Write-Host ""
Write-Host "1. Đang cấp quyền cho schema public..." -ForegroundColor Yellow

$env:PGPASSWORD = $passwordPlain

# Cấp quyền
$grantSchema = @"
\c quanlysach
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO postgres;
ALTER DATABASE quanlysach OWNER TO postgres;
"@

$grantSchema | & psql -U $username -d postgres 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Đã cấp quyền thành công!" -ForegroundColor Green
} else {
    Write-Host "   ⚠️  Có thể cần chạy với quyền superuser" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "2. Đang tạo bảng books..." -ForegroundColor Yellow

$createTable = @"
\c quanlysach
CREATE TABLE IF NOT EXISTS books (
    id VARCHAR(100) NOT NULL,
    book_type VARCHAR(20) NOT NULL CHECK (book_type IN ('SACH_GIAO_KHOA', 'SACH_THAM_KHAO')),
    ngay_nhap DATE NOT NULL,
    don_gia DOUBLE PRECISION NOT NULL,
    so_luong INTEGER NOT NULL,
    nha_xuat_ban VARCHAR(255) NOT NULL,
    tinh_trang VARCHAR(10) CHECK (tinh_trang IN ('MOI', 'CU')),
    thue DOUBLE PRECISION,
    PRIMARY KEY (id)
);
GRANT ALL PRIVILEGES ON TABLE books TO postgres;
"@

$createTable | & psql -U $username -d postgres 2>&1 | Out-Null

if ($LASTEXITCODE -eq 0) {
    Write-Host "   ✅ Bảng books đã được tạo!" -ForegroundColor Green
} else {
    Write-Host "   ❌ Không thể tạo bảng. Kiểm tra quyền." -ForegroundColor Red
}

Write-Host ""
Write-Host "3. Kiểm tra bảng..." -ForegroundColor Yellow

$checkTable = & psql -U $username -d quanlysach -tAc "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'books';" 2>&1

if ($checkTable -eq "1") {
    Write-Host "   ✅ Bảng books đã tồn tại!" -ForegroundColor Green
} else {
    Write-Host "   ❌ Bảng books chưa tồn tại" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Hoàn tất!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Bây giờ bạn có thể chạy lại ứng dụng:" -ForegroundColor Yellow
Write-Host "  mvn spring-boot:run" -ForegroundColor White
Write-Host ""

# Xóa password khỏi memory
$passwordPlain = $null
$env:PGPASSWORD = $null

