document.addEventListener('DOMContentLoaded', function () {
    const editButtons = document.querySelectorAll('.btn-warning');
    editButtons.forEach(button => {
        button.addEventListener('click', function () {
            const modalId = this.getAttribute('data-bs-target');
            const modal = document.querySelector(modalId);

            modal.querySelector('#productId').value = this.getAttribute('data-id');
            modal.querySelector('#productName').value = this.getAttribute('data-name');
            modal.querySelector('#productPrice').value = this.getAttribute('data-price');
            modal.querySelector('#productQuantity').value = this.getAttribute('data-quantity');
            modal.querySelector('#productImage').value = this.getAttribute('data-image');
            modal.querySelector('#productDescription').value = this.getAttribute('data-description');

        });
    });
});

