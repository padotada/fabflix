/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    let movieTableBodyElement = jQuery("#movie_table_body");

    for (let i = 0; i <resultData.length; i++) {
        let movie = resultData[i];
        let genreHTML = "N/A";
        if(movie.genres && movie.genres.length > 0){
            genreHTML = movie.genres.join(", ");
        }

        let starsHTML = "N/A";
        if(movie.stars && movie.stars.length > 0){
            starsHTML = movie.stars.map(star => `<a href="single-star.html?id=${star.star_id}">${star.star_name}</a>`)
                .join(", ");
        }
        let rowHTML = `<tr>
                <td><a href="single-movie.html?id=${movie.movie_id}">${movie.movie_title}</a></td>
                <td>${movie.movie_year}</td>
                <td>${movie.movie_director}</td>
                <td>${genreHTML}</td>
                <td>${starsHTML}</td>
                <td>${movie.movie_rating ?? "N/A"}</td>
            </tr>`;
        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "movielist", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMovieResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});