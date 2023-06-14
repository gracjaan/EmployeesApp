window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();
    const users = await getStudents();

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

    const a = document.createElement("a");
    /** @TODO check for link? */
    li.append(a);

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("bg-primary", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4", "my-2");
    a.append(itemContainer);

    const name = document.createElement("p");
    name.classList.add("text-text");
    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    itemContainer.append(name);

    return li;
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