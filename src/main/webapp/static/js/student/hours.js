window.addEventListener("helpersLoaded", async () => {
    const date = document.getElementById("date");
    date.addEventListener("click", () => select("date"));

    const hours = document.getElementById("hours");
    hours.addEventListener("click", () => select("hours"));

    const contracts = await updateContracts();
    await updatePage(contracts);
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

function getSelectedWeek() {
    const header = document.getElementById("dropdown-header");
    const weekNumber = header.getAttribute("data-week-number").toString();
    return weekNumber;
}

function fetchSheet(userid, contract) {
    return fetch("/earnit/api/users/" + userid + "/contracts/" + contract.id + "/worked/" + getCurrentYear() + "/" + getSelectedWeek() + "?hours=true", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

function submitForm() {
    const dateInput = document.getElementById('date-input');
    const hoursInput = document.getElementById('hours-input');
    const positionInput = document.getElementById("position-header")
    const descriptionInput = document.getElementById('description-input');

    const formData = {
        day: dateInput.value,
        minutes: hoursInput.value * 60,
        work: descriptionInput.value
    };

    console.log(formData)

    if (validateForm(formData, positionInput.getAttribute("data-id")) === false) {
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
    fetch("/earnit/api/users/" + uid + "/contracts/" + ucid + "/worked/" + getCurrentYear() + "/" + getCurrentWeek(),
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
        })
        .catch(e => alert("Could not submit hours"))
}

function submitEdittedForm(uid, ucid, year, week, date, hours, position, description) {

    if (validateForm() === false) {
        return;
    }

    let json = {date: date, hours: hours, position: position, description: description}
    fetch("/users/" + uid + "/contracts/" + ucid + "/worked/" + year + "/" + week,
        {
            method: "PUT",
            body: JSON.stringify(json),
            headers: {
                "Content-type": "application/json",
                "Accept": "application/json"
            }
        })
        .catch(e => console.error(e))
}

function validateForm(formData, position) {
    if (formData.day === '' || formData.minutes === '' || formData.work === '' || position === null) {
        alert('Please fill in all the fields.');
        return false;
    }

    return true;
}


function toggleEdit(button) {
    const entry = button.parentNode;
    const textElements = entry.querySelectorAll('.text-text');
    const editButton = entry.querySelector('.edit-button');

    textElements.forEach(element => {
        const isEditable = element.contentEditable === 'true';
        element.contentEditable = !isEditable;
    });

    const isEditing = entry.classList.contains('editing');
    entry.classList.toggle('editing', !isEditing);
    editButton.innerHTML = isEditing ? '<img src="/earnit/static/icons/pencil.svg" class="h-6 w-6" alt="pencil" />' : '<img src="/earnit/static/icons/checkmark.svg" class="h-6 w-6" alt="arrow" />';

    if (!isEditing) {
        // Submission logic here
        const updatedData = {
            date: textElements[0].textContent,
            hours: textElements[1].textContent,
            position: textElements[2].textContent,
            description: textElements[3].textContent
        };

        // Send the updatedData to the server or perform any necessary actions
        console.log(updatedData);
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

function selectWeek(option) {
    const header = document.getElementById("dropdown-header");
    const range = document.getElementById("dropdown-range")
    const weekNumber = parseInt(option.dataset.weekNumber);
    const dateRange = getWeekDateRange(weekNumber);
    header.textContent = option.textContent;
    header.setAttribute("data-week-number", option.getAttribute("data-week-number"))
    range.textContent = dateRange;
    console.log(header);

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

function getWeekDateRange(weekNumber) {
    const currentDate = new Date();
    const year = currentDate.getFullYear();

    // Find the first day of the year
    const firstDayOfYear = new Date(year, 0, 1);
    const dayOfWeek = firstDayOfYear.getDay();
    const startDate = new Date(firstDayOfYear.getTime() - dayOfWeek * 24 * 60 * 60 * 1000);

    // Calculate the start and end dates of the selected week
    const startOfWeek = new Date(startDate.getTime() + (weekNumber - 1) * 7 * 24 * 60 * 60 * 1000);
    const endOfWeek = new Date(startOfWeek.getTime() + 6 * 24 * 60 * 60 * 1000);

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

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
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