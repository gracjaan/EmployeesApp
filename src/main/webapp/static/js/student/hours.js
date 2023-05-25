window.addEventListener("load", () => {
    obtainContractsForUser()
    fetchSheet()
})

function parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    })
        .join(''));

    return JSON.parse(jsonPayload);
}

function obtainContractsForUser(uid) {
    return fetch("/users/" + uid + "/contracts")
        .then(response => response.json())
        .then(data => {
            return data;
        })
        .catch(e => console.error(e));
}

function fetchSheet(uid, ucid, year, week) {
    return fetch("/users/"+ uid + "/contracts/" + ucid + "/worked/" + year + "/" + week)
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
}

function submitForm (uid, ucid, year, week, date, hours, position, description) {

    if (validateForm() == false){
        return;
    }

    let json = {date: date, hours: hours, position: position, description: description}
    fetch ("/users/"+ uid + "/contracts/" + ucid + "/worked/" + year + "/" + week,
        {
            method: "POST",
            body: JSON.stringify(json),
            headers: {
                "Content-type" : "application/json",
                "Accept" : "application/json"
            }
        })
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

function validateForm () {
    const dateInput = document.getElementById('date').value;
    const hoursInput = document.getElementById('hours').value;
    const positionInput = document.getElementById('position').value;
    const descriptionInput = document.getElementById('student-last-prefix').value;

    if (dateInput === '' || hoursInput === '' || positionInput === '' || descriptionInput === '') {
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
        submitEdittedForm();
    }
}