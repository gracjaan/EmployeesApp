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

        if (res.status !== 200) {
            error.innerText = "An error occurred, try again later";
            error.classList.remove("hidden");
            return;
        }

        const data = (await res.json());
        Cookies.set('earnit-token', data.token, { expires: new Date(data.expires) })

        window.location.replace("/earnit/");
    })
})