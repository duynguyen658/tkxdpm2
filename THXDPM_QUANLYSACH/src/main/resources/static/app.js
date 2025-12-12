const API_BASE_URL = '/api/books';

let currentBooks = [];
let isEditMode = false;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    loadBooks();
    loadStatistics();
    setupDateInput();
});

// Setup date input to today's date
function setupDateInput() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('ngayNhap').value = today;
}

// Load all books
async function loadBooks() {
    try {
        const response = await fetch(API_BASE_URL);
        const data = await response.json();
        
        if (data.success) {
            currentBooks = data.books || [];
            displayBooks(currentBooks);
        } else {
            showToast('Không thể tải danh sách sách', 'error');
        }
    } catch (error) {
        console.error('Error loading books:', error);
        showToast('Lỗi kết nối đến server', 'error');
        document.getElementById('booksTableBody').innerHTML = 
            '<tr><td colspan="9" class="empty">Không thể kết nối đến server</td></tr>';
    }
}

// Display books in table
function displayBooks(books) {
    const tbody = document.getElementById('booksTableBody');
    
    if (books.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="empty">Không có sách nào</td></tr>';
        return;
    }
    
    tbody.innerHTML = books.map(book => `
        <tr>
            <td><strong>${book.maSach}</strong></td>
            <td><span class="badge ${book.loaiSach === 'Sách giáo khoa' ? 'badge-primary' : 'badge-success'}">${book.loaiSach}</span></td>
            <td>${formatDate(book.ngayNhap)}</td>
            <td>${formatCurrency(book.donGia)}</td>
            <td>${book.soLuong}</td>
            <td>${book.nhaXuatBan}</td>
            <td>${book.tinhTrang ? `<span class="badge ${book.tinhTrang === 'mới' ? 'badge-success' : 'badge-warning'}">${book.tinhTrang}</span>` : 
                (book.thue ? `Thuế: ${formatCurrency(book.thue)}` : '-')}</td>
            <td><strong>${formatCurrency(book.thanhTien)}</strong></td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-success btn-sm" onclick="editBook('${book.maSach}')">
                        <i class="fas fa-edit"></i> Sửa
                    </button>
                    <button class="btn btn-danger btn-sm" onclick="deleteBook('${book.maSach}')">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
    
    // Update total books count
    document.getElementById('totalBooks').textContent = books.length;
}

// Load statistics
async function loadStatistics() {
    try {
        // Load total by type
        const totalResponse = await fetch(`${API_BASE_URL}/statistics/total-by-type`);
        const totalData = await totalResponse.json();
        
        if (totalData.success) {
            document.getElementById('totalGiaoKhoa').textContent = formatCurrency(totalData.tongThanhTienSachGiaoKhoa);
            document.getElementById('totalThamKhao').textContent = formatCurrency(totalData.tongThanhTienSachThamKhao);
        }
        
        // Load average price
        const avgResponse = await fetch(`${API_BASE_URL}/statistics/average-price`);
        const avgData = await avgResponse.json();
        
        if (avgData.success) {
            document.getElementById('averagePrice').textContent = formatCurrency(avgData.trungBinhCongDonGia);
        } else {
            // Khi không có sách tham khảo, hiển thị thông báo
            document.getElementById('averagePrice').textContent = 'N/A';
        }
    } catch (error) {
        console.error('Error loading statistics:', error);
    }
}

// Search books
async function searchBooks(keyword) {
    if (!keyword.trim()) {
        loadBooks();
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/search?keyword=${encodeURIComponent(keyword)}`);
        const data = await response.json();
        
        if (data.success) {
            displayBooks(data.books || []);
        } else {
            showToast('Không tìm thấy sách', 'info');
            displayBooks([]);
        }
    } catch (error) {
        console.error('Error searching books:', error);
        showToast('Lỗi khi tìm kiếm', 'error');
    }
}

// Handle search input
function handleSearch(event) {
    if (event.key === 'Enter' || event.type === 'input') {
        const keyword = event.target.value;
        searchBooks(keyword);
    }
}

// Clear search
function clearSearch() {
    document.getElementById('searchInput').value = '';
    loadBooks();
}

// Filter by publisher
async function handlePublisherFilter(event) {
    if (event.key === 'Enter' || event.type === 'input') {
        const publisher = event.target.value.trim();
        if (!publisher) {
            loadBooks();
            return;
        }
        
        try {
            const response = await fetch(`${API_BASE_URL}/publisher/${encodeURIComponent(publisher)}`);
            const data = await response.json();
            
            if (data.success) {
                displayBooks(data.books || []);
            } else {
                showToast('Không tìm thấy sách của nhà xuất bản này', 'info');
                displayBooks([]);
            }
        } catch (error) {
            console.error('Error filtering by publisher:', error);
            showToast('Lỗi khi lọc theo nhà xuất bản', 'error');
        }
    }
}

// Open add book modal
function openAddBookModal() {
    isEditMode = false;
    document.getElementById('modalTitle').textContent = 'Thêm Sách Mới';
    document.getElementById('bookForm').reset();
    document.getElementById('bookId').value = '';
    
    // Enable all fields
    document.getElementById('bookType').disabled = false;
    document.getElementById('maSach').disabled = false;
    
    setupDateInput();
    handleBookTypeChange();
    document.getElementById('bookModal').classList.add('show');
}

// Close book modal
function closeBookModal() {
    document.getElementById('bookModal').classList.remove('show');
    isEditMode = false;
    document.getElementById('bookForm').reset();
}

