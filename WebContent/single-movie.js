/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target){
    let url = window.location.href;
    target = target.replace(/[\[\]]/g, "\\$&");
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleSingleMovieResult(resultData) {
    console.log("handleSingleMovieResult: populating movie info from resultData");
    let movie = resultData[0];
    if (!movie) return;
    console.log(movie);
    console.log("movie id:", movie.movie_id);

    let genreHTML = "N/A";
    if(movie.genres && movie.genres.length > 0){
        genreHTML = movie.genres.join(", ");
        genreHTML = movie.genres.map(genre => `<a href="movie-list.html?genre=${genre}&search=false">${genre}</a>`)
            .join(", ");
    }
    let starsHTML = (movie.stars && movie.stars.length)
        ? movie.stars.map(star => `<a href="single-star.html?id=${star.star_id}">${star.star_name}</a>`).join(", ")
        : "N/A";
    let priceHTML = movie.movie_price != null ? "$" + Number(movie.movie_price).toFixed(2) : "N/A";
    let movieInfoElement = jQuery("#movie_info");
    movieInfoElement.empty().append(`
        <p>${movie.movie_title}</p>
        <p>Year: ${movie.movie_year}</p>
        <p>Director: ${movie.movie_director}</p>
        <p>Genres: ${genreHTML}</p>
        <p>Stars: ${starsHTML}</p>
        <p>Rating: ${movie.movie_rating ?? "N/A"}</p>      
        <p>Price: ${priceHTML}</p>     
    `);

    let movieTableBodyElement = jQuery("#movie_table_body");
    movieTableBodyElement.empty().append(`
        <tr>
            <td>${movie.movie_title}</td>
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
        </tr>
    `);

    return;
}

function handleBackButton(resultData) {
    let backBtn = $("#back_button");
    const params = new URLSearchParams();
    let user = resultData[0];

    if (user.title !== "") params.append("title", user.title);
    if (user.year !== "") params.append("year", user.year);
    if (user.director !== "") params.append("director", user.director);
    if (user.star !== "") params.append("star", user.star);

    backBtn.prop("href", "movie-list.html?" + params.toString());
}

// For now keeping both the table format and plain text format
// Can change later

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

let movieId = getParameterByName('id');
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: `single_movie?id=${movieId}`,
    success: (resultData) => handleSingleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: `return_movielist`,
    success: (resultData) => handleBackButton(resultData)
});

function addToCart(movieId) {
    jQuery.ajax({
        method: "POST",
        url: "api/cart",
        data: {
            action: "add",
            movieId: movieId
        },
        success: function (resultData) {
            console.log("Added to cart:", resultData);
            alert("Movie added to cart!");
        },
        error: () => alert("Failed to add movie to cart.")

    });
}
jQuery(document).on("click", ".add-to-cart-button", function () {
    const movieId = jQuery(this).data("movie-id");
    addToCart(movieId);
});

