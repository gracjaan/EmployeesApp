window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");

    fetch("/earnit/api/users/" + getUserId(), {
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

    const workedWeeks = await getHours(getUserCompany(), getJWTCookie());
    updateChart(workedWeeks)
});

function getHours(companyId, token) {
    return fetch(`/earnit/api/companies/${companyId}/invoices/${getCurrentYear()}/${getCurrentWeek()}?totalHours=true&user=true`, {
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





