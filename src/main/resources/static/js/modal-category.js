document.addEventListener('DOMContentLoaded', function () {
    console.log("Script loaded");  // This will confirm that the script is loading.

    const editButtons = document.querySelectorAll('.btn-warning');
    editButtons.forEach(button => {
        console.log("Button clicked");  // This will confirm if the event listener is attached correctly
        button.addEventListener('click', function () {
            const modalId = this.getAttribute('data-bs-target');
            const modal = document.querySelector(modalId);

            // Update modal fields with data-attributes
            modal.querySelector('#categoryName').value = this.getAttribute('data-category-name');
        });
    });
});