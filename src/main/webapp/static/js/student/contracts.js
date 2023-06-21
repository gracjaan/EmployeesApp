let arrayOfDivs = [];
let currentPage = 0;
let lastIndex = 0;
window.addEventListener("helpersLoaded", async () => {
    updatePage(await obtainContractsForUser(getUserId()))
})

function obtainContractsForUser(uid) {
    return fetch("/earnit/api/users/" + uid + "/contracts", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

function obtainInvoice(contract) {
    return fetch("/earnit/api/users/" + getUserId() + "/contracts/" + contract.id + "/invoices?totalHours=true&company=true", {
        headers: {
            'authorization': `token ${getJWTCookie()}`
        }
    })
        .then(response => response.json())
        .catch(e => null);
}

async function updatePage(contracts) {
    const entries = document.getElementById("entries");
    entries.innerText = "";

    for (const contract of contracts) {
        const invoice = await obtainInvoice(contract);
        arrayOfDivs.push(entries.appendChild(createEntry(contract, invoice)))
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
    if (currentPage == 0) {
        arrayOfDivs[currentPage].classList.toggle("hidden");
        currentPage = lastIndex;
        arrayOfDivs[currentPage].classList.toggle("hidden");
    } else {
        currentPage -= 1;
        arrayOfDivs[currentPage + 1].classList.toggle("hidden");
        arrayOfDivs[currentPage].classList.toggle("hidden");
    }
}

function createEntry(contract, invoice) {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-secondary", "p-4", "relative", "flex-col", "gap-2", "justify-between", "w-full", "h-full", "hidden");
    entryContainer.style.overflowY = "auto";

    const entryInfo0 = document.createElement("div");
    entryInfo0.classList.add("text-text", "font-bold", "uppercase");
    entryInfo0.innerText = invoice[0].company.name;
    entryContainer.appendChild(entryInfo0);

    const entryInfo1 = document.createElement("div");
    entryInfo1.classList.add("text-text", "font-bold", "uppercase");
    entryInfo1.innerText = contract.contract.role;
    entryContainer.appendChild(entryInfo1);

    const entryInfo2 = document.createElement("div");
    entryInfo2.classList.add("text-text", "mt-4", "mb-4");
    entryInfo2.innerText = contract.contract.description;
    entryContainer.appendChild(entryInfo2);

    const entryInfo3 = document.createElement("div");
    entryInfo3.classList.add("overflow-auto");

    for (const i of invoice) {
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
        ea.addEventListener("click", () => generateInvoice(contract, i));
        //onclick and others

        const image1 = document.createElement("img");
        image1.classList.add("h-6", "w-6");
        image1.src = "/earnit/static/icons/download-single.svg"
        ea.appendChild(image1);

        ei.appendChild(ea)

        entryInfo3.appendChild(ei);
    }

    entryContainer.appendChild(entryInfo3);

    return entryContainer;
}

function generateInvoice (contract, invoice) {
    return
}


// todo consider what information we want to include
// todo consider mobile view
