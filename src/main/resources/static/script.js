// Mock Database using localStorage
let eventsDB = JSON.parse(localStorage.getItem('eventsDB')) || [];
let usersDB = JSON.parse(localStorage.getItem('usersDB')) || [];
let currentUser = JSON.parse(localStorage.getItem('currentUser')) || null;
let nextEventId = Math.max(0, ...eventsDB.map(e => e.id || 0)) + 1;

const DEFAULT_USER = {
    id: 1,
    fullName: 'Default User',
    email: 'user@example.com',
    password: 'user123',
    role: 'USER',
    createdAt: '2026-01-01T00:00:00.000Z'
};

// Initialize the app
document.addEventListener('DOMContentLoaded', function () {
    seedDefaultUser();

    if (eventsDB.length === 0) {
        eventsDB = [
            {
                id: 1,
                name: "Summer Music Festival",
                description: "A vibrant music festival with top artists",
                eventDate: "2026-07-15T18:00:00",
                location: "Central Park, NY",
                totalTickets: 500,
                ticketPrice: 89.99
            },
            {
                id: 2,
                name: "Tech Conference 2026",
                description: "Future of technology and innovation",
                eventDate: "2026-08-20T09:00:00",
                location: "Convention Center, SF",
                totalTickets: 1200,
                ticketPrice: 299.00
            }
        ];
        saveEvents();
    }

    if (currentUser) {
        showDashboard();
    } else {
        showRegistration();
    }
});

// Save events to localStorage
function saveEvents() {
    localStorage.setItem('eventsDB', JSON.stringify(eventsDB));
}

function saveUsers() {
    localStorage.setItem('usersDB', JSON.stringify(usersDB));
}

function seedDefaultUser() {
    const exists = usersDB.some(user => user.email && user.email.toLowerCase() === DEFAULT_USER.email);
    if (!exists) {
        usersDB.unshift({ ...DEFAULT_USER });
        saveUsers();
    }
}

function saveCurrentUser(user) {
    currentUser = user;
    localStorage.setItem('currentUser', JSON.stringify(user));
}

function clearCurrentUser() {
    currentUser = null;
    localStorage.removeItem('currentUser');
}

function setSectionVisibility(sectionId) {
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => {
        tab.style.display = tab.id === sectionId ? 'block' : 'none';
    });
}

function showAuthMessage(targetId, text, className) {
    const message = document.getElementById(targetId);
    if (!message) return;
    message.textContent = text;
    message.className = `form-message ${className}`.trim();
}

function requireAuth(redirectMessage = 'Please log in to access the dashboard.') {
    if (currentUser) {
        return true;
    }

    showLogin();
    showAuthMessage('login-message', redirectMessage, 'error');
    return false;
}

function showRegistration() {
    setSectionVisibility('register');
    const message = document.getElementById('registration-message');
    if (message && !message.textContent) {
        message.textContent = 'Create your account to unlock the dashboard.';
        message.className = 'form-message info';
    }
    const loginMessage = document.getElementById('login-message');
    if (loginMessage) {
        loginMessage.textContent = '';
        loginMessage.className = 'form-message';
    }
    const dashboardButton = document.getElementById('dashboard-button');
    if (dashboardButton) {
        dashboardButton.style.display = 'none';
    }
}

function showLogin() {
    setSectionVisibility('login');
    const message = document.getElementById('login-message');
    if (message && !message.textContent) {
        message.textContent = 'Sign in with your registered account.';
        message.className = 'form-message info';
    }
    const registrationMessage = document.getElementById('registration-message');
    if (registrationMessage) {
        registrationMessage.textContent = '';
        registrationMessage.className = 'form-message';
    }
    const dashboardButton = document.getElementById('dashboard-button');
    if (dashboardButton) {
        dashboardButton.style.display = 'none';
    }
}

function showDashboard() {
    if (!requireAuth()) {
        return;
    }

    setSectionVisibility('events');
    loadEvents();
    loadTickets();
    loadBookings();
}

