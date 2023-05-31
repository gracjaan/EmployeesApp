window.addEventListener("helpersLoaded", async () => {
    console.log(await getRequestsForCompany(getUserCompany(), getJWTCookie()));
});

function getRequestsForCompany(uid, token) {
    return fetch(`/earnit/api/companies/${uid}/approves`, {
        headers: {
            'authorization': `token ${token}`,
            'accept-type': 'application/json'
        }
    })
        .then(async (res) => await res.json())
        .catch(() => []);
}