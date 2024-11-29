document.querySelectorAll('.rating-container i').forEach((star, index, stars) => {
    star.addEventListener('click', () => {
        // Remove "active" from all stars
        stars.forEach(s => s.classList.remove('active'));

        // Add "active" to clicked star and all previous stars
        for (let i = 0; i <= index; i++) {
            stars[i].classList.add('active');
        }

        // Set the hidden input field to the selected rating value
        document.getElementById('rating').value = index + 1; // Star index is 0-based, so add 1
    });
});

// Optional: Disable form submission if no rating is selected
const form = document.querySelector('form');
form.addEventListener('submit', (e) => {
    if (document.getElementById('rating').value === '') {
        e.preventDefault(); // Prevent form submission if rating is not selected
        alert('Please select a rating!');
    }
});


document.getElementById('addToCartButton').addEventListener('click', function(e) {
    e.preventDefault(); // Ngừng hành động mặc định của link
    var quantity = document.getElementById('quantity').value; // Lấy giá trị quantity từ input
    var productId = document.querySelector('input[name="productId"]').value; // Lấy productId
    window.location.href = "/cart/addToCart?productId=" + productId + "&quantity=" + quantity; // Chuyển hướng với tham số
});