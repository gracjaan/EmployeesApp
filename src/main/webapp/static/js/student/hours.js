window.addEventListener("helpersLoaded", async () => {
    console.log(getUserId())
    const contracts = await obtainContractsForUser(getUserId())

    console.log(contracts)

    if (contracts === null){
      return;
    }

    const positionContent = document.getElementById('position-content');

    contracts.forEach(c => {
        const option = document.createElement('div');
        option.classList.add('py-2', 'px-4', 'hover:bg-gray-100', 'rounded-lg', 'cursor-pointer');
        option.textContent = c.contract.role;
        option.setAttribute('data-role', c.contract.role);
        option.addEventListener('click', () => selectPosition(option));
        console.log(option)
        positionContent.appendChild(option);
    });

    //fetchSheet(getUserId(), contracts)

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

function getSelectedWeek () {
    const header = document.getElementById("dropdown-header");
    const weekNumber = parseInt(header.dataset.weekNumber);
    return weekNumber;
}

function fetchSheet(userid, contracts) {
    const promises = contracts.map((contract) => {
    return fetch("/users/"+ userid + "/contracts/" + contract.id + "/worked/" + getCurrentYear() + "/" + getSelectedWeek())
        .then (response => response.json())
        .then (data => {
            html = "<div className=\"rounded-xl bg-primary p-4 relative flex justify-between\">";
            for (const item of data){
                html+=
                    "<div className=\"text-text font-bold uppercase\">" + item.day + "</div>" +
                    "<div className=\"text-text\">" + item.minutes + "</div>" +
                    "<div className=\"text-text\">" + item.position + "</div>" +
                    "<div className=\"text-text\">" + item.work + "</div>";
            }
            html += "</div>";
            document.getElementById("items").innerHTML = html;
        })
        .catch(e => console.error(e))
    });

    Promise.all(promises)
        .then (results => {
            const combinedHtml = results.join("");
            document.getElementById("items").innerHTML = combinedHtml;
        })
        .catch(e => console.error(e))
}

function submitForm () {
    const dateInput = document.getElementById('date');
    const hoursInput = document.getElementById('hours');
    const positionInput = document.getElementById('position');
    const descriptionInput = document.getElementById('description');

    const formData = {
        date: dateInput.value,
        hours: hoursInput.value,
        position: positionInput.value,
        description: descriptionInput.value
    };

    console.log(formData)

    if (validateForm(formData) == false){
        return;
    }

    sendFormDataToServer(formData, getUserId(), );
}

function getCurrentYear () {
    const currentDate = new Date();
    return currentDate.getFullYear();
}

function getCurrentWeek () {
    const currentDate = new Date();
    const startDate = new Date(currentDate.getFullYear(), 0, 1);
    const days = Math.floor((currentDate - startDate) /
        (24 * 60 * 60 * 1000));

    const weekNumber = Math.ceil(days / 7);

    return weekNumber;
}

function sendFormDataToServer (formData, uid, ucid) {
    fetch ("/users/"+ uid + "/contracts/" + ucid + "/worked/" + getCurrentYear() + "/" + getCurrentWeek(),
        {
            method: "POST",
            body: JSON.stringify(formData),
            headers: {
                "Content-type" : "application/json",
                "Accept" : "application/json"
            }
        })
        .then (response => console.log("Form submitted successfully"))
        .catch(e => console.error(e))
}

function submitEdittedForm (uid, ucid, year, week, date, hours, position, description) {

    if (validateForm() == false){
        return;
    }

    let json = {date: date, hours: hours, position: position, description: description}
    fetch ("/users/"+ uid + "/contracts/" + ucid + "/worked/" + year + "/" + week,
        {
            method: "PUT",
            body: JSON.stringify(json),
            headers: {
                "Content-type" : "application/json",
                "Accept" : "application/json"
            }
        })
        .catch(e => console.error(e))
}

function validateForm (formData) {
    if (formData.date === '' || formData.hours === '' || formData.position === '' || formData.description === '') {
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
    range.textContent = dateRange;
    toggleWeek();
}

function selectPosition(option) {
    const header = document.getElementById("position-header");
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

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("dropdown-content");
    const button = document.getElementById("dropdown-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("position-content");
    const button = document.getElementById("position-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});