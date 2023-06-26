window.addEventListener("helpersLoaded", () => {
    const email = document.getElementById("email");
    const password = document.getElementById("password");
    const submit = document.getElementById("submit");
    const error = document.getElementById("error");

    document.addEventListener("keypress", function(event) {
        // If the user presses the "Enter" key on the keyboard
        if (event.key === "Enter") {
            // Cancel the default action, if needed
            event.preventDefault();

            // Move focus between elements
            if (document.activeElement === email) {
                password.focus();
            } else if (document.activeElement === password) {
                submit.focus();
                submit.click();
            } else if (document.activeElement === submit) {
                submit.click();
            } else {
                email.focus();
            }
        }
    });

// https://stackoverflow.com/questions/46155/how-can-i-validate-an-email-address-in-javascript
    const validateEmail = (email) => {
        return String(email)
            .toLowerCase()
            .match(
                /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            );
    };

    function createCompanyItem(company, companyName) {
        const container = document.createElement("div");
        container.classList.add("py-2", "px-4", "hover:bg-gray-100", "cursor-pointer");
        container.innerText = companyName;
        container.setAttribute("data-company", company);
        return container;
    }

    function selectCompany(option) {
        const header = document.getElementById("company-header");
        header.setAttribute('data-company', option.getAttribute("data-company"));
        header.textContent = option.textContent;
        toggleCompany();
    }

    document.addEventListener("click", function (event) {
        const dropdown = document.getElementById("company-content");
        const button = document.getElementById("company-button");
        const targetElement = event.target;

        if (!dropdown.classList.contains("hidden") && !button.contains(targetElement)) {
            dropdown.classList.add("hidden");
        }
    });


    const companySubmit = document.getElementById("company-submit");
    companySubmit.addEventListener("click", () => {
        const companyDialog = document.getElementById("company-dialog");
        const companyError = document.getElementById("company-error");
        const dropdownHeader = document.getElementById("company-header");

        if (!dropdownHeader.hasAttribute("data-company")) {
            companyError.innerText = "No company selected";
            companyError.classList.remove("hidden");
            companyDialog.classList.add("hidden");
            return;
        }

        const companyId = dropdownHeader.getAttribute("data-company");

        const emailValue = email.value.trim();
        const passwordValue = password.value.trim();

        if (!validateEmail(emailValue)) {
            error.innerText = "Email invalid";
            error.classList.remove("hidden");
            companyDialog.classList.add("hidden");
            return;
        }

        if (passwordValue.length < 1) {
            error.innerText = "Password to short";
            error.classList.remove("hidden");
            companyDialog.classList.add("hidden");
            return;
        }

        // Send login request
        fetch("/earnit/api/login", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    email: emailValue,
                    password: passwordValue,
                    companyId: companyId
                })
            }
        ).then(async res => {
            if (res.status === 401) {
                error.innerText = "Invalid credentials";
                error.classList.remove("hidden");
                companyDialog.classList.add("hidden");
                return;
            }

            if (res.status === 404) {
                error.innerText = "Account not found";
                error.classList.remove("hidden");
                companyDialog.classList.add("hidden");
                return;
            }

            if (res.status !== 200) {
                error.innerText = "An error occurred, try again later";
                error.classList.remove("hidden");
                companyDialog.classList.add("hidden");
                return;
            }

            const data = (await res.json());
            Cookies.set('earnit-token', data.token, { expires: new Date(data.expires) })

            window.location.replace("/earnit/");
        })
    })

    submit.addEventListener("click", () => {
        const emailValue = email.value.trim();
        const passwordValue = password.value.trim();
        // Simple client validation
        if (!validateEmail(emailValue)) {
            error.innerText = "Email invalid";
            error.classList.remove("hidden");
            return;
        }

        if (passwordValue.length < 1) {
            error.innerText = "Password to short";
            error.classList.remove("hidden");
            return;
        }

        error.classList.add("hidden");

        // Send login request
        fetch("/earnit/api/login", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                },
                body: JSON.stringify({
                    email: emailValue,
                    password: passwordValue
                })
            }
        ).then(async res => {
            if (res.status === 401) {
                error.innerText = "Invalid credentials";
                error.classList.remove("hidden");
                return;
            }

            if (res.status === 404) {
                error.innerText = "Account not found";
                error.classList.remove("hidden");
                return;
            }

            if (res.status !== 200) {
                error.innerText = "An error occurred, try again later";
                error.classList.remove("hidden");
                return;
            }

            const data = (await res.json());
            Cookies.set('earnit-token', data.token, { expires: new Date(data.expires) })

            if (getUserCompany() !== null) {
                const dropdown = document.getElementById("company-content");

                dropdown.addEventListener("click", async (e) => {
                    const element = e.target;
                    if (!element.hasAttribute("data-company")) return;

                    await selectCompany(element);
                });

                const companies = await fetch(`/earnit/api/users/${getUserId()}/companies`, {
                    headers: {
                        'authorization': `token ${getJWTCookie()}`,
                        'Accept': 'application/json',
                    },
                }).then((res) => res.json())
                    .catch(() => null);

                dropdown.innerText = "";
                for (const company of companies) {
                    dropdown.append(createCompanyItem(company.id, company.name))
                }

                const companyDialog = document.getElementById("company-dialog");
                companyDialog.classList.remove("hidden");
            } else {
                window.location.replace("/earnit/");
            }
        })
    })
})

function toggleCompany() {
    const dropdown = document.getElementById("company-content");
    dropdown.classList.toggle("hidden");
}