window.addEventListener("helpersLoaded", async () => {
    fetch(`/earnit/api/companies/${getUserCompany()}/contracts?userContracts=true&userContractsUser=true`, {
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
        })
});

function createContractItem(contract) {
    const contractContainer = document.createElement("div");
    contractContainer.classList.add("w-full", "grid", "grid-cols-[1fr]", "sm:grid-cols-[3fr_1fr]", "sm:flex-row", "gap-4", "bg-secondary", "rounded-2xl", "p-4");

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
    contractUsersContainer.classList.add("flex", "flex-col", "gap-2", "overflow-y-auto", "max-h-40", "h-full", "w-full", "scrollbar-custom", "scrollbar-rounded-xl", "scrollbar-track-secondary", "scrollbar-thumb-text");
    contractContainer.append(contractUsersContainer);
    
    for (const userContract of contract.userContracts) {
        const userContractElement = document.createElement("div");
        userContractElement.classList.add("text-text", "font-bold", "whitespace-nowrap", "bg-primary", "py-2", "px-4", "rounded-xl", "w-full", "cursor-pointer");
        userContractElement.innerText = getName(userContract.user.firstName, userContract.user.lastName, userContract.user.lastNamePrefix);
        contractUsersContainer.append(userContractElement);
    }

    return contractContainer;
}