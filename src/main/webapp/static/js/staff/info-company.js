// When page is loaded it waits for the company details, company roles and employees
window.addEventListener("helpersLoaded", async () => {
    const companyId = getIdCompany();
    const company = await getCompany(companyId);
    const roles = await getCompanyRoles(company.id)
    let employees = await getEmployees(company.id)


    const name = document.getElementById("name");
    const status = document.getElementById("status")
    const roleList = document.getElementById("roles");
    const employeeList = document.getElementById("employeeList");
    roleList.innerText = "";
    employeeList.innerText = "";

    if (company === null) {
        location.href = "/error/404"
        return;
    }

    name.innerText = company.name;
    if (company.active){
        status.innerText = "Enabled";
    }
    else {
        status.innerText = "Disabled";
    }

    if (roles !== null) {

        if (roles.length === 0) {
            const noRoles = document.createElement("div");
            noRoles.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
            noRoles.innerText = "No roles";
            roleList.append(noRoles)
        }

        for (const role of roles) {
            roleList.append(createRoleElement(role));
        }
    }
    else {
        // do something to show that no roles are available
    }
    if (employees !== null) {

        if (employees.length === 0) {
            const noEmployees = document.createElement("div");
            noEmployees.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
            noEmployees.innerText = "No employees";
            employeeList.append(noEmployees)
        }

        for (const employee of employees){
            employeeList.append(createEmployeeElement(employee))
        }
    }
    else{
        // do something to show that the company has not employees
    }
});


function getIdCompany() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("id"))) {
        location.replace("/overview");
        return;
    }
    return search.get("id");

}

//Fetches company details
function getCompany(companyId) {
    return fetch(`/api/companies/` + companyId, {
            method: "GET",
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        }
    )
        .then(res => res.json())
        .catch(() => null)
}

//Fetches company roles
function getCompanyRoles(companyId) {
    return fetch(`/api/companies/` + companyId + "/contracts", {
            method: "GET",
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        }
    )
        .then(res => res.json())
        .catch(() => null)
}

//Fetches company employees
function getEmployees(companyId){
    return fetch("/api/companies/"+ companyId + "/students", {
            method: "GET",
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        }
    )
        .then(res => res.json())
        .catch(() => null)
}

async function getUser(userId){
    return await fetch("/api/users/" + userId,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
        .catch(() => null);
}

//Formats a role element
function createRoleElement(role) {
    const userRoleContainer = document.createElement("div");
    userRoleContainer.classList.add("text-text", "whitespace-nowrap", "bg-primary", "py-6", "px-6", "rounded-xl", "cursor-pointer", "flex", "w-fit", "flex-col", "items-center", "justify-center")


    const roleName = document.createElement("div");
    roleName.classList.add("font-bold");
    roleName.innerText = role.role;
    userRoleContainer.append(roleName);

    const roleDescription = document.createElement("div");
    roleDescription.innerText = role.description;
    userRoleContainer.append(roleDescription);

    return userRoleContainer;
}

//Formats an employee element
function createEmployeeElement(user) {

    const entryContainer = document.createElement("a");
    entryContainer.classList.add("rounded-xl", "bg-primary", "py-2", "pl-4", "pr-2", "relative", "flex", "justify-between");
    entryContainer.href = "/info-user?id=" + user.id
    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "grid-cols-[3fr_2fr_2fr_1fr]", "grid", "items-center");
    entryContainer.appendChild(entryInfo);

    const name = document.createElement("div");
    name.classList.add("text-text", "font-bold", "uppercase");
    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    entryInfo.appendChild(name);

    const status = document.createElement("div");
    status.classList.add("text-text");
    if (user.active){
        status.innerText = "Status: Enabled";
    }
    else{
        status.innerText = "Status: Disabled";
    }
    entryInfo.appendChild(status);

    const role = document.createElement("div");
    role.classList.add("text-text");
    if (user.type === "STUDENT"){
        role.innerText = "Student";
    }
    else if (user.type === "ADMINISTRATOR"){
        role.innerText = "Administrator";
    }
    else if (user.type === "COMPANY"){
        role.innerText = "Company";
    }
    entryInfo.appendChild(role);

    return entryContainer;
}





