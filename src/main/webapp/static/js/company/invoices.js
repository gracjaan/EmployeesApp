const contract = document.getElementById("contract");
contract.addEventListener("click", () => select("contract"));

const hours = document.getElementById("hours");
hours.addEventListener("click", () => select("hours"));

window.addEventListener("helpersLoaded", async () => {
    /* @TODO download invoices */
    await updateHours();
});

async function updateHours() {
    const request = await getRequestForCompany(getUserCompany(), 2023, 22, getJWTCookie());
    console.log(request)
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

// modified from https://stackoverflow.com/questions/16590500/calculate-date-from-week-number-in-javascript
function getDateOfISOWeek(w, y) {
    const simple = new Date(y, 0, 1 + (w - 1) * 7);
    const dow = simple.getDay();
    const ISOweekStart = simple;

    if (dow <= 4) {
        ISOweekStart.setDate(simple.getDate() - simple.getDay() + 1);
    } else {
        ISOweekStart.setDate(simple.getDate() + 8 - simple.getDay());
    }

    return ISOweekStart;
}

// modified from https://stackoverflow.com/questions/563406/how-to-add-days-to-date
function addDays(date, days) {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
}

function formatNumber(number) {
    return number.toLocaleString('en-US', {
        minimumIntegerDigits: 2,
        useGrouping: false
    });
}