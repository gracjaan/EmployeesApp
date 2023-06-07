window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();
    console.log(companies)
    const users = await getStudents();

    if (companies === null || users === null) {
        alert("Could not load users or companies");
        return;
    }

    const usersElement = document.getElementById("dropdown-student-content");
    usersElement.innerText = "";

    const companiesElement = document.getElementById("dropdown-company-content");
    companiesElement.innerText = "";

    for (const user of users) {
        usersElement.append(createUser(user));
    }

    for (const company of companies) {
        companiesElement.append(createCompany(company));
    }
})


const userId = ''
const hourlyWage = ''

function createUser(user) {
    const div = document.createElement("div");
    div.addEventListener("click", ()=>{
        editUserInfo(user)
    })

    const a = document.createElement("a");

    div.append(a);

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("hover:bg-gray-100", "cursor-pointer", "color-text", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4");
    a.append(itemContainer);

    const name = document.createElement("p");
    name.classList.add("text-background");
    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    itemContainer.append(name);

    return div;
}

function createCompany(company) {
    const div = document.createElement("div");

    const a = document.createElement("a");

    div.addEventListener("click", ()=>{
        editCompanyInfo(company)
        getContracts(company.id)
    })

    div.append(a);

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("hover:bg-gray-100", "cursor-pointer", "color-text", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4");
    a.append(itemContainer);

    const name = document.createElement("p");
    name.classList.add("text-background");
    name.innerText = company.name;
    itemContainer.append(name);

    return div;
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

async function obtainUser(uid){
    return await fetch("/earnit/api/users/{uid}",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json()).catch(() => null);
}

async function obtainCompany(cid){
    return await fetch("/earnit/api/companies/{company_id}",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
        .catch(() => null);
}

function sendFormDataServer(){
    return fetch("/earnit/api//companies/{company_id}/contracts/{contract_id}/employees ",
        {method: "POST",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            },
        body:JSON.stringify({
            userId, hourlyWage
        })}
    ).then((res) => res.json())
        .catch(() => null);
}

function editUserInfo(user){
    userDiv = document.getElementById("user-name-display");
    userDiv.innerHTML = ""
}

function editCompanyInfo(company){

}

function getContracts(cid) {
    return fetch("/earnit/api///companies/{cid}/contracts ",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            },
           })
}

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("dropdown-student-content");
    const button = document.getElementById("dropdown-student-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("dropdown-company-content");
    const button = document.getElementById("dropdown-company-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("dropdown-position-content");
    const button = document.getElementById("dropdown-position-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("dropdown-type-content");
    const button = document.getElementById("dropdown-type-button");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});
// --------------------------------------------------------------------------------
// Toggles all the dropdown menu's to show up

function toggleStudent() {
    const dropdown = document.getElementById("dropdown-student-content");
    dropdown.classList.toggle("hidden");
}

function toggleCompany() {
    const dropdown = document.getElementById("dropdown-company-content");
    dropdown.classList.toggle("hidden");
}

function toggleType() {
    const dropdown = document.getElementById("dropdown-type-content");
    dropdown.classList.toggle("hidden");
}

function togglePosition() {
    const dropdown = document.getElementById("dropdown-position-content");
    dropdown.classList.toggle("hidden");
}

// ------------------------------------------------------------------------------------
