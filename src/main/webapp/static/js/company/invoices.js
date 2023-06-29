window.addEventListener("helpersLoaded", async () => {
    const week = document.getElementById("week");

    //Whenever something changes in the date, we change the year and week
    await updateHours(parseInt(week.getAttribute("data-year")), parseInt(week.getAttribute("data-week")))
    week.addEventListener("change", (e) => {
        updateHours(e.detail.year, e.detail.week);
    })
    //Whenever the hours change, we update them
    const hours = document.getElementById("hours");
    hours.addEventListener("change", (e) => {
       updateHours(parseInt(week.getAttribute("data-year")), parseInt(week.getAttribute("data-week")))
    })
    //if the contract changes, we update the contract
    const contract = document.getElementById("contract");
    contract.addEventListener("change", (e) => {
        updateHours(parseInt(week.getAttribute("data-year")), parseInt(week.getAttribute("data-week")))
    })
    //If there is a other user, we change the user
    const user = document.getElementById("user");
    user.addEventListener("change", (e) => {
        updateHours(parseInt(week.getAttribute("data-year")), parseInt(week.getAttribute("data-week")))
    })
});

async function updateHours(year, week) {
    const request = await getRequestForCompany(getUserCompany(), year, week, getJWTCookie());
    // Update page to data
    updatePage(request);
}

//Get the order in which the invoices should be ordered
function getOrder() {
    const contract = document.getElementById("contract");
    const contractSelected = contract.getAttribute("data-selected");

    const hours = document.getElementById("hours");
    const hoursSelected = hours.getAttribute("data-selected");

    const user = document.getElementById("user");
    const userSelected = user.getAttribute("data-selected");

    let order = "";
    if (contractSelected > 0) {
        order += "contract.role:" + (contractSelected === "1" ? "asc" : "desc");
    } else if (hoursSelected > 0) {
        order += "worked_week.total_hours:" + (hoursSelected === "1" ? "asc" : "desc");
    } else if (userSelected > 0) {
        order += "user.last_name:" + (userSelected === "1" ? "asc" : "desc");
    }

    return order;
}

//gets the requests for a week and year and will convert the requests in the invoices
function updatePage(request) {
    const entries = document.getElementById("entries");
    entries.innerHTML = "";

    if (request === null || request.length < 1) {
        const noInvoices = document.createElement("div");
        noInvoices.classList.add("text-text", "font-bold", "w-full", "flex", "justify-center", "my-2");
        noInvoices.innerText = "No invoices";
        entries.append(noInvoices);
        return;
    }
    //creates an invoice entry for every single request
    for (const workedWeek of request) {
        entries.appendChild(createEntry(workedWeek));
    }
}

//creates a invoice entry which also allows the user to download the invoice
//for the particular request for a worked week
function createEntry(workedWeek) {
    // const testcontainter = document.createElement("a");

    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-primary", "cursor-pointer", workedWeek.status === "NOT_CONFIRMED" || workedWeek.status === "CONFIRMED" ? "py-3" : "py-2", "px-4", "relative", "flex", "justify-between");

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "sm:grid-cols-[3fr_2fr_2fr_1fr_1fr]", "grid-cols-[1fr_1fr_1fr_1fr]", "grid", "items-center");
    entryContainer.appendChild(entryInfo);

    const name = document.createElement("div");
    name.classList.add("sm:col-span-1", "col-span-4", "break-words", "text-text", "font-bold", "uppercase");
    name.innerText = getName(workedWeek.user.firstName, workedWeek.user.lastName, workedWeek.user.lastNamePrefix);
    entryInfo.appendChild(name);

    const hours = document.createElement("div");
    hours.classList.add("text-text");
    hours.innerText = `${workedWeek.totalMinutes / 60}H`;
    entryInfo.appendChild(hours);

    const role = document.createElement("div");
    role.classList.add("text-text");
    role.innerText = workedWeek.contract.role;
    entryInfo.appendChild(role);

    const statusContainer = document.createElement("div");
    statusContainer.classList.add("w-full", "flex", "justify-end")
    entryInfo.appendChild(statusContainer);

    if (!(workedWeek.status === "NOT_CONFIRMED" || workedWeek.status === "CONFIRMED")) {
        const status = document.createElement("div");
        status.classList.add("rounded-xl", workedWeek.status === "APPROVED" ? "bg-accent-success" : "bg-[#FD8E28]", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center", "items-center")
        statusContainer.appendChild(status);

        const img = document.createElement("img");
        img.alt = "checkmark";
        img.src = `/static/icons/${workedWeek.status === "APPROVED" ? "checkmark" : "light-white"}.svg`;
        img.classList.add("w-4", "h-4")
        status.append(img);
    }
    //For downloading the invoice
    const downloadContainer = document.createElement("div");
    downloadContainer.classList.add("flex", "items-center", "justify-end", "w-full");
    entryInfo.appendChild(downloadContainer);

    const downloadButton = document.createElement("button");
    downloadContainer.appendChild(downloadButton);

    entryContainer.addEventListener("click",(e) => {
        if (downloadButton === e.target || downloadButton.contains(e.target)) {
            downloadInvoice(workedWeek);
        } else {
            window.location.href = "/request?worked_week=" + workedWeek.id;
        }
    });

    const downloadImage = document.createElement("img");
    downloadImage.classList.add("h-6", "w-6");
    downloadImage.src = "/static/icons/downloadSingle.svg"
    downloadButton.appendChild(downloadImage);

    return entryContainer;
}

//get the invoice for a particular week
function downloadInvoice(workedWeek) {
    fetch(`/api/companies/${getUserCompany()}/invoices/download/week/${workedWeek.id}`, {
        headers: {
            'authorization': `token ${getJWTCookie()}`,
        }
    })
        .then(async res =>  ({ data: await res.blob(), filename: res.headers.get("content-disposition").split('filename = ')[1] }))
        .then(({ data, filename }) => {
            const a = document.createElement("a");
            a.href = window.URL.createObjectURL(data);
            a.download = filename;
            a.click();
        });
}

//downloads all the invoices for the company and combines them into a zip
function downloadAllInvoice() {
    const weekSelector = document.getElementById("week");
    const year = parseInt(weekSelector.getAttribute("data-year"));
    const week = parseInt(weekSelector.getAttribute("data-week"));

    fetch(`/api/companies/${getUserCompany()}/invoices/download/${year}/${week}`, {
        headers: {
            'authorization': `token ${getJWTCookie()}`,
        }
    })
        .then(async res =>  ({ data: await res.blob(), filename: res.headers.get("content-disposition").split('filename = ')[1] }))
        .then(({ data, filename }) => {
            const a = document.createElement("a");
            a.href = window.URL.createObjectURL(data);
            a.download = filename;
            a.click();
        });
}

function getQueryParams() {
    const order = getOrder();
    return `user=true&contract=true&totalHours=true${order.length > 0 ? `&order=${order}` : ""}`
}

//Gets a certain request
function getRequestForCompany(companyId, year, week, token) {
    return fetch(`/api/companies/${companyId}/invoices/${year}/${week}?${getQueryParams()}`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .catch(() => null);
}