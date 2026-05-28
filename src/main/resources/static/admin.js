document.addEventListener('DOMContentLoaded', () => {
    loadAdminStats();
    loadAdminEvents();
    loadAdminTickets();
    loadAdminBookings();
});

async function fetchJson(url) {
    const response = await fetch(url, {
        headers: {
            'Authorization': 'Basic ' + btoa('admin:admin123')
        }
    });

    if (!response.ok) {
        throw new Error(`Request failed: ${url}`);
    }

    return response.json();
}

async function loadAdminStats() {
    const container = document.getElementById('admin-stats');

    try {
        const stats = await fetchJson('/api/admin/stats');

        container.innerHTML = `
            <div class="card">
                <div class="card-title">Total Events</div>
                <div class="price">${stats.totalEvents}</div>
            </div>

            <div class="card">
                <div class="card-title">Total Tickets</div>
                <div class="price">${stats.totalTickets}</div>
            </div>

            <div class="card">
                <div class="card-title">Available Tickets</div>
                <div class="price">${stats.availableTickets}</div>
            </div>

            <div class="card">
                <div class="card-title">Booked Tickets</div>
                <div class="price">${stats.bookedTickets}</div>
            </div>

            <div class="card">
                <div class="card-title">Total Bookings</div>
                <div class="price">${stats.totalBookings}</div>
            </div>
        `;
    } catch (error) {
        container.innerHTML = '<div class="empty-state"><h3>Unable to load admin statistics</h3></div>';
    }
}

async function loadAdminEvents() {
    const container = document.getElementById('admin-events');

    try {
        const events = await fetchJson('/api/events');

        container.innerHTML = events.map(event => `
            <div class="card">
                <div class="card-title">${event.name}</div>

                <p>${event.description}</p>

                <p>
                    <b>Date:</b>
                    ${formatDate(event.eventDate)}
                </p>

                <p>
                    <b>Location:</b>
                    ${event.location}
                </p>

                <p>
                    <b>Total Tickets:</b>
                    ${event.totalTickets}
                </p>

                <div class="price">
                    $${Number(event.ticketPrice).toFixed(2)}
                </div>

                <button class="danger-btn" onclick="deleteEvent(${event.id})">
                    Delete Event
                </button>
            </div>
        `).join('');
    } catch (error) {
        container.innerHTML = '<div class="empty-state"><h3>Unable to load events</h3></div>';
    }
}

async function loadAdminTickets() {
    const container = document.getElementById('admin-tickets');

    try {
        const tickets = await fetchJson('/api/tickets');

        container.innerHTML = tickets.map(ticket => `
            <div class="card">
                <div class="card-title">${ticket.ticketNumber}</div>

                <p>
                    <b>Event ID:</b>
                    ${ticket.eventId}
                </p>

                <p>
                    <b>Status:</b>
                    <span class="${ticket.status === 'AVAILABLE' ? 'status-available' : 'status-booked'}">
                        ${ticket.status}
                    </span>
                </p>

                <div class="price">
                    $${Number(ticket.price).toFixed(2)}
                </div>

                <button class="danger-btn" onclick="deleteTicket(${ticket.id})">
                    Delete Ticket
                </button>
            </div>
        `).join('');
    } catch (error) {
        container.innerHTML = '<div class="empty-state"><h3>Unable to load tickets</h3></div>';
    }
}

async function loadAdminBookings() {
    const container = document.getElementById('admin-bookings');

    try {
        const bookings = await fetchJson('/api/bookings');

        container.innerHTML = bookings.map(booking => `
            <div class="card">
                <div class="card-title">${booking.customerName}</div>

                <p>
                    <b>Email:</b>
                    ${booking.customerEmail}
                </p>

                <p>
                    <b>Status:</b>
                    ${booking.status}
                </p>

                <p>
                    <b>Booking Date:</b>
                    ${formatDate(booking.bookingDate)}
                </p>

                <button class="danger-btn" onclick="deleteBooking(${booking.id})">
                    Delete Booking
                </button>
            </div>
        `).join('');
    } catch (error) {
        container.innerHTML = '<div class="empty-state"><h3>Unable to load bookings</h3></div>';
    }
}

async function deleteEvent(id) {
    if (!confirm('Delete this event?')) return;

    await fetch(`/api/admin/events/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Basic ' + btoa('admin:admin123')
        }
    });

    location.reload();
}

async function deleteTicket(id) {
    if (!confirm('Delete this ticket?')) return;

    await fetch(`/api/admin/tickets/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Basic ' + btoa('admin:admin123')
        }
    });

    location.reload();
}

async function deleteBooking(id) {
    if (!confirm('Delete this booking?')) return;

    await fetch(`/api/admin/bookings/${id}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Basic ' + btoa('admin:admin123')
        }
    });

    location.reload();
}

function formatDate(dateString) {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString();
}

function adminLogout() {
    localStorage.removeItem('currentUser');
    window.location.href = '/index.html';
}