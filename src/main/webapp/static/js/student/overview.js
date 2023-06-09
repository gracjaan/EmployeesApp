window.addEventListener("helpersLoaded", async () => {

    const date = document.getElementById("date");
    date.addEventListener("click", () => select("date"));

    const hours = document.getElementById("hours");
    hours.addEventListener("click", () => select("hours"));

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
    return fetch(`/earnit/api/users/${userid}/contracts/${contract.id}/worked/${getCurrentYear()}/${getCurrentWeek()}?${getQueryParams()}`, {
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

    for (const contract of contracts) {
        const workedHours = await fetchSheet(getUserId(), contract);
        if (workedHours === null) continue;

        for (const hour of workedHours.hours) {
            entries.appendChild(createEntry(hour, contract.contract, getCurrentWeek(), getCurrentYear()))
        }
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

    const editContainer = document.createElement("div");
    editContainer.classList.add("flex", "items-center");
    entryContainer.appendChild(editContainer);

    const edit1 = document.createElement("button");
    edit1.classList.add("edit-button", "mr-5");
    edit1.setAttribute("id", "edit1")
    //edit1.addEventListener("click", () => toggleEdit(edit1));
    editContainer.appendChild(edit1);

    const edit2 = document.createElement("button");
    edit2.classList.add("edit-button");
    //edit2.addEventListener("click", () => deleteWorkedFromServer(getUserId(), entryContainer.getAttribute("contract-id")));
    editContainer.appendChild(edit2);

    const image1 = document.createElement("img");
    image1.classList.add("h-6", "w-6");
    image1.src = "/earnit/static/icons/pencil.svg"
    edit1.appendChild(image1);

    const image2 = document.createElement("img");
    image2.classList.add("h-5", "w-5");
    image2.src = "/earnit/static/icons/bin.svg"
    edit2.appendChild(image2);

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

function getCurrentYear() {
    const currentDate = new Date();
    return currentDate.getFullYear();
}

function getCurrentWeek() {
    const currentDate = new Date();
    const startDate = new Date(currentDate.getFullYear(), 0, 1);
    const days = Math.floor((currentDate - startDate) /
        (24 * 60 * 60 * 1000));

    const weekNumber = Math.ceil(days / 7);

    return weekNumber;
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

function weeksInYear(year) {
    return Math.max(
        getWeek(new Date(year, 11, 31))
        , getWeek(new Date(year, 11, 31-7))
    );
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


// todo deletbutton and edit button should not exist
// todo filtering buttons (position, week)
// todo make the chart working