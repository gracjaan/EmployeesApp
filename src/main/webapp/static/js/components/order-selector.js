window.addEventListener("helpersLoaded", () => {
    function setupOrderSelector(orderSelector) {
        orderSelector.addEventListener("click", () => select(orderSelector));
        orderSelector.setAttribute("data-selected", "0");
        orderSelector.classList.add("group");

        const button = document.createElement("button");
        button.classList.add("rounded-xl", "bg-text", "px-4", "py-2", "flex", "gap-4", "items-center");
        orderSelector.append(button);

        const span = document.createElement("span");
        span.classList.add("text-black", "text-left");
        span.innerText = orderSelector.getAttribute("title");
        button.append(span);

        const imgDiv = document.createElement("div");
        imgDiv.classList.add("aspect-square", "w-4", "flex", "items-center", "justify-center")
        button.append(imgDiv);

        const imgDown = document.createElement("img");
        imgDown.classList.add("group-data-[selected='1']:block", "hidden");
        imgDown.alt = "chevron down";
        imgDown.src = "/static/icons/arrow-down-black.svg";
        imgDiv.append(imgDown);

        const imgUp = document.createElement("img");
        imgUp.classList.add("group-data-[selected='2']:block", "hidden");
        imgUp.alt = "chevron up";
        imgUp.src = "/static/icons/arrow-up-black.svg";
        imgDiv.append(imgUp);

        const imgLine = document.createElement("img");
        imgLine.classList.add("group-data-[selected='0']:block", "hidden");
        imgLine.alt = "line";
        imgLine.src = "/static/icons/line-black.svg";
        imgDiv.append(imgLine);
    }

    async function select(orderSelector) {
        const selected = orderSelector.getAttribute("data-selected");
        const orderGroupId = orderSelector.getAttribute("group");

        let orderSelectorsToReset;
        if (orderGroupId === null) {
            orderSelectorsToReset = document.querySelectorAll("order-selector[data-selected='1'], order-selector[data-selected='2']");
        } else {
            orderSelectorsToReset = document.querySelectorAll(`order-selector[data-selected='1'][group='${orderGroupId}'], order-selector[data-selected='2'][group='${orderGroupId}']`);
        }

        for (const orderSelectorToReset of orderSelectorsToReset) {
            orderSelectorToReset.setAttribute("data-selected", "0");
        }

        let state = Number.parseInt(selected ?? "0") + 1;
        if (state > 2) state = 0;

        orderSelector.setAttribute("data-selected", state + "");
        orderSelector.dispatchEvent(new CustomEvent("change",{detail: {
                selected: state
            }}))
    }

    const orderSelectors = document.getElementsByTagName("order-selector");

    for (const orderSelector of orderSelectors) {
        setupOrderSelector(orderSelector);
    }
});