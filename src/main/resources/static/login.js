// login.js

document.getElementById('loginForm').addEventListener('submit', async (e) => {
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

            // 1. Data Save karein (Token, Role, ID)
            localStorage.setItem('token', data.token);
            localStorage.setItem('role', data.role);
            localStorage.setItem('userId', data.userId);
            localStorage.setItem('firstName', data.firstName);
            if (data.clubId) {
        localStorage.setItem('clubId', data.clubId);
    }

            // 2. INTENT CHECK: Kya user register karne aaya tha?
            if (data.role === 'STUDENT') {
                // handleSavedIntent check karega ki koi event register hona baaki hai ya nahi
                const redirected = await window.InfoNest.handleSavedIntent();
                
                // Agar koi saved action nahi tha, toh normal dashboard par bhej do
                if (!redirected) {
                    window.InfoNest.redirectToDashboard(data.role);
                }
            } else {
                // Faculty ya Admin ke liye normal redirect
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