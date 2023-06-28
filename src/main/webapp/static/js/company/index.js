window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    const requests = await getRequestsForCompany(getUserCompany(), getJWTCookie())
    const requestsDiv = document.getElementById("newRequests");

    if(Object.keys(requests).length > 0){
        requestsDiv.classList.remove("hidden");
    }
    else{
        requestsDiv.classList.add("hidden");
    }

    fetch("/api/users/" + getUserId(), {
            method: "GET",
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        }
    )
        .then(async res => {
            const json = await res.json();
            name.innerText = "Welcome back, " + json.firstName;
        })

    const notifications = await obtainNotifications();
    createEntries(notifications);

    const workedWeeks = await getHours(getUserCompany(), getJWTCookie());
    updateChart(workedWeeks)
});

function getRequestsForCompany(uid, token) {
    return fetch(`/api/companies/${uid}/approves?user=true&contract=true&order=worked_week.year:asc,worked_week.week:asc`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .catch(() => []);
}

function createEntries (notifications) {
    const container = document.getElementById("entries");

    if (notifications.length === 0){
        const placeholder = document.createElement("div");
        placeholder.classList.add("text-text", "font-bold", "uppercase", "text-center");
        placeholder.innerText = "No notifications"
        container.appendChild(placeholder);
    }

    notifications.forEach(notification => {
        const outer = document.createElement("div");
        outer.classList.add("rounded-2xl", "bg-primary", "mx-2", "mt-2", "p-4", "relative", "last:mb-2");
        // outer.addEventListener('click', () => {
        //     const n = document.getElementById()
        // })

        const inner1 = document.createElement("div");
        inner1.classList.add("text-text", "font-bold", "uppercase");
        inner1.innerText = notification.message;
        outer.appendChild(inner1)

        const inner2 = document.createElement("div");
        inner2.classList.add("text-text", "uppercase");
        inner2.innerText = notification.date;
        outer.appendChild(inner2)

        if (!notification.seen){
            const inner3 = document.createElement("div");
            inner3.classList.add("bg-accent-fail", "rounded-full", "w-4", "h-4", "absolute", "-top-1", "-left-1");
            inner3.setAttribute("id", notification.id)
            inner3.addEventListener('click', () => toggleSeen(notification.id))
            outer.appendChild(inner3)
        }
        container.appendChild(outer)
    })
}

function toggleSeen (id) {
    return fetch("/api/companies/" + getUserCompany() + "/notifications/" + id, {
        method: 'POST',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            "Content-type": "application/json",
            "Accept": "application/json"
        }
    })
        .then(() => {
            const dot = document.getElementById(id);
            dot.classList.add("hidden")
        })
        .catch(() => null);
}

function obtainNotifications() {
    return fetch("/api/companies/" + getUserCompany() + "/notifications", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(() => null);
}

function getHours(companyId, token) {
    return fetch(`/api/companies/${companyId}/invoices/${getCurrentYear()}/${getCurrentWeek()}?totalHours=true&user=true`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    }).then(res => res.json()).catch(() => null)
}

function updateChart(studentsPerCompany) {
    const labels = [];
    const ids = [];
    const dataset = [];

    for (const workedWeek of studentsPerCompany) {
        const label = escapeHtml(workedWeek.user.firstName);
        const hours = workedWeek.totalMinutes / 60;
        const id = workedWeek.user.id;

        if (ids.includes(id)) {
            dataset[ids.indexOf(id)] += hours;
        } else {
            ids.push(id);
            labels.push(label);
            dataset.push(hours);
        }
    }

    const ctx = document.getElementById('myChart');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Number of students per company',
                data: dataset,
                backgroundColor: ['rgb(237,76,76)'],
                color: "white",
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    labels: {
                        color: "white"
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        color: "white"
                    }
                },
                x: {
                    ticks: {
                        color: "white"
                    }
                }
            }
        }
    });
}

function getCurrentYear() {
    const currentDate = new Date();
    return currentDate.getFullYear();
}

function getCurrentWeek() {
    return getWeek(new Date());
}

function getWeek(ofDate) {
    const date = new Date(ofDate);
    date.setHours(0, 0, 0, 0);
    // Thursday in current week decides the year.
    date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7);
    // January 4 is always in week 1.
    const week1 = new Date(date.getFullYear(), 0, 4);
    // Adjust to Thursday in week 1 and count number of weeks from date to week1.
    return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000
        - 3 + (week1.getDay() + 6) % 7) / 7);
}


function updatePage(request) {
    const entries = document.getElementById("entries");
    entries.innerHTML = "";

    // if (request === null || request.length < 1) {
    //     const noInvoices = document.createElement("div");
    //     noInvoices.classList.add("text-text", "font-bold", "w-full", "flex", "justify-center", "my-2");
    //     noInvoices.innerText = "No invoices";
    //     entries.append(noInvoices);
    //     return;
    // }
    entries.appendChild(createEntry())
    entries.appendChild(createEntry())
    entries.appendChild(createEntry())
    entries.appendChild(createEntry())
    entries.appendChild(createEntry())
    entries.appendChild(createEntry())
    entries.appendChild(createEntry())
    entries.appendChild(createEntry())
    entries.appendChild(createEntry())
    // for (const workedWeek of request) {
    //     entries.appendChild(createEntry());
    // }
}

function createEntry() {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-primary", "px-4", "py-2", "relative");

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "items-center");
    entryContainer.appendChild(entryInfo);

    const noteInfo = document.createElement("div");
    noteInfo.classList.add("text-text", "font-bold", "uppercase");
    noteInfo.innerText = "Hello World a;lkdjf;lasflks;df;lk";
    // noteInfo.innerText = getName(notification.user.firstName, notification.user.lastName, notification.user.lastNamePrefix) + notification.notification;
    entryInfo.appendChild(noteInfo);

    const noteDate = document.createElement("div");
    noteDate.classList.add("text-text");
    // noteDate.innerText = notification.date;
    noteDate.innerText = "22-34"
    entryInfo.appendChild(noteDate);

    return entryContainer;
}



