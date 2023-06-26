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

// At least 8 characters, min 1: number, lowercase, uppercase and special character
const validatePassword = (password) => /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/.test(password);

const validateConfirmPassword = (confirmPassword, password) => password === confirmPassword;

// https://stackoverflow.com/questions/201323/how-can-i-validate-an-email-address-using-a-regular-expression/14075810#14075810
const validateEmail = (email) => {
    return String(email)
        .toLowerCase()
        .match(
            /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        );
};

// State handler
const states = {
    'choose': choose,
    'student-info': studentInfo,
    'student-account': studentAccount,
    'student-details': studentDetails,
    'company-info': companyInfo,
    'company-account': companyAccount,
    'company-details': companyDetails,
}

let state = "choose";
updateContent(state);

function updateContent(newState) {
    setTimeout(() => {
        document.getElementById("error").innerText = "";
        document.getElementById("error").classList.add("hidden");

        if (!(newState in states)) return;

        if (document.getElementById("cache-" + newState) !== null) {
            // Move current to doc
            const currentCacheHolder = document.getElementById("cache-" + state);
            currentCacheHolder.style.display = "none";
            document.body.appendChild(currentCacheHolder);

            // Move new to signup
            const signUpContent = document.getElementById("signup-content");
            const cacheHolder = document.getElementById("cache-" + newState);
            cacheHolder.style.display = "block";
            signUpContent.appendChild(cacheHolder);

            // Update state
            state = newState;
            return;
        }

        fetch(`./${newState}.html`)
            .then((res) => res.text())
            .then(page => {
                const signUpContent = document.getElementById("signup-content");
                if (!signUpContent) {
                    setTimeout(() => updateContent(newState), 100);
                    return;
                }

                if (document.getElementById("cache-" + state) !== null) {
                    // Move current to doc
                    const currentCacheHolder = document.getElementById("cache-" + state);
                    currentCacheHolder.style.display = "none";
                    document.body.appendChild(currentCacheHolder);
                }

                // Create new state
                const cacheHolder = document.createElement("div");
                cacheHolder.id = "cache-" + newState;
                cacheHolder.innerHTML = page;
                signUpContent.appendChild(cacheHolder);

                // Update state
                states[newState](signUpContent.innerHTML);
                state = newState;
            });
    }, 100);
}

function choose() {
    const companyCard = document.getElementById("company");
    const studentCard = document.getElementById("student");
    const submit = document.getElementById("submit");

    // Choose state
    const urlParams = new URLSearchParams(window.location.search);
    const type = urlParams.get('type');
    let selected = null;
    select(type ?? "company");

    // Update url with new selected type
    function updateURL(card) {
        const urlParams = new URLSearchParams(window.location.search);
        const type = urlParams.get('type');

        if (type === card) return;
        urlParams.set("type", card);

        const newURL = window.location.protocol + "//" + window.location.host + window.location.pathname + '?' + urlParams.toString();
        window.history.replaceState({path: newURL}, '', newURL);
    }

    // Select type
    function select(card) {
        updateURL(card);
        if (selected === card) return;

        const cardElement = (card === "student" ? studentCard : companyCard);
        cardElement.classList.remove("border-primary");
        cardElement.classList.add("border-text");

        const otherCardElement = (card !== "student" ? studentCard : companyCard);
        otherCardElement.classList.add("border-primary");
        otherCardElement.classList.remove("border-text");

        selected = card;
    }

    // Add event listeners for type
    companyCard.addEventListener("click", select.bind(undefined, "company"));
    studentCard.addEventListener("click", select.bind(undefined, "student"));

    submit.addEventListener('click', () => {
        if (state !== 'choose') return;

        updateContent(selected + "-info");
    });
}

function studentInfo() {
    const back = document.getElementById("student-info-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("choose");
    });

    submit.addEventListener("click", () => {
        if (state !== 'student-info') return;

        const first = document.getElementById("student-first").value.trim();
        if (!validateFirstName(first)) {
            document.getElementById("error").innerText = "First name needs to be at least 3 characters";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const last = document.getElementById("student-last").value.trim();
        if (!validateLastName(last)) {
            document.getElementById("error").innerText = "Last name needs to be at least 3 characters";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        updateContent("student-details");
    })
}

function studentAccount() {
    const back = document.getElementById("student-account-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("student-details");
    });

    submit.addEventListener("click", () => {
        if (state !== 'student-account') return;

        const email = document.getElementById("student-email").value.trim();
        if (!validateEmail(email)) {
            document.getElementById("error").innerText = "The email address is not valid";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const password = document.getElementById("student-password").value.trim();
        if (!validatePassword(password)) {
            document.getElementById("error").innerText = "The password is not valid. It needs to contain at least: an uppercase letter, a lowercase letter, a number and a special character";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const confirmPassword = document.getElementById("student-confirm-password").value.trim();
        if (!validateConfirmPassword(confirmPassword, password)) {
            document.getElementById("error").innerText = "The password and confirmed password do not match";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const kvk = document.getElementById("student-kvk").value.trim();
        const btw = document.getElementById("student-btw").value.trim();
        const firstName = document.getElementById("student-first").value.trim();
        const lastName = document.getElementById("student-last").value.trim();
        const lastNamePrefix = document.getElementById("student-last-prefix").value.trim();
        const address = document.getElementById("student-address-1").value.trim() + " " + document.getElementById("student-address-2").value.trim();

        document.getElementById("error").innerText = "";
        document.getElementById("error").classList.add("hidden");

        fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email,
                firstName,
                lastName,
                lastNamePrefix,
                password,
                kvk,
                btw,
                address
            })
        }).then(async res => {
            let error = null;

            switch (res.status) {
                case 400:
                    error = "Request failed";
                    break;
                case 422:
                    const field = (await res.json()).field;
                    let fieldName = field;

                    switch (field) {
                        case "firstName":
                            fieldName = "first name";
                            break;
                        case "lastName":
                            fieldName = "last name";
                            break;
                        case "lastNamePrefix":
                            fieldName = "last name prefix";
                            break;
                    }

                    error = "Value for " + fieldName + " is invalid";
                    break;
                case 409:
                    error = "There is already an account registered with this email address"
                    break;
            }

            if (error != null) {
                document.getElementById("error").innerText = error;
                document.getElementById("error").classList.remove("hidden");
                return;
            }

            // Account created goto login
            window.location.replace("/login");
        })
    });
}

function studentDetails() {
    const back = document.getElementById("student-details-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("student-info");
    });

    submit.addEventListener("click", () => {
        if (state !== 'student-details') return;

        const address1 = document.getElementById("student-address-1").value.trim();
        if (!validateAddress1(address1)) {
            document.getElementById("error").innerText = "Address 1 needs to be at least 6 characters";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const address2 = document.getElementById("student-address-2").value.trim();
        if (!validateAddress2(address2)) {
            document.getElementById("error").innerText = "Address 2 needs to be at least 1 character";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const kvk = document.getElementById("student-kvk").value.trim();
        if (!validateKVK(kvk)) {
            document.getElementById("error").innerText = "KVK number needs to be in valid format";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const btw = document.getElementById("student-btw").value.trim();
        if (!validateBTW(btw)) {
            document.getElementById("error").innerText = "BTW number needs to be in valid format";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        updateContent("student-account");
    })
}

function companyInfo() {
    const back = document.getElementById("company-info-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("choose");
    });

    submit.addEventListener("click", () => {
        if (state !== 'company-info') return;

        const first = document.getElementById("company-first").value.trim();
        if (!validateFirstName(first)) {
            document.getElementById("error").innerText = "First name needs to be at least 3 characters";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const last = document.getElementById("company-last").value.trim();
        if (!validateLastName(last)) {
            document.getElementById("error").innerText = "Last name needs to be at least 3 characters";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        updateContent("company-details");
    })
}

let userId = null;

function companyAccount() {
    const back = document.getElementById("company-account-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("company-info");
    });

    submit.addEventListener("click", async () => {
        if (state !== 'company-account') return;

        const email = document.getElementById("company-email").value.trim();
        if (!validateEmail(email)) {
            document.getElementById("error").innerText = "The email address is not valid";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const password = document.getElementById("company-password").value.trim();
        if (!validatePassword(password)) {
            document.getElementById("error").innerText = "The password is not valid. It needs to contain at least: an uppercase letter, a lowercase letter, a number and a special character";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const confirmPassword = document.getElementById("company-confirm-password").value.trim();
        if (!validateConfirmPassword(confirmPassword, password)) {
            document.getElementById("error").innerText = "The password and confirmed password do not match";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        document.getElementById("error").innerText = "";
        document.getElementById("error").classList.add("hidden");

        const firstName = document.getElementById("company-first").value.trim();
        const lastName = document.getElementById("company-last").value.trim();
        const lastNamePrefix = document.getElementById("company-last-prefix").value.trim();

        let kvk = null;
        const btw = null;
        let address = null;

        if (userId === null) {
            const user = await fetch('/api/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({
                    email,
                    firstName,
                    lastName,
                    lastNamePrefix,
                    password,
                    kvk,
                    btw,
                    address
                })
            }).then(async res => {
                let error = null;

                switch (res.status) {
                    case 400:
                        error = "Request failed";
                        break;
                    case 422:
                        const field = (await res.json()).field;
                        let fieldName = field;

                        switch (field) {
                            case "firstName":
                                fieldName = "first name";
                                break;
                            case "lastName":
                                fieldName = "last name";
                                break;
                            case "lastNamePrefix":
                                fieldName = "last name prefix";
                                break;
                        }

                        error = "Value for " + fieldName + " is invalid";
                        break;
                    case 409:
                        error = "There is already an account registered with this email address"
                        break;
                }

                if (error != null) {
                    document.getElementById("error").innerText = error;
                    document.getElementById("error").classList.remove("hidden");
                    return null;
                }

                return await res.json();
            })

            if (user !== null && user.id !== null) {
                userId = user.id;
            }
        }

        const name = document.getElementById("company-name").value.trim();
        address = document.getElementById("company-address-1").value.trim() + " " + document.getElementById("company-address-2").value.trim();
        kvk = document.getElementById("company-kvk").value.trim();

        if (userId !== null) {
            fetch('/api/companies', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({
                    name,
                    userId: userId,
                    kvk,
                    address
                })
            }).then(async res => {
                error = null;

                switch (res.status) {
                    case 400:
                        error = "Request failed";
                        break;
                    case 422:
                        const field = (await res.json()).field;

                        switch (field) {
                            case "name":
                                error = "Value for name is invalid";
                                break;
                            case "userId":
                                error = "Failed to create company with user";
                                break;
                        }

                        break;
                    case 403:
                        error = "Forbidden"
                        break;
                }

                if (error != null) {
                    document.getElementById("error").innerText = error;
                    document.getElementById("error").classList.remove("hidden");
                    return;
                }

                if (res.status !== 200) {
                    document.getElementById("error").innerText = "Could not create company, try again";
                    document.getElementById("error").classList.remove("hidden");
                    return;
                }

                // Account created goto login
                window.location.replace("/login");
            });
        }
    });
}

function companyDetails() {
    const back = document.getElementById("company-details-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("company-info");
    });

    submit.addEventListener("click", () => {
        if (state !== 'company-details') return;

        const name = document.getElementById("company-name").value.trim();
        if (!validateName(name)) {
            document.getElementById("error").innerText = "Company name needs to be at least 3 characters";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const address1 = document.getElementById("company-address-1").value.trim();
        if (!validateAddress1(address1)) {
            document.getElementById("error").innerText = "Address 1 needs to be at least 6 characters";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const address2 = document.getElementById("company-address-2").value.trim();
        if (!validateAddress2(address2)) {
            document.getElementById("error").innerText = "Address 2 needs to be at least 1 character";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        const kvk = document.getElementById("company-kvk").value.trim();
        if (!validateKVK(kvk)) {
            document.getElementById("error").innerText = "KVK number needs to be in valid format";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

        updateContent("company-account");
    })
}