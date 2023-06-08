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

async function updatePage(contracts) {
    const entries = document.getElementById("entries");
    entries.innerText = "";

    for (const contract of contracts) {
        entries.appendChild(createEntry(contract))
    }
}

function createEntry(contract) {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-secondary", "p-4", "relative", "flex-col", "justify-between", "w-full", "h-full");

    const entryInfo1 = document.createElement("div");
    entryInfo1.classList.add("text-text", "font-bold", "uppercase");
    entryInfo1.innerText = contract.contract.role;
    entryContainer.appendChild(entryInfo1);

    const entryInfo2 = document.createElement("div");
    entryInfo2.classList.add("text-text");
    entryInfo2.innerText = contract.contract.description;
    entryContainer.appendChild(entryInfo2);

    return entryContainer;
}