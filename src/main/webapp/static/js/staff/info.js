window.addEventListener("helpersLoaded", async () => {
    const companies = await getCompany();
    const users = await getUser();
});

async function getUser(userId){
    return await fetch("/earnit/api/users/" + userId,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
        .catch(() => null);
}

async function getCompany(companyId){
    return await fetch("/earnit/api/companies/" + companyId,
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json())
        .catch(() => null);

}

function getIdCompany() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("user") && !search.has("company")) || (!search.has("user") && !search.has("company"))) {
        location.replace("/earnit/overview");
        return;
    }
    if (search.has("company")) {
        return search.get("company");
    }
    return null;
}
function getIdUser() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("user") && !search.has("company")) || (!search.has("user") && !search.has("company"))) {
        location.replace("/earnit/overview");
        return;
    }
    if (search.has("user")){
        return search.get("user");
    }
    return null;

}
