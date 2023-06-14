const date = document.getElementById("date");
date.addEventListener("click", () => select("date"));

const hours = document.getElementById("hours");
hours.addEventListener("click", () => select("hours"));

const acceptButton = document.getElementById("accept");
acceptButton.addEventListener("click", () => approve(getUserCompany(), getWorkedWeekId(), getJWTCookie()));

const rejectButton = document.getElementById("reject");
rejectButton.addEventListener("click", () => reject(getUserCompany(), getWorkedWeekId(), getJWTCookie()));

const undoButton = document.getElementById("undo");
undoButton.addEventListener("click", () => undo(getUserCompany(), getWorkedWeekId(), getJWTCookie()));

window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    name.addEventListener("click", () => {
        location.href = "/earnit/user?id=" + name.getAttribute("data-user-id")
    })

    await updateHours();
});

async function updateHours() {
    const request = await getRequestForStaff(getWorkedWeekId(), getJWTCookie());
    if (request === null) {
        location.replace("/earnit/requests");
        return;
    }

    // Update page to data
    updatePage(request);
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

function approve(companyId, workedWeekId, token) {
    fetch(`/earnit/api/companies/${companyId}/approves/${workedWeekId}?${getQueryParams()}`, {
        method: 'POST',
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .then((request) => {
            updatePage(request);
        })
        .catch(() => null);
}

function reject(companyId, workedWeekId, token) {
    fetch(`/earnit/api/companies/${companyId}/approves/${workedWeekId}?${getQueryParams()}`, {
        method: 'DELETE',
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .then((request) => {
            updatePage(request);
        })
        .catch(() => null);
}

function undo(companyId, workedWeekId, token) {
    fetch(`/earnit/api/companies/${companyId}/approves/${workedWeekId}?${getQueryParams()}`, {
        method: 'PUT',
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json',
            'content-type': 'application/json'
        },
        body: JSON.stringify({
            approve: null
        })
    })
        .then(async (res) => await res.json())
        .then((request) => {
            updatePage(request);
        })
        .catch(() => null);
}

function updatePage(request) {
    const name = document.getElementById("name");
    name.innerHTML = getName(escapeHtml(request.user.firstName), escapeHtml(request.user.lastName), escapeHtml(request.user.lastNamePrefix), "<br />");
    name.setAttribute("data-user-id", request.user.id);

    const entries = document.getElementById("entries");
    entries.innerHTML = "";
    for (const hour of request.hours) {
        entries.appendChild(createEntry(request.year, request.week, request.contract, hour));
    }

    if (request.approved !== null) {
        document.getElementById("accept").classList.add("hidden");
        document.getElementById("reject").classList.add("hidden");
        document.getElementById("undo").classList.remove("hidden");

        if (request.approved) {
            document.getElementById("rejected").classList.add("hidden");
            document.getElementById("accepted").classList.remove("hidden");
        } else {
            document.getElementById("rejected").classList.remove("hidden");
            document.getElementById("accepted").classList.add("hidden");
        }
    } else {
        document.getElementById("accept").classList.remove("hidden");
        document.getElementById("reject").classList.remove("hidden");

        document.getElementById("rejected").classList.add("hidden");
        document.getElementById("accepted").classList.add("hidden");

        document.getElementById("undo").classList.add("hidden");
    }
}

function createEntry(year, week, contract, entry) {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-primary", "p-4", "relative", "flex", "justify-between");

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "grid-cols-[1fr_1fr_1fr]", "sm:grid-cols-[1fr_1fr_2fr_5fr]", "grid");
    entryContainer.appendChild(entryInfo);

    const calculatedDate = addDays(getDateOfISOWeek(week, year), entry.day);

    const date = document.createElement("div");
    date.classList.add("text-text", "font-bold", "uppercase");
    date.innerText = `${formatNumber(calculatedDate.getDate())}.${formatNumber(calculatedDate.getMonth() + 1)}`;
    entryInfo.appendChild(date);

    const hours = document.createElement("div");
    hours.classList.add("text-text", "font-bold", "sm:font-normal");
    hours.innerText = `${entry.minutes / 60}H`;
    entryInfo.appendChild(hours);

    const role = document.createElement("div");
    role.classList.add("text-text", "font-bold", "sm:font-normal");
    role.innerText = contract.role;
    entryInfo.appendChild(role);

    const description = document.createElement("div");
    description.classList.add("text-text", "col-span-3", "sm:col-span-1");
    description.innerText = entry.work;
    entryInfo.appendChild(description);

    return entryContainer;
}

function getQueryParams() {
    const order = getOrder();
    return `user=true&contract=true&hours=true${order.length > 0 ? `&order=${order}`: ""}`
}

function getRequestForStaff(workedWeekId, token) {
    return fetch(`/earnit/api/staff/rejects/${workedWeekId}?${getQueryParams()}`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .catch(() => null);
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

    await updateHours();
}

// modified from https://stackoverflow.com/questions/16590500/calculate-date-from-week-number-in-javascript
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

function getWorkedWeekId() {
    const search = new URLSearchParams(location.search);
    if (!search.has("worked_week")) {
        location.replace("/earnit/requests");
        return;
    }

    return search.get("worked_week");
}