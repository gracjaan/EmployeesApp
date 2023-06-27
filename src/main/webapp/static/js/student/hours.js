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

    const suggestionDate = document.getElementById("suggestion-date");
    suggestionDate.addEventListener("change", async () => {
        const contracts = await updateContracts();
        await updatePage(contracts);
    });

    const suggestionHours = document.getElementById("suggestion-hours");
    suggestionHours.addEventListener("change", async () => {
        const contracts = await updateContracts();
        await updatePage(contracts);
    });

    const acceptButton = document.getElementById("accept");
    acceptButton.addEventListener("click", () => approveSuggestion(getSuggestionContractId(), getSuggestionYear(), getSuggestionWeek(), getJWTCookie()));

    const rejectButton = document.getElementById("reject");
    rejectButton.addEventListener("click", () => rejectSuggestion(getSuggestionContractId(), getSuggestionYear(), getSuggestionWeek(), getJWTCookie()));

    const dropdown = document.getElementById("day-content");

    dropdown.addEventListener("click", async (e) => {
        const element = e.target;
        if (!element.hasAttribute("data-day")) return;

        await selectDay(element);
    });
    dropdown.appendChild(createDayItem(0, "Monday"));
    dropdown.appendChild(createDayItem(1, "Tuesday"));
    dropdown.appendChild(createDayItem(2, "Wednesday"));
    dropdown.appendChild(createDayItem(3, "Thursday"));
    dropdown.appendChild(createDayItem(4, "Friday"));
    dropdown.appendChild(createDayItem(5, "Saturday"));
    dropdown.appendChild(createDayItem(6, "Sunday"));

    const range = document.getElementById("dropdown-range")
    range.innerText = getWeekDateRange(getSelectedWeek(), getSelectedYear());

    const contracts = await updateContracts();
    await updatePage(contracts);

    const week = document.getElementById("week");
    week.addEventListener("change", async (e) => {
        const contracts = await updateContracts();
        await updatePage(contracts);

        const range = document.getElementById("dropdown-range")
        range.innerText = getWeekDateRange(e.detail.week, e.detail.year);
    })
});

function createDayItem(day, dayName) {
    const container = document.createElement("div");
    container.classList.add("py-2", "px-4", "hover:bg-gray-100", "cursor-pointer");
    container.innerText = dayName;
    container.setAttribute("data-day", day);
    return container;
}

async function updateContracts() {
    const contracts = await obtainContractsForUser(getUserId())

    if (contracts === null) {
        return null;
    }

    const positionContent = document.getElementById('position-content');
    positionContent.innerText = "";

    if (contracts.length === 0){
        const option = document.createElement('div');
        option.classList.add('py-2', 'px-4', 'hover:bg-gray-100', 'rounded-lg', 'cursor-pointer', 'text-center', 'font-bold');
        option.textContent = "No contracts"
        positionContent.appendChild(option);

        return contracts;
    }

    contracts.forEach(c => {
        const option = document.createElement('div');
        option.classList.add('py-2', 'px-4', 'hover:bg-gray-100', 'rounded-lg', 'cursor-pointer');
        option.textContent = c.contract.role;
        option.setAttribute('data-role', c.contract.role);
        option.setAttribute('data-id', c.contract.id);
        option.addEventListener('click', () => selectPosition(option));
        positionContent.appendChild(option);
    });

    return contracts;
}

function toggleNote() {
    if (document.getElementById("confirm-button").getAttribute("data-checked") === "0") {
        const companyDialog = document.getElementById("company-dialog");
        companyDialog.classList.toggle("hidden");
    } else {
        unconfirmWorkedWeek()
    }
}

function cancelNote() {
    const note = document.getElementById("note");
    note.value = "";

    const companyDialog = document.getElementById("company-dialog");
    companyDialog.classList.toggle("hidden");
}

