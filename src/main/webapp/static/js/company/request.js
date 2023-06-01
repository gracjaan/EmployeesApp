window.addEventListener("helpersLoaded", async () => {
    const search = new URLSearchParams(location.search);
    if (!search.has("worked_week")) {
        location.replace("/earnit/requests");
        return;
    }

    const request = await getRequestForCompany(getUserCompany(), search.get("worked_week"), getJWTCookie());
    if (request === null) {
        // location.replace("/earnit/requests");
        // return;
    }

    console.log(request)
});

function getRequestForCompany(companyId, workedWeekId, token) {
    return fetch(`/earnit/api/companies/${companyId}/approves/${workedWeekId}?user=true&contract=true&hours=true`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .catch(() => null);
}