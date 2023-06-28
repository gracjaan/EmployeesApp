const ctx = document.getElementById('myChart');

window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    const week = document.getElementById("week");
    week.innerText = "Week " + getCurrentWeek();

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
        .catch(e => null)

    const notifications = await obtainNotifications();
    createEntries(notifications);


    initializeChart()
});

function toggleSeen (id) {
    return fetch("/api/users/" + getUserId() + "/notifications/" + id, {
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

function obtainInvoices(contract) {
    return fetch("/api/users/" + getUserId() + "/contracts/" + contract.id + "/invoices?totalHours=true&company=true", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

function obtainContractsForUser() {
    return fetch("/api/users/" + getUserId() + "/contracts", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(() => null);
}

function obtainNotifications() {
    return fetch("/api/users/" + getUserId() + "/notifications", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(() => null);
}

async function getData() {
    let data = [];
    const contracts = await obtainContractsForUser();
    let arrayOfInvoices = [];
    for (let contract of contracts) {
        arrayOfInvoices.push(await obtainInvoices(contract))
    }

    let week0 = 0;
    let week1 = 0;
    let week2 = 0;
    let week3 = 0;
    let week4 = 0;
    let week5 = 0;

    arrayOfInvoices.forEach(invoice => {
        console.log(invoice)
        invoice.forEach(i => {
            console.log(i)
            if (i.week === getCurrentWeek() - 5) {
                week0 += i.totalMinutes;
            } else if (i.week === getCurrentWeek() - 4) {
                week1 += i.totalMinutes;
            } else if (i.week === getCurrentWeek() - 3) {
                week2 += i.totalMinutes;
            } else if (i.week === getCurrentWeek() - 2) {
                week3 += i.totalMinutes;
            } else if (i.week === getCurrentWeek() - 1) {
                week4 += i.totalMinutes;
            } else if (i.week === getCurrentWeek()) {
                week5 += i.totalMinutes;
            }
        })
    })
    console.log([week0 / 60, week1 / 60, week2 / 60, week3 / 60, week4 / 60, week5 / 60])
    return [week0 / 60, week1 / 60, week2 / 60, week3 / 60, week4 / 60, week5 / 60]
}

async function initializeChart() {
    const data = await getData(); // Wait for the data to be fetched
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: [
                "Week " + (getCurrentWeek() - 5),
                "Week " + (getCurrentWeek() - 4),
                "Week " + (getCurrentWeek() - 3),
                "Week " + (getCurrentWeek() - 2),
                "Week " + (getCurrentWeek() - 1),
                "Week " + getCurrentWeek()
            ],
            datasets: [{
                label: 'Weekly worked hours from all contracts',
                data: data, // Use the fetched data
                fill: false,
                borderColor: 'rgb(237, 76, 76)',
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}