async function confirmWorkedWeek() {
    const note = document.getElementById("note").value.trim();
    if (note.length < 10) {
        const err = document.getElementById("error");
        err.innerText = "Note needs to be at least 10 characters long"
        err.classList.remove("hidden");
        return;
    }

    const error = document.getElementById("confirm-error");
    error.classList.add("hidden");

    const contracts = await obtainContractsForUser(getUserId())
    if (contracts === null) {
        const error = document.getElementById("confirm-error");
        error.classList.remove("hidden");
        error.innerText = "Could not confirm worked week";
        return;
    }

    contracts.forEach(c => {
        fetch("/api/users/" + getUserId() + "/contracts/" + c.contract.id + "/worked/" + getSelectedYear() + "/" + getSelectedWeek() + "/note",
            {
                method: "PUT",
                body: document.getElementById("note").value.toString(),
                headers: {
                    'authorization': `token ${getJWTCookie()}`,
                    "Content-type": "text/plain"
                }
            })
            .then((res) => {
                if (res.status !== 200) {
                    throw new Error();
                }

                fetch("/api/users/" + getUserId() + "/contracts/" + c.contract.id + "/worked/" + getSelectedYear() + "/" + getSelectedWeek() + "/confirm",
                    {
                        method: "POST",
                        headers: {
                            'authorization': `token ${getJWTCookie()}`,
                            "Content-type": "application/json",
                            "Accept": "application/json"
                        }
                    })
                    .then(() => {
                        document.getElementById("confirm-button").setAttribute("data-checked", "1");
                    })
                    .catch(() => {
                        const error = document.getElementById("confirm-error");
                        error.classList.remove("hidden");
                        error.innerText = "Could not confirm worked week";
                    })
            })
            .catch(() => {
                const error = document.getElementById("confirm-error");
                error.classList.remove("hidden");
                error.innerText = "Could not update note";
            })
    })

    const companyDialog = document.getElementById("company-dialog");
    companyDialog.classList.toggle("hidden");
}

async function unconfirmWorkedWeek() {
    const error = document.getElementById("confirm-error");
    error.classList.add("hidden");

    const contracts = await obtainContractsForUser(getUserId())
    if (contracts === null) {
        const error = document.getElementById("confirm-error");
        error.classList.remove("hidden");
        error.innerText = "Could not unconfirm worked week";
        return;
    }

    contracts.forEach(c => {
        fetch("/api/users/" + getUserId() + "/contracts/" + c.contract.id + "/worked/" + getSelectedYear() + "/" + getSelectedWeek() + "/confirm",
            {
                method: "DELETE",
                headers: {
                    'authorization': `token ${getJWTCookie()}`,
                    "Content-type": "application/json",
                    "Accept": "application/json"
                }
            })
            .then((res) => {
                if (res.status === 200) document.getElementById("confirm-button").setAttribute("data-checked", "0");
                else {
                    const error = document.getElementById("confirm-error");
                    error.classList.remove("hidden");
                    error.innerText = "Could not unconfirm worked week, because the week has passed";
                }
            })
            .catch(() => {
                const error = document.getElementById("confirm-error");
                error.classList.remove("hidden");
                error.innerText = "Could not unconfirm worked week";
            })
    })
}

function getSuggestionContractId() {
    const search = new URLSearchParams(location.search);
    if (!search.has("suggestion")) {
        return null;
    }

    return search.get("suggestion").split("!")[0];
}

function getSuggestionYear() {
    const search = new URLSearchParams(location.search);
    if (!search.has("suggestion")) {
        return null;
    }

    return search.get("suggestion").split("!")[1];
}

function getSuggestionWeek() {
    const search = new URLSearchParams(location.search);
    if (!search.has("suggestion")) {
        return null;
    }

    return search.get("suggestion").split("!")[2];
}

async function updatePage(contracts) {
    const entries = document.getElementById("entries");
    const suggestionEntries = document.getElementById("suggestion-entries");

    const suggestionId = getSuggestionContractId();
    const showSuggestion = suggestionId !== null;

    const suggestionDialog = document.getElementById("suggestion");
    suggestionDialog.classList.toggle("hidden", !showSuggestion);

    if (showSuggestion) {
        const workedHours = await fetchSheet(getUserId(), { id: suggestionId }, getSuggestionYear(), getSuggestionWeek(), true);

        suggestionEntries.innerText = "";
        if (workedHours !== null) {
            const suggestionRole = document.getElementById("suggestion-role");
            suggestionRole.innerText = workedHours.contract.role;

            for (const hour of workedHours.hours) {
                suggestionEntries.append(createEntry(hour, workedHours.contract, workedHours.status !== "NOT_CONFIRMED", workedHours.status === "APPROVED", workedHours.status, getSuggestionYear(), getSuggestionWeek(), true))
            }
        } else {
            suggestionDialog.classList.toggle("hidden", true);
        }
    }

    const workEntries = [];
    let confirmed = true;
    let workedHoursCreated = false;
    for (const contract of contracts) {
        const workedHours = await fetchSheet(getUserId(), contract, getSelectedYear(), getSelectedWeek());
        if (workedHours === null) continue;

        workedHoursCreated = true;
        if (workedHours.status === "NOT_CONFIRMED") confirmed = false;

        for (const hour of workedHours.hours) {
            workEntries.push({hour, contract: contract.contract, workedWeek: workedHours });
        }
    }

    if (!workedHoursCreated && confirmed) {
        confirmed = false;
    }

    const confirm = document.getElementById("confirm-button");
    confirm.setAttribute("data-checked", confirmed ? "1" : "0");

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
        entries.appendChild(createEntry(workEntry.hour, workEntry.contract, workEntry.workedWeek.status !== "NOT_CONFIRMED", workEntry.workedWeek.status === "APPROVED", workEntry.workedWeek.status, getSelectedWeek(), getSelectedYear()))
    }
    
    if (workEntries === null || workEntries.length < 1){
        const noInvoices = document.createElement("div");
        noInvoices.classList.add("text-text", "font-bold", "w-full", "flex", "justify-center", "my-2");
        noInvoices.innerText = "No entries";
        entries.append(noInvoices)
    }
}

