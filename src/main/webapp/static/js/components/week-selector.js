window.addEventListener("helpersLoaded", ()=> {
    function createWeekSelector(parent) {
        const dropdownContainer = document.createElement("div");
        dropdownContainer.classList.add("relative");
        parent.append(dropdownContainer);

        const dropdownButton = document.createElement("button");
        dropdownButton.classList.add("rounded-xl", "bg-primary", "px-4", "py-2", "flex", "items-center");
        dropdownContainer.append(dropdownButton);

        const dropdownButtonDisplay = document.createElement("div");
        dropdownButtonDisplay.classList.add("text-white", "mr-5", "whitespace-nowrap");
        dropdownButtonDisplay.innerText = "Week 10";
        dropdownButton.append(dropdownButtonDisplay);

        const imgDiv = document.createElement("div");
        imgDiv.classList.add("aspect-square", "w-4", "flex", "items-center", "justify-center", "ml-2")
        dropdownButton.append(imgDiv);

        const dropdownButtonChevron = document.createElement("img");
        dropdownButtonChevron.src = "/static/icons/arrow-down.svg";
        dropdownButtonChevron.alt = "arrow down";
        imgDiv.append(dropdownButtonChevron);

        const dropdownContent = document.createElement("div");
        dropdownContent.classList.add("z-20", "bg-white", "shadow-lg", "rounded-lg", "fixed", "mt-2", "hidden", "w-fit", "max-h-32", "overflow-y-auto", "scrollbar-custom", "scrollbar-rounded-xl");
        dropdownContainer.append(dropdownContent);

        dropdownButton.addEventListener("click", () => {
            dropdownContent.classList.toggle("hidden");
        })
    }

    function setupWeeks(weekSelector) {
        /** @type {HTMLDivElement} */
        const dropdownContent = weekSelector.querySelector(":scope > div > div");

        dropdownContent.addEventListener("click", async (e) => {
            const element = e.target;
            if (!element.hasAttribute("data-week")) return;

            await selectWeek(weekSelector, element);
        });

        dropdownContent.addEventListener("scroll", () => {
            if (Math.abs(dropdownContent.scrollHeight - dropdownContent.scrollTop - dropdownContent.clientHeight) < 1) {
                let last = dropdownContent.lastElementChild;
                while (!last.hasAttribute("data-week")) {
                    const index = Array.from(dropdownContent.children).indexOf(last);
                    if (index === 0) return;

                    last = dropdownContent.children.item(index - 1);
                }

                const year = parseInt(last.getAttribute("data-year"))
                const week = parseInt(last.getAttribute("data-week"))

                addWeeks(weekSelector, 5, year, week);
            }
        })

        dropdownContent.innerText = "";
        addWeeks(weekSelector, 5, getCurrentYear(), getCurrentWeek() + 1);

        if (getWeekOfURL() !== null) {
            let found = false;
            while (!found) {
                for (const child of dropdownContent.children) {
                    if (child.getAttribute("data-week") === getWeekOfURL()) {
                        child.click();
                        found = true;
                    }
                }

                let last = dropdownContent.lastElementChild;
                while (!last.hasAttribute("data-week")) {
                    const index = Array.from(dropdownContent.children).indexOf(last);
                    if (index === 0) return;

                    last = dropdownContent.children.item(index - 1);
                }

                const year = parseInt(last.getAttribute("data-year"))
                const week = parseInt(last.getAttribute("data-week"))

                addWeeks(weekSelector, 5, year, week);
            }
        } else {
            dropdownContent.children.item(0).click();
        }
    }

    function addWeeks(weekSelector, amount, lastYear, lastWeek) {
        /** @type {HTMLDivElement} */
        const dropdownContent = weekSelector.querySelector(":scope > div > div");

        while (amount-- > 0) {
            lastWeek--;

            if (lastWeek < 1) {
                lastYear--;
                lastWeek = weeksInYear(lastYear);
                dropdownContent.appendChild(createYearItem(lastYear));
            }

            dropdownContent.appendChild(createWeekItem(lastYear, lastWeek));
        }
    }

    function createYearItem(year) {
        const container = document.createElement("div");
        container.classList.add("py-2", "px-4", "font-bold", "whitespace-nowrap");
        container.innerText = year;

        return container;
    }

    function createWeekItem(year, week) {
        const container = document.createElement("div");
        container.classList.add("py-2", "pl-4", "pr-6", "hover:bg-gray-100", "cursor-pointer", "whitespace-nowrap");
        container.innerText = "Week " + week;
        container.setAttribute("data-year", year);
        container.setAttribute("data-week", week);

        return container;
    }

    async function selectWeek(weekSelector, option) {
        const week = parseInt(option.dataset.week);
        const year = parseInt(option.dataset.year);

        weekSelector.setAttribute("data-week", week)
        weekSelector.setAttribute("data-year", year)

        /** @type {HTMLDivElement} */
        const dropdownContent = weekSelector.querySelector(":scope > div > div");
        /** @type {HTMLButtonElement} */
        const dropdownButton = weekSelector.querySelector(":scope > div > button");
        /** @type {HTMLButtonElement} */
        const dropdownButtonDisplay = dropdownButton.querySelector(":scope > div");

        dropdownButtonDisplay.innerText = option.innerText;
        dropdownContent.classList.add("hidden");

        weekSelector.dispatchEvent(new CustomEvent("change",{detail: {
                year: year,
                week: week
            }}))
    }

    function weeksInYear(year) {
        return Math.max(
            getWeek(new Date(year, 11, 31))
            , getWeek(new Date(year, 11, 31-7))
        );
    }

    function getWeek(ofDate) {
        const date = new Date(ofDate);
        date.setHours(0, 0, 0, 0);
        // Thursday in current week decides the year.
        date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7);
        // January 4 is always in week 1.
        const week1 = new Date(date.getFullYear(), 0, 4);
        // Adjust to Thursday in week 1 and count number of weeks from date to week1.
        return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000
            - 3 + (week1.getDay() + 6) % 7) / 7);
    }

    function getCurrentYear() {
        const currentDate = new Date();
        return currentDate.getFullYear();
    }

    function getCurrentWeek() {
        return getWeek(new Date());
    }

    function getWeekOfURL() {
        const search = new URLSearchParams(location.search);
        if (!search.has("week")) {
            return null;
        }

        return search.get("week");
    }

    document.addEventListener("click", function (event) {
        const weekSelectors = document.getElementsByTagName("week-selector");

        for (const weekSelector of weekSelectors) {
            /** @type {HTMLDivElement} */
            const dropdownContent = weekSelector.querySelector(":scope > div > div");
            /** @type {HTMLButtonElement} */
            const dropdownButton = weekSelector.querySelector(":scope > div > button");

            const targetElement = event.target;

            if (!dropdownContent.classList.contains("hidden") && !dropdownContent.contains(targetElement) && !dropdownButton.contains(targetElement)) {
                dropdownContent.classList.add("hidden");
            }
        }
    });

    const weekSelectors = document.getElementsByTagName("week-selector");

    for (const weekSelector of weekSelectors) {
        createWeekSelector(weekSelector);
        setupWeeks(weekSelector);
    }
})