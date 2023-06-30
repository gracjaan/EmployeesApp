// When page is loaded it waits for a list of users and companies
window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();
    const users = await getStudents();

    const confirmButton = document.getElementById("confirmButton")
    const cancelButton = document.getElementById("cancelButton")
    const popUpElement = document.getElementById("popUp");

    confirmButton.addEventListener("click", async () => {
        popUpElement.classList.add("hidden")
        await sendFormDataServer()
    })

    cancelButton.addEventListener("click", async () => {
        popUpElement.classList.add("hidden")
    })

    if (companies === null || users === null) {
        alertPopUp("Could not load users or companies", false);
        return;
    }

    const usersElement = document.getElementById("searchUser");
    usersElement.innerText = "";

    const companiesElement = document.getElementById("searchCompany");
    companiesElement.innerText = ""

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

    const createLink = document.getElementById("createLink");
    createLink.addEventListener("click", async () => {
        displayPopUp()
    })

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

//Formats user element
function createUser(user) {
    const li = document.createElement("li");
    li.addEventListener("click", ()=>{
        editUserInfo(user)
        userId=user.id;
    })

    const a = document.createElement("a");

    li.append(a);

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("hover:bg-gray-100", "cursor-pointer", "color-text", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4");
    a.append(itemContainer);

    const name = document.createElement("p");
    name.classList.add("text-background");
    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    itemContainer.append(name);

    return li;
}

//Formats company element
function createCompany(company) {
    const li = document.createElement("li");
    li.addEventListener("click", ()=>{
        editCompanyInfo(company)
        companyId=company.id;
        toggleSearchBar();
    })
    const a = document.createElement("a");

    li.append(a);

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("hover:bg-gray-100", "cursor-pointer", "color-text", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4");
    a.append(itemContainer);

    const name = document.createElement("p");
    name.classList.add("text-background");
    name.innerText = company.name;
    itemContainer.append(name);

    return li;
}
//When a user is selected from the list, their name will appear on the page
function editUserInfo(user){
    const userDiv = document.getElementById("user-name-display");
    userDiv.innerText = "";

    const encapsulatingDiv = document.createElement("div");
    encapsulatingDiv.classList.add("flex", "flex-row", "items-center");
    encapsulatingDiv.setAttribute("id", "user-name-display");

    const userIMage = document.createElement("img");
    userIMage.alt = "user logo"
    userIMage.classList.add("h-14", "mr-4");
    userIMage.src = "/static/icons/user.svg"

    const userName = document.createElement("p");
    userName.classList.add("text-text", "font-bold", "text-2xl");
    userName.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);

    encapsulatingDiv.append(userIMage);
    encapsulatingDiv.append(userName);

    userDiv.append(encapsulatingDiv)
}

//When a company is selected from the list, their name will appear on the page
async function editCompanyInfo(company) {
    const companyDiv = document.getElementById("company-name-display");
    companyDiv.innerText = "";

    const encapsulatingDiv = document.createElement("div");
    encapsulatingDiv.classList.add("flex", "flex-row", "items-center");
    encapsulatingDiv.setAttribute("id", "company-name-display");

    const companyImage = document.createElement("img");
    companyImage.alt = "company logo"
    companyImage.classList.add("h-14", "mr-4");
    companyImage.src = "/static/icons/building.svg"

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
    if (contracts.length === 0){
        const noContracts = document.createElement("div");
        noContracts.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noContracts.innerText = "No contracts. You can create a contract on the homepage";
        contractsList.append(noContracts)
    }

    for (const contract of contracts) {
        contractsList.append(createContract(contract));
    }
}

//Formats contract element
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

//Fetches students
async function getStudents() {
    return await fetch("/api/users",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json()).catch(() => null);
}

//Fetches students
async function getCompanies() {
    return await fetch("/api/companies",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
        .catch(() => null);
}

//When a value is input into the hourly wage box, it is stored in this variable
function updateHourlyWage(e) {
    hourlyWage = e.value;
}

//Fetches contracts on a specific company. It is called when a company is selected
async function getContracts(cid) {

    return await fetch("/api/companies/" + cid + "/contracts",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            },
        })
        .then(response => response.json())
        .catch(e => null);
}

