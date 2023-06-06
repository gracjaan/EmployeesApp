const ctx = document.getElementById('myChart');

new Chart(ctx, {
    type: 'bar',
    data: {
        labels: ["Pepijn", "Tom", "Thomas", "Dirck"],
        datasets: [{
            label: 'My First Dataset',
            data: [12, 35, 21, 30],
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
        console.log(json);
        name.innerHTML = "Welcome back, " + json.firstName;
})
});





