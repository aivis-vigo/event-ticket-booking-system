// API Base URL
const API_BASE = '/api';

// Initialize the app
document.addEventListener('DOMContentLoaded', function () {
    loadEvents();
});

// Tab switching
function showTab(tabName) {
    // Hide all tabs
    const tabs = document.querySelectorAll('.tab-content');
    tabs.forEach(tab => tab.style.display = 'none');

    // Remove active class from all nav buttons
    const navBtns = document.querySelectorAll('.nav-btn');
    navBtns.forEach(btn => btn.classList.remove('active'));

    // Show selected tab and mark nav button as active
    document.getElementById(tabName).style.display = 'block';
    event.target.classList.add('active');

    // Load data for selected tab
    if (tabName === 'events') {
        loadEvents();
    } else if (tabName === 'tickets') {
        loadTickets();
    } else if (tabName === 'bookings') {
        loadBookings();
    }
}

// Load and display events
function loadEvents() {
    const container = document.getElementById('events-container');
    container.innerHTML = '<div class="loader">Loading events...</div>';

    fetch(`${API_BASE}/events`)
        .then(response => response.json())
        .then(events => {
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
        })
        .catch(error => {
            console.error('Error loading events:', error);
            container.innerHTML = '<div class="empty-state"><h3>Error loading events</h3></div>';
        });
}

function createEvent() {
    const eventData = {
        name: document.getElementById('eventName').value,
        description: document.getElementById('eventDescription').value,
        eventDate: document.getElementById('eventDate').value,
        location: document.getElementById('eventLocation').value,
        totalTickets: parseInt(document.getElementById('totalTickets').value),
        ticketPrice: parseFloat(document.getElementById('ticketPrice').value)
    };

    fetch(`${API_BASE}/events`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(eventData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create event');
            }
            return response.json();
        })
        .then(data => {
            alert('Event created successfully!');
            loadEvents();
        })
        .catch(error => {
            console.error(error);
            alert(error.message);
        });
}

function deleteEvent(id) {
    if (!confirm('Delete this event?')) {
        return;
    }

    fetch(`${API_BASE}/events/${id}`, {
        method: 'DELETE'
    })
        .then(response => {
            if (response.ok) {
                alert('Event deleted');
                loadEvents();
            } else {
                alert('Failed to delete');
            }
        });
}

function editEvent(id) {
    const updatedName = prompt('Enter new event name');

    if (!updatedName) return;

    const updatedData = {
        name: updatedName,
        description: "Updated Description",
        eventDate: "2026-12-31T18:00:00",
        location: "Updated Location",
        totalTickets: 100,
        ticketPrice: 50
    };

    fetch(`${API_BASE}/events/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Update failed');
            }
            return response.json();
        })
        .then(data => {
            alert('Event updated!');
            loadEvents();
        })
        .catch(error => {
            alert(error.message);
        });
}

function searchEvents() {
    const keyword = document.getElementById('keyword').value;

    fetch(`${API_BASE}/events?keyword=${keyword}`)
        .then(response => response.json())
        .then(displayEvents)
        .catch(error => console.error(error));
}

function loadUpcomingEvents() {
    fetch(`${API_BASE}/events/upcoming`)
        .then(response => response.json())
        .then(displayEvents)
        .catch(error => console.error(error));
}

function searchByLocation() {
    const location = document.getElementById('locationSearch').value;

    fetch(`${API_BASE}/events/search?location=${location}`)
        .then(response => response.json())
        .then(displayEvents)
        .catch(error => console.error(error));
}

function searchByPrice() {
    const minPrice = document.getElementById('minPrice').value;
    const maxPrice = document.getElementById('maxPrice').value;

    fetch(`${API_BASE}/events/search?minPrice=${minPrice}&maxPrice=${maxPrice}`)
        .then(response => response.json())
        .then(displayEvents)
        .catch(error => console.error(error));
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

// Load and display tickets
function loadTickets() {
    const container = document.getElementById('tickets-container');
    container.innerHTML = '<div class="loader">Loading tickets...</div>';

    fetch(`${API_BASE}/tickets`)
        .then(response => response.json())
        .then(tickets => {
            if (tickets.length === 0) {
                container.innerHTML = '<div class="empty-state"><h3>No tickets found</h3></div>';
                return;
            }

            container.innerHTML = tickets.map(ticket => `
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">${escapeHtml(ticket.ticketNumber)}</div>
                        <span class="card-badge ${ticket.status.toLowerCase()}">${ticket.status}</span>
                    </div>
                    <div class="card-meta">
                        <div class="meta-item">
                            <span class="meta-label">Event:</span>
                            <span class="meta-value">${escapeHtml(ticket.event.name)}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-label">Price:</span>
                            <span class="meta-value">$${ticket.price.toFixed(2)}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-label">Status:</span>
                            <span class="meta-value">${ticket.status}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-label">Ticket ID:</span>
                            <span class="meta-value">#${ticket.id}</span>
                        </div>
                    </div>
                </div>
            `).join('');
        })
        .catch(error => {
            console.error('Error loading tickets:', error);
            container.innerHTML = '<div class="empty-state"><h3>Error loading tickets</h3></div>';
        });
}

// Load and display bookings
function loadBookings() {
    const container = document.getElementById('bookings-container');
    container.innerHTML = '<div class="loader">Loading bookings...</div>';

    fetch(`${API_BASE}/bookings`)
        .then(response => response.json())
        .then(bookings => {
            if (bookings.length === 0) {
                container.innerHTML = '<div class="empty-state"><h3>No bookings found</h3></div>';
                return;
            }

            container.innerHTML = bookings.map(booking => `
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">${escapeHtml(booking.customerName)}</div>
                        <span class="card-badge confirmed">${booking.status}</span>
                    </div>
                    <div class="card-meta">
                        <div class="meta-item">
                            <span class="meta-label">Event:</span>
                            <span class="meta-value">${escapeHtml(booking.event.name)}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-label">Ticket:</span>
                            <span class="meta-value">${escapeHtml(booking.ticket.ticketNumber)}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-label">Email:</span>
                            <span class="meta-value">${escapeHtml(booking.customerEmail)}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-label">Booked:</span>
                            <span class="meta-value">${formatDate(booking.bookingDate)}</span>
                        </div>
                        <div class="meta-item">
                            <span class="meta-label">Booking ID:</span>
                            <span class="meta-value">#${booking.id}</span>
                        </div>
                    </div>
                </div>
            `).join('');
        })
        .catch(error => {
            console.error('Error loading bookings:', error);
            container.innerHTML = '<div class="empty-state"><h3>Error loading bookings</h3></div>';
        });
}

// Helper function to format dates
function formatDate(dateString) {
    const options = {year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'};
    return new Date(dateString).toLocaleDateString('en-US', options);
}

// Helper function to escape HTML
function escapeHtml(text) {
    if (!text) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

