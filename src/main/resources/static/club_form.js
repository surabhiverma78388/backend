document.getElementById('customRegistrationForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    // 1. Form ka data collect karein
    const formData = new FormData(e.target);
    const dataObject = Object.fromEntries(formData.entries());

    // 2. LocalStorage se regId nikalein (Jo processRegistration ne save ki thi)
    const regId = localStorage.getItem('currentRegId');

    if (!regId) {
        alert("Registration ID not found. Please try registering again.");
        window.location.href = 'clubdashboard.html';
        return;
    }

    try {
        // 3. Backend update API call karein
        const response = await window.InfoNest.authenticatedFetch('/student/update-form-data', {
            method: 'PUT',
            body: JSON.stringify({
                regId: regId,
                formData: JSON.stringify(dataObject) // Saara data JSON string bankar jayega
            })
        });

        if (response.ok) {
            alert("Registration Successful! Data saved in DB.");
            
            // Cleanup: regId delete kar dein ab iska kaam khatam
            localStorage.removeItem('currentRegId');
            
            // Student Dashboard par bhej dein
            window.location.href = 'student_db.html';
        } else {
            alert("Failed to save form data.");
        }
    } catch (err) {
        console.error("Form submission error:", err);
        alert("Error connecting to server.");
    }
});