window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompany();
    const users = await getUser();
});


function getIdCompany() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("id"))) {
        location.replace("/earnit/overview");
        return;
    }
    return search.get("id");

}

function getCompany(companyId) {
    return fetch(`/earnit/api/companies/` + companyId, {
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

function getCompanyRoles(companyId) {
    return fetch(`/earnit/api/companies/` + companyId + "/contracts", {
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

function getEmployees(companyId, contractId){
    return fetch(`/earnit/api/companies/` + companyId + '/contracts/' + contractId + '/employees', {
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
    return await fetch("/earnit/api/users/" + userId,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
        .catch(() => null);
}

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

// async function createEmployeeElement(employee) {
//
//     const entryContainer = document.createElement("a");
//     entryContainer.classList.add("rounded-xl", "bg-primary", "py-2", "pl-4", "pr-2", "relative", "flex", "justify-between");
//     entryContainer.href = "/earnit/user-info?id=" + employee.id;
//     const entryInfo = document.createElement("div");
//     entryInfo.classList.add("w-full", "grid-cols-[3fr_2fr_2fr_1fr]", "grid", "items-center");
//     entryContainer.appendChild(entryInfo);
//
//     const user = await getUser(employee.id)
//     console.log(user);
//     const name = document.createElement("div");
//     name.classList.add("text-text", "font-bold", "uppercase");
//     name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
//     entryInfo.appendChild(name);
//
//     const status = document.createElement("div");
//     status.classList.add("text-text");
//     if (user.active){
//         status.innerText = "Status: Enabled";
//     }
//     else{
//         status.innerText = "Status: Disabled";
//     }
//     entryInfo.appendChild(status);
//
//     const role = document.createElement("div");
//     role.classList.add("text-text");
//     role.innerText = "Student";
//     entryInfo.appendChild(role);
//
//     return entryContainer;
// }
//
//

}

function getIdCompany() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("user") && !search.has("company")) || (!search.has("user") && !search.has("company"))) {
        location.replace("/earnit/overview");
        return;
    }
    if (search.has("company")) {
        return search.get("company");
    }
    return null;
}
