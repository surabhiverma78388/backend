// login.js - single, canonical login handler

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/api/v1/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            if (response.ok) {
                const data = await response.json();

                // Save auth data
                localStorage.setItem('token', data.token);
                localStorage.setItem('role', data.role);
                localStorage.setItem('userId', data.userId);
                localStorage.setItem('firstName', data.firstName || '');
                if (data.clubId) {
                    localStorage.setItem('clubId', data.clubId);
                }

                // If student, try to handle any saved intent (registration)
                if (data.role === 'STUDENT') {
                    const redirected = await window.InfoNest.handleSavedIntent();
                    if (!redirected) {
                        window.InfoNest.redirectToDashboard(data.role);
                    }
                } else {
                    // Faculty/Admin redirect
                    window.InfoNest.redirectToDashboard(data.role);
                }
            } else {
                const error = await response.text();
                alert("Login Failed: " + error);
            }
        } catch (err) {
            console.error("Login Error:", err);
            alert("Something went wrong. Please try again.");
        }
    });
});