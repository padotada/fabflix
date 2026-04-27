/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
console.log("movie-list.js loaded");
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movies_table_body");
    movieTableBodyElement.empty();
    for (let i = 0; i <resultData.length; i++) {
        let movie = resultData[i];
        let genreHTML = "N/A";
        if(movie.genres && movie.genres.length > 0){
            genreHTML = movie.genres.join(", ");
            genreHTML = movie.genres.map(genre => `<a href="movie-list.html?genre=${genre}&search=false">${genre}</a>`)
                .join(", ");
        }

        let starsHTML = "N/A";
        if(movie.stars && movie.stars.length > 0){
            starsHTML = movie.stars.map(star => `<a href="single-star.html?id=${star.star_id}">${star.star_name}</a>`)
                .join(", ");
        }
        let priceHTML = movie.movie_price != null ? "$" + Number(movie.movie_price).toFixed(2) : "N/A";
        let rowHTML = `<tr>
                <td><a href="single-movie.html?id=${movie.movie_id}">${movie.movie_title}</a></td>
                <td>${movie.movie_year}</td>
                <td>${movie.movie_director}</td>
                <td>${genreHTML}</td>
                <td>${starsHTML}</td>
                <td>${movie.movie_rating ?? "N/A"}</td>
                <td>${priceHTML}</td>
                <td>
                    <button class="add-to-cart-button" data-movie-id="${movie.movie_id}">
                        Add to Cart
                    </button>
                </td>
            </tr>`;
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
const urlParams = new URLSearchParams(window.location.search);

console.log("title =", urlParams.get("title"));
console.log("year =", urlParams.get("year"));
console.log("director =", urlParams.get("director"));
console.log("star =", urlParams.get("star"));

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "movielist", // Setting request url, which is mapped by StarsServlet in Stars.java
    data: {
        search: urlParams.get("search"),
        title: urlParams.get("title"),
        year: urlParams.get("year"),
        director: urlParams.get("director"),
        star: urlParams.get("star"),
        first: urlParams.get("first"),
        genre: urlParams.get("genre")
    },
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

let sortForm = $("#search_filter");
function submitSortForm(event) {
    event.preventDefault();

    const params = new URLSearchParams(window.location.search);
    console.log(params.toString());
    params.set('title_first', $("#tiebreak").val());
    params.set('title_sort', $("#title").val());
    params.set('rating_sort', $("#rating").val());
    console.log(params.toString());
    window.location.href = "movie-list.html?" + params.toString();
}
sortForm.submit(submitSortForm);

let pageCount = urlParams.get("page");
console.log("page hiding check");
console.log(pageCount);
if(!pageCount || pageCount === "1") {
    console.log("page hidden");
    jQuery("#prev_container").css('visibility','hidden');
}