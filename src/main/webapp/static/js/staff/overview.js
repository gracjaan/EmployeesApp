window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();
    const users = await getStudents();

    if (companies === null || users === null) {
        alert("Could not load users or companies");
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


function displayPopUpUser(user, enabling) {


    const popUpElement = document.getElementById("popUp");
    popUpElement.classList.remove("hidden");
    const paragraph = document.getElementById("popUpParagraph");
        if (enabling) {
            paragraph.innerText = "Are you sure you want to enable " + getName(user.firstName, user.lastName, user.lastNamePrefix) + "'s account?";
        } else {
            paragraph.innerText = "Are you sure you want to disable " + getName(user.firstName, user.lastName, user.lastNamePrefix) + "'s account?";
        }

    const cancelButton = document.getElementById("cancelButton");
    const confirmButton = document.getElementById("confirmButton");

    cancelButton.addEventListener("click", () => {
        popUpElement.classList.add("hidden");
    })

    confirmButton.addEventListener("click", () => {
        if (enabling) {
            console.log(enableUser(user));
            popUpElement.classList.add("hidden");

            return true;
        } else {
            console.log(disableUser(user));
            popUpElement.classList.add("hidden");
            return true;
        }
        popUpElement.classList.add("hidden");
    })
}

function displayPopUpCompany(company, enabling) {
    const popUpElement = document.getElementById("popUp");
    popUpElement.classList.remove("hidden");
    const paragraph = document.getElementById("popUpParagraph");

    if (enabling) {
        paragraph.innerText = "Are you sure you want to enable " + company.name + "'s account?";
    } else {
        paragraph.innerText = "Are you sure you want to disable " + company.name + "'s account?";
    }

    const cancelButton = document.getElementById("cancelButton");
    const confirmButton = document.getElementById("confirmButton");

    cancelButton.addEventListener("click", () => {
        popUpElement.classList.add("hidden");
    })

    confirmButton.addEventListener("click", () => {
        if (enabling) {
            console.log(enableCompany(company));
            popUpElement.classList.add("hidden");

            return true;
        } else {
            console.log(disableCompany(company));
            popUpElement.classList.add("hidden");
            return true;
        }
        popUpElement.classList.add("hidden");
    })
}


function createUser(user) {
    const li = document.createElement("li");

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("flex", "flex-row", "justify-between", "bg-primary", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4", "my-2", "items-center");

    const nameDiv = document.createElement("div");
    const name = document.createElement("p");
    name.classList.add("text-text", "font-montserrat");
    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    nameDiv.append(name);

    const statusDiv = document.createElement("div");

    const buttonDiv = document.createElement("div");
    buttonDiv.classList.add("flex", "flex-row", "gap-2");


    const disableDiv = document.createElement("div");
    disableDiv.classList.add("rounded-xl", "bg-accent-fail", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center");
    const crossImage = document.createElement("img");
    crossImage.src = "/earnit/static/icons/white-cross.svg";
    crossImage.classList.add("h-4", "w-4")
    crossImage.alt = "disable"
    disableDiv.append(crossImage);

    disableDiv.addEventListener("click", async () => {
        if (await displayPopUpUser(user, false)) {
            disableDiv.classList.add("hidden")
            disableDiv.classList.remove("hidden")
        }
    })
    buttonDiv.append(disableDiv);

    const enableDiv = document.createElement("div");
    enableDiv.classList.add("rounded-xl", "bg-accent-success", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center");
    const checkmarkImage = document.createElement("img");
    checkmarkImage.src = "/earnit/static/icons/checkmark.svg";
    checkmarkImage.classList.add("h-4", "w-4")
    checkmarkImage.alt = "enable"
    enableDiv.append(checkmarkImage)

    enableDiv.addEventListener("click", async () => {
        if (await displayPopUpUser(user, true)) {
            disableDiv.classList.add("hidden");
            disableDiv.classList.remove("hidden");
        }
    })
    buttonDiv.append(enableDiv);

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
    return fetch("/earnit/api/users/" + user.id, {
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
    return fetch("/earnit/api/users/" + user.id, {
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

    const nameDiv = document.createElement("div");
    const name = document.createElement("p");
    name.classList.add("text-text", "font-montserrat");
    name.innerText = company.name;
    nameDiv.append(name);

    const buttonDiv = document.createElement("div");
    buttonDiv.classList.add("flex", "flex-row", "gap-2");

    const disableDiv = document.createElement("div");
    disableDiv.classList.add("rounded-xl", "bg-accent-fail", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center");
    const crossImage = document.createElement("img");
    crossImage.src = "/earnit/static/icons/white-cross.svg";
    crossImage.classList.add("h-4", "w-4")
    crossImage.alt = "disable"
    disableDiv.append(crossImage);

    disableDiv.addEventListener("click", async () => {
        if (await displayPopUpCompany(company, false)) {
            disableDiv.classList.add("hidden")
            disableDiv.classList.remove("hidden")
        }
    })
    buttonDiv.append(disableDiv);

    const enableDiv = document.createElement("div");
    enableDiv.classList.add("rounded-xl", "bg-accent-success", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center");
    const checkmarkImage = document.createElement("img");
    checkmarkImage.src = "/earnit/static/icons/checkmark.svg";
    checkmarkImage.classList.add("h-4", "w-4")
    checkmarkImage.alt = "enable"
    enableDiv.append(checkmarkImage)

    enableDiv.addEventListener("click", async () => {
        if (await displayPopUpCompany(company, true)) {
            disableDiv.classList.add("hidden")
            disableDiv.classList.remove("hidden")
        }
    })
    buttonDiv.append(enableDiv);

    if(company.active === true) {
        enableDiv.classList.add("hidden")
        disableDiv.classList.remove("hidden")
    } else {
        disableDiv.classList.add("hidden")
        enableDiv.classList.remove("hidden")
    }
    itemContainer.append(nameDiv);
    itemContainer.append(buttonDiv);
    li.append(itemContainer);

    return li;
}

function enableCompany(company){
    company.active = true;
    return fetch("/earnit/api/companies/" + company.id, {
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
    return fetch("/earnit/api/companies/" + company.id, {
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
    return await fetch("/earnit/api/users",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json()).catch(() => null);
}

async function getCompanies() {
    return await fetch("/earnit/api/companies",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
    .catch(() => null);
}