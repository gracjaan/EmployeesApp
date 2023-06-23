window.addEventListener("helpersLoaded", async () => {
    const userId = getIdUser();
    const user = await getUser(userId);
    const userContracts = await getUserContracts(userId)
    const companies = await getCompanies(userId)
    console.log(companies);


    const hours = document.getElementById("hours");
    hours.addEventListener("change", () => updateInvoices())

    const contract = document.getElementById("contract");
    contract.addEventListener("change", () => updateInvoices())

    const week = document.getElementById("week");
    week.addEventListener("change", () => updateInvoices())

    const name = document.getElementById("name");
    const email = document.getElementById("email");
    //const status = document.getElementById("status")
    const contracts = document.getElementById("contracts");

    if (user === null) {
        location.href = "/earnit/error/404"
        return;
    }
    console.log(user.email)
    name.innerText = getName(user.firstName, user.lastName, user.lastNamePrefix);
    email.innerText = user.email;
    // if (user.active){
    //     status.innerText = "enabled";
    // }
    // else {
    //     status.innerText = "disabled";
    // }

    for (const userContract of userContracts) {
        contracts.append(createUserContractItem(userContract));
    }
    await updateInvoices(companies, user);
});


function getUser(userId) {
    return fetch(`/earnit/api/users/` + userId, {
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

function getIdUser() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("user"))) {
        location.replace("/earnit/overview");
        return;
    }
    return search.get("user");

}
