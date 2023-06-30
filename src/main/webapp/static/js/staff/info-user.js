// When page is loaded it waits for the user details, user contracts and companies the user works for
window.addEventListener("helpersLoaded", async () => {
    const userId = getIdUser();
    const user = await getUser(userId);
    const userContracts = await getUserContracts(userId)
    console.log("These are all the user contracts")
    console.log(userContracts)
    const companies = await getCompanies(userId)

    const hours = document.getElementById("hours");
    hours.addEventListener("change", () => updateInvoices())

    const contract = document.getElementById("contract");
    contract.addEventListener("change", () => updateInvoices())

    const week = document.getElementById("week");
    week.addEventListener("change", () => updateInvoices())

    const name = document.getElementById("name");
    const email = document.getElementById("email");
    const status = document.getElementById("status")
    const contracts = document.getElementById("contracts");

    if (user === null) {
        location.href = "/error/404"
        return;
    }

    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    email.innerText = user.email;

    if (user.active){
        status.innerText = "Enabled";
    }
    else {
        status.innerText = "Disabled";
    }

    if (userContracts.length === 0) {
        const noContracts = document.createElement("div");
        noContracts.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noContracts.innerText = "No contracts";
        contracts.append(noContracts)
    }

    for (const userContract of userContracts) {
        contracts.append(createUserContractItem(userContract));
    }
    await updateInvoices(companies, user);
});

async function updateInvoices(companies, user) {
    const invoices = document.getElementById("invoices");
    //invoices.innerText = "";

    if (companies.length === 0) {
        const noCompanies = document.createElement("div");
        noCompanies.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noCompanies.innerText = "No companies";
        invoices.append(noCompanies)
    }

    for (const userCompanies of companies) {
        const workedWeeks = await getInvoices(userCompanies.id, user.id)

        if (workedWeeks === null) return;

        console.log(workedWeeks)

        if (workedWeeks.length === 0) {
            const noInvoices = document.createElement("div");
            noInvoices.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
            noInvoices.innerText = "No invoices";
            invoices.append(noInvoices)
        }

        for (const workedWeek of workedWeeks) {
            invoices.append(createInvoiceItem(workedWeek));
        }
    }

}

