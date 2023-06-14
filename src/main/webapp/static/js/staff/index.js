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