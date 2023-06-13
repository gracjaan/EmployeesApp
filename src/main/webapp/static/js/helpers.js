const event = new Event("helpersLoaded");

function loadCookiesLib() {
    const script = document.createElement('script');
    script.src = "https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js";
    script.addEventListener("load", () => {
        window.dispatchEvent(event);
    })

    document.head.appendChild(script);
}

window.addEventListener("load", loadCookiesLib);

// https://stackoverflow.com/questions/38552003/how-to-decode-jwt-token-in-javascript-without-using-a-library
function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}

function getJWTCookie() {
    return Cookies.get('earnit-token');
}

function getJWT() {
    return parseJwt(getJWTCookie());
}

function getUserId() {
    return getJWT().user_id;
}

function getUserCompany() {
    return getJWT().user_company;
}

function getUser() {
    return fetch("/earnit/api/users/" + getUserId(), {
        headers: {
            'authorization': `token ${getJWTCookie()}`,
            "accept-type": "application/json"
        }
    }).then((res) => res.json()).catch(() => null);
}

function getName(firstName, lastName, lastNamePrefix, separator = " ") {
    return [firstName, lastNamePrefix, lastName].filter(Boolean).join(separator);
}

const escapeHtml = (unsafe) => {
    if (unsafe === null || unsafe === undefined) return unsafe;
    return unsafe.replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
}