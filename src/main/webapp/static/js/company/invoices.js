window.addEventListener("helpersLoaded", async () => {
    /* @TODO download invoices */
    const week = document.getElementById("week");

    await updateHours(parseInt(week.getAttribute("data-year")), parseInt(week.getAttribute("data-week")))
    week.addEventListener("change", (e) => {
        updateHours(e.detail.year, e.detail.week);
    })

    const hours = document.getElementById("hours");
    hours.addEventListener("change", (e) => {
       updateHours(parseInt(week.getAttribute("data-year")), parseInt(week.getAttribute("data-week")))
    })

    const contract = document.getElementById("contract");
    contract.addEventListener("change", (e) => {
        updateHours(parseInt(week.getAttribute("data-year")), parseInt(week.getAttribute("data-week")))
    })

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

    for (const workedWeek of request) {
        entries.appendChild(createEntry(workedWeek));
    }
}

function createEntry(workedWeek) {
    // const testcontainter = document.createElement("a");

    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-primary", workedWeek.approved === null ? "py-3" : "py-2", "pl-4", "pr-2", "relative", "flex", "justify-between");

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "grid-cols-[3fr_2fr_2fr_1fr]", "grid", "items-center");
    entryContainer.appendChild(entryInfo);

    const name = document.createElement("div");
    name.classList.add("text-text", "font-bold", "uppercase");
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

    const downloadContainer = document.createElement("div");
    downloadContainer.classList.add("flex", "items-center");
    entryContainer.appendChild(downloadContainer);

    const download1 = document.createElement("button");
    download1.classList.add("mr-5");
    download1.setAttribute("id", "download1")
    downloadContainer.appendChild(download1);

    entryContainer.addEventListener("click",(e) => {
        if (download1 === e.target || download1.contains(e.target)) {
            downloadInvoice();
        } else {
            window.location.href = "/earnit/request?worked_week=" + workedWeek.id;
        }
    });

    const image1 = document.createElement("img");
    image1.classList.add("h-6", "w-6");
    image1.src = "/earnit/static/icons/downloadSingle.svg"
    download1.appendChild(image1);

    if (workedWeek.approved !== null) {
        const statusContainer = document.createElement("div");
        statusContainer.classList.add("w-full", "flex", "justify-end")
        entryInfo.appendChild(statusContainer);

        const status = document.createElement("div");
        status.classList.add("rounded-xl", workedWeek.approved ? "bg-accent-success" : "bg-accent-fail", "p-2", "items-center", "text-white", "w-fit", "aspect-square", "flex", "justify-center", "items-center")
        statusContainer.appendChild(status);

        const img = document.createElement("img");
        img.alt = "checkmark";
        img.src = `/earnit/static/icons/${workedWeek.approved ? "checkmark" : "white-cross"}.svg`;
        img.classList.add("w-4", "h-4")
        status.append(img);
    }

    return entryContainer;
}

function downloadInvoice() {
    alert("Download Single")
}

function downloadAllInvoice() {
    alert("jeMoederPoeder")
}

function getQueryParams() {
    const order = getOrder();
    return `user=true&contract=true&totalHours=true${order.length > 0 ? `&order=${order}` : ""}`
}

function getRequestForCompany(companyId, year, week, token) {
    return fetch(`/earnit/api/companies/${companyId}/invoices/${year}/${week}?${getQueryParams()}`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .catch(() => null);
}