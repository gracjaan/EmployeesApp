window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();

    const companiesElement = document.getElementById("dropdown-company-content");
    companiesElement.innerText = ""

    for (const company of companies) {
        companiesElement.append(createCompany(company));
    }

})

async function getCompanies() {
    return await fetch("/earnit/api/companies",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
        .catch(() => null);
}

function createCompany(company) {
    const div = document.createElement("div");
    div.addEventListener("click", ()=>{
        editCompanyInfo(company)
        companyId=company.id;
    })
    div.setAttribute("company-id", company.id)

    const a = document.createElement("a");

    div.append(a);

    const itemContainer = document.createElement("div");
    itemContainer.classList.add("hover:bg-gray-100", "cursor-pointer", "color-text", "rounded-xl", "w-full", "h-fit", "p-2", "pl-4");
    a.append(itemContainer);

    const name = document.createElement("p");
    name.classList.add("text-background");
    name.innerText = company.name;
    itemContainer.append(name);

    return div;
}

function toggleCompany() {
    const dropdown = document.getElementById("dropdown-company-content");
    dropdown.classList.toggle("hidden");
}

document.addEventListener("click", function(event) {
    const dropdown = document.getElementById("dropdown-company-content");
    const button = document.getElementById("dropdown-company-button");

    const targetElement = event.target;

    if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
        dropdown.classList.add("hidden");
    }
});

async function editCompanyInfo(company){
    const companyDiv = document.getElementById("company-name-display");
    companyDiv.innerText = "";

    const encapsulatingDiv = document.createElement("div");
    encapsulatingDiv.classList.add("flex", "flex-center");
    encapsulatingDiv.setAttribute("id", "company-name-display");

    const companyImage = document.createElement("img");
    companyImage.alt = "company logo"
    companyImage.classList.add("h-14", "mr-8");
    companyImage.src = "/earnit/static/icons/building.svg"

    const companyName = document.createElement("p");
    companyName.classList.add("text-text", "font-bold", "text-2xl");
    companyName.innerText = company.name;
    encapsulatingDiv.append(companyImage);
    encapsulatingDiv.append(companyName);

    companyDiv.append(encapsulatingDiv)


//     Now we need to show all the contracts for the company//
    const contractsList = document.getElementById("contract-list");
    contractsList.innerText = "";
    let contracts = await getContracts(company.id)
    console.log(contracts);
    for (const contract of contracts) {
        contractsList.append(createContract(contract));
    }
}

