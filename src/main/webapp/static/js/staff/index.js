window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    const companies = await getStudentsPerCompany();
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

    updateChart(companies)
});



async function getStudentsPerCompany() {
    return await fetch("/earnit/api/staff/companies",
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