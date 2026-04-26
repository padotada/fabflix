function handleCartResult(data){
    let tableBody = jQuery("#cart_table_body");
    if(data.cart.length === 0){
        tableBody.append(`
        <tr>
            <td colspan="5">Your cart is empty.</td>
        </tr>`);
        jQuery.text("Total: $0.00");
        return;
    }
    for(let i = 0; i < data.cart.length; i++){
        let item = data.cart[i];
        let row = `
        <tr>
            <td>${item.title}</td>
            <td>${item.quantity}</td>
            <td>${Number(item.price).toFixed(2)}</td>
            <td>${Number(item.subtotal).toFixed(2)}</td>
            <td>
                <button onclick="updateCart('increase', '${item.movieId}')">+</button>
                <button onclick="updateCart('decrease', '${item.movieId}')">-</button>
                <button onclick="updateCart('delete', '${item.movieId}')">Delete</button>
            </td>
        </tr>
        `;
        tableBody.append(row);
    }
    $("#cart_total").text("Total: $" + Number(data.total).toFixed(2));
}

function loadCart(){
    jQuery.ajax({
        url: "api/cart",
        method: "GET",
        success: (data)=>handleCartResult(data),
        error: ()=>console.log("Failed to load cart")
    });
}

function updateCart(action, movieId){
    jQuery.ajax({
        url: "api/cart",
        method: "POST",
        data: {
            action: action,
            movieId: movieId
        },
        success: (data)=>handleCartResult(data),
        error: ()=>console.log("Failed to update cart")
    });
}

loadCart();