function handleRegistration(event) {
    event.preventDefault();

    const fullName = document.getElementById('fullName').value.trim();
    const email = document.getElementById('email').value.trim().toLowerCase();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const role = document.getElementById('role').value;
    const message = document.getElementById('registration-message');

    if (!fullName || !email || !password || !confirmPassword || !role) {
        message.textContent = 'Please fill in every field.';
        message.className = 'form-message error';
        return;
    }

    if (!email.includes('@')) {
        message.textContent = 'Please enter a valid email address.';
        message.className = 'form-message error';
        return;
    }

    if (password.length < 6) {
        message.textContent = 'Password must be at least 6 characters long.';
        message.className = 'form-message error';
        return;
    }

    if (password !== confirmPassword) {
        message.textContent = 'Passwords do not match.';
        message.className = 'form-message error';
        return;
    }

    const emailExists = usersDB.some(user => user.email === email);
    if (emailExists) {
        message.textContent = 'An account with this email already exists.';
        message.className = 'form-message error';
        return;
    }

    const newUser = {
        id: Date.now(),
        fullName,
        email,
        password,
        role,
        createdAt: new Date().toISOString()
    };

    usersDB.unshift(newUser);
    saveUsers();
    saveCurrentUser({
        id: newUser.id,
        fullName: newUser.fullName,
        email: newUser.email,
        role: newUser.role
    });

    message.textContent = `Welcome, ${fullName}! Your account has been created.`;
    message.className = 'form-message success';
    document.getElementById('register-form').reset();

    const dashboardButton = document.getElementById('dashboard-button');
    if (dashboardButton) {
        dashboardButton.style.display = 'inline-flex';
    }
}

function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('loginEmail').value.trim().toLowerCase();
    const password = document.getElementById('loginPassword').value;
    const message = document.getElementById('login-message');

    if (!email || !password) {
        message.textContent = 'Please enter both your email and password.';
        message.className = 'form-message error';
        return;
    }

    const user = usersDB.find(item => item.email === email && item.password === password);
    if (!user) {
        message.textContent = 'Invalid email or password.';
        message.className = 'form-message error';
        return;
    }

    saveCurrentUser({
        id: user.id,
        fullName: user.fullName,
        email: user.email,
        role: user.role
    });

    message.textContent = `Welcome back, ${user.fullName}!`;
    message.className = 'form-message success';
    document.getElementById('login-form').reset();
    showDashboard();
}

function logoutUser() {
    clearCurrentUser();
    showLogin();
}

// Load and display events
function loadEvents() {
    if (!requireAuth()) return;

    const container = document.getElementById('events-container');
    container.innerHTML = '<div class="loader">Loading events...</div>';

    setTimeout(() => {
        if (eventsDB.length === 0) {
            container.innerHTML = '<div class="empty-state"><h3>No events found</h3></div>';
            return;
        }

        container.innerHTML = eventsDB.map(event => `
            <div class="card">
                <div class="card-header">
                    <div class="card-title">${escapeHtml(event.name)}</div>
                </div>
                <p class="card-description">${escapeHtml(event.description)}</p>
                <div class="card-meta">
                    <div class="meta-item">
                        <span class="meta-label">📅 Date:</span>
                        <span class="meta-value">${formatDate(event.eventDate)}</span>
                    </div>
                    <div class="meta-item">
                        <span class="meta-label">📍 Location:</span>
                        <span class="meta-value">${escapeHtml(event.location)}</span>
                    </div>
                    <div class="meta-item">
                        <span class="meta-label">🎫 Tickets:</span>
                        <span class="meta-value">${event.totalTickets}</span>
                    </div>
                    <div class="price">$${event.ticketPrice.toFixed(2)}</div>
                </div>
                <div class="card-actions">
                    <button onclick="editEvent(${event.id})">Edit</button>
                    <button class="danger-btn" onclick="deleteEvent(${event.id})">Delete</button>
                </div>
            </div>
        `).join('');
    }, 300);
}

// ✅ Renamed from createEvent to avoid conflict
function createNewEvent() {
    if (!requireAuth()) return;

    const name = document.getElementById('eventName').value.trim();
    const description = document.getElementById('eventDescription').value.trim();
    const eventDate = document.getElementById('eventDate').value;
    const location = document.getElementById('eventLocation').value.trim();
    const totalTickets = parseInt(document.getElementById('totalTickets').value);
    const ticketPrice = parseFloat(document.getElementById('ticketPrice').value);

    if (!name || !description || !eventDate || !location || isNaN(totalTickets) || isNaN(ticketPrice)) {
        alert("Please fill in all fields correctly");
        return;
    }

    const newEvent = {
        id: nextEventId++,
        name,
        description,
        eventDate,
        location,
        totalTickets,
        ticketPrice
    };

    eventsDB.unshift(newEvent);
    saveEvents();

    alert('✅ Event created successfully!');

    // Clear form
    document.getElementById('eventName').value = '';
    document.getElementById('eventDescription').value = '';
    document.getElementById('eventDate').value = '';
    document.getElementById('eventLocation').value = '';
    document.getElementById('totalTickets').value = '';
    document.getElementById('ticketPrice').value = '';

    loadEvents();
}

function deleteEvent(id) {
    if (!requireAuth()) return;

    if (!confirm('Delete this event?')) return;
    eventsDB = eventsDB.filter(event => event.id !== id);
    saveEvents();
    alert('Event deleted');
    loadEvents();
}

function editEvent(id) {
    if (!requireAuth()) return;

    const event = eventsDB.find(e => e.id === id);
    if (!event) return;

    const newName = prompt('Enter new event name:', event.name);
    if (newName === null || newName.trim() === '') return;

    event.name = newName.trim();
    saveEvents();
    alert('Event updated!');
    loadEvents();
}

// Search & Filter functions
function searchEvents() {
    if (!requireAuth()) return;

    const keyword = document.getElementById('keyword').value.toLowerCase().trim();
    if (!keyword) return loadEvents();

    const filtered = eventsDB.filter(event =>
        event.name.toLowerCase().includes(keyword) ||
        event.description.toLowerCase().includes(keyword) ||
        event.location.toLowerCase().includes(keyword)
    );
    displayEvents(filtered);
}

function loadUpcomingEvents() {
    if (!requireAuth()) return;

    const now = new Date();
    const upcoming = eventsDB.filter(event => new Date(event.eventDate) > now);
    displayEvents(upcoming);
}

function searchByLocation() {
    if (!requireAuth()) return;

    const location = document.getElementById('locationSearch').value.toLowerCase().trim();
    if (!location) return loadEvents();

    const filtered = eventsDB.filter(event =>
        event.location.toLowerCase().includes(location)
    );
    displayEvents(filtered);
}

function searchByPrice() {
    if (!requireAuth()) return;

    const minPrice = parseFloat(document.getElementById('minPrice').value) || 0;
    const maxPrice = parseFloat(document.getElementById('maxPrice').value) || Infinity;

    const filtered = eventsDB.filter(event =>
        event.ticketPrice >= minPrice && event.ticketPrice <= maxPrice
    );
    displayEvents(filtered);
}

function displayEvents(events) {
    if (!requireAuth()) return;

    const container = document.getElementById('events-container');
    if (events.length === 0) {
        container.innerHTML = '<div class="empty-state"><h3>No events found</h3></div>';
        return;
    }

    container.innerHTML = events.map(event => `
        <div class="card">
            <div class="card-header">
                <div class="card-title">${escapeHtml(event.name)}</div>
            </div>
            <p class="card-description">${escapeHtml(event.description)}</p>
            <div class="card-meta">
                <div class="meta-item">
                    <span class="meta-label">📍 Location:</span>
                    <span class="meta-value">${escapeHtml(event.location)}</span>
                </div>
                <div class="price">$${event.ticketPrice.toFixed(2)}</div>
            </div>
            <div class="card-actions">
                <button onclick="editEvent(${event.id})">Edit</button>
                <button class="danger-btn" onclick="deleteEvent(${event.id})">Delete</button>
            </div>
        </div>
    `).join('');
}

// Placeholder for other tabs
function loadTickets() {
    if (!requireAuth()) return;

    document.getElementById('tickets-container').innerHTML =
        '<div class="empty-state"><h3>No tickets available yet</h3></div>';
}

function loadBookings() {
    if (!requireAuth()) return;

    document.getElementById('bookings-container').innerHTML =
        '<div class="empty-state"><h3>No bookings yet</h3></div>';
}

// Helper functions
function formatDate(dateString) {
    const options = {year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'};
    return new Date(dateString).toLocaleDateString('en-US', options);
}

function escapeHtml(text) {
    if (!text) return '';
    const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#039;' };
    return text.replace(/[&<>"']/g, m => map[m]);
}