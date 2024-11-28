// Get elements
const paymentMethodSelect = document.getElementById('payment_method');
const submitBtn = document.getElementById('submitBtn');
const paymentModal = new bootstrap.Modal(document.getElementById('paymentModal'));
const finalSubmitBtn = document.getElementById('finalSubmitBtn');

// Function to toggle button and modal based on payment method
paymentMethodSelect.addEventListener('change', function() {
    if (paymentMethodSelect.value === 'Mobile Banking') {
        submitBtn.textContent = 'Scan QR';  // Change button text
        submitBtn.addEventListener('click', function(event) {
            event.preventDefault();  // Prevent form submission
            paymentModal.show();      // Show modal
        });
    } else {
        submitBtn.textContent = 'Submit Order';  // Reset button text
        submitBtn.addEventListener('click', function() {
            // Allow normal form submission for cash payment
        });
    }
});