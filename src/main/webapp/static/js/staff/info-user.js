window.addEventListener("helpersLoaded", async () => {
    const user = await getUser();
    createUser(user)
});

function createUser(userId) {

}

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

function getIdUser() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("user"))) {
        location.replace("/earnit/overview");
        return;
    }
    return search.get("user");

}
