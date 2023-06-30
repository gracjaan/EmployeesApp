const date = document.getElementById("date");
date.addEventListener("click", () => select("date"));

const hours = document.getElementById("hours");
hours.addEventListener("click", () => select("hours"));

const acceptButton = document.getElementById("accept");
acceptButton.addEventListener("click", () => approve(getWorkedWeekId(), getJWTCookie()));

const rejectButton = document.getElementById("reject");
rejectButton.addEventListener("click", () => reject(getWorkedWeekId(), getJWTCookie()));

window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    name.addEventListener("click", () => {
        location.href = "/user?id=" + name.getAttribute("data-user-id")
    })

    await updateHours();
});


async function updateHours() {
    const request = await getRequestForStaff(getWorkedWeekId(), getJWTCookie());
    if (request === null) {
        location.replace("/requests");
        return;
    }

    // Update page to data
    updatePage(request);
}
//Orders the list based on the staff preference
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

//Handles request in favor of the student
function approve(workedWeekId, token) {
    fetch(`/api/staff/rejects/${workedWeekId}?${getQueryParams()}`, {
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

//Handles request in favor of the company
function reject(workedWeekId, token) {
    fetch(`/api/staff/rejects/${workedWeekId}?${getQueryParams()}`, {
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

function isCompanyAccepted(workedWeek) {
    if (typeof workedWeek.hours === 'undefined' || workedWeek.hours.length < 1) return null;

    return workedWeek.hours[0].minutes === workedWeek.hours[0].suggestion;
}

function updatePage(request) {
    const name = document.getElementById("name");
    name.innerHTML = getName(escapeHtml(request.user.firstName), escapeHtml(request.user.lastName), escapeHtml(request.user.lastNamePrefix), "<br />");
    name.setAttribute("data-user-id", request.user.id);

    const studentName = document.getElementById("student-name");
    studentName.innerText= getName(request.user.firstName, request.user.lastName, request.user.lastNamePrefix);

    const companyName = document.getElementById("company-name");
    companyName.innerText = request.company.name;

    const entries = document.getElementById("entries");
    entries.innerHTML = "";

    if (request.hours.length === 0) {
        const noEntries = document.createElement("div");
        noEntries.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noEntries.innerText = "No entries";
        entries.append(noEntries)
    }

    for (const hour of request.hours) {
        entries.appendChild(createEntry(request.year, request.week, request.contract, hour, request.status === "APPROVED"));
    }

    const usernote = document.getElementById("user-note");
    if (request.note === undefined) {
        usernote.innerText = "Not provided";
    }
    else {
        usernote.innerText = request.note;
    }

    const companynote = document.getElementById("company-note");
    if (request.companyNote === undefined){
        companynote.innerText = "Not provided";
    }
    else{
        companynote.innerText = request.companyNote;
    }

    if (request.status !== "SUGGESTION_DENIED") {
        document.getElementById("accept").classList.add("hidden");
        document.getElementById("reject").classList.add("hidden");

        if (!isCompanyAccepted(request)) {
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
    }
}

//Creates request element
function createEntry(year, week, contract, entry, approved) {
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

    const hoursDiv = document.createElement("div");
    hoursDiv.classList.add("flex", "gap-1", "justify-center", "items-center", "sm:justify-start")
    entryInfo.appendChild(hoursDiv);

    const hasSuggestion = entry.suggestion !== undefined && entry.suggestion !== null && !approved;
    const hours = document.createElement("div");
    hours.classList.add("text-text", "font-bold", "sm:font-normal");
    hours.innerText = `${entry.minutes / 60}H`;
    hoursDiv.append(hours);

    if (hasSuggestion) {
        const arrow = document.createElement("img");
        arrow.src = "/static/icons/arrow-right-white.svg";
        arrow.classList.add("w-4");
        hoursDiv.append(arrow);

        const suggestedHours = document.createElement("div");
        suggestedHours.classList.add("text-[#FD8E28]", "font-bold", "sm:font-normal");
        suggestedHours.innerText = `${entry.suggestion / 60}H`;
        hoursDiv.append(suggestedHours);
    }

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
    return `user=true&contract=true&hours=true&company=true${order.length > 0 ? `&order=${order}`: ""}`
}

function getRequestForStaff(workedWeekId, token) {
    return fetch(`/api/staff/rejects/${workedWeekId}?${getQueryParams()}`, {
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
        location.replace("/requests");
        return;
    }

    return search.get("worked_week");
}