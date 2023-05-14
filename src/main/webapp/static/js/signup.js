// Validation
const validateFirstName = (firstName) => validateName(firstName);
const validateLastName = (lastName) => validateName(lastName);
const validateName = (name) => name.length > 2;

// At least 8 characters, min 1: number, lowercase, uppercase and special character
const validatePassword = (password) => /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$/.test(password);

const validateConfirmPassword = (confirmPassword, password) => password === confirmPassword;

// https://stackoverflow.com/questions/46155/how-can-i-validate-an-email-address-in-javascript
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
    'company-info': companyInfo,
    'company-account': companyAccount,
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

        updateContent("student-account");
    })
}

function studentAccount() {
    const back = document.getElementById("student-account-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("student-info");
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

        // TODO: POST '/earnit/api/users', with email, password, first name, last name, last name prefix.

    });
}

function companyInfo() {
    const back = document.getElementById("company-info-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("choose");
    });

    submit.addEventListener("click", () => {
        if (state !== 'company-info') return;

        const name = document.getElementById("company-name").value.trim();
        if (!validateName(name)) {
            document.getElementById("error").innerText = "Company name needs to be at least 3 characters";
            document.getElementById("error").classList.remove("hidden");
            return;
        }

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

        updateContent("company-account");
    })
}

function companyAccount() {
    const back = document.getElementById("company-account-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("company-info");
    });

    submit.addEventListener("click", () => {
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

        // TODO: POST '/earnit/api/users', with email, password, first name, last name, last name prefix.
        // TODO: POST '/earnit/api/companies', with user, company name.
    });
}