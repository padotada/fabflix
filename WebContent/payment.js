function loadPaymentTotal() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/cart",
        success: function (data) {
            jQuery("#payment_total").text("Total: $" + Number(data.total).toFixed(2));

            if (data.cart.length === 0) {
                showError("Your cart is empty. Please add movies before checking out.");
                jQuery("#payment_form button[type='submit']").prop("disabled", true);
            }
        },
        error: function () {
            showError("Failed to load cart total.");
        }
    });
}

function showError(message) {
    jQuery("#payment_error")
        .text(message)
        .show();
}

function hideError() {
    jQuery("#payment_error")
        .hide()
        .text("");
}

function submitPaymentForm(event) {
    event.preventDefault();

    hideError();

    const firstName = jQuery("#first_name").val().trim();
    const lastName = jQuery("#last_name").val().trim();
    const creditCardId = jQuery("#credit_card_id").val().trim();
    const expiration = jQuery("#expiration").val();

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: "api/place-order",
        data: {
            firstName: firstName,
            lastName: lastName,
            creditCardId: creditCardId,
            expiration: expiration
        },
        success: function (resultData) {
            if (resultData.status === "success") {
                window.location.href = "confirmation.html";
            } else {
                showError(resultData.message || "Payment failed. Please check your information.");
            }
        },
        error: function () {
            showError("Payment failed. Please try again.");
        }
    });
}

jQuery("#payment_form").submit(submitPaymentForm);

loadPaymentTotal();