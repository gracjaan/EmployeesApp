window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();
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
    div.addEventListener("click", ()=>{
        editCompanyInfo(company)
    })
    const a = document.createElement("a");

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

function editUserInfo(user){
    const userDiv = document.getElementById("user-name-display");
    userDiv.innerText = "";

    const encapsulatingDiv = document.createElement("div");
    encapsulatingDiv.classList.add("flex", "flex-center");
    encapsulatingDiv.id = "user-name-display";

    const userIMage = document.createElement("img");
    userIMage.alt = "user logo"
    userIMage.classList.add("h-14", "mr-8");
    userIMage.src = "/earnit/static/icons/user.svg"

    const userName = document.createElement("p");
    userName.classList.add("text-text", "font-bold", "text-2xl");
    userName.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);

    encapsulatingDiv.append(userIMage);
    encapsulatingDiv.append(userName);

    userDiv.append(encapsulatingDiv)
}
async function editCompanyInfo(company){
    const companyDiv = document.getElementById("company-name-display");
    companyDiv.innerText = "";

    const encapsulatingDiv = document.createElement("div");
    encapsulatingDiv.classList.add("flex", "flex-center");
    encapsulatingDiv.id = "company-name-display";

    const companyImage = document.createElement("img");
    companyImage.alt = "company logo"
    companyImage.classList.add("h-14", "mr-8");
    companyImage.src = "/earnit/static/icons/building.svg"

    const companyName = document.createElement("p");
    companyName.classList.add("text-text", "font-bold", "text-2xl");
    companyName.innerText = company.name;
    encapsulatingDiv.append(companyImage);
    encapsulatingDiv.append(companyName);

    companyDiv.append(encapsulatingDiv)


//     Now we need to show all the contracts for the company//
    const contractsList = document.getElementById("contract-list");
    contractsList.innerText = "";
    let contracts = await getContracts(company.id)
    console.log(contracts);
    for (const contract of contracts) {
        contractsList.append(createContract(contract));
    }
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
