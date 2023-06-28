window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    const companies = await getStudentsPerCompany();
    const requests = await getRejectedWeeks()
    const requestsDiv = document.getElementById("newRequests");

    if(requests !== null && requests.length > 0){
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

    updateChart(companies)
});

async function getRejectedWeeks() {
    return await fetch(`/api/staff/rejects`,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json()).catch(() => null);
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

function obtainNotifications() {
    return fetch("/api/users/" + getUserId() + "/notifications", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(() => null);
}

async function getStudentsPerCompany() {
    return await fetch("/api/staff/companies",
        {
            method: "GET",
            headers: {
                "accept-type": "application/json",
                'authorization': `token ${getJWTCookie()}`
            }
        }
    ).then((res) => res.json())
        .catch(() => null);

}


function updateChart(companies) {
    const labels = [];
    const ids = [];
    const dataset = [];

    for (const company of companies) {
        const label = escapeHtml(company.name);
        const count = company.count;

        const id = company.id;

        if (ids.includes(id)) {
            dataset[ids.indexOf(id)] += count;
        } else {
            ids.push(id);
            labels.push(label);
            dataset.push(count);
        }
    }

    const ctx = document.getElementById('myChart');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Students per company',
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