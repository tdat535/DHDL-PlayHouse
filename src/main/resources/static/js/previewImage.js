<script>
    document.addEventListener('DOMContentLoaded', function() {
    function previewImage(event, id) {
        const input = event.target;
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                const previewImage = document.getElementById('imagePreview_' + id);
                if (previewImage) {
                    previewImage.src = e.target.result;
                }
            };
            reader.readAsDataURL(input.files[0]);
        }
    }
});
</script>