function createEntry(entry, contract, confirmed, approved, status, week, year, forSuggestion = false) {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-primary", "px-4", status === "NOT_CONFIRMED" || status === "CONFIRMED" ? "py-3" : "py-2", "relative", "flex", "justify-between");
    entryContainer.setAttribute("contract-id", contract.id)
    entryContainer.setAttribute("data-id", entry.id)
    entryContainer.setAttribute("data-week", week)
    entryContainer.setAttribute("data-year", year)
    entryContainer.setAttribute("data-day", entry.day)

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "grid-cols-[1fr_3fr]", "sm:grid-cols-[1fr_1fr_2fr_5fr_1fr]", "grid", "items-center");
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
    role.classList.add("text-text", "sm:col-span-1", "col-span-2");
    role.innerText = contract.role;
    entryInfo.appendChild(role);

    const description = document.createElement("div");
    description.classList.add("text-text", "sm:col-span-1", "col-span-2");
    description.innerText = entry.work;
    entryInfo.appendChild(description);

    const statusContainer = document.createElement("div");
    statusContainer.classList.add("w-full", "flex", "justify-end", "sm:col-span-1", "col-span-2")
    entryInfo.appendChild(statusContainer);

    if (!(status === "NOT_CONFIRMED" || status === "CONFIRMED")) {
        const statusElement = document.createElement("div");
        statusElement.classList.add("rounded-xl", status === "APPROVED" ? "bg-accent-success" : "bg-[#FD8E28]", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center", "items-center")
        statusContainer.appendChild(statusElement);

        const img = document.createElement("img");
        img.alt = "checkmark";
        img.src = `/static/icons/${status === "APPROVED" ? "checkmark" : "light-white"}.svg`;
        img.classList.add("w-4", "h-4")
        statusElement.append(img);

        if (status === "SUGGESTED" && !forSuggestion) {
            entryContainer.classList.add("cursor-pointer");
            entryContainer.addEventListener("click", async () => {
                const newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?suggestion=' + contract.id + `!${getSelectedYear()}!${getSelectedWeek()}`;

                if (history.pushState) {
                    window.history.pushState({ path: newUrl }, '', newUrl);
                    const contracts = await updateContracts();
                    await updatePage(contracts);
                } else {
                    window.location.href = newUrl;
                }
            })
        }
    }

    const editContainer = document.createElement("div");
    editContainer.classList.add("flex", "items-center", "flex-col", "sm:flex-row", "justify-center", "gap-5");
    entryContainer.appendChild(editContainer);

    if (!confirmed) {
        const edit1 = document.createElement("button");
        edit1.classList.add("edit-button");
        edit1.setAttribute("id", "edit1")
        edit1.addEventListener("click", () => toggleEdit(edit1));
        editContainer.appendChild(edit1);

        const edit2 = document.createElement("button");
        edit2.classList.add("edit-button");
        edit2.addEventListener("click", () => deleteWorkedFromServer(getUserId(), entryContainer.getAttribute("contract-id"), entryContainer.getAttribute("data-id")));
        editContainer.appendChild(edit2);

        const image1 = document.createElement("img");
        image1.classList.add("h-6", "w-6");
        image1.src = "/static/icons/pencil.svg"
        edit1.appendChild(image1);

        const image2 = document.createElement("img");
        image2.classList.add("h-5", "w-5");
        image2.src = "/static/icons/bin.svg"
        edit2.appendChild(image2);
    }

    return entryContainer;
}

function obtainContractsForUser(uid) {
    return fetch("/api/users/" + uid + "/contracts", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(() => null);
}

function getSelectedWeek() {
    const header = document.getElementById("week");
    return header.getAttribute("data-week").toString();
}

function getSelectedYear() {
    const header = document.getElementById("week");
    return header.getAttribute("data-year").toString();
}

function fetchSheet(userid, contract, year, week, suggestion = false) {
    return fetch(`/api/users/${userid}/contracts/${contract.id}/worked/${year}/${week}?${getQueryParams(suggestion)}`, {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(() => null);
}

function getQueryParams(suggestion = false) {
    const order = getOrder(suggestion);
    return `user=true&contract=true&hours=true${order.length > 0 ? `&order=${order}` : ""}`
}

function getOrder(suggestion = false) {
    const date = document.getElementById(suggestion ? "suggestion-date" : "date");
    const dateSelected = date.getAttribute("data-selected");

    const hours = document.getElementById(suggestion ? "suggestion-hours" : "hours");
    const hoursSelected = hours.getAttribute("data-selected");

    let order = "";
    if (dateSelected > 0) {
        order += "hours.day:" + (dateSelected === "1" ? "asc" : "desc");
    } else if (hoursSelected > 0) {
        order += "hours.minutes:" + (hoursSelected === "1" ? "asc" : "desc");
    }

    return order;
}

function approveSuggestion(contractId, year, week, token) {
    fetch(`/api/users/${getUserId()}/contracts/${contractId}/worked/${year}/${week}/suggestions?${getQueryParams(true)}`, {
        method: 'POST',
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => {
            if (res.status !== 200) throw new Error();

            const newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;

            if (history.pushState) {
                window.history.pushState({ path: newUrl }, '', newUrl);
                const contracts = await updateContracts();
                await updatePage(contracts);
            } else {
                window.location.href = newUrl;
            }
        })
        .catch(() => null);
}

function rejectSuggestion(contractId, year, week, token) {
    fetch(`/api/users/${getUserId()}/contracts/${contractId}/worked/${year}/${week}/suggestions?${getQueryParams(true)}`, {
        method: 'DELETE',
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => {
            if (res.status !== 200) throw new Error();

            const newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;

            if (history.pushState) {
                window.history.pushState({ path: newUrl }, '', newUrl);
                const contracts = await updateContracts();
                await updatePage(contracts);
            } else {
                window.location.href = newUrl;
            }
        })
        .catch(() => null);
}

async function submitForm() {
    const dayInput = document.getElementById("day-header");
    const hoursInput = document.getElementById('hours-input');
    const positionInput = document.getElementById("position-header")
    const descriptionInput = document.getElementById('description-input');

    const formData = {
        day: parseInt(dayInput.getAttribute("data-day")),
        minutes: hoursInput.value * 60,
        work: descriptionInput.value
    };

    if (validateForm(formData, positionInput.getAttribute("data-id")) === false) {
        const contracts = await updateContracts();
        await updatePage(contracts);
        return;
    }

    const error = document.getElementById("submit-error");
    error.classList.add("hidden");
    sendFormDataToServer(getUserId(), positionInput.getAttribute("data-id").toString(), formData);
}

function sendFormDataToServer(uid, ucid, formData) {
    fetch("/api/users/" + uid + "/contracts/" + ucid + "/worked/" + getSelectedYear() + "/" + getSelectedWeek(),
        {
            method: "POST",
            body: JSON.stringify(formData),
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                "Content-type": "application/json",
                "Accept": "application/json"
            }
        })
        .then(async response => {
            const contracts = await updateContracts();
            await updatePage(contracts);

            if (!response.ok) throw new Error();
        })
        .catch(() => {
            const error = document.getElementById("submit-error");
            error.classList.remove("hidden");
            error.innerText = "Could not submit hours. Make sure that the week has not been confirmed yet.";
        })
}

function deleteWorkedFromServer(uid, ucid, hid) {
    fetch("/api/users/" + uid + "/contracts/" + ucid + "/worked/" + getSelectedYear() + "/" + getSelectedWeek(),
        {
            method: "DELETE",
            body: hid.toString(),
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'accept-type': 'application/json'
            }
        })
        .then(async response => {
            const contracts = await updateContracts();
            await updatePage(contracts);

            if (!response.ok) throw new Error();
        })
        .catch(() => null);

}

async function submitEdittedForm(data) {

    if (validateEdittedForm(data) === false) {
        return false;
    }

    let json = {
        day: data.day, minutes: data.minutes, work: data.work, id: data.id
        //position: data.position
    }
    const updated = await fetch("/api/users/" + getUserId() + "/contracts/" + data.ucid + "/worked/" + data.year + "/" + data.week,
        {
            method: "PUT",
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

function validateForm(formData, position) {
    console.log(formData, position)
    if (formData.day < 0 || formData.day > 6 || formData.minutes === '' || formData.work === '' || position === null) {
        const error = document.getElementById("submit-error");
        error.classList.remove("hidden");
        error.innerText = "Please fill in all inputs"
        return false;
    }

    return true;
}

function validateEdittedForm(formData) {
    if (formData.minutes === '' || formData.work === '') {
        const error = document.getElementById("edit-error");
        error.classList.remove("hidden");
        error.innerText = "Please fill in all inputs"
        return false;
    }

    return true;
}

async function toggleEdit(button) {
    const entry = button.parentNode.parentNode;
    const textElements = entry.querySelectorAll('.text-text');
    const editButton = entry.querySelector('.edit-button');

    textElements.forEach((element, index) => {
        if (index !== 2 && index !== 0) {
            const isEditable = element.contentEditable === 'true';
            element.contentEditable = !isEditable;
        }
    });

    const isEditing = entry.classList.contains('editing');
    entry.classList.toggle('editing', !isEditing);
    editButton.innerHTML = isEditing ? '<img src="/static/icons/pencil.svg" class="h-6 w-6" alt="pencil" />' : '<img src="/static/icons/checkmark.svg" class="h-6 w-6" alt="arrow" />';

    if (isEditing) {
        // Submission logic here
        const updatedData = {
            id: entry.getAttribute("data-id"),
            day: entry.getAttribute("data-day"),
            minutes: parseFloat(textElements[1].textContent) * 60,
            // position: textElements[2].textContent,
            work: textElements[3].textContent,
            ucid: entry.getAttribute("contract-id"),
            week: entry.getAttribute("data-week"),
            year: entry.getAttribute("data-year")

        };

        // Send the updatedData to the server or perform any necessary actions
        if (!(await submitEdittedForm(updatedData))) {
            textElements.forEach((element, index) => {
                if (index !== 2 && index !== 0) {
                    const isEditable = element.contentEditable === 'true';
                    element.contentEditable = !isEditable;
                }
            });

            const isEditing = entry.classList.contains('editing');
            entry.classList.toggle('editing', !isEditing);
            editButton.innerHTML = isEditing ? '<img src="/static/icons/pencil.svg" class="h-6 w-6" alt="pencil" />' : '<img src="/static/icons/checkmark.svg" class="h-6 w-6" alt="arrow" />';
        } else {
            const error = document.getElementById("edit-error");
            error.classList.add("hidden");
        }
    } else {
        const error = document.getElementById("edit-error");
        error.classList.add("hidden");
    }
}

//______________________________________________DROPDOWNS______________________________________________________________
function togglePosition() {
    const dropdown = document.getElementById("position-content");
    dropdown.classList.toggle("hidden");
}

function toggleDay() {
    const dropdown = document.getElementById("day-content");
    dropdown.classList.toggle("hidden");
}

function selectPosition(option) {
    const header = document.getElementById("position-header");
    header.setAttribute('data-role', option.getAttribute("data-role"));
    header.setAttribute('data-id', option.getAttribute("data-id"));
    header.textContent = option.textContent;
    togglePosition();
}

function selectDay(option) {
    const header = document.getElementById("day-header");
    header.setAttribute('data-day', option.getAttribute("data-day"));
    header.textContent = option.textContent;
    togglePosition();
}

function getWeekDateRange(weekNumber, year) {
    // Find the first day of the year
    const startOfWeek = getDateOfISOWeek(weekNumber, year);

    // Calculate the start and end dates of the selected week
    const endOfWeek = addDays(getDateOfISOWeek(weekNumber, year), 6);

    // Format the date range
    const startDateFormatted = formatDate(startOfWeek);
    const endDateFormatted = formatDate(endOfWeek);
    return startDateFormatted + " - " + endDateFormatted;
}

function formatDate(date) {
    const day = date.getDate();
    const month = date.getMonth() + 1;
    const year = date.getFullYear();
    return day + "." + month + "." + year;
}

document.addEventListener("click", function (event) {
    const dropdown = document.getElementById("position-content");
    const button = document.getElementById("position-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});

document.addEventListener("click", function (event) {
    const dropdown = document.getElementById("day-content");
    const button = document.getElementById("day-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});


//__________________________________FILTERS___________________________________________

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

// todo when pencil clicked, bin should change into cross button
// todo when bin clicked, be asked to confirm your action
// todo alert should dissapear when other week is selected
// todo when submit button clicked, input fields should go blank
