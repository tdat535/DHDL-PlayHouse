function highlightSelected(selectedInput) {
    // Xóa lớp 'selected' khỏi tất cả các tùy chọn
    document.querySelectorAll('.shipping-option').forEach(option => {
        option.classList.remove('selected');
    });

    // Thêm lớp 'selected' vào mục cha của tùy chọn được chọn
    selectedInput.parentElement.classList.add('selected');

    // Đảm bảo input được đánh dấu 'checked'
    selectedInput.checked = true;
}

// Đặt trạng thái ban đầu khi tải trang
document.addEventListener("DOMContentLoaded", function () {
    console.log("Trang đã tải");
    const selectedInput = document.querySelector('input[name="shippingOption"]:checked');
    if (selectedInput) {
        highlightSelected(selectedInput);
    }
});

// Thêm sự kiện để xử lý khi nhấp vào label
document.querySelectorAll('.form-check-label').forEach(label => {
    label.addEventListener('click', function () {
        const input = document.querySelector(`#${label.htmlFor}`);
        if (input) {
            // Gán checked cho input khi nhấp vào label
            input.checked = true;
            highlightSelected(input);
            document.getElementById('shippingForm').submit(); // Gửi form
        }
    });
});

// Thêm sự kiện khi nhấp vào chính input
document.querySelectorAll('.form-check-input').forEach(input => {
    input.addEventListener('change', function () {
        if (input.checked) {
            highlightSelected(input);
            document.getElementById('shippingForm').submit(); // Gửi form
        }
    });
});
