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
});


function updateChart(workedWeeks) {
    const labels = [];
    const ids = [];
    const dataset = [];

    for (const workedWeek of workedWeeks) {
        const label = escapeHtml(workedWeek.user.firstName);
        const hours = workedWeek.totalMinutes / 60
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
                label: 'Hours worked this week per student',
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