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
        location.href = "/user?id=" + name.getAttribute("data-user-id")
    })

    await updateHours();
});


async function updateHours() {
    const request = await getRequestForCompany(getUserCompany(), getWorkedWeekId(), getJWTCookie());
    if (request === null) {
        location.replace("/requests");
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
    fetch(`/api/companies/${companyId}/approves/${workedWeekId}?${getQueryParams()}`, {
        method: 'POST',
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .then((request) => {
            updatePage(request);
            alertPopUp("Accepted the week", true)
        })
        .catch(() => null);
}

let noteConfirmation = null;

function rejectConfirm() {
    if (noteConfirmation === null) return;

    toggleNote();

    fetch(`/api/companies/${noteConfirmation.companyId}/approves/${noteConfirmation.workedWeekId}/note`, {
        method: 'POST',
        headers: {
            'authorization': `token ${noteConfirmation.token}`,
            'content-type': 'application/json'
        },
        body: JSON.stringify({
            note: document.getElementById("note").value
        })
    })
        .then(async (res) => {
            if (res.status !== 200) throw new Error();

            fetch(`/api/companies/${noteConfirmation.companyId}/approves/${noteConfirmation.workedWeekId}?${getQueryParams()}`, {
                method: 'DELETE',
                headers: {
                    'authorization': `token ${noteConfirmation.token}`,
                    'accept-type': 'application/json'
                }
            })
                .then(async (res) => await res.json())
                .then((request) => {
                    updatePage(request);
                    alertPopUp("rejected the week. Request will be considered soon", false)
                })
                .catch(() => null);
        })
        .catch(() => null);
}

function reject(companyId, workedWeekId, token) {
    noteConfirmation = {companyId, workedWeekId, token};
    toggleNote();
}

function undo(companyId, workedWeekId, token) {
    fetch(`/api/companies/${companyId}/approves/${workedWeekId}?${getQueryParams()}`, {
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
            alertPopUp("undid the previous action", true)
        })
        .catch(() => null);
}

function updatePage(workedWeek) {
    const name = document.getElementById("name");
    name.innerHTML = getName(escapeHtml(workedWeek.user.firstName), escapeHtml(workedWeek.user.lastName), escapeHtml(workedWeek.user.lastNamePrefix), "<br />");
    name.setAttribute("data-user-id", workedWeek.user.id);

    const noteText = document.getElementById("noteText");
    if (workedWeek.note === null || workedWeek.note === "") {
        noteText.innerText = "No note added";
    } else {
        noteText.innerText = workedWeek.note;
    }

    const entries = document.getElementById("entries");
    entries.innerHTML = "";

    if (workedWeek.hours.length === 0) {
        const noEntries = document.createElement("div");
        noEntries.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noEntries.innerText = "No invoices";
        entries.append(noEntries)
    }

    for (const hour of workedWeek.hours) {
        entries.appendChild(createEntry(workedWeek.year, workedWeek.week, workedWeek.contract, hour, workedWeek.status !== "CONFIRMED", workedWeek.status === "APPROVED"));
    }

    if (!(workedWeek.status === "NOT_CONFIRMED" || workedWeek.status === "CONFIRMED")) {
        document.getElementById("accept").classList.add("hidden");
        document.getElementById("reject").classList.add("hidden");
        document.getElementById("undo").classList.remove("hidden");

        if (workedWeek.status === "APPROVED") {
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

function createEntry(year, week, contract, entry, sent, approved) {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-primary", "p-4", "relative", "flex", "justify-between");

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "grid-cols-[1fr_3fr]", "sm:grid-cols-[1fr_2fr_2fr_5fr]", "grid");
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
        suggestedHours.classList.add("text-[#FD8E28]", "font-bold", "sm:font-normal", "hour-input");
        suggestedHours.innerText = `${entry.suggestion / 60}H`;
        hoursDiv.append(suggestedHours);
    } else {
        hours.classList.add("hour-input");
    }

    const role = document.createElement("div");
    role.classList.add("text-text", "font-bold", "sm:font-normal", "col-span-3", "sm:col-span-1");
    role.innerText = contract.role;
    entryInfo.appendChild(role);

    const description = document.createElement("div");
    description.classList.add("text-text", "col-span-3", "sm:col-span-1");
    description.innerText = entry.work;
    entryInfo.appendChild(description);

    if (!sent) {
        const editContainer = document.createElement("div");
        editContainer.classList.add("flex", "items-center");
        entryContainer.appendChild(editContainer);

        const edit1 = document.createElement("button");
        edit1.classList.add("edit-button", "mr-5");
        edit1.setAttribute("id", "edit1")
        edit1.addEventListener("click", () => toggleEdit(edit1, entry));
        editContainer.appendChild(edit1);

        const image1 = document.createElement("img");
        image1.classList.add("h-6", "w-6");
        image1.src = "/static/icons/pencil.svg"
        edit1.appendChild(image1);
    }

    return entryContainer;
}

function toggleNote() {
    const companyDialog = document.getElementById("company-dialog");
    companyDialog.classList.toggle("hidden");
}

function cancelNote() {
    const note = document.getElementById("note");
    note.value = "";

    const companyDialog = document.getElementById("company-dialog");
    companyDialog.classList.toggle("hidden");
}

function toggleEditIcon(entryElement, editButton, hourElement) {
    // Toggle editable element
    const isEditable = hourElement.contentEditable === 'true';
    hourElement.contentEditable = !isEditable;

    // Update icon
    const isEditing = entryElement.classList.contains('editing');
    entryElement.classList.toggle('editing', !isEditing);
    editButton.innerHTML = isEditing ? '<img src="/static/icons/pencil.svg" class="h-6 w-6" alt="pencil" />' : '<img src="/static/icons/checkmark.svg" class="h-6 w-6" alt="arrow" />';

    return !isEditing;
}

async function toggleEdit(button, entry) {
    const entryElement = button.parentNode.parentNode;
    const hourElement = entryElement.querySelector('.hour-input');
    const editButton = entryElement.querySelector('.edit-button');

    const isEditing = toggleEditIcon(entryElement, editButton, hourElement);

    if (!isEditing) {
        const suggestedMinutes = parseFloat(hourElement.textContent) * 60;

        // Submission logic here
        const updatedData = {
            id: entry.id,
            suggestion: suggestedMinutes === entry.minutes ? null : suggestedMinutes,
        };

        // Send the updatedData to the server or perform any necessary actions
        if(!(await submitEdittedForm(updatedData))) {
            toggleEditIcon(entryElement, editButton, hourElement);
        } else {
            const error = document.getElementById("edit-error");
            error.classList.add("hidden");

            await updateHours();
        }
    } else {
        const error = document.getElementById("edit-error");
        error.classList.add("hidden");
    }
}

function getQueryParams() {
    const order = getOrder();
    return `user=true&contract=true&hours=true${order.length > 0 ? `&order=${order}`: ""}`
}

function getRequestForCompany(companyId, workedWeekId, token) {
    return fetch(`/api/companies/${companyId}/approves/${workedWeekId}?${getQueryParams()}`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .catch(() => null);
}

async function submitEdittedForm(data) {
    let json = { suggestion: data.suggestion }

    const updated = await fetch("/api/companies/" + getUserCompany() + "/approves/suggest/" + data.id,
        {
            method: "POST",
            body: JSON.stringify(json),
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                "Content-type": "application/json",
                "Accept": "application/json"
            }
        })
        .then((res) => res.status === 200)
        .catch(() => false);

    if (!updated) {
        const error = document.getElementById("edit-error");
        error.classList.remove("hidden");
        error.innerText = "Could not update"
    }

    return updated;
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

function alertPopUp(message, positive) {
    let confirmation = document.getElementById("alertPopup");
    let accent = document.getElementById("accent")
    let image = document.getElementById("confirmationIcon")
    let p = document.getElementById("popUpAlertParagraph")
    p.innerText = message

    if (positive) {
        accent.classList.add("bg-accent-success")
        image.src = "/static/icons/checkmark.svg"
    } else {
        accent.classList.add("bg-[#FD8E28]")
        image.src = "/static/icons/light-white.svg"
    }
    confirmation.classList.remove("hidden");
    setTimeout(function () {
            confirmation.classList.add("hidden");
            if (positive) {
                accent.classList.remove("bg-accent-success")
            } else {
                accent.classList.remove("bg-[#FD8E28]")
            }
        }, 2000
    );
}