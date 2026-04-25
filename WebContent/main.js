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

function addGenres(resultData)
{
    let genre_list = resultData[0].genres;
    for (let i = 0; i < genre_list.length; i++) {
        genreSelect.append($('<option>', {value: genre_list[i], text: genre_list[i]}));
    }
}

const genreSelect = $("#genres");
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "genres", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => addGenres(resultData)
});


let letter_form = $("#browse_letter");
function submitLetterForm(event) {
    event.preventDefault();

    const letter = select.val();
    const params = new URLSearchParams();
    params.append("first", letter);
    params.append("search", "false");

    window.location.href = "movie-list.html?" + params.toString();
}
letter_form.submit(submitLetterForm);

let genre_form = $("#browse_genre");
function submitGenreForm(event) {
    event.preventDefault();

    const genre = genreSelect.val();
    const params = new URLSearchParams();
    params.append("genre", genre);
    params.append("search", "false");

    window.location.href = "movie-list.html?" + params.toString();
}
genre_form.submit(submitGenreForm);