window.addEventListener("helpersLoaded", async () => {
    const companies = await getUser();
    const users = await getStudents();
});

function getUser(){}

function getId() {
    const search = new URLSearchParams(location.search);
    if ((!search.has("user") && !search.has("company")) || (!search.has("user") && !search.has("company"))) {
        location.replace("/earnit/overview");
        return;
    }
    if (search.has("user")){
        return search.get("user");
    }
    else {
        return search.get("company");
    }
}
