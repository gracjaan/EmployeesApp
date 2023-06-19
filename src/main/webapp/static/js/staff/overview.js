window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();
    const users = await getStudents();
    console.log(users)
    console.log(companies)

    if (companies === null || users === null) {
        alert("Could not load users or companies");
        return;
    }

    const usersElement = document.getElementById("users");
    usersElement.innerText = "";

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

    const nameDiv = document.createElement("div");
    const name = document.createElement("p");
    name.classList.add("text-text", "font-montserrat");
    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    nameDiv.append(name);

    const statusDiv = document.createElement("div");
    const status = document.createElement("p");
    status.classList.add("text-text", "font-montserrat");
    status.innerText = "Status: " + user.active
    statusDiv.append(status);

    const buttonDiv = document.createElement("div");
    buttonDiv.classList.add("flex", "flex-row", "gap-2");

    const disableDiv = document.createElement("div");
    disableDiv.classList.add("rounded-xl", "bg-accent-fail", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center");
    const crossImage = document.createElement("img");
    crossImage.src = "/earnit/static/icons/white-cross.svg";
    crossImage.classList.add("h-4", "w-4")
    crossImage.alt = "disable"
    disableDiv.append(crossImage);

    disableDiv.addEventListener("click", ()=>{
        enableUser(user, statusDiv)
    })

    const enableDiv = document.createElement("div");
    enableDiv.classList.add("rounded-xl", "bg-accent-success", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center");
    const checkmarkImage = document.createElement("img");
    checkmarkImage.src = "/earnit/static/icons/checkmark.svg";
    checkmarkImage.classList.add("h-4", "w-4")
    checkmarkImage.alt = "enable"
    enableDiv.append(checkmarkImage)

    enableDiv.addEventListener("click", ()=>{
        disableUser(user, statusDiv)
    })

    buttonDiv.append(enableDiv);
    buttonDiv.append(disableDiv);
    itemContainer.append(nameDiv);
    itemContainer.append(statusDiv);
    itemContainer.append(buttonDiv);
    li.append(itemContainer);

    return li;
}

function enableUser(user, statusDiv){
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
function disableUser(user, statusDiv){
    user.active = false;
    console.log(JSON.stringify(user))
    return fetch("/earnit/api/users/" + user.id, {
        method: 'PUT',
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(user)
    }).then(async res => ({
        status: res.status,
        json: await res.json()
    }, statusDiv.innerText = user.active))
        .catch(() => null)
}

function createCompany(company) {
    const li = document.createElement("li");

    const a = document.createElement("a");

    li.append(a);

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("bg-primary", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4", "my-2");
    a.append(itemContainer);

    const name = document.createElement("p");
    name.classList.add("text-text");
    name.innerText = company.name;
    itemContainer.append(name);

    return li;
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