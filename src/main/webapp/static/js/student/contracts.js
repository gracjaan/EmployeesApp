let arrayOfDivs = [];
let currentPage = 0;
let lastIndex = 0;
window.addEventListener("helpersLoaded", async () => {
    //updates the page with the contracts of the user
    updatePage(await obtainContractsForUser(getUserId()))
})

//gets all the contracts for the user
function obtainContractsForUser(uid) {
    return fetch("/api/users/" + uid + "/contracts", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

//gets an invoice for all the hours that are worked under the contract
function obtainInvoice(contract) {
    return fetch("/api/users/" + getUserId() + "/contracts/" + contract.id + "/invoices?totalHours=true&company=true", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

//updates the page by inserting the given contract onto the right element
async function updatePage(contracts) {
    const entries = document.getElementById("entries");
    entries.innerText = "";

    for (const contract of contracts) {
        const invoice = await obtainInvoice(contract);
        arrayOfDivs.push(entries.appendChild(createEntry(contract, invoice)))
    }

    if (arrayOfDivs === null || arrayOfDivs.length < 1){
        const noContracts = document.createElement("div");
        noContracts.classList.add("text-text", "font-bold", "w-full", "flex", "justify-center", "my-2");
        noContracts.innerText = "No contracts yet";
        entries.append(noContracts)
    }

    arrayOfDivs[0].classList.toggle("hidden");
    lastIndex = arrayOfDivs.length - 1;
}

function handleRightClick() {
    if (currentPage < lastIndex) {
        currentPage += 1;
        arrayOfDivs[currentPage - 1].classList.toggle("hidden");
        arrayOfDivs[currentPage].classList.toggle("hidden");
    } else {
        arrayOfDivs[currentPage].classList.toggle("hidden");
        currentPage = 0;
        arrayOfDivs[currentPage].classList.toggle("hidden");
    }


}

function handleLeftClick() {
    if (currentPage === 0) {
        arrayOfDivs[currentPage].classList.toggle("hidden");
        currentPage = lastIndex;
        arrayOfDivs[currentPage].classList.toggle("hidden");
    } else {
        currentPage -= 1;
        arrayOfDivs[currentPage + 1].classList.toggle("hidden");
        arrayOfDivs[currentPage].classList.toggle("hidden");
    }
}

//creates the contract entry with the invoice
function createEntry(userContract, invoices) {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-secondary", "p-4", "relative", "flex-col", "gap-2", "justify-between", "w-full", "h-full", "hidden");
    entryContainer.style.overflowY = "auto";

    const rowcontainer = document.createElement("div");
    rowcontainer.classList.add("w-full", "flex", "flex-row", "justify-between", "items-center")

    const leftrow = document.createElement("div");

    const entryInfo0 = document.createElement("div");
    entryInfo0.classList.add("text-text", "font-bold", "uppercase");
    entryInfo0.innerText = userContract.contract.company.name;
    leftrow.appendChild(entryInfo0);

    const entryInfo1 = document.createElement("div");
    entryInfo1.classList.add("text-text", "font-bold", "uppercase");
    entryInfo1.innerText = userContract.contract.role;
    leftrow.appendChild(entryInfo1);

    const rightrow = document.createElement("button");
    rightrow.addEventListener("click", () => generateAllInvoices(userContract));

    const img = document.createElement("img");
    img.alt = "download-all"
    img.classList.add("h-8", "w-8");
    img.src = "/static/icons/download.svg"
    rightrow.appendChild(img)

    rowcontainer.appendChild(leftrow);
    rowcontainer.appendChild(rightrow)
    entryContainer.appendChild(rowcontainer);

    const entryInfo2 = document.createElement("div");
    entryInfo2.classList.add("text-text", "mt-4", "mb-4", "text-justify");
    entryInfo2.innerText = userContract.contract.description;
    entryContainer.appendChild(entryInfo2);

    const entryInfo3 = document.createElement("div");
    entryInfo3.classList.add("overflow-auto");

    for (const i of invoices) {
        const ei = document.createElement("div");
        ei.classList.add("bg-primary", "rounded-lg", "mt-2", "p-4", "flex", "flex-row", "justify-between");

        const eo = document.createElement("div");
        eo.classList.add("text-text", "font-bold")
        eo.innerText = "Week " + i.week;
        ei.appendChild(eo)

        const ep = document.createElement("div");
        ep.classList.add("text-text")
        ep.innerText = (i.totalMinutes)/60 + "H";
        ei.appendChild(ep)

        const ea = document.createElement("button");
        ea.addEventListener("click", () => generateInvoice(userContract, i));
        //onclick and others

        const image1 = document.createElement("img");
        image1.classList.add("h-6", "w-6");
        image1.src = "/static/icons/download-single.svg"
        ea.appendChild(image1);

        ei.appendChild(ea)

        entryInfo3.appendChild(ei);
    }

    entryContainer.appendChild(entryInfo3);

    return entryContainer;
}

//generates a invoice given the contract and the overall invoice for the contract
function generateInvoice (contract, invoice) {
    fetch(`/api/users/${getUserId()}/contracts/${contract.id}/invoices/download/${invoice.year}/${invoice.week}`, {
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

//gets all the separate invoices for a contract
function generateAllInvoices (contract) {
    fetch(`/api/users/${getUserId()}/contracts/${contract.id}/invoices/download`, {
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

