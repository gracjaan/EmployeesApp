const contract = document.getElementById("contract");
contract.addEventListener("click", () => select("contract"));

const hours = document.getElementById("hours");
hours.addEventListener("click", () => select("hours"));

window.addEventListener("helpersLoaded", async () => {
    /* @TODO download invoices */
    const week = document.getElementById("week");

    await updateHours(parseInt(week.getAttribute("data-year")), parseInt(week.getAttribute("data-week")))
    week.addEventListener("change", (e) => {
        updateHours(e.detail.year, e.detail.week);
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

    let order = "";
    if (contractSelected > 0) {
        order += "contract.role:" + (contractSelected === "1" ? "asc" : "desc");
    } else if (hoursSelected > 0) {
        order += "hours.total:" + (hoursSelected === "1" ? "asc" : "desc");
    }

    return order;
}

function updatePage(request) {
    const entries = document.getElementById("entries");
    entries.innerHTML = "";
    for (const workedWeek of request) {
        entries.appendChild(createEntry(workedWeek));
    }
}

function createEntry(workedWeek) {
    const entryContainer = document.createElement("a");
    entryContainer.classList.add("rounded-xl", "bg-primary", "p-4", "relative", "flex", "justify-between");
    entryContainer.href = "/earnit/request?worked_week=" + workedWeek.id;

    const entryInfo = document.createElement("div");
    entryInfo.classList.add("w-full", "grid-cols-[3fr_1fr_1fr]", "grid");
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

    return entryContainer;
}

function getQueryParams() {
    const order = getOrder();
    return `user=true&contract=true&totalHours=true${order.length > 0 ? `&order=${order}`: ""}`
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

async function select(type) {
    const contract = document.getElementById("contract");
    const contractSelected = contract.getAttribute("data-selected");

    const hours = document.getElementById("hours");
    const hoursSelected = hours.getAttribute("data-selected");

    if (type === "contract") {
        hours.setAttribute("data-selected", "0");

        let state = Number.parseInt(contractSelected ?? "0") + 1;
        if (state > 2) state = 0;
        contract.setAttribute("data-selected", state + "");
    } else {
        contract.setAttribute("data-selected", "0");

        let state = Number.parseInt(hoursSelected ?? "0") + 1;
        if (state > 2) state = 0;
        hours.setAttribute("data-selected", state + "");
    }

    await updateHours();
}