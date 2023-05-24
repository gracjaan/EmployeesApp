function fetchSheet(uid, ucid, year, week) {
    fetch("/users/"+ uid + "/contracts/" + ucid + "/worked/" + year + "/" + week)
        .then (response => response.json())
        .then (data => {
            html = "<div className=\"rounded-xl bg-primary p-4 relative flex justify-between\">";
            for (const item of data){
                html+=
                    "<div className=\"text-text font-bold uppercase\">" + item.day + "</div>" +
                    "<div className=\"text-text\">" + item.minutes + "</div>" +
                    "<div className=\"text-text\">" + item.position + "</div>" +
                    "<div className=\"text-text\">" + item.work + "</div>";
            }
            html += "</div>";
            document.getElementById("items").innerHTML = html;
        })
        .catch(e => console.error(e))
}