// Handle book type change
function handleBookTypeChange() {
    const bookType = document.getElementById('bookType').value;
    const tinhTrangGroup = document.getElementById('tinhTrangGroup');
    const thueGroup = document.getElementById('thueGroup');
    const tinhTrang = document.getElementById('tinhTrang');
    const thue = document.getElementById('thue');
    
    if (bookType === 'SACH_GIAO_KHOA') {
        tinhTrangGroup.style.display = 'block';
        thueGroup.style.display = 'none';
        tinhTrang.required = true;
        thue.required = false;
        thue.value = '';
    } else if (bookType === 'SACH_THAM_KHAO') {
        tinhTrangGroup.style.display = 'none';
        thueGroup.style.display = 'block';
        tinhTrang.required = false;
        thue.required = true;
        tinhTrang.value = '';
    } else {
        tinhTrangGroup.style.display = 'none';
        thueGroup.style.display = 'none';
        tinhTrang.required = false;
        thue.required = false;
    }
}

// Edit book
async function editBook(bookId) {
    const book = currentBooks.find(b => b.maSach === bookId);
    if (!book) {
        showToast('Không tìm thấy sách', 'error');
        return;
    }
    
    isEditMode = true;
    document.getElementById('modalTitle').textContent = 'Cập Nhật Sách';
    document.getElementById('bookId').value = book.maSach;
    
    const bookTypeSelect = document.getElementById('bookType');
    bookTypeSelect.value = book.loaiSach === 'Sách giáo khoa' ? 'SACH_GIAO_KHOA' : 'SACH_THAM_KHAO';
    bookTypeSelect.disabled = true; // Disable book type in edit mode
    
    document.getElementById('maSach').value = book.maSach;
    document.getElementById('maSach').disabled = true; // Disable maSach in edit mode
    
    // Convert date from dd/MM/yyyy to yyyy-MM-dd
    const dateParts = book.ngayNhap.split('/');
    if (dateParts.length === 3) {
        document.getElementById('ngayNhap').value = `${dateParts[2]}-${dateParts[1]}-${dateParts[0]}`;
    }
    
    document.getElementById('donGia').value = book.donGia;
    document.getElementById('soLuong').value = book.soLuong;
    document.getElementById('nhaXuatBan').value = book.nhaXuatBan;
    
    if (book.tinhTrang) {
        document.getElementById('tinhTrang').value = book.tinhTrang;
    }
    if (book.thue) {
        document.getElementById('thue').value = book.thue;
    }
    
    handleBookTypeChange();
    document.getElementById('bookModal').classList.add('show');
}

// Delete book
async function deleteBook(bookId) {
    if (!confirm(`Bạn có chắc chắn muốn xóa sách ${bookId}?`)) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/${bookId}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (data.success) {
            showToast(data.message || 'Đã xóa sách thành công', 'success');
            loadBooks();
            loadStatistics();
        } else {
            showToast(data.message || 'Không thể xóa sách', 'error');
        }
    } catch (error) {
        console.error('Error deleting book:', error);
        showToast('Lỗi khi xóa sách', 'error');
    }
}

// Handle form submit
async function handleSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const bookType = formData.get('bookType');
    const bookId = formData.get('bookId');
    
    // Convert date from yyyy-MM-dd to dd/MM/yyyy
    const ngayNhap = formData.get('ngayNhap');
    const dateParts = ngayNhap.split('-');
    const formattedDate = `${dateParts[2]}/${dateParts[1]}/${dateParts[0]}`;
    
    const requestBody = {
        ngayNhap: formattedDate,
        donGia: parseFloat(formData.get('donGia')),
        soLuong: parseInt(formData.get('soLuong')),
        nhaXuatBan: formData.get('nhaXuatBan')
    };
    
    // Add optional fields
    const maSach = formData.get('maSach');
    if (maSach && maSach.trim()) {
        requestBody.maSach = maSach.trim();
    }
    
    // Add type-specific fields
    if (isEditMode) {
        // For update, include type-specific fields based on existing book
        const existingBook = currentBooks.find(b => b.maSach === bookId);
        if (existingBook) {
            if (existingBook.loaiSach === 'Sách giáo khoa') {
                requestBody.tinhTrang = formData.get('tinhTrang');
            } else {
                requestBody.thue = parseFloat(formData.get('thue')) || 0;
            }
        }
    } else {
        // For add, include bookType and type-specific fields
        requestBody.bookType = bookType;
        if (bookType === 'SACH_GIAO_KHOA') {
            requestBody.tinhTrang = formData.get('tinhTrang');
        } else {
            requestBody.thue = parseFloat(formData.get('thue')) || 0;
        }
    }
    
    try {
        const url = isEditMode 
            ? `${API_BASE_URL}/${bookId}`
            : API_BASE_URL;
        
        const method = isEditMode ? 'PUT' : 'POST';
        
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });
        
        const data = await response.json();
        
        if (data.success) {
            showToast(data.message || (isEditMode ? 'Đã cập nhật sách thành công' : 'Đã thêm sách thành công'), 'success');
            closeBookModal();
            loadBooks();
            loadStatistics();
        } else {
            showToast(data.message || 'Có lỗi xảy ra', 'error');
        }
    } catch (error) {
        console.error('Error submitting form:', error);
        showToast('Lỗi kết nối đến server', 'error');
    }
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Format date
function formatDate(dateString) {
    // If already in dd/MM/yyyy format, return as is
    if (dateString.includes('/')) {
        return dateString;
    }
    // Otherwise, format from yyyy-MM-dd
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}

// Show toast message
function showToast(message, type = 'info') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('bookModal');
    if (event.target === modal) {
        closeBookModal();
    }
}

