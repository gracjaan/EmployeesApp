window.addEventListener("helpersLoaded", async () => {

    const date = document.getElementById("date");
    date.addEventListener("change", async () => {
        const contracts = await updateContracts();
        await updatePage(contracts);
    });

    const hours = document.getElementById("hours");
    hours.addEventListener("change", async () => {
        const contracts = await updateContracts();
        await updatePage(contracts);
    });

    const ctx = document.getElementById('myChart');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"],
            datasets: [{
                label: 'My First Dataset',
                data: [65, 59, 80, 81, 56, 55, 40],
                fill: false,
                borderColor: 'rgb(75, 192, 192)',
                tension: 0.3
            }, {
                label: 'My Second Dataset',
                data: [20, 30, 90, 37, 56, 88, 44],
                fill: false,
                borderColor: 'rgb(77, 20, 100)',
                tension: 0.3
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });

    updatePage(await obtainContractsForUser(getUserId()))

    const week = document.getElementById("week");
    week.addEventListener("change", async (e) => {
        const contracts = await updateContracts();
        await updatePage(contracts);
    })
})

function obtainContractsForUser(uid) {
    return fetch("/earnit/api/users/" + uid + "/contracts", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

function fetchSheet(userid, contract) {
    return fetch(`/earnit/api/users/${userid}/contracts/${contract.id}/worked/${getSelectedYear()}/${getSelectedWeek()}?${getQueryParams()}`, {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

async function updatePage(contracts) {
    const entries = document.getElementById("entries");
    entries.innerText = "";

    const workEntries = [];

    for (const contract of contracts) {
        const workedHours = await fetchSheet(getUserId(), contract);
        if (workedHours === null) continue;

        for (const hour of workedHours.hours) {
            workEntries.push({hour, contract: contract.contract});
        }
    }

    const date = document.getElementById("date");
    const dateSelected = date.getAttribute("data-selected");

    const hours = document.getElementById("hours");
    const hoursSelected = hours.getAttribute("data-selected");

    let order = 0;
    let key = "";
    if (dateSelected > 0) {
        order = (dateSelected === "1" ? 1 : -1);
        key = "day"
    } else if (hoursSelected > 0) {
        order = (hoursSelected === "1" ? 1 : -1);
        key = "minutes"
    }

    workEntries.sort((a, b) => a.hour[key] - b.hour[key]);
    if (order < 0) {
        workEntries.reverse();
    }

    entries.innerText = "";
    for (const workEntry of workEntries) {
        entries.appendChild(createEntry(workEntry.hour, workEntry.contract, getSelectedWeek(), getSelectedYear()))
    }
}

function createEntry(entry, contract, week, year) {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-primary", "p-4", "relative", "flex", "justify-between");
    entryContainer.setAttribute("contract-id", contract.id)
    entryContainer.setAttribute("data-week", week)
    entryContainer.setAttribute("data-year", year)

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "grid-cols-[1fr_1fr_2fr_5fr]", "grid");
    entryContainer.appendChild(entryInfo);

    const calculatedDate = addDays(getDateOfISOWeek(week, year), entry.day);

    const date = document.createElement("div");
    date.classList.add("text-text", "font-bold", "uppercase");
    date.innerText = `${formatNumber(calculatedDate.getDate())}.${formatNumber(calculatedDate.getMonth() + 1)}`;
    entryInfo.appendChild(date);

    const hours = document.createElement("div");
    hours.classList.add("text-text");
    hours.innerText = `${entry.minutes / 60}H`;
    entryInfo.appendChild(hours);

    const role = document.createElement("div");
    role.classList.add("text-text");
    role.innerText = contract.role;
    entryInfo.appendChild(role);

    const description = document.createElement("div");
    description.classList.add("text-text");
    description.innerText = entry.work;
    entryInfo.appendChild(description);

    return entryContainer;
}

async function select(type) {
    const date = document.getElementById("date");
    const dateSelected = date.getAttribute("data-selected");

    const hours = document.getElementById("hours");
    const hoursSelected = hours.getAttribute("data-selected");

    if (type === "date") {
        hours.setAttribute("data-selected", "0");

        let state = Number.parseInt(dateSelected ?? "0") + 1;
        if (state > 2) state = 0;
        date.setAttribute("data-selected", state + "");
    } else {
        date.setAttribute("data-selected", "0");

        let state = Number.parseInt(hoursSelected ?? "0") + 1;
        if (state > 2) state = 0;
        hours.setAttribute("data-selected", state + "");
    }
}

function getQueryParams() {
    const order = getOrder();
    return `user=true&contract=true&hours=true${order.length > 0 ? `&order=${order}`: ""}`
}

function getOrder() {
    const date = document.getElementById("date");
    const dateSelected = date.getAttribute("data-selected");

    const hours = document.getElementById("hours");
    const hoursSelected = hours.getAttribute("data-selected");

    let order = "";
    if (dateSelected > 0) {
        order += "hours.day:" + (dateSelected === "1" ? "asc" : "desc");
    } else if (hoursSelected > 0) {
        order += "hours.minutes:" + (hoursSelected === "1" ? "asc" : "desc");
    }

    return order;
}

function getSelectedYear() {
    const header = document.getElementById("week");
    return header.getAttribute("data-year").toString();
}

function getSelectedWeek() {
    const header = document.getElementById("week");
    return header.getAttribute("data-week").toString();
}

function getDateOfISOWeek(w, y) {
    const simple = new Date(y, 0, 1 + (w - 1) * 7);
    const dow = simple.getDay();
    const ISOweekStart = simple;

    if (dow <= 4) {
        ISOweekStart.setDate(simple.getDate() - simple.getDay() + 1);
    } else {
        ISOweekStart.setDate(simple.getDate() + 8 - simple.getDay());
    }

    return ISOweekStart;
}

// modified from https://stackoverflow.com/questions/563406/how-to-add-days-to-date
function addDays(date, days) {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
}

function formatNumber(number) {
    return number.toLocaleString('en-US', {
        minimumIntegerDigits: 2,
        useGrouping: false
    });
}


async function updateContracts() {
    const contracts = await obtainContractsForUser(getUserId())

    if (contracts === null) {
        return null;
    }

    return contracts;
}


// todo deletbutton and edit button should not exist
// todo filtering buttons (position, week)
// todo make the chart working