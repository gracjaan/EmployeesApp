window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompanies();

    const companiesElement = document.getElementById("dropdown-company-content");
    companiesElement.innerText = ""

    if (companies.length === 0) {
        const noCompanies = document.createElement("div");
        noCompanies.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
        noCompanies.innerText = "No companies";
        companiesElement.append(noCompanies)
    }

    for (const company of companies) {
        companiesElement.append(createCompany(company));
    }

})

async function getCompanies() {
    return await fetch("/api/companies",
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
    companyDiv.setAttribute("company-id", company.id)

    const encapsulatingDiv = document.createElement("div");
    encapsulatingDiv.classList.add("flex", "items-center");

    const companyImage = document.createElement("img");
    companyImage.alt = "company logo"
    companyImage.classList.add("h-14", "mr-8");
    companyImage.src = "/static/icons/building.svg"

    const companyName = document.createElement("p");
    companyName.classList.add("text-text", "font-bold", "text-2xl");
    companyName.innerText = company.name;
    encapsulatingDiv.append(companyImage);
    encapsulatingDiv.append(companyName);

    companyDiv.append(encapsulatingDiv)

}

function submitForm () {
    const cid = document.getElementById("company-name-display").getAttribute("company-id")
    const name = document.getElementById("name").value;
    const description = document.getElementById("description").value;

    const formData = {
        id: null,
        role: name,
        description: description
    };

    fetch ("/api/companies/"+ cid + "/contracts/",
        {
            method: "POST",
            body: JSON.stringify(formData),
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`,
                'content-type': "application/json"
            }
        })
        .then (response => alertPopUp("Created contract successfully", true))
        .catch(e => console.error(e))
}

function alertPopUp(message, positive) {
    let confirmation = document.getElementById("successfulContractCreation");
    let accent = document.getElementById("accent")
    let image = document.getElementById("confirmationIcon")
    let p = document.getElementById("popUpParagraph")
    p.innerText = message

    if (positive){
        accent.classList.add("bg-accent-success")
        image.src = "/static/icons/checkmark.svg"
    }
    else{
        accent.classList.add("bg-accent-fail")
        image.src = "/static/icons/white-cross.svg"
    }
    confirmation.classList.remove("hidden");
    setTimeout(function (){
        confirmation.classList.add("hidden");
        if (positive){
            accent.classList.remove("bg-accent-success")
        }
        else{
            accent.classList.remove("bg-accent-fail")
        }
        }, 2000
    );

}

