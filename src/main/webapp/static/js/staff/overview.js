window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();
    const users = await getStudents();

    if (companies === null || users === null) {
        alertPopUp("Could not load users or companies", false);
        return;
    }

    const usersElement = document.getElementById("users");
    //usersElement.innerText = "";

    const companiesElement = document.getElementById("companies");
    companiesElement.innerText = "";

    for (const user of users) {
        usersElement.append(createUser(user));
    }
    for (const company of companies) {
        companiesElement.append(createCompany(company));
    }
});


function createUser(user) {
    const li = document.createElement("li");
    const itemContainer = document.createElement("div");
    itemContainer.classList.add("flex", "flex-row", "justify-between", "bg-primary", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4", "my-2", "items-center");
    const nameDiv = document.createElement("a");
    nameDiv.href = "/info-user?id=" + user.id
    // nameDiv.addEventListener("click", () => {
    //     location.href = "/info-user?id=" + user.id
    // })

    const name = document.createElement("p");
    name.classList.add("text-text", "font-montserrat");
    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    nameDiv.append(name);
    const statusDiv = document.createElement("div");
    const buttonDiv = document.createElement("div");
    buttonDiv.classList.add("flex", "flex-row", "gap-2");

    const disableDiv = document.createElement("div");
    disableDiv.classList.add("rounded-xl", "bg-accent-fail", "p-2", "items-center", "text-white", "w-fit", "flex", "justify-center", "gap-2", "group");

    const crossImage = document.createElement("img");
    crossImage.src = "/static/icons/white-cross.svg";
    crossImage.classList.add("h-4", "w-4")
    crossImage.alt = "disable"
    disableDiv.append(crossImage);

    const disableTextDiv = document.createElement("div");
    disableTextDiv.classList.add("cursor-pointer", "justify-center", "items-center", "group-hover:flex", "hidden", "text-sm", "text-text");
    const disableText = document.createElement("p");
    disableText.innerText = "Disable User";
    disableText.classList.add("whitespace-nowrap");
    disableTextDiv.append(disableText);
    disableDiv.append(disableTextDiv);

    itemContainer.append(disableDiv);

    const enableDiv = document.createElement("div");
    enableDiv.classList.add("cursor-pointer", "rounded-xl", "bg-accent-success", "p-2", "items-center", "text-white", "w-fit", "flex", "justify-center", "gap-2", "group");
    const checkmarkImage = document.createElement("img");
    checkmarkImage.src = "/static/icons/checkmark.svg";
    checkmarkImage.classList.add("h-4", "w-4")
    checkmarkImage.alt = "enable"
    enableDiv.append(checkmarkImage)
    const enableTextDiv = document.createElement("div");
    enableTextDiv.classList.add("justify-center", "items-center", "group-hover:flex", "hidden", "text-sm", "text-text");
    const enableText = document.createElement("p");
    enableText.innerText = "Enable User";
    enableText.classList.add("whitespace-nowrap");
    enableTextDiv.append(enableText);
    enableDiv.append(enableTextDiv);

    itemContainer.append(enableDiv);

    enableDiv.addEventListener("click", async () => {
        displayPopUpUser(user, true, enableDiv, disableDiv)
    })
    buttonDiv.append(enableDiv);

    disableDiv.addEventListener("click", async () => {
        displayPopUpUser(user, false, enableDiv, disableDiv)
    })
    buttonDiv.append(disableDiv);

    if(user.active === true) {
        enableDiv.classList.add("hidden");
        disableDiv.classList.remove("hidden");
    } else {
        disableDiv.classList.add("hidden");
        enableDiv.classList.remove("hidden");
    }

    itemContainer.append(nameDiv);
    itemContainer.append(statusDiv);
    itemContainer.append(buttonDiv);
    li.append(itemContainer);

    return li;
}

function enableUser(user){
    user.active = true;
    return fetch("/api/users/" + user.id, {
        method: 'put',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    }).then(async res => ({
        status: res.status,
        json: await res.json()
    }))
        .catch(() => null)
}

function disableUser(user){
    user.active = false;
    return fetch("/api/users/" + user.id, {
        method: 'delete',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(async res => ({
        status: res.status,
        json: await res.json()
    })
        .catch(() => null))
}
function createCompany(company) {
    const li = document.createElement("li");

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("flex", "flex-row", "justify-between", "bg-primary", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4", "my-2", "items-center");

    const nameDiv = document.createElement("a");
    nameDiv.href = "/info-company?id=" + company.id;

    const name = document.createElement("p");
    name.classList.add("text-text", "font-montserrat");
    name.innerText = company.name;

    nameDiv.append(name);
    itemContainer.append(nameDiv)

    const disableDiv = document.createElement("div");
    disableDiv.classList.add("rounded-xl", "bg-accent-fail", "p-2", "items-center", "text-white", "w-fit", "flex", "justify-center", "gap-2", "group");

    const crossImage = document.createElement("img");
    crossImage.src = "/static/icons/white-cross.svg";
    crossImage.classList.add("h-4", "w-4")
    crossImage.alt = "disable"
    disableDiv.append(crossImage);

    const disableTextDiv = document.createElement("div");
    disableTextDiv.classList.add("cursor-pointer", "justify-center", "items-center", "group-hover:flex", "hidden", "text-sm", "text-text");
    const disableText = document.createElement("p");
    disableText.innerText = "Disable Company";
    disableText.classList.add("whitespace-nowrap");
    disableTextDiv.append(disableText);
    disableDiv.append(disableTextDiv);

    itemContainer.append(disableDiv);

    const enableDiv = document.createElement("div");
    enableDiv.classList.add("cursor-pointer","rounded-xl", "bg-accent-success", "p-2", "items-center", "text-white", "w-fit", "flex", "justify-center", "gap-2", "group");
    const checkmarkImage = document.createElement("img");
    checkmarkImage.src = "/static/icons/checkmark.svg";
    checkmarkImage.classList.add("h-4", "w-4")
    checkmarkImage.alt = "enable"
    enableDiv.append(checkmarkImage)
    const enableTextDiv = document.createElement("div");
    enableTextDiv.classList.add("justify-center", "items-center", "group-hover:flex", "hidden", "text-sm", "text-text");
    const enableText = document.createElement("p");
    enableText.innerText = "Enable Company";
    enableText.classList.add("whitespace-nowrap");
    enableTextDiv.append(enableText);
    enableDiv.append(enableTextDiv);

    itemContainer.append(enableDiv);

    disableDiv.addEventListener("click", async () => {
        displayPopUpCompany(company, false, enableDiv, disableDiv)
    })

    enableDiv.addEventListener("click", async () => {
        displayPopUpCompany(company, true, enableDiv, disableDiv)
    })

    if(company.active === true) {
        enableDiv.classList.add("hidden")
        disableDiv.classList.remove("hidden")
    } else {
        disableDiv.classList.add("hidden")
        enableDiv.classList.remove("hidden")
    }

    li.append(itemContainer);

    return li;
}

function displayPopUpUser(user, enabling, enableDiv, disableDiv) {
    const popUpElement = document.getElementById("popUp");
    const confirmButton = document.getElementById("confirmButton")
    const cancelButton = document.getElementById("cancelButton")
    popUpElement.classList.remove("hidden");

    const paragraph = document.getElementById("popUpParagraph");
    if (enabling) {
        paragraph.innerText = "Are you sure you want to enable " + getName(user.firstName, user.lastName, user.lastNamePrefix) + "'s account?";
    }
    else{
        paragraph.innerText = "Are you sure you want to disable " + getName(user.firstName, user.lastName, user.lastNamePrefix) + "'s account?";
    }

    confirmButton.addEventListener("click", async () => {
        if (enabling) {
            enableUser(user)
            enableDiv.classList.add("hidden")
            disableDiv.classList.remove("hidden")
        }
        else{
            disableUser(user)
            disableDiv.classList.add("hidden")
            enableDiv.classList.remove("hidden")
        }
        popUpElement.classList.add("hidden")

    })

    cancelButton.addEventListener("click", async () => {
        popUpElement.classList.add("hidden")
    })
}

function displayPopUpCompany(company, enabling, enableDiv, disableDiv) {
    const popUpElement = document.getElementById("popUp");
    const confirmButton = document.getElementById("confirmButton")
    const cancelButton = document.getElementById("cancelButton")
    popUpElement.classList.remove("hidden");

    const paragraph = document.getElementById("popUpParagraph");
    if (enabling) {
        paragraph.innerText = "Are you sure you want to enable " + company.name + "'s account?";
    }
    else{
        paragraph.innerText = "Are you sure you want to disable " + company.name + "'s account?";
    }

    confirmButton.addEventListener("click", async () => {
        if (enabling) {
            enableCompany(company)
            enableDiv.classList.add("hidden")
            disableDiv.classList.remove("hidden")
        }
        else{
            disableCompany(company)
            disableDiv.classList.add("hidden")
            enableDiv.classList.remove("hidden")
        }
        popUpElement.classList.add("hidden")

    })

    cancelButton.addEventListener("click", async () => {
        popUpElement.classList.add("hidden")
    })
}

function enableCompany(company){
    company.active = true;
    return fetch("/api/companies/" + company.id, {
        method: 'put',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(company)
    }).then(async res => ({
        status: res.status,
        json: await res.json()
    }))
        .catch(() => null)
}
function disableCompany(company){
    company.active = false;
    return fetch("/api/companies/" + company.id, {
        method: 'delete',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(async res => ({
        status: res.status,
        json: await res.json()
    }))
        .catch(() => null)
}

async function getStudents() {
    return await fetch(`/api/users${getQueryParamsUsers()}`,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json()).catch(() => null);
}

async function getCompanies() {
    return await fetch(`/api/companies${getQueryParamsCompany()}`,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
    .catch(() => null);
}


function getOrderCompany() {
    const nameCompany = document.getElementById("name-company");
    const companySelected = nameCompany.getAttribute("data-selected");

    let order = "";
    if (name > 0) {
        order += "company.id:" + (nameSelected === "1" ? "asc" : "desc");
    }
    return order;
}
function getOrderUsers() {
    const nameUser = document.getElementById("name-user");
    const nameSelected = nameUser.getAttribute("data-selected");
    let order = "";
    if (name > 0) {
        order += "user.last_name:" + (nameSelected === "1" ? "asc" : "desc");
    }
    return order;
}
function getQueryParamsCompany() {
    const order = getOrderCompany();
    return `${order.length > 0 ? `order=${order}`: ""}`
}
function getQueryParamsUsers() {
    const order = getOrderUsers();
    return `${order.length > 0 ? `order=${order}`: ""}`
}

function searchUser() {
    let input = document.getElementById('searchUsers');
    let filter = input.value.toUpperCase();
    let ol = document.getElementById("users");
    let li = ol.getElementsByTagName('li');

    for (let i = 0; i < li.length; i++) {
        let p = li[i].getElementsByTagName("p")[0];
        let txtValue = p.textContent || p.innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            li[i].style.display = "";
        } else {
            li[i].style.display = "none";
        }
    }
}

function searchCompany(){
    let input = document.getElementById('searchCompany');
    let filter = input.value.toUpperCase();
    let ol = document.getElementById("companies");
    let li = ol.getElementsByTagName('li');

    for (let i = 0; i < li.length; i++) {
        let p = li[i].getElementsByTagName("p")[0];
        let txtValue = p.textContent || p.innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            li[i].style.display = "";
        } else {
            li[i].style.display = "none";
        }
    }
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