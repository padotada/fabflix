console.log("main.js loaded");

let searchForm = $("#search_form");

function submitSearchForm(event){
    event.preventDefault();

    const title = $("#title").val().trim();
    const year = $("#year").val().trim();
    const director = $("#director").val().trim();
    const star = $("#star").val().trim();

    const params = new URLSearchParams();

    if (title !== "") {params.append("title", title);}
    if (year !== "") {params.append("year", year);}
    if (director !== "") {params.append("director", director);}
    if (star !== "") {params.append("star", star);}
    params.append("search", "true");

    window.location.href = "movie-list.html?" + params.toString();
}
searchForm.submit(submitSearchForm);


const select = $("#letters");
const alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

for (let i = 0; i < alphabet.length; i++) {
    select.append($('<option>', {value: alphabet[i], text: alphabet[i]}));
}