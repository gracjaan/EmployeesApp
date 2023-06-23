// Validation
const validateFirstName = (firstName) => validateName(firstName);
const validateLastName = (lastName) => validateName(lastName);
const validateName = (name) => name.length > 2;

//Validating company details
const validateAddress1 = (address1) => address1.length > 6;
const validateAddress2 = (address2) => address2.length > 0;
const validateKVK = (kvkNumber) => {
    let kvkNumberRegex = /^\d{8}$/;
    return kvkNumberRegex.test(kvkNumber);
};
const validateBTW = (btwNumber) => {
    let btwNumberRegex = /^(NL)?\d{9}B\d{2}$/;
    return btwNumberRegex.test(btwNumber);
}
const validateEmail = (email) => {
    return String(email)
        .toLowerCase()
        .match(
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        );
};
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
            const ca = document.getElementById("company-address");
            const ck = document.getElementById("company-kvk");

            name.value = company.name;
            ca.value = company.address;
            ck.value = company.kvk;

        } else if (company === undefined) {
            const companyElement = document.getElementById("company");
            companyElement.classList.add("hidden")
        }
    }

    updatePage(await getUser(), await getCompany());

    function updateUser(user) {
        const email = document.getElementById("email").value.trim();
        if (!validateEmail(email)) {
            document.getElementById("user-error").innerText = "Invalid email format";
            document.getElementById("user-error").classList.remove("hidden");
            return;
        }

        const firstname = document.getElementById("firstname").value.trim();
        if (!validateFirstName(firstname)) {
            document.getElementById("user-error").innerText = "First name needs to be at least 3 characters";
            document.getElementById("user-error").classList.remove("hidden");
            return;
        }

        const lastname = document.getElementById("lastname").value.trim();
        if (!validateLastName(lastname)) {
            document.getElementById("user-error").innerText = "Last name needs to be at least 3 characters";
            document.getElementById("user-error").classList.remove("hidden");
            return;
        }

        const address = document.getElementById("address").value.trim();
        if (!validateAddress1(address)) {
            document.getElementById("user-error").innerText = "Address needs to be at least 6 characters";
            document.getElementById("user-error").classList.remove("hidden");
            return;
        }

        const kvk = document.getElementById("kvk").value.trim();
        if (!validateKVK(kvk)) {
            document.getElementById("user-error").innerText = "Invalid KVK format";
            document.getElementById("user-error").classList.remove("hidden");
            return;
        }

        const btw = document.getElementById("btw").value.trim();
        if (!validateBTW(btw)) {
            document.getElementById("user-error").innerText = "Invalid BTW format";
            document.getElementById("user-error").classList.remove("hidden");
            return;
        }

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
        const ca = document.getElementById("company-address");
        const ck = document.getElementById("company-kvk");

        const updatedCompany = await updateCompany({
            name: name.value,
            address: ca.value,
            kvk: ck.value
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

// todo: validation of input fields