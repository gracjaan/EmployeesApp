function fetchSheet(uid, ucid, year, week) {
    fetch("/users/"+ uid + "/contracts/" + ucid + "/worked/" + year + "/" + week)
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