//Fetches user details
function getUser(userId) {
    return fetch(`/api/users/` + userId, {
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

//Fetches user contracts
function getUserContracts(userId) {
    return fetch(`/api/users/` + userId + "/contracts", {
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

function getIdUser() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("id"))) {
        location.replace("/overview");
        return;
    }
    return search.get("id");

}
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

//Fetches user invoices
function getInvoices(companyId, userId) {
    return fetch('/api/companies/' +companyId + '/invoices/' + userId + '?' + getQueryParamsForInvoices(), {
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

//Formats user contract element
function createUserContractItem(userContract) {
    const userContractContainer = document.createElement("div");
    userContractContainer.classList.add("text-text", "whitespace-nowrap", "bg-primary", "py-6", "px-6", "rounded-xl", "cursor-pointer", "flex", "w-fit", "flex-col", "items-center", "justify-center")


    const userContractRole = document.createElement("div");
    userContractRole.classList.add("font-bold");
    userContractRole.innerText = userContract.contract.role;
    userContractContainer.append(userContractRole);

    const userContractWage = document.createElement("div");
    userContractWage.innerText = userContract.hourlyWage / 100 + " €";
    userContractContainer.append(userContractWage);

    const buttonStatusDiv = document.createElement("div")
    buttonStatusDiv.classList.add("mt-1")

    const enableDiv = document.createElement("div");
    enableDiv.classList.add("hidden", "flex", "flex-row", "space-between", "bg-accent-success", "p-2", "gap-2", "cursor-pointer", "items-center", "justify-center", "rounded-xl");
    const checkmarkImage = document.createElement("img");
    checkmarkImage.src = "/static/icons/checkmark.svg";
    checkmarkImage.classList.add("h-4", "w-4")
    checkmarkImage.alt = "enable contract"
    enableDiv.append(checkmarkImage)
    const enableText = document.createElement("p");
    enableText.innerText = "Enable Contract";
    enableText.classList.add("text-text");
    enableDiv.append(enableText);
    buttonStatusDiv.append(enableDiv)

    const disableDiv = document.createElement("div");
    disableDiv.classList.add("hidden", "flex", "flex-row", "space-between", "bg-accent-fail", "p-2", "gap-2", "cursor-pointer", "items-center", "justify-center", "rounded-xl");
    const crossImage = document.createElement("img");
    crossImage.src = "/static/icons/white-cross.svg";
    crossImage.classList.add("h-4", "w-4")
    crossImage.alt = "disable contract"
    disableDiv.append(crossImage)
    const disableText = document.createElement("p");
    disableText.innerText = "Disable Contract";
    disableText.classList.add("text-text");
    disableDiv.append(disableText);
    buttonStatusDiv.append(disableDiv)

    userContractContainer.append(buttonStatusDiv)

    disableDiv.addEventListener("click", async () => {
        await disableUserContract(userContract)
        enableDiv.classList.remove("hidden");
        disableDiv.classList.add("hidden");
    })
    enableDiv.addEventListener("click", async () => {
        await enableUserContract(userContract)
            enableDiv.classList.add("hidden");
            disableDiv.classList.remove("hidden");
    })
    if(userContract.active === true) {
        enableDiv.classList.add("hidden");
        disableDiv.classList.remove("hidden");
    } else {
        disableDiv.classList.add("hidden");
        enableDiv.classList.remove("hidden");
    }

    return userContractContainer;
}

function disableUserContract(contract){
    console.log(contract)
    fetch ("/api/companies/"+ contract.contract.company.id + "/contracts/" + contract.contract.id + "/employees/" + contract.id,
        {
            method: "DELETE",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`,
            }
        })
        .then (response => console.log(response))

        // .then (response => alertPopUp("Disabled contract successfully", true))
        .catch(e => console.log(e))

    // .catch(e => alertPopUp("Unable to disable the contract"))
}
function enableUserContract(contract){
    console.log(contract)
    fetch ("/api/companies/"+ contract.contract.company.id + "/contracts/" + contract.contract.id + "/employees/" + contract.id,
        {
            method: "POST",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`,
            }
        })
        .then (response => alertPopUp("Enabled contract successfully", true))
        .catch(e => alertPopUp("Unable to enable the contract"))
}

//Formats user invoices element
function createInvoiceItem(workedWeek) {
    const entryContainer = document.createElement("a");
    entryContainer.classList.add("rounded-xl", "bg-primary", (workedWeek.status === "NOT_CONFIRMED" || workedWeek.status === "CONFIRMED") ? "py-3" : "py-2", "pl-4", "pr-2", "relative", "flex", "justify-between");
    // entryContainer.href = "/request?worked_week=" + workedWeek.id;
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

//Fetches companies the user works for
function getCompanies(userId) {
    return fetch(`/api/users/` + userId + "/companies", {
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

function alertPopUp(message, positive) {
    let confirmation = document.getElementById("alertPopup");
    let accent = document.getElementById("accent")
    let image = document.getElementById("confirmationIcon")
    let p = document.getElementById("popUpAlertParagraph")
    p.innerText = message

    if (positive) {
        accent.classList.add("bg-accent-success")
        image.src = "/static/icons/checkmark.svg"
    } else {
        accent.classList.add("bg-[#FD8E28]")
        image.src = "/static/icons/light-white.svg"
    }
    confirmation.classList.remove("hidden");
    setTimeout(function () {
            confirmation.classList.add("hidden");
            if (positive) {
                accent.classList.remove("bg-accent-success")
            } else {
                accent.classList.remove("bg-[#FD8E28]")
            }
        }, 2000
    );
}
