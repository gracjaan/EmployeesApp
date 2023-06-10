window.addEventListener("helpersLoaded", async () => {
    const name = document.getElementById("name");
    const email = document.getElementById("email");
    const contracts = document.getElementById("contracts");

    const user = await getUser();
    console.log(user)

    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    email.innerText = user.email;

    for (const userContract of user.userContracts) {
        contracts.append(createUserContractItem(userContract));
    }
})

function createUserContractItem(userContract) {
    const userContractContainer = document.createElement("div");
    userContractContainer.classList.add("text-text", "font-bold", "whitespace-nowrap", "bg-primary", "py-2", "px-4", "rounded-xl", "cursor-pointer", "flex", "justify-between")
    userContractContainer.addEventListener("click", () => {
        location.href = "/earnit/contracts#" + userContract.contract.id;
    })

    const userContractRole = document.createElement("div");
    userContractRole.classList.add("font-bold");
    userContractRole.innerText = userContract.contract.role;
    userContractContainer.append(userContractRole);

    const userContractWage = document.createElement("div");
    userContractWage.classList.add("font-bold");
    userContractWage.innerText = userContract.hourlyWage * 100 + " €";
    userContractContainer.append(userContractWage);

    return userContractContainer;
}

function getUserId() {
    const search = new URLSearchParams(location.search);
    if (!search.has("id")) {
        history.back()
        return;
    }

    return search.get("id");
}

function getUser() {
    return fetch(`/earnit/api/companies/${getUserCompany()}/students/${getUserId()}?userContracts=true&userContractsContract=true`, {
            method: "GET",
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        }
    )
        .then(res => res.json())
        .catch(() => null)
}