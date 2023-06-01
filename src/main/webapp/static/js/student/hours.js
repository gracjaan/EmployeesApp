window.addEventListener("helpersLoaded", async () => {
    const contracts = await obtainContractsForUser(getUserId())

    if (contracts === null){
      return;
    }

    for (const contract of contracts) {
        fetchSheet(getUserId(), contract)

    }

})

function obtainContractsForUser(uid) {
    return fetch("/users/" + uid + "/contracts")
        .then(response => response.json())
        .catch(e => null);
}

function getSelectedWeek () {
    const header = document.getElementById("dropdown-header");
    const weekNumber = parseInt(header.dataset.weekNumber);
    return weekNumber;
}

function fetchSheet(userid, contract, week, ) {
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