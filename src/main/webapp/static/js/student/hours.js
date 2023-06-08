window.addEventListener("helpersLoaded", async () => {
    const date = document.getElementById("date");
    date.addEventListener("click", () => select("date"));

    const hours = document.getElementById("hours");
    hours.addEventListener("click", () => select("hours"));

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

    setupWeeks();
});

function createDayItem(day, dayName) {
    const container = document.createElement("div");
    container.classList.add("py-2", "px-4", "hover:bg-gray-100", "cursor-pointer");
    container.innerText = dayName;
    container.setAttribute("data-day", day);
    return container;
}

function setupWeeks() {
    const dropdown = document.getElementById("dropdown-content");
    dropdown.addEventListener("click", async (e) => {
        const element = e.target;
        if (!element.hasAttribute("data-week-number")) return;

        await selectWeek(element);
    });
    dropdown.addEventListener("scroll", () => {
        if (Math.abs(dropdown.scrollHeight - dropdown.scrollTop - dropdown.clientHeight) < 1) {
            let last = dropdown.lastElementChild;
            while (!last.hasAttribute("data-week-number")) {
                const index = Array.from(dropdown.children).indexOf(last);
                if (index === 0) return;

                last = dropdown.children.item(index - 1);
            }

            const year = parseInt(last.getAttribute("data-year"))
            const week = parseInt(last.getAttribute("data-week-number"))

            addWeeks(5, year, week);
        }
    })

    dropdown.innerText = "";
    addWeeks(5, getCurrentYear(), getCurrentWeek() + 1);
    dropdown.children.item(0).click();
}

function addWeeks(amount, lastYear, lastWeek) {
    const dropdown = document.getElementById("dropdown-content");

    while (amount-- > 0) {
        lastWeek--;

        if (lastWeek < 1) {
            lastYear--;
            lastWeek = weeksInYear(lastYear);
            dropdown.appendChild(createYearItem(lastYear));
        }

        dropdown.appendChild(createWeekItem(lastYear, lastWeek));
    }
}

function createYearItem(year) {
    const container = document.createElement("div");
    container.classList.add("py-2", "px-4", "font-bold");
    container.innerText = year;

    return container;
}

function createWeekItem(year, week) {
    const container = document.createElement("div");
    container.classList.add("py-2", "px-4", "hover:bg-gray-100", "cursor-pointer");
    container.innerText = "Week " + week;
    container.setAttribute("data-year", year);
    container.setAttribute("data-week-number", week);

    return container;
}

async function updateContracts() {
    const contracts = await obtainContractsForUser(getUserId())

    if (contracts === null) {
        return null;
    }

    const positionContent = document.getElementById('position-content');
    positionContent.innerText = "";

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

async function confirmWorkedWeek () {
    const contracts = await obtainContractsForUser(getUserId())
    contracts.forEach(c => {
        fetch("/earnit/api/users/" + getUserId() + "/contracts/" + c.contract.id + "/worked/" + getCurrentYear() + "/" + getCurrentWeek() + "/confirm",
            {
                method: "POST",
                body: JSON.stringify({confirmed: true}),
                headers: {
                    'authorization': `token ${getJWTCookie()}`,
                    "Content-type": "application/json",
                    "Accept": "application/json"
                }
            })
            .then( response => {
                alert("The worked week was confirmed")
            })
            .catch(e => alert("Could not submit hours"))

    })
}

async function updatePage(contracts) {
    const entries = document.getElementById("entries");
    entries.innerText = "";

    for (const contract of contracts) {
        const workedHours = await fetchSheet(getUserId(), contract);
        if (workedHours === null) continue;

        for (const hour of workedHours.hours) {
            entries.appendChild(createEntry(hour, contract.contract, getSelectedWeek(), getSelectedYear()))
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
    edit1.addEventListener("click", () => toggleEdit(edit1));
    editContainer.appendChild(edit1);

    const edit2 = document.createElement("button");
    edit2.classList.add("edit-button");
    edit2.addEventListener("click", () => deleteWorkedFromServer(getUserId(), entryContainer.getAttribute("contract-id")));
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

function obtainContractsForUser(uid) {
    return fetch("/earnit/api/users/" + uid + "/contracts", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

function getSelectedWeek() {
    const header = document.getElementById("dropdown-header");
    return header.getAttribute("data-week-number").toString();
}

function getSelectedYear() {
    const header = document.getElementById("dropdown-header");
    return header.getAttribute("data-year").toString();
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

    sendFormDataToServer(getUserId(), positionInput.getAttribute("data-id").toString(), formData);
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

function sendFormDataToServer(uid, ucid, formData) {
    fetch("/earnit/api/users/" + uid + "/contracts/" + ucid + "/worked/" + getSelectedYear() + "/" + getSelectedWeek(),
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
        .catch(e => alert("Could not submit hours"))
}

function deleteWorkedFromServer (uid, ucid) {
    fetch("/earnit/api/users/" + uid + "/contracts/" + ucid + "/worked/" + getSelectedYear() + "/" + getSelectedWeek(),
        {
            method: "DELETE",
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'accept-type': 'application/json'
            }
        })
        .then(async (res) => await res.json())
        .then(async (request) => {
            const contracts = await updateContracts();
            await updatePage(contracts);
        })
        .catch(() => null);

}

function submitEdittedForm(data) {

    if (validateEdittedForm(data) === false) {
        return;
    }

    let json = {day: data.day, minutes: data.minutes, position: data.position, work: data.work}
    fetch("/earnit/api/users/" + getUserId() + "/contracts/" + data.ucid + "/worked/" + data.year + "/" + data.week,
        {
            method: "PUT",
            body: JSON.stringify(json),
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                "Content-type": "application/json",
                "Accept": "application/json"
            }
        })
        .catch(e => console.error(e))
}

function validateForm(formData, position) {
    console.log(formData, position)
    if (formData.day < 0 || formData.day > 6 || formData.minutes === '' || formData.work === '' || position === null) {
        alert('Please fill in all the fields.');
        return false;
    }

    return true;
}

function validateEdittedForm(formData) {
    if (formData.minutes === '' || formData.work === '') {
        alert('Please fill in all the fields.');
        return false;
    }

    return true;
}

function toggleEdit(button) {
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
    editButton.innerHTML = isEditing ? '<img src="/earnit/static/icons/pencil.svg" class="h-6 w-6" alt="pencil" />' : '<img src="/earnit/static/icons/checkmark.svg" class="h-6 w-6" alt="arrow" />';

    if (isEditing) {
        // Submission logic here
        const updatedData = {
            day: textElements[0].textContent,
            minutes: textElements[1].textContent,
            position: textElements[2].textContent,
            work: textElements[3].textContent,
            ucid: entry.getAttribute("contract-id"),
            week: entry.getAttribute("data-week"),
            year: entry.getAttribute("data-year")

        };

        // Send the updatedData to the server or perform any necessary actions
        submitEdittedForm(updatedData);
    }
}


//______________________________________________DROPDOWNS______________________________________________________________

function toggleWeek() {
    const dropdown = document.getElementById("dropdown-content");
    dropdown.classList.toggle("hidden");
}

function togglePosition() {
    const dropdown = document.getElementById("position-content");
    dropdown.classList.toggle("hidden");
}

function toggleDay() {
    const dropdown = document.getElementById("day-content");
    dropdown.classList.toggle("hidden");
}

async function selectWeek(option) {
    const header = document.getElementById("dropdown-header");
    const range = document.getElementById("dropdown-range")
    const weekNumber = parseInt(option.dataset.weekNumber);
    const year = parseInt(option.dataset.year);
    const dateRange = getWeekDateRange(weekNumber, year);
    header.textContent = option.textContent;
    header.setAttribute("data-week-number", option.getAttribute("data-week-number"))
    header.setAttribute("data-year", option.getAttribute("data-year"))
    range.textContent = dateRange;

    const dropdown = document.getElementById("dropdown-content");
    dropdown.classList.add("hidden");

    const contracts = await updateContracts();
    await updatePage(contracts);
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
    const dropdown = document.getElementById("dropdown-content");
    const button = document.getElementById("dropdown-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !dropdown.contains(targetElement) && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});

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