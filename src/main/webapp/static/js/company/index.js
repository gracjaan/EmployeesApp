const ctx = document.getElementById('myChart');

new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ["Pepijn", "Tom", "Thomas", "Dirck", "Test", "Test2"],
        datasets: [{
            label: 'Hours worked last week per student',
            data: [12, 35, 21, 30, 23, 40],
            backgroundColor: ["lightgoldenrodyellow"],
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


const name = document.getElementById("name");
window.addEventListener("helpersLoaded", async () => {
fetch("/earnit/api/users/"+getUserId(), {
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





