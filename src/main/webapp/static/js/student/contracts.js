let arrayOfDivs = [];
let currentPage = 0;
let lastIndex = 0;
window.addEventListener("helpersLoaded", async () => {
    updatePage(await obtainContractsForUser(getUserId()))
    arrayOfDivs[0].classList.toggle("hidden");
    lastIndex = arrayOfDivs.length - 1;
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
        arrayOfDivs.push(entries.appendChild(createEntry(contract)))
    }
}

function handleRightClick () {
    if (currentPage < lastIndex){
        currentPage += 1;
        arrayOfDivs[currentPage-1].classList.toggle("hidden");
        arrayOfDivs[currentPage].classList.toggle("hidden");
    }
    else {
        arrayOfDivs[currentPage].classList.toggle("hidden");
        currentPage = 0;
        arrayOfDivs[currentPage].classList.toggle("hidden");
    }


}

function handleLeftClick () {
    if (currentPage == 0){
        arrayOfDivs[currentPage].classList.toggle("hidden");
        currentPage = lastIndex;
        arrayOfDivs[currentPage].classList.toggle("hidden");
    }
    else {
        currentPage -= 1;
        arrayOfDivs[currentPage+1].classList.toggle("hidden");
        arrayOfDivs[currentPage].classList.toggle("hidden");
    }
}

function createEntry(contract) {
    const entryContainer = document.createElement("div");
    entryContainer.classList.add("rounded-xl", "bg-secondary", "p-4", "relative", "flex-col", "justify-between", "w-full", "h-full", "hidden");

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


// todo consider what information we want to include
// todo consider mobile view
