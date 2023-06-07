let page = 0;
let size = 0;
let jsonForChange;
const role = document.getElementById("role");
const description = document.getElementById("description")
window.addEventListener("helpersLoaded", async () => {
    fetch("/earnit/api/companies/"+getUserCompany()+"/contracts", {
            method: "GET",
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            }
        }
    )
        .then(async res => {
            const json = await res.json();
            size = json.length;
            console.log(json);
            role.innerHTML = json[page].role;
            description.innerHTML = json[page].description;
            jsonForChange = json;
            console.log(jsonForChange)
        })
});

function changePage(next) {
    if (next === true) page++;
    else page--;

    if (page >= size) {
        page = 0;
    } else if (page < 0) {
        page = size - 1;
    }

    console.log(page)
    role.innerHTML = jsonForChange[page].role;
    description.innerHTML = jsonForChange[page].description;
}