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
    if (!movie) return resultData[1];

    let genreHTML = (movie.genres && movie.genres.length) ? movie.genres.join(", ") : "N/A";
    let starsHTML = (movie.stars && movie.stars.length)
        ? movie.stars.map(star => `<a href="single-star.html?id=${star.star_id}">${star.star_name}</a>`).join(", ")
        : "N/A";

    let movieInfoElement = jQuery("#movie_info");
    movieInfoElement.empty().append(`
        <p>${movie.movie_title}</p>
        <p>Year: ${movie.movie_year}</p>
        <p>Director: ${movie.movie_director}</p>
        <p>Genres: ${genreHTML}</p>
        <p>Stars: ${starsHTML}</p>
        <p>Rating: ${movie.movie_rating ?? "N/A"}</p>           
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
        </tr>
    `);

    return resultData[1];
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

let backBtn = $("#back_button");
const params = new URLSearchParams();

let user = null;

if (user.title !== "") params.append("title", user.title);
if (user.year !== "") params.append("year", user.year);
if (user.director !== "") params.append("director", user.director);
if (user.star !== "") params.append("star", user.star);

function onClick() { window.location.href = "movie-list.html?" + params.toString();}
backBtn.addEventListener("click", onClick)
