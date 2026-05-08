let login_form = $("#dashboard_login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleDashboardLoginResult(resultDataString) {

    console.log("handle login response");
    console.log(resultDataString["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataString["status"] === "success") {
        window.location.replace("_dashboard.html");
    } else {
        console.log("show error message");
        console.log(resultDataString["message"]);
        $("#login_error_message").text(resultDataString["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit dashboard login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/dashboard-login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            dataType: "json",
            success: handleDashboardLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);
