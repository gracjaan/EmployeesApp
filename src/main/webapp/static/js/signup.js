const companyCard = document.getElementById("company");
const studentCard = document.getElementById("student");

const urlParams = new URLSearchParams(window.location.search);
const type = urlParams.get('type');
let selected = null;
select(type ?? "company");

function updateURL(card) {
    const urlParams = new URLSearchParams(window.location.search);
    const type = urlParams.get('type');

    if (type === card) return;
    urlParams.set("type", card);

    const newURL = window.location.protocol + "//" + window.location.host + window.location.pathname + '?' + urlParams.toString();
    window.history.replaceState({ path: newURL }, '', newURL);
}

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

companyCard.addEventListener("click", select.bind(undefined, "company"))
studentCard.addEventListener("click", select.bind(undefined, "student"))