//Sends the post request to the server to create the link between user and company
function sendFormDataServer() {
    if (hourlyWage < 0) {
        alertPopUp("Negative hourly wage is not allowed", false);
        return;
    }

    if (hourlyWage === null || userId === null || contractId === null || companyId === null) {
        alertPopUp("Fill in all inputs", false);
        return;
    }

    return fetch("/api/companies/" + companyId + "/contracts/" + contractId +"/employees",
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
            alertPopUp("Successfully created link", true);

        } else {
            alertPopUp("Link failed, try again, code: " + res.status, false);

        }
    })
        .catch(() => null);
}


document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("dropdown-user-content");
    const choosingUser = document.getElementById("dropdown-user-button");
    const searchbar = document.getElementById("searchbarUser");
    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !choosingUser.contains(targetElement) && targetElement !== searchbar && !searchbar.contains(targetElement)) {
        dropdown.classList.add("hidden");

    }
});

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("dropdown-company-content");
    const button = document.getElementById("dropdown-company-button");
    const searchbar = document.getElementById("searchbarCompany");

    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement) && targetElement !== searchbar && !searchbar.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});
// --------------------------------------------------------------------------------
// Toggles all the dropdown menu's to show up

function toggleStudent() {
    const dropdown = document.getElementById("dropdown-user-content");
    dropdown.classList.toggle("hidden");
}

function toggleCompany() {
    const dropdown = document.getElementById("dropdown-company-content");
    dropdown.classList.toggle("hidden");
}

function toggleSearchBar() {
    const searchBar = document.getElementById("searchBarRoles");
    searchBar.classList.remove("hidden");
}

// ------------------------------------------------------------------------------------

//Handles search bar to find user
function searchUser() {
    let input = document.getElementById('searchUsers');
    let filter = input.value.toUpperCase();
    let ol = document.getElementById("searchUser");
    let li = ol.getElementsByTagName('li');

    for (let i = 0; i < li.length; i++) {
        let p = li[i].getElementsByTagName("p")[0];
        console.log(p)
        let txtValue = p.textContent || p.innerText;
        console.log(txtValue)
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            li[i].style.display = "";
        } else {
            li[i].style.display = "none";
        }
    }
}

//Handles search bar to find company
function searchCompany(){
    let input = document.getElementById('searchCompanies');
    let filter = input.value.toUpperCase();
    let ol = document.getElementById("searchCompany");
    let li = ol.getElementsByTagName('li');

    for (let i = 0; i < li.length; i++) {
        let p = li[i].getElementsByTagName("p")[0];
        console.log(p)

        let txtValue = p.textContent || p.innerText;
        console.log(txtValue)
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            li[i].style.display = "";
        } else {
            li[i].style.display = "none";
        }
    }
}

//Handles search bar to find role
function searchRole(){
    let input = document.getElementById('searchRoles');
    let filter = input.value.toUpperCase();
    let ol = document.getElementById("contract-list");
    let li = ol.getElementsByTagName('li');

    for (let i = 0; i < li.length; i++) {
        let p = li[i].getElementsByTagName("p")[0];
        console.log(p)

        let txtValue = p.textContent || p.innerText;
        console.log(txtValue)
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
            li[i].style.display = "";
        } else {
            li[i].style.display = "none";
        }
    }
}

function displayPopUp(){
    const popUpElement = document.getElementById("popUp");
    popUpElement.classList.remove("hidden");

    const paragraph = document.getElementById("popUpParagraph");
    paragraph.innerText = "Are you sure you want to create the link"
}

function alertPopUp(message, positive) {
    let confirmation = document.getElementById("successfulContractCreation");
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