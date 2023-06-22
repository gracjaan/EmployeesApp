window.addEventListener("helpersLoaded", () => {
    function setupOrderSelector(backElement) {
        backElement.addEventListener("click", () => select(backElement));
        backElement.setAttribute("data-selected", "0");
        backElement.classList.add("group");


        const header = document.createElement("header");
        header.classList.add("flex", "bg-primary", "w-fit", "p-2", "rounded-br-lg", "cursor-pointer");
        header.addEventListener("click", () => {
                    if (header.hasAttribute("data-home")) {
                        location.href = "/earnit"
                    } else {
                        history.back();
                    }
                })

        backElement.append(header);

        const imgLeft = document.createElement("img");
        imgLeft.classList.add("mr-3");
        imgLeft.alt = "arrow left";
        imgLeft.src = "/earnit/static/icons/arrow-left.svg";
        header.append(imgLeft);

        const div = document.createElement("div");
        div.classList.add("text-text");
        div.innerText = "Back";
        header.append(div);
    }

    const backElements = document.getElementsByTagName("back");

    for (const backElement of backElements) {
        setupOrderSelector(backElement);
    }
});