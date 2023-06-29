window.addEventListener("helpersLoaded", async () => {
    const date = document.getElementById("date");
    date.addEventListener("change", (e) => {
        updatePage();
    })

    const hours = document.getElementById("hours");
    hours.addEventListener("change", (e) => {
        updatePage();
    })

    const contract = document.getElementById("contract");
    contract.addEventListener("change", (e) => {
        updatePage();
    })

    await updatePage();
})

async function updatePage() {
    const rejectedWeeks = await getRejectedWeeks();

    if (rejectedWeeks === null) {
        alertPopUp("Could not load rejected weeks", false);
        return;
    }

    const rejectedWeeksElement = document.getElementById("rejected-weeks");
    rejectedWeeksElement.innerText = ""

    if (rejectedWeeks.length === 0){
        const noRejectedWeeks = document.createElement("div");
        noRejectedWeeks.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noRejectedWeeks.innerText = "No requests";
        rejectedWeeksElement.append(noRejectedWeeks)
    }

    for (const rejectedWeek of rejectedWeeks) {
        rejectedWeeksElement.append(createRejectedWeek(rejectedWeek));
    }
}

//Fetches rejected weeks
async function getRejectedWeeks() {
    return await fetch(`/api/staff/rejects?${getQueryParams()}`,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json()).catch(() => null);
}

function getQueryParams() {
    const order = getOrder();
    return `user=true&company=true&contract=true&totalHours=true${order.length > 0 ? `&order=${order}`: ""}`
}
//Orders the list based on the staff preference
function getOrder() {
    const date = document.getElementById("date");
    const dateSelected = date.getAttribute("data-selected");

    const hours = document.getElementById("hours");
    const hoursSelected = hours.getAttribute("data-selected");

    const contract = document.getElementById("contract");
    const contractSelected = contract.getAttribute("data-selected");

    let order = "";
    if (dateSelected > 0) {
        order += "worked_week.week:" +  (dateSelected === "1" ? "asc" : "desc") + ",hours.day:" + (dateSelected === "1" ? "asc" : "desc");
    } else if (hoursSelected > 0) {
        order += "hours.minutes:" + (hoursSelected === "1" ? "asc" : "desc");
    } else if (contractSelected > 0) {
        order += "contract.role:" + (contractSelected === "1" ? "asc" : "desc");
    }

    return order;
}
//Formats rejected week element
function createRejectedWeek(rejectedWeek) {
    const listElement = document.createElement("li");

    const linkElement = document.createElement("a");
    linkElement.href = "/request?worked_week=" + rejectedWeek.id;
    listElement.append(linkElement);

    const weekDiv = document.createElement("div");
    weekDiv.setAttribute("data-selected", '0')
    weekDiv.classList.add("justify-between", "flex", "flex-row", "data-[selected='1']:border-white", "border-2", "border-primary", "block", "bg-primary", "rounded-xl", "w-full", "px-4", "py-2", "overflow-y-auto", "scrollbar-custom", "scrollbar-track-text", "scrollbar-rounded-xl", "scrollbar-thumb-background", "max-h-[5rem]");
    linkElement.append(weekDiv);

    const employee = document.createElement("p")
    employee.classList.add("text-text");
    employee.innerText = getName(rejectedWeek.user.firstName, rejectedWeek.user.lastName, rejectedWeek.user.lastNamePrefix);
    weekDiv.append(employee);

    const company = document.createElement("p")
    company.classList.add("text-text");
    company.innerText = rejectedWeek.company.name;
    weekDiv.append(company);

    const contract = document.createElement("p")
    contract.classList.add("text-text");
    contract.innerText = rejectedWeek.contract.role;
    weekDiv.append(contract);

    const week = document.createElement("p")
    week.classList.add("text-text");
    week.innerText = "Week " + rejectedWeek.week;
    weekDiv.append(week);

    const hours = document.createElement("p")
    hours.classList.add("text-text");
    hours.innerText = ((rejectedWeek.totalMinutes) / 60).toString() + "H";
    weekDiv.append(hours);

    return listElement;
}

function alertPopUp(message, positive) {
    let confirmation = document.getElementById("alertPopup");
    let accent = document.getElementById("accent")
    let image = document.getElementById("confirmationIcon")
    let p = document.getElementById("popUpAlertParagraph")
    p.innerText = message

    if (positive){
        accent.classList.add("bg-accent-success")
        image.src = "/static/icons/checkmark.svg"
    }
    else{
        accent.classList.add("bg-accent-fail")
        image.src = "/static/icons/white-cross.svg"
    }
    confirmation.classList.remove("hidden");
    setTimeout(function (){
            confirmation.classList.add("hidden");
            if (positive){
                accent.classList.remove("bg-accent-success")
            }
            else{
                accent.classList.remove("bg-accent-fail")
            }
        }, 2000
    );

}






