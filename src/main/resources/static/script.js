// Mock Database using localStorage
let eventsDB = JSON.parse(localStorage.getItem('eventsDB')) || [];
let nextEventId = Math.max(0, ...eventsDB.map(e => e.id || 0)) + 1;

// Initialize the app
document.addEventListener('DOMContentLoaded', function () {
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
    loadEvents();
});

// Save events to localStorage
function saveEvents() {
    localStorage.setItem('eventsDB', JSON.stringify(eventsDB));
}

// Tab switching
function showTab(tabName) {
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.style.display = 'none');

    const navBtns = document.querySelectorAll('.nav-btn');
    navBtns.forEach(btn => btn.classList.remove('active'));

    document.getElementById(tabName).style.display = 'block';

    const activeBtn = Array.from(navBtns).find(btn =>
        btn.getAttribute('onclick') && btn.getAttribute('onclick').includes(tabName)
    );
    if (activeBtn) activeBtn.classList.add('active');

    if (tabName === 'events') loadEvents();
    else if (tabName === 'tickets') loadTickets();
    else if (tabName === 'bookings') loadBookings();
}

// Load and display events
function loadEvents() {
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
    if (!confirm('Delete this event?')) return;
    eventsDB = eventsDB.filter(event => event.id !== id);
    saveEvents();
    alert('Event deleted');
    loadEvents();
}

function editEvent(id) {
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
    const now = new Date();
    const upcoming = eventsDB.filter(event => new Date(event.eventDate) > now);
    displayEvents(upcoming);
}

function searchByLocation() {
    const location = document.getElementById('locationSearch').value.toLowerCase().trim();
    if (!location) return loadEvents();

    const filtered = eventsDB.filter(event =>
        event.location.toLowerCase().includes(location)
    );
    displayEvents(filtered);
}

function searchByPrice() {
    const minPrice = parseFloat(document.getElementById('minPrice').value) || 0;
    const maxPrice = parseFloat(document.getElementById('maxPrice').value) || Infinity;

    const filtered = eventsDB.filter(event =>
        event.ticketPrice >= minPrice && event.ticketPrice <= maxPrice
    );
    displayEvents(filtered);
}

function displayEvents(events) {
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
    document.getElementById('tickets-container').innerHTML =
        '<div class="empty-state"><h3>No tickets available yet</h3></div>';
}

function loadBookings() {
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