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

function getUserEmail() {
    return getJWT().user_email;
}

function getUser() {
    return fetch("/earnit/api/users/" + getUserId(), {
        headers: {
            "accept-type": "application/json"
        }
    }).then((res) => res.json()).catch(() => null);
}

