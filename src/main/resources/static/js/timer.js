document.addEventListener("DOMContentLoaded", function () {
    const sendEmailBtn = document.getElementById('sendEmail');
    const resendTimer = document.getElementById('resendTimer');
    let countdownInterval;

    sendEmailBtn.addEventListener('click', function () {
        const email = document.getElementById('email').value;

        if (!email) {
            alert('Please enter your email!');
            return;
        }

        console.log('Checking if email exists:', email);

        // Disable the icon and send request to check email existence
        sendEmailBtn.style.pointerEvents = 'none';
        sendEmailBtn.style.opacity = '0.5'; // Add visual feedback

        // Send AJAX request to check if the email exists
        fetch('/authentication/checkEmailExistence', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email }),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to check email existence.');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    // If email exists, send the verification code and start the countdown
                    sendVerificationCode(email);
                    startCountdown(60); // Start countdown after successful verification code sending
                } else {
                    // If email doesn't exist, show error message and reset button
                    alert('The email does not exist. Please check and try again.');
                    resetButton();
                }
            })
            .catch(error => {
                console.error('Error details:', error);

                // Show a specific error message if the issue is with the email existence check
                alert('An error occurred while checking the email. Please try again later.');
                resetButton(); // Reset the button in case of error
            });
    });

    function sendVerificationCode(email) {
        fetch('/authentication/sendVerificationCode', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email: email }),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to send verification code.');
                }
                return response.json();
            })
            .then(data => {
                console.log('Verification code sent:', data);
                if (data.success) {
                    alert('Verification code sent to your email!');
                } else {
                    alert(data.message || 'Failed to send email. Please try again.');
                }
            })
            .catch(error => {
                console.error('Error details:', error);

                // Show a specific error message if the issue is with sending the verification code
                alert('An error occurred while sending the verification code. Please try again later.');
            });
    }

    function startCountdown(duration) {
        let timeLeft = duration;

        // Display the timer message
        resendTimer.style.display = 'block'; // Hiển thị timer
        resendTimer.textContent = `Haven't received the email? Try again in ${timeLeft}s`;

        countdownInterval = setInterval(() => {
            timeLeft--;
            if (timeLeft <= 0) {
                clearInterval(countdownInterval);
                resetButton();
            } else {
                resendTimer.textContent = `Haven't received the email? Try again in ${timeLeft}s`;
            }
        }, 1000);
    }

    function resetButton() {
        // Reset the icon and hide the timer
        sendEmailBtn.style.pointerEvents = 'auto';
        sendEmailBtn.style.opacity = '1';
        resendTimer.style.display = 'none';  // Hide the timer
    }
});
