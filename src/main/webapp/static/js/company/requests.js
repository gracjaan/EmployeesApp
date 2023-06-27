window.addEventListener("helpersLoaded", async () => {
    const requestsContainer = document.getElementById("requests");
    if (typeof requestsContainer === "undefined") return;

    const requests = await getRequestsForCompany(getUserCompany(), getJWTCookie());
    for (const request of requests) {
        const container = createRequestCard(request);
        requestsContainer.appendChild(container);
    }

    if (requests === null || requests.length < 1) {
        const noRequests = document.createElement("div");
        noRequests.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noRequests.innerText = "No requests to approve";
        requestsContainer.append(noRequests)
    }
});

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

function createRequestCard(workedWeek) {
    const container = document.createElement("a");
    container.classList.add("bg-primary", "block", "rounded-xl", "py-4", "pl-4", "pr-8", "w-full", "break-inside-avoid-column");
    container.href = "/request?worked_week=" + workedWeek.id;

    const name = document.createElement("h3");
    name.classList.add("text-text", "text-2xl", "font-bold");
    name.innerHTML = getName(escapeHtml(workedWeek.user.firstName), escapeHtml(workedWeek.user.lastName), escapeHtml(workedWeek.user.lastNamePrefix), "<br />");
    container.appendChild(name);

    const role = document.createElement("p");
    role.classList.add("text-text");
    role.innerText = workedWeek.contract.role;
    container.appendChild(role);

    const week = document.createElement("p");
    week.classList.add("text-text");
    week.innerText = "Week " + workedWeek.week;
    container.appendChild(week);

    return container;
}