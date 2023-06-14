window.addEventListener("helpersLoaded", async () => {
    const rejectedWeeks = await getRejectedWeeks();


    if (rejectedWeeks === null) {
        alert("Could not load rejected weeks");
        return;
    }

    const rejectedWeeksElement = document.getElementById("dropdown-company-content");
    rejectedWeeksElement.innerText = ""



    for (const rejectedWeek of rejectedWeeks) {
        rejectedWeeksElement.append(createRejectedWeek(rejectedWeek));
    }




})

async function getRejectedWeeks() {
    return await fetch("/earnit/api/staff/rejects",
        {method: "GET",
            headers: {
                "accept-type" : "application/json",
                'authorization': `token ${getJWTCookie()}`
            }}
    ).then((res) => res.json()).catch(() => null);
}

function createComment(note, companyNote, employee, company){
    const commentDiv = document.getElementById("comments");

    const userCommentDiv = document.createElement("div")
    userCommentDiv.classList.add("bg-primary", "rounded-xl", "w-full","p-2","pl-4")
    const userHeaderDiv = document.createElement("div")
    userHeaderDiv.classList.add("items-center", "gap-4" ,"flex", "flex-row")


    const companyCommentDiv = document.createElement("div")
    companyCommentDiv.classList.add("bg-primary", "rounded-xl", "w-full","p-2","pl-4")
    const companyHeaderDiv = document.createElement("div")
    companyHeaderDiv.classList.add("items-center", "gap-4" ,"flex", "flex-row")

}
function createRejectedWeek(rejectedWeek) {
    const listElement = document.createElement("li");

    const weekDiv = document.createElement("div");
    weekDiv.setAttribute("data-selected", '0')
    weekDiv.classList.add("justify-between", "flex flex-row", "data-[selected='1']:border-white", "hover:border-text", "border-2", "border-primary", "block bg-primary", "rounded-xl", "w-full", "p-2", "pl-2", "overflow-y-auto", "scrollbar-custom", "scrollbar-track-text", "scrollbar-rounded-xl", "scrollbar-thumb-background", "max-h-[5rem]");
    const employee = document.createElement("p")
    employee.classList.add("text-text");
    employee.innerText = rejectedWeek.role;
    const company = document.createElement("p")
    company.classList.add("text-text");
    company.innerText = rejectedWeek.role;
    const role = document.createElement("p")
    role.classList.add("text-text");
    role.innerText = rejectedWeek.role;
    const week = document.createElement("p")
    week.classList.add("text-text");
    week.innerText = rejectedWeek.role;
    const hours = document.createElement("p")
    hours.classList.add("text-text");
    hours.innerText = rejectedWeek.role;


    listElement.addEventListener("click", ()=>{
        createComment(rejectedWeek.note, rejectedWeek.companyNote);
        contractId=contract.id;
        const selected = parseInt(weekDiv.getAttribute("data-selected"))
        if (selected === 0) {
            const orderedList = document.getElementById("contract-list");
            const userDivs = orderedList.querySelectorAll("[data-selected='1']")
            for (const userDiv1 of userDivs) {
                userDiv1.setAttribute("data-selected", "0")
            }
            weekDiv.setAttribute("data-selected", "1");
        }

    })

}

function getNotes() {

}

function submitForm() {

}