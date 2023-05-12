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

        // TODO: check if valid: first, last, prefix
        updateContent("student-account");
    })
}

function studentAccount() {
    const back = document.getElementById("student-account-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("student-info");
    });

    // TODO: submit and check if valid
}

function companyInfo() {
    const back = document.getElementById("company-info-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("choose");
    });

    submit.addEventListener("click", () => {
        if (state !== 'company-info') return;

        // TODO: check if valid: name
        updateContent("company-account");
    })
}

function companyAccount() {
    const back = document.getElementById("company-account-back");
    const submit = document.getElementById("submit");

    back.addEventListener('click', () => {
        updateContent("company-info");
    });

    // TODO: submit and check if valid
}