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
function handleSingleStarResult(resultData) {
    console.log("handleSingleStarResult: populating star info from resultData");
    let star = resultData[0];
    if (!star) return;

    let movieHTML = (star.movies && star.movies.length) ? star.movies.map(movie => `<a href="single-movie.html?id=${movie.movie_id}">${movie.movie_title}</a> (${movie.movie_year})`).join(", ")
        : "N/A";

    let starInfoElement = jQuery("#star_info");
    starInfoElement.empty().append(`
        <p>${star.star_name}</p>
        <p>Year of Birth: ${star.birth_year}</p>
        <p>Movies: ${movieHTML}</p>       
    `);

    let starTableBodyElement = jQuery("#star_table_body");
    starTableBodyElement.empty().append(`
        <tr>
            <td>${star.star_name}</td>
            <td>${star.birth_year}</td>
            <td>${movieHTML}</td>
        </tr>
    `);
}

// For now keeping both the table format and plain text format
// Can change later

function handleBackButton(resultData) {
    let backBtn = $("#back_button");
    const params = new URLSearchParams();
    let user = resultData[0];

    let searchMode = user.search;
    console.log(user);
    if(searchMode === true)
    {
        params.append("search", "true")
        if (user.title !== "") params.append("title", user.title);
        if (user.year !== "") params.append("year", user.year);
        if (user.director !== "") params.append("director", user.director);
        if (user.star !== "") params.append("star", user.star);
    }
    else
    {
        params.append("search", "false")
        if (user.genre !== "") params.append("genre", user.genre);
        if (user.first !== "") params.append("first", user.first);
    }

    params.append("page", user.page);
    params.append("records", user.records);

    params.append("title_first", user.title_first)
    params.append("title_sort", user.title_sort);
    params.append("rating_sort", user.rating_sort);


    backBtn.prop("href", "movie-list.html?" + params.toString());
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let starId = getParameterByName('id');
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: `single_star?id=${starId}`,
    success: (resultData) => handleSingleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: `return_movielist`,
    success: (resultData) => handleBackButton(resultData)
});