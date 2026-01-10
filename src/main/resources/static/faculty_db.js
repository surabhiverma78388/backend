const auth = window.InfoNest.getAuthData();
const clubId = auth.clubId; // Faculty ki clubId stored hai

// FETCH AND FILL PLACEHOLDERS
async function fetchEventForUpdate() {
    const eventName = document.getElementById('searchEventName').value;
    const response = await window.InfoNest.authenticatedFetch(`/faculty/event-details/${clubId}/${eventName}`);
    
    if (response.ok) {
        const event = await response.json();
        document.getElementById('editEventId').value = event.eventId;
        document.getElementById('editEventName').value = event.eventName;
        document.getElementById('editDescription').value = event.description;
        document.getElementById('editRegLink').value = event.regLink;
        
        document.getElementById('updateEventForm').style.display = 'block';
    } else {
        alert("Event not found in your club!");
    }
}

// VIEW SUBMISSIONS
async function loadSubmissions() {
    const response = await window.InfoNest.authenticatedFetch(`/faculty/submissions/${clubId}`);
    const data = await response.json();
    const tbody = document.getElementById('submissionBody');
    tbody.innerHTML = '';

    data.forEach(reg => {
        tbody.innerHTML += `
            <tr>
                <td>${reg.userId}</td>
                <td><pre>${reg.formData}</pre></td>
                <td>
                    <button onclick="updateRegStatus(${reg.regId}, 'APPROVED')">Approve</button>
                    <button onclick="updateRegStatus(${reg.regId}, 'REJECTED')">Reject</button>
                </td>
            </tr>
        `;
    });
    document.getElementById('submissionsDiv').style.display = 'block';
}

async function updateRegStatus(regId, status) {
    await window.InfoNest.authenticatedFetch(`/faculty/update-status/${regId}?status=${status}`, {
        method: 'PUT'
    });
    alert("Application " + status);
    loadSubmissions(); // Refresh list
}