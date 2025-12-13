# Script PowerShell để thiết lập PostgreSQL cho dự án Quản Lý Sách

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Thiết lập PostgreSQL Database" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Kiểm tra PostgreSQL service
Write-Host "1. Kiểm tra PostgreSQL service..." -ForegroundColor Yellow
$pgService = Get-Service -Name "*postgresql*" -ErrorAction SilentlyContinue

if ($null -eq $pgService) {
    Write-Host "   ❌ PostgreSQL service không tìm thấy!" -ForegroundColor Red
    Write-Host "   Vui lòng cài đặt PostgreSQL trước." -ForegroundColor Red
    exit 1
} else {
    Write-Host "   ✅ Tìm thấy PostgreSQL service: $($pgService.Name)" -ForegroundColor Green
    
    if ($pgService.Status -ne "Running") {
        Write-Host "   ⚠️  Service chưa chạy. Đang khởi động..." -ForegroundColor Yellow
        Start-Service -Name $pgService.Name
        Start-Sleep -Seconds 2
        Write-Host "   ✅ Service đã được khởi động" -ForegroundColor Green
    } else {
        Write-Host "   ✅ Service đang chạy" -ForegroundColor Green
    }
}

Write-Host ""
Write-Host "2. Kiểm tra kết nối PostgreSQL..." -ForegroundColor Yellow

# Yêu cầu thông tin
$username = Read-Host "   Nhập username PostgreSQL (mặc định: postgres)"
if ([string]::IsNullOrWhiteSpace($username)) {
    $username = "postgres"
}

$password = Read-Host "   Nhập password PostgreSQL" -AsSecureString
$passwordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($password)
)

# Thử kết nối
Write-Host "   Đang kiểm tra kết nối..." -ForegroundColor Yellow

try {
    $env:PGPASSWORD = $passwordPlain
    $result = & psql -U $username -d postgres -c "SELECT version();" 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Kết nối thành công!" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Kết nối thất bại!" -ForegroundColor Red
        Write-Host "   Lỗi: $result" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "   ❌ Không thể kết nối. Đảm bảo psql đã được thêm vào PATH." -ForegroundColor Red
    Write-Host "   Hoặc sử dụng pgAdmin để tạo database thủ công." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "3. Tạo database 'quanlysach'..." -ForegroundColor Yellow

$dbExists = & psql -U $username -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='quanlysach'" 2>&1

if ($dbExists -eq "1") {
    Write-Host "   ✅ Database 'quanlysach' đã tồn tại" -ForegroundColor Green
} else {
    Write-Host "   Đang tạo database..." -ForegroundColor Yellow
    $createResult = & psql -U $username -d postgres -c "CREATE DATABASE quanlysach;" 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "   ✅ Database 'quanlysach' đã được tạo thành công!" -ForegroundColor Green
    } else {
        Write-Host "   ❌ Không thể tạo database!" -ForegroundColor Red
        Write-Host "   Lỗi: $createResult" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "4. Cập nhật application.properties..." -ForegroundColor Yellow

$propertiesFile = "src\main\resources\application.properties"
if (Test-Path $propertiesFile) {
    $content = Get-Content $propertiesFile -Raw
    
    # Cập nhật username và password
    $content = $content -replace "spring\.datasource\.username=.*", "spring.datasource.username=$username"
    $content = $content -replace "spring\.datasource\.password=.*", "spring.datasource.password=$passwordPlain"
    
    Set-Content -Path $propertiesFile -Value $content -NoNewline
    Write-Host "   ✅ Đã cập nhật application.properties" -ForegroundColor Green
    Write-Host "   ⚠️  Lưu ý: Password đã được lưu dạng plain text trong file!" -ForegroundColor Yellow
} else {
    Write-Host "   ⚠️  Không tìm thấy application.properties" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "✅ Thiết lập hoàn tất!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Bạn có thể chạy ứng dụng bằng:" -ForegroundColor Yellow
Write-Host "  mvn spring-boot:run" -ForegroundColor White
Write-Host ""

# Xóa password khỏi memory
$passwordPlain = $null
$env:PGPASSWORD = $null

