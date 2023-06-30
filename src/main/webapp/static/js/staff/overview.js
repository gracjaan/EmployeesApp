// When page is loaded it waits for a list of users and companies
window.addEventListener("helpersLoaded", async () => {
    document.getElementById("name-user").addEventListener("change", () => {
        updatePage()
    })
    document.getElementById("name-company").addEventListener("change", () => {
        updatePage()
    })

    const cancelButton = document.getElementById("cancelButton")
    const popUpElement = document.getElementById("popUp");
    const confirmButton = document.getElementById("confirmButton")

    cancelButton.addEventListener("click", async () => {
        popUpElement.classList.add("hidden")
    })

    confirmButton.addEventListener("click", async () => {
        const userId = confirmButton.getAttribute("data-user-id");
        const companyId = confirmButton.getAttribute("data-company-id");

        const isCompany = (userId === null || userId === "") && companyId !== null;

        const parent = document.getElementById(isCompany ? "company-" + companyId : "user-" + userId)
        const enableDiv = parent.querySelector(`.enable`)
        const disableDiv = parent.querySelector(`.disable`)

        if (confirmButton.getAttribute("data-enabling") === "1") {
            if (isCompany) {
                await enableCompany(companyId)
            } else {
                await enableUser(userId)
            }

            disableDiv.classList.remove("hidden")
            enableDiv.classList.add("hidden")
        }
        else{
            if (isCompany) {
                await disableCompany(companyId)
            } else {
                await disableUser(userId)
            }

            enableDiv.classList.remove("hidden")
            disableDiv.classList.add("hidden")
        }
        popUpElement.classList.add("hidden")

    })
    await updatePage();
});

async function updatePage() {
    const companies = await getCompanies();
    const users = await getStudents();

    if (companies === null || users === null) {
        alertPopUp("Could not load users or companies", false);
        return;
    }

    const usersElement = document.getElementById("users");
    usersElement.innerText = "";

    const companiesElement = document.getElementById("companies");
    companiesElement.innerText = "";

    if (users.length === 0) {
        const noUsers = document.createElement("div");
        noUsers.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noUsers.innerText = "No users";
        usersElement.append(noUsers)
    }

    if (companies.length === 0) {
        const noCompanies = document.createElement("div");
        noCompanies.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noCompanies.innerText = "No companies";
        companiesElement.append(noCompanies)
    }

    for (const user of users) {
        usersElement.append(createUser(user));
    }
    for (const company of companies) {
        companiesElement.append(createCompany(company));
    }
}

