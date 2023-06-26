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
            labels: [
                "Week " + (getCurrentWeek() - 5),
                "Week " + (getCurrentWeek() - 4),
                "Week " + (getCurrentWeek() - 3),
                "Week " + (getCurrentWeek() - 2),
                "Week " + (getCurrentWeek() - 1),
                "Week " + getCurrentWeek()
            ],
            datasets: await getData()
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
    return fetch("/api/users/" + uid + "/contracts", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

function obtainInvoices(contract) {
    return fetch("/api/users/" + getUserId() + "/contracts/" + contract.id + "/invoices?totalHours=true&company=true", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

function fetchSheet(userid, contract) {
    return fetch(`/api/users/${userid}/contracts/${contract.id}/worked/${getSelectedYear()}/${getSelectedWeek()}?${getQueryParams()}`, {
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

    if (workEntries === null || workEntries.length < 1){
        const noInvoices = document.createElement("div");
        noInvoices.classList.add("text-text", "font-bold", "w-full", "flex", "justify-center", "my-2");
        noInvoices.innerText = "No invoices";
        entries.append(noInvoices)
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
    date.classList.add("px-1", "text-text", "font-bold", "uppercase");
    date.innerText = `${formatNumber(calculatedDate.getDate())}.${formatNumber(calculatedDate.getMonth() + 1)}`;
    entryInfo.appendChild(date);

    const hours = document.createElement("div");
    hours.classList.add("px-1", "text-text");
    hours.innerText = `${entry.minutes / 60}H`;
    entryInfo.appendChild(hours);

    const role = document.createElement("div");
    role.classList.add("px-1", "text-text");
    role.innerText = contract.role;
    entryInfo.appendChild(role);

    const description = document.createElement("div");
    description.classList.add("px-1", "text-text");
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

async function getData() {
    let data = [];
    const contracts = await obtainContractsForUser(getUserId());
    let arrayOfInvoices = [];
    for (let contract of contracts) {
        arrayOfInvoices.push(await obtainInvoices(contract))
    }

    console.log(arrayOfInvoices)
    console.log(contracts)

    const defined = [{borderColor: 'rgb(237, 76, 76)', name: "My first data"}, {borderColor: 'rgb(255, 172, 28)', name: "My second data"}, {borderColor: 'rgb(231,226,95)', name: "My third data"}, {borderColor: 'rgb(75, 192, 192)', name: "My fourth data"}];

    let index = 0;
    arrayOfInvoices.forEach(invoice => {
        let week0 = 0;
        let week1 = 0;
        let week2 = 0;
        let week3 = 0;
        let week4 = 0;
        let week5 = 0;

        invoice.forEach(i => {

            if (i.week === getCurrentWeek() - 5) {
                week0 += i.totalMinutes;
            } else if (i.week === getCurrentWeek() - 4) {
                week1 += i.totalMinutes;
            } else if (i.week === getCurrentWeek() - 3) {
                week2 += i.totalMinutes;
            } else if (i.week === getCurrentWeek() - 2) {
                week3 += i.totalMinutes;
            } else if (i.week === getCurrentWeek() - 1) {
                week4 += i.totalMinutes;
            } else if (i.week === getCurrentWeek()) {
                week5 += i.totalMinutes;
            }
        })

        data.push({
            label: contracts[index].contract.role,
            data: [week0/60, week1/60, week2/60, week3/60, week4/60, week5/60],
            fill: false,
            borderColor: defined[index%3].borderColor,
            tension: 0.3
        })

        index ++;
    })
    console.log(data)
    return data;
}

function generateAllInvoices () {
    fetch(`/api/users/${getUserId()}/invoices/download/${getSelectedYear()}/${getSelectedWeek()}`, {
        headers: {
            'authorization': `token ${getJWTCookie()}`,
        }
    })
        .then(async res =>  ({ data: await res.blob(), filename: res.headers.get("content-disposition").split('filename = ')[1] }))
        .then(({ data, filename }) => {
            const a = document.createElement("a");
            a.href = window.URL.createObjectURL(data);
            a.download = filename;
            a.click();
        });
}


// todo deletbutton and edit button should not exist
// todo filtering buttons (position, week)
// todo make the chart working