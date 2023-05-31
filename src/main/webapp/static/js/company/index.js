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
