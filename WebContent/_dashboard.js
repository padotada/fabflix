let addStarForm = $("#add-star-form");
let addMovieForm = $("#add-movie-form");
let metadataContainer = $("#metadata-container");

function showMessage(selector, status, message){
    let element = $(selector);
    element.removeClass("text-success text-danger");
    if(status === "success"){
        element.addClass("text-success");
    }else{
        element.addClass("text-danger");
    }
    element.text(message);
}

function handleAddStarResult(resultData){
    showMessage("#add-star-message", resultData["status"], resultData["message"]);
    if(resultData["status"] === "success"){
        addStarForm[0].reset();
    }
}
function submitAddStarForm(event){
    event.preventDefault();
    $.ajax("api/add-star", {
        method: "POST",
        data: addStarForm.serialize(),
        dataType: "json",
        success: handleAddStarResult,
        error: function(err){
            console.log(err.responseText);
            showMessage("#add-star-message", "fail", "Request failed.");
        }
    });
}

function handleMetadataResult(resultData){
    metadataContainer.empty();
    if(resultData["status"] !== "success"){
        metadataContainer.append(`<div class="text-danger">${resultData["message"]}</div>`);
        return;
    }
    let tables = resultData["tables"];
    for(let i = 0; i < tables.length; i++){
        let table = tables[i];
        let tableHtml = `
            <h5 class="mt-4">${table["tableName"]}</h5>
            <table class="table table-bordered table-sm">
                <thead>
                    <tr>
                        <th>Attribute</th>
                        <th>Type</th>
                        <th>Size</th>
                    </tr>
                </thead>
                <tbody>
        `;
        let columns = table["columns"];
        for(let j = 0; j < columns.length; j++){
            let column = columns[j];
            tableHtml += `
                <tr>
                    <td>${column["columnName"]}</td>
                    <td>${column["columnType"]}</td>
                    <td>${column["columnSize"]}</td>
                </tr>
            `;
        }
        tableHtml += `
                </tbody>
            </table>
        `;

        metadataContainer.append(tableHtml);
    }
}

function loadMetadata(){
    $.ajax("api/metadata", {
        method: "GET",
        dataType:"json",
        success: handleMetadataResult,
        error: function(err){
            console.log(err.responseText);
            metadataContainer.html(`<div class="text-danger">Failed to load metadata.</div>`);
        }
    });
}

function handleAddMovieResult(resultData){
    showMessage("#add-movie-message", resultData["status"], resultData["message"]);
    if (resultData["status"] === "success") {
        addMovieForm[0].reset();
    }
}

function submitAddMovieForm(event){
    event.preventDefault();
    $.ajax("api/add-movie", {
        method: "POST",
        data: addMovieForm.serialize(),
        dataType: "json",
        success: handleAddMovieResult,
        error: function(err){
            console.log(err.responseText);
            showMessage("#add-movie-message", "fail", "Request failed");
        }
    });
}

addStarForm.submit(submitAddStarForm);
$("#load-metadata-button").click(loadMetadata);
addMovieForm.submit(submitAddMovieForm);
