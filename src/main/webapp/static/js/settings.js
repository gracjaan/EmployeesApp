window.addEventListener("helpersLoaded", async () => {
    function getCompany() {
        if (getUserCompany() === null) return undefined;
        return fetch("/earnit/api/companies/" + getUserCompany(), {
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Accept': 'application/json',
            }
        }).then(res => res.json())
            .catch(() => null)
    }

    function updatePage(user, company) {
        if (user !== null) {
            const email = document.getElementById("email");
            const firstName = document.getElementById("firstname");
            const lastName = document.getElementById("lastname");
            const lastNamePrefix = document.getElementById("lastnameprefix");
            const address = document.getElementById("address");
            const kvk = document.getElementById("kvk");
            const btw = document.getElementById("btw");

            email.value = user.email;
            firstName.value = user.firstName;
            lastName.value = user.lastName;
            lastNamePrefix.value = user.lastNamePrefix;
            address.value = user.address;
            kvk.value = user.kvk;
            btw.value = user.btw;
        }

        if (company !== null && company !== undefined) {
            const companyElement = document.getElementById("company");
            companyElement.classList.remove("hidden")

            const name = document.getElementById("name");
            name.value = company.name;
        } else if (company === undefined) {
            const companyElement = document.getElementById("company");
            companyElement.classList.add("hidden")
        }
    }

    updatePage(await getUser(), await getCompany());

    function updateUser(user) {
        return fetch("/earnit/api/users/" + getUserId(), {
            method: 'put',
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(user)
        }).then(async res => ({
            status: res.status,
            json: await res.json()
        }))
            .catch(() => null)
    }

    function updateCompany(company) {
        return fetch("/earnit/api/companies/" + getUserCompany(), {
            method: 'put',
            headers: {
                'authorization': `token ${getJWTCookie()}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(company)
        }).then(async res => ({
            status: res.status,
            json: await res.json()
        }))
            .catch(() => null)
    }

    document.getElementById("update-user").addEventListener("click", async () => {
        const error = document.getElementById("user-error");
        error.classList.add("hidden");
        const success = document.getElementById("user-success");
        success.classList.add("hidden");

        const email = document.getElementById("email");
        const firstName = document.getElementById("firstname");
        const lastName = document.getElementById("lastname");
        const lastNamePrefix = document.getElementById("lastnameprefix");
        const address = document.getElementById("address");
        const kvk = document.getElementById("kvk");
        const btw = document.getElementById("btw");

        const updatedUser = await updateUser({
            email: email.value,
            firstName: firstName.value,
            lastName: lastName.value,
            lastNamePrefix: lastNamePrefix.value,
            address: address.value,
            kvk: kvk.value,
            btw: btw.value
        })

        if (updatedUser.status === 200) {
            success.classList.remove("hidden");
            success.innerText = "Updated user";
            updatePage(updatedUser.json, null);
            return;
        }

        error.classList.remove("hidden");

        if (updatedUser.status === 409) {
            error.innerText = `The ${updatedUser.json.field} is already taken`
            return;
        }

        if (updatedUser.status === 422) {
            error.innerText = `The ${updatedUser.json.field} is invalid`
            return;
        }

        error.innerText = "There was an error while trying to update";
    })


    document.getElementById("update-company").addEventListener("click", async () => {
        const error = document.getElementById("company-error");
        error.classList.add("hidden");
        const success = document.getElementById("company-success");
        success.classList.add("hidden");

        const name = document.getElementById("name");

        const updatedCompany = await updateCompany({
            name: name.value
        })

        if (updatedCompany.status === 200) {
            success.classList.remove("hidden");
            success.innerText = "Updated company";
            updatePage(null, updatedCompany.json);
            return;
        }

        error.classList.remove("hidden");

        if (updatedCompany.status === 409) {
            error.innerText = `The ${updatedCompany.json.field} is already taken`
            return;
        }

        if (updatedCompany.status === 422) {
            error.innerText = `The ${updatedCompany.json.field} is invalid`
            return;
        }

        error.innerText = "There was an error while trying to update";
    })
})