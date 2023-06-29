window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    const email = document.getElementById("email");
    const contracts = document.getElementById("contracts");

    //filter that sort the users invoices on the amount of hours
    const hours = document.getElementById("hours");
    hours.addEventListener("change", () => updateInvoices())

    //filter that sort the users invoices on the contracts
    const contract = document.getElementById("contract");
    contract.addEventListener("change", () => updateInvoices())

    //filter that sort the users invoices on the weeks
    const week = document.getElementById("week");
    week.addEventListener("change", () => updateInvoices())

    //if we don't have a user, we can't show any user info, so we error
    const user = await getUser();
    if (user === null) {
        location.href = "/error/404"
        return;
    }

    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    email.innerText = user.email;
     //if there are no contracts, we want to show that no contracts are available
    if (user.userContracts.length === 0) {
        const noContracts = document.createElement("div");
        noContracts.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noContracts.innerText = "No contracts";
        contracts.append(noContracts)
    }

    //inserts a contract
    for (const userContract of user.userContracts) {
        contracts.append(createUserContractItem(userContract));
    }

    await updateInvoices();
})

//shows all the invoices
async function updateInvoices() {
    const workedWeeks = await getInvoices();
    if (workedWeeks === null) return;

    const invoices = document.getElementById("invoices");
    invoices.innerText = "";
    //if there are no invoices, we want to show that there are no results
    if (workedWeeks.length === 0) {
        const noInvoices = document.createElement("div");
        noInvoices.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noInvoices.innerText = "No invoices";
        invoices.append(noInvoices)
    }
    //creates all the invoice items for the user
    for (const workedWeek of workedWeeks) {
        invoices.append(createInvoiceItem(workedWeek));
    }
}
//create the contract element
function createUserContractItem(userContract) {
    const userContractContainer = document.createElement("div");
    userContractContainer.classList.add("text-text", "whitespace-nowrap", "bg-primary", "py-6", "px-6", "rounded-xl", "cursor-pointer", "flex", "w-fit", "flex-col", "items-center", "justify-center")
    userContractContainer.addEventListener("click", () => {
        location.href = "/contracts#" + userContract.contract.id;
    })

    const userContractRole = document.createElement("div");
    userContractRole.classList.add("font-bold");
    userContractRole.innerText = userContract.contract.role;
    userContractContainer.append(userContractRole);

    const userContractWage = document.createElement("div");
    userContractWage.innerText = userContract.hourlyWage / 100 + " €";
    userContractContainer.append(userContractWage);

    return userContractContainer;
}

//creates the invoice element
function createInvoiceItem(workedWeek) {
    const entryContainer = document.createElement("a");
    entryContainer.classList.add("rounded-xl", "bg-primary", (workedWeek.status === "NOT_CONFIRMED" || workedWeek.status === "CONFIRMED") ? "py-3" : "py-2", "pl-4", "pr-2", "relative", "flex", "justify-between");
    entryContainer.href = "/request?worked_week=" + workedWeek.id;

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "grid-cols-[3fr_2fr_2fr_1fr]", "grid", "items-center");
    entryContainer.appendChild(entryInfo);

    const week = document.createElement("div");
    week.classList.add("text-text", "font-bold", "uppercase");
    week.innerText = `Week ${workedWeek.week} (${workedWeek.year})`;
    entryInfo.appendChild(week);

    const hours = document.createElement("div");
    hours.classList.add("text-text");
    hours.innerText = `${workedWeek.totalMinutes / 60}H`;
    entryInfo.appendChild(hours);

    const role = document.createElement("div");
    role.classList.add("text-text");
    role.innerText = workedWeek.contract.role;
    entryInfo.appendChild(role);

    if (!(workedWeek.status === "NOT_CONFIRMED" || workedWeek.status === "CONFIRMED")) {
        const statusContainer = document.createElement("div");
        statusContainer.classList.add("w-full", "flex", "justify-end")
        entryInfo.appendChild(statusContainer);

        const status = document.createElement("div");
        status.classList.add("rounded-xl", workedWeek.status === "APPROVED" ? "bg-accent-success" : "bg-[#FD8E28]", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center", "items-center")
        statusContainer.appendChild(status);

        const img = document.createElement("img");
        img.alt = "checkmark";
        img.src = `/static/icons/${workedWeek.status === "APPROVED" ? "checkmark" : "light-white"}.svg`;
        img.classList.add("w-4", "h-4")
        status.append(img);
    }

    return entryContainer;
}
//gets the id of the student from the URL
function getUserId() {
    const search = new URLSearchParams(location.search);
    if (!search.has("id")) {
        history.back()
        return;
    }

    return search.get("id");
}

//Here we state what parameters we want to get from the response in the body
function getQueryParamsForUser() {
    const order = getOrder();
    return `userContracts=true&userContractsContract=true`
}

//gets the student user that we retrieved from the URL
function getUser() {
    return fetch(`/api/companies/${getUserCompany()}/students/${getUserId()}?${getQueryParamsForUser()}`, {
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

//Gets the order in which the invoices should be ordered
function getOrder() {
    const contract = document.getElementById("contract");
    const contractSelected = contract.getAttribute("data-selected");

    const week = document.getElementById("week");
    const weekSelected = week.getAttribute("data-selected");

    const hours = document.getElementById("hours");
    const hoursSelected = hours.getAttribute("data-selected");

    let order = "";
    if (contractSelected > 0) {
        order += "contract.role:" + (contractSelected === "1" ? "asc" : "desc");
    } else if (weekSelected > 0) {
        order += "worked_week.year:" + (weekSelected === "1" ? "asc" : "desc") + ",worked_week.week:" + (weekSelected === "1" ? "asc" : "desc");
    } else if (hoursSelected > 0) {
        order += "worked_week.total_hours:" + (hoursSelected === "1" ? "asc" : "desc");
    }

    return order;
}

function getQueryParamsForInvoices() {
    const order = getOrder();
    return `contract=true&totalHours=true${order.length > 0 ? `&order=${order}` : ""}`
}

//gets all the invoices for a user
function getInvoices() {
    return fetch(`/api/companies/${getUserCompany()}/invoices/${getUserId()}?${getQueryParamsForInvoices()}`, {
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