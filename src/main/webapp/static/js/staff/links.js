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
    companiesElement.innerText = ""



    for (const user of users) {
        usersElement.append(createUser(user));
    }

    for (const company of companies) {
        companiesElement.append(createCompany(company));
    }


})


let userId = null;
let hourlyWage = null;
let contractId = null;
let companyId = null;

function createUser(user) {
    const div = document.createElement("div");
    div.addEventListener("click", ()=>{
        editUserInfo(user)
        userId=user.id;
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
        companyId=company.id;
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
    encapsulatingDiv.classList.add("flex", "items-center");
    encapsulatingDiv.setAttribute("id", "user-name-display");

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
    encapsulatingDiv.classList.add("flex", "items-center");
    encapsulatingDiv.setAttribute("id", "company-name-display");

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
    for (const contract of contracts) {
        contractsList.append(createContract(contract));
    }
}

function createContract(contract) {
    const listElement = document.createElement("li");

    const userDiv = document.createElement("div");
    userDiv.setAttribute("data-selected", '0')
    userDiv.classList.add("data-[selected='1']:border-white", "hover:border-text","border-2", "border-primary", "block", "columns-2", "bg-primary", "rounded-xl", "w-full", "p-2", "pl-4");
    const role = document.createElement("p")
    role.classList.add("text-text");
    role.innerText = contract.role;

    const blockDiv = document.createElement("div");
    blockDiv.classList.add("block", "overflow-y-auto", "scrollbar-custom", "scrollbar-track-text", "scrollbar-rounded-xl", "scrollbar-thumb-background", "max-h-[5rem]");

    const description = document.createElement("p")
    description.classList.add("text-text", "break-inside-avoid-column")
    description.innerText = contract.description;

    listElement.addEventListener("click", ()=>{
        contractId=contract.id;
        const selected = parseInt(userDiv.getAttribute("data-selected"))
        if (selected === 0) {
            const orderedList = document.getElementById("contract-list");
            const userDivs = orderedList.querySelectorAll("[data-selected='1']")
            for (const userDiv1 of userDivs) {
                userDiv1.setAttribute("data-selected", "0")
            }
            userDiv.setAttribute("data-selected", "1");
        }

    })

    blockDiv.append(description);
    userDiv.append(role);
    userDiv.append(blockDiv);
    listElement.append(userDiv);

    return listElement;
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

function updateHourlyWage(e) {
    hourlyWage = e.value;
}

async function getContracts(cid) {

    return await fetch("/earnit/api/companies/" + cid + "/contracts",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            },
        })
        .then(response => response.json())
        .catch(e => null);
}
function sendFormDataServer() {
    if (hourlyWage < 0) {
        alert("Negative hourly wage is not allowed");
        return;
    }

    if (hourlyWage === null || userId === null || contractId === null || companyId === null) {
        alert("Fill in all inputs");
        return;
    }
    return fetch("/earnit/api/companies/" + companyId + "/contracts/" + contractId +"/employees",
        {method: "POST",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`,
                'content-type': "application/json"
            },
        body:JSON.stringify({
            userId, hourlyWage: hourlyWage*100
        })}
    ).then((res) => {
        if(res.status === 200) {
            alert("Successfully created link");

        } else {
            alert("Link failed, try again, code: " + res.status);

        }
    })
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

// ------------------------------------------------------------------------------------