//Formats user element
function createUser(user) {
    const li = document.createElement("li");
    li.id = "user-" + user.id;
    const itemContainer = document.createElement("div");
    itemContainer.classList.add("flex", "flex-row", "justify-between", "bg-primary", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4", "my-2", "items-center", "gap-2");
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
    disableDiv.classList.add("w-[128px]", "h-[36px]", "flex", "flex-row", "justify-between", "items-center", "disable","bg-accent-fail", "p-2",  "rounded-xl", "cursor-pointer");

    const crossImage = document.createElement("img");
    crossImage.src = "/static/icons/white-cross.svg";
    crossImage.classList.add("h-4", "w-4")
    crossImage.alt = "disable"
    disableDiv.append(crossImage);

    const disableTextDiv = document.createElement("div");
    disableTextDiv.classList.add("flex",  "flex-row", "justify-between", "items-center", "text-text", "text-sm");
    const disableText = document.createElement("p");
    disableText.innerText = "Disable User";
    disableText.classList.add("whitespace-nowrap");
    disableTextDiv.append(disableText);
    disableDiv.append(disableTextDiv);

    itemContainer.append(disableDiv);

    const enableDiv = document.createElement("div");
    enableDiv.classList.add("w-[128px]", "h-[36px]", "flex", "flex-row", "justify-between", "items-center","bg-accent-success", "enable", "p-2",  "rounded-xl", "cursor-pointer");
    const checkmarkImage = document.createElement("img");
    checkmarkImage.src = "/static/icons/checkmark.svg";
    checkmarkImage.classList.add("h-4", "w-4")
    checkmarkImage.alt = "enable"
    enableDiv.append(checkmarkImage)
    const enableTextDiv = document.createElement("div");
    enableTextDiv.classList.add("flex",  "flex-row", "justify-between", "items-center", "text-text", "text-sm");
    const enableText = document.createElement("p");
    enableText.innerText = "Enable User";
    enableText.classList.add("whitespace-nowrap");
    enableTextDiv.append(enableText);
    enableDiv.append(enableTextDiv);

    itemContainer.append(enableDiv);

    enableDiv.addEventListener("click", async () => {
        displayPopUpUser(user, true)
    })
    buttonDiv.append(enableDiv);

    disableDiv.addEventListener("click", async () => {
        displayPopUpUser(user, false)
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

//Post request to enable user called when staff click on the green tick
async function enableUser(userId){
    return fetch("/api/users/" + userId, {
        method: 'put',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({...(await getStudents()).find(x => x.id === userId), active: true})
    }).then(async res => {
        if (res.status !== 200) throw new Error();
        return await res.json()
    })
        .catch(() => null)
}

//Post request to disable user when staff click on the red cross
function disableUser(userId){
    return fetch("/api/users/" + userId, {
        method: 'delete',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(async res => {
        if (res.status !== 200) throw new Error();
        return await res.json()
    }).catch(() => null)
}

//Formats company element
function createCompany(company) {
    const li = document.createElement("li");
    li.id = "company-" + company.id;

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("flex", "flex-row", "justify-between", "bg-primary", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4", "my-2", "items-center", "gap-2");

    const nameDiv = document.createElement("a");
    nameDiv.href = "/info-company?id=" + company.id;

    const name = document.createElement("p");
    name.classList.add("text-text", "font-montserrat");
    name.innerText = company.name;

    nameDiv.append(name);
    itemContainer.append(nameDiv)

    const disableDiv = document.createElement("div");
    disableDiv.classList.add("w-[164px]", "h-[36px]", "flex", "flex-row", "justify-between", "items-center", "disable","bg-accent-fail", "p-2",  "rounded-xl", "cursor-pointer");

    const crossImage = document.createElement("img");
    crossImage.src = "/static/icons/white-cross.svg";
    crossImage.classList.add("h-4", "w-4")
    crossImage.alt = "disable"
    disableDiv.append(crossImage);

    const disableTextDiv = document.createElement("div");
    disableTextDiv.classList.add("flex",  "flex-row", "justify-between", "items-center", "text-text", "text-sm");
    const disableText = document.createElement("p");
    disableText.innerText = "Disable Company";
    disableText.classList.add("whitespace-nowrap");
    disableTextDiv.append(disableText);
    disableDiv.append(disableTextDiv);

    itemContainer.append(disableDiv);

    const enableDiv = document.createElement("div");
    enableDiv.classList.add("w-[164px]", "h-[36px]", "flex", "flex-row", "justify-between", "items-center", "enable","bg-accent-success", "p-2",  "rounded-xl", "cursor-pointer");
    const checkmarkImage = document.createElement("img");
    checkmarkImage.src = "/static/icons/checkmark.svg";
    checkmarkImage.classList.add("h-4", "w-4")
    checkmarkImage.alt = "enable"
    enableDiv.append(checkmarkImage)
    const enableTextDiv = document.createElement("div");
    enableTextDiv.classList.add("flex",  "flex-row", "justify-between", "items-center", "text-text", "text-sm");
    const enableText = document.createElement("p");
    enableText.innerText = "Enable Company";
    enableText.classList.add("whitespace-nowrap");
    enableTextDiv.append(enableText);
    enableDiv.append(enableTextDiv);

    itemContainer.append(enableDiv);

    disableDiv.addEventListener("click", async () => {
        displayPopUpCompany(company, false)
    })

    enableDiv.addEventListener("click", async () => {
        displayPopUpCompany(company, true)
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

function displayPopUpUser(user, enabling) {
    const popUpElement = document.getElementById("popUp");
    const confirmButton = document.getElementById("confirmButton")
    popUpElement.classList.remove("hidden");

    const paragraph = document.getElementById("popUpParagraph");
    if (enabling) {
        paragraph.innerText = "Are you sure you want to enable " + getName(user.firstName, user.lastName, user.lastNamePrefix) + "'s account?";
    }
    else{
        paragraph.innerText = "Are you sure you want to disable " + getName(user.firstName, user.lastName, user.lastNamePrefix) + "'s account?";
    }

    confirmButton.setAttribute("data-enabling", enabling ? "1" : "0");
    confirmButton.setAttribute("data-user-id", user.id);
    confirmButton.removeAttribute("data-company-id");

}

function displayPopUpCompany(company, enabling) {
    const popUpElement = document.getElementById("popUp");
    const confirmButton = document.getElementById("confirmButton")
    popUpElement.classList.remove("hidden");

    const paragraph = document.getElementById("popUpParagraph");
    if (enabling) {
        paragraph.innerText = "Are you sure you want to enable " + company.name + "'s account?";
    }
    else{
        paragraph.innerText = "Are you sure you want to disable " + company.name + "'s account?";
    }

    confirmButton.setAttribute("data-enabling", enabling ? "1" : "0");
    confirmButton.setAttribute("data-company-id", company.id);
    confirmButton.removeAttribute("data-user-id");
}

//Post request to enable company called when staff click on the green tick
async function enableCompany(companyId){
    return fetch("/api/companies/" + companyId, {
        method: 'put',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({...(await getCompanies()).find(x => x.id === companyId), active: true})
    }).then(async res => ({
        status: res.status,
        json: await res.json()
    }))
        .catch(() => null)
}

//Post request to disable company called when staff click on the red cross
function disableCompany(companyId){
    return fetch("/api/companies/" + companyId, {
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

//Fetches students
async function getStudents() {
    return await fetch(`/api/users?${getQueryParamsUsers()}`,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json()).catch(() => null);
}

//Fetches companies
async function getCompanies() {
    return await fetch(`/api/companies?${getQueryParamsCompany()}`,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
    .catch(() => null);
}

//Orders the company list based on the staff preference
function getOrderCompany() {
    const nameCompany = document.getElementById("name-company");
    const companySelected = nameCompany.getAttribute("data-selected");

    let order = "";
    if (companySelected > 0) {
        order += "company.name:" + (companySelected === "1" ? "asc" : "desc");
    }
    return order;
}
//Orders the user list based on the staff preference
function getOrderUsers() {
    const nameUser = document.getElementById("name-user");
    const nameSelected = nameUser.getAttribute("data-selected");
    let order = "";
    if (nameSelected > 0) {
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

//Handles search abr for students
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
//Handles search abr for companies
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