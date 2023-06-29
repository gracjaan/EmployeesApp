window.addEventListener("helpersLoaded", async () => {
    fetch(`/api/companies/${getUserCompany()}/contracts?userContracts=true&userContractsUser=true`, {
            method: "GET",
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        }
    )
        .then(async res => {
            const contracts = await res.json();

            const contractsElement = document.getElementById("contracts");
            contractsElement.innerText = "";

            for (const contract of contracts) {
                contractsElement.append(createContractItem(contract));
            }

            if (contracts.length === 0){
                const noContracts = document.createElement("div");
                noContracts.classList.add("text-text", "font-bold", "w-full", "flex", "my-2");
                noContracts.innerText = "No contracts";
                contractsElement.append(noContracts)
            }
        })
});

function createContractItem(contract) {
    const contractContainer = document.createElement("div");
    contractContainer.classList.add("w-full", "grid", "grid-cols-[1fr]", "sm:grid-cols-[3fr_1fr]", "sm:flex-row", "gap-4", "bg-secondary", "rounded-2xl", "p-4");
    contractContainer.id = contract.id;

    const contractInformation = document.createElement("div");
    contractInformation.classList.add("flex", "flex-col", "gap-2");
    contractContainer.append(contractInformation);

    const contractInformationRole = document.createElement("div");
    contractInformationRole.classList.add("text-text", "text-2xl", "font-bold");
    contractInformationRole.innerText = contract.role;
    contractInformation.append(contractInformationRole);

    const contractInformationDescription = document.createElement("div");
    contractInformationDescription.classList.add("text-text");
    contractInformationDescription.innerText = contract.description;
    contractInformation.append(contractInformationDescription);

    const contractUsersContainer = document.createElement("div");
    contractUsersContainer.classList.add("flex", "flex-col", "gap-2", "sm:items-end", "overflow-y-auto", "max-h-40", "h-full", "w-full", "scrollbar-custom", "scrollbar-rounded-xl", "scrollbar-track-secondary", "scrollbar-thumb-text");
    contractContainer.append(contractUsersContainer);
    
    for (const userContract of contract.userContracts) {
        const userContractElement = document.createElement("div");
        userContractElement.classList.add("text-text", "whitespace-nowrap", "bg-primary", "w-fit", "gap-2", "py-2", "px-4", "flex", "justify-between", "rounded-xl", "cursor-pointer");
        contractUsersContainer.append(userContractElement);

        userContractElement.addEventListener("click", () => location.href = "/user?id=" + userContract.user.id)

        const userContractElementName = document.createElement("div");
        userContractElementName.classList.add("font-bold")
        userContractElementName.innerText = getName(userContract.user.firstName, userContract.user.lastName, userContract.user.lastNamePrefix);
        userContractElement.append(userContractElementName);

        const userContractElementWage = document.createElement("div");
        userContractElementWage.innerText = userContract.hourlyWage / 100 + ' €';
        userContractElement.append(userContractElementWage);
    }

    return contractContainer;
}