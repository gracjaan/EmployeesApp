window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    const requests = await getRequestsForCompany(getUserCompany(), getJWTCookie())
    const requestsDiv = document.getElementById("newRequests");

    //if there are no new requests from students we don't want to display notification on the request
    if(requests.length > 0){
        requestsDiv.classList.remove("hidden");
    }
    else{
        requestsDiv.classList.add("hidden");
    }
    //Gets the company user and displays the name
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

    //checks if there are any notifications that need to be showed
    const notifications = await obtainNotifications();
    createEntries(notifications);

    const workedWeeks = await getHours(getUserCompany(), getJWTCookie());
    updateChart(workedWeeks)
});

//Gets all the requests that students have posted for their company to review
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

//creates the notification elements
//Formats one notification element
function createEntries (notifications) {
    const container = document.getElementById("entries");

    if (notifications.length === 0){
        const cont = document.createElement("div");
        cont.classList.add("h-full", "flex", "justify-center", "items-center")

        const placeholder = document.createElement("div");
        placeholder.classList.add("text-text", "font-bold", "uppercase", "text-center");
        placeholder.innerText = "No notifications"
        cont.appendChild(placeholder);

        container.appendChild(cont)
    }
    //for each notification, checks what the type of the notification is
    notifications.forEach(notification => {
        const outer = document.createElement("div");
        outer.classList.add("rounded-2xl", "bg-primary", "mx-2", "mt-2", "p-4", "relative", "last:mb-2", "cursor-pointer");
        outer.addEventListener('click', () => {
            toggleSeen(notification.id)

            switch (notification.type) {
                case "SUGGESTION ACCEPTED":
                    window.location.href = `/request?worked_week=${notification.workedWeekId}`
                    break;
                case "SUGGESTION REJECTED":
                    window.location.href = `/request?worked_week=${notification.workedWeekId}`
                    break;
                case "LINK":
                    window.location.href = `/user?id=${notification.userId}`
                    break;
                case "CONFLICT":
                    window.location.href = `/request?worked_week=${notification.workedWeekId}`
                    break;
            }
        })

        const inner1 = document.createElement("div");
        inner1.classList.add("text-text", "font-bold", "uppercase");
        inner1.innerText = notification.title;
        outer.appendChild(inner1)

        const description = document.createElement("div");
        description.classList.add("text-text");
        description.innerText = notification.description;
        outer.appendChild(description)

        const inner2 = document.createElement("div");
        inner2.classList.add("text-text", "uppercase");
        inner2.innerText = notification.date;
        outer.appendChild(inner2)

        //If the norification is seen, it will say that it is seen by removing the 'not-seen-incon'
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

//Send a request to the database to change the database to seen
//When a notification is clicked, sends a post request to change the seen attribute to true
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

//gets all the notifications for a company
// Get request to obtain notifications
function obtainNotifications() {
    return fetch("/api/companies/" + getUserCompany() + "/notifications", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(() => null);
}

// Get request to obtain hours of employees for the graph
function getHours(companyId, token) {
    return fetch(`/api/companies/${companyId}/invoices/${getCurrentYear()}/${getCurrentWeek()}?totalHours=true&user=true`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    }).then(res => res.json()).catch(() => null)
}

// Inputs all the relevant data into the graph
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
                label: 'Hours worked per student',
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
 //gets the current year
function getCurrentYear() {
    const currentDate = new Date();
    return currentDate.getFullYear();
}
//gets the current week
function getCurrentWeek() {
    return getWeek(new Date());
}

//gets the week that has the given date
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



