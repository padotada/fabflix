function handleConfirmationResult(resultData) {
    console.log("handleConfirmationResult:", resultData);

    $("#sale_ids").text(resultData["sale_ids"].join(", "));

    let tableBody = $("#confirmation_table_body");
    tableBody.empty();

    for (let i = 0; i < resultData["items"].length; i++) {
        let item = resultData["items"][i];

        let rowHTML = `
            <tr>
                <td>${item.movie_title}</td>
                <td>${item.quantity}</td>
                <td>$${Number(item.price).toFixed(2)}</td>
                <td>$${Number(item.subtotal).toFixed(2)}</td>
            </tr>
        `;

        tableBody.append(rowHTML);
    }

    $("#total_price").text(Number(resultData["total_price"]).toFixed(2));
}

$.ajax({
    dataType: "json",
    method: "GET",
    url: "api/confirmation",
    success: handleConfirmationResult,
    error: function(xhr) {
        console.log("confirmation error:", xhr);
        alert("Could not load confirmation details.");
    }
});