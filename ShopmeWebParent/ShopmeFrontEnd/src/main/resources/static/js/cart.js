$(document).ready(function() {
	$(".link-delete").on("click", function(e) {
		e.preventDefault();
		link = $(this);
		itemId = link.attr("itemId");
		$("#deleteItem").attr("href", $(this).attr("href"))
		$("#modalDeleteBody").text("Are you sure to delete this item?");
		$("#deleteItem").click(itemId,function(){
			removeProduct(itemId);
		});
		$("#modalDelete").modal();
		
	});

	$("#totalCost").text('$' + calculateTotalCost());
/*	$("[input^='quantity']").on("input", function() {
		$("#totalCost").text('$' + calculateTotalCost());
	})
	$("[input^='quantity']").on("change", function() {
		$("#totalCost").text('$' + calculateTotalCost());
	})
*/


	$(".linkMinus").on("click", function(e) {
		e.preventDefault();
		decreaseQuantity($(this));
	});
	$(".linkPlus").on("click", function(e) {
		e.preventDefault();
		increaseQuantity($(this));
	});
});

function decreaseQuantity(button) {
	productId = button.attr("pid");
	quantity = $("#quantity" + productId);
	if (quantity.val() > 1) {
		quantity.val(quantity.val() - 1);
		updateQuantity(productId, quantity.val());
	}
	
}
function increaseQuantity(button) {
	productId = button.attr("pid");
	quantity = $("#quantity" + productId);
	quantity.val(parseInt(quantity.val()) + 1);
	updateQuantity(productId, quantity.val());
}
function updateQuantity(productId, quantity) {
	url = contextPath + "cart/update/" + productId + "/" + quantity;
	

	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		$("#modalTitle").text("Notification");
		$("#modalBody").text("Updated total price: $" + response + " for " + quantity + " of this product");
		$("#modalOk").click(function(){
			window.location.reload();
		})
		$("#modalDialog").modal();
		
	}).fail(function(){
		alert('fail');
	});
}
function removeProduct(productId){
	url = contextPath + "cart/delete/" + productId;
	
	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		window.location.reload();
	}).fail(function(response){
		alert(response);
	});
	
}

function calculateTotalCost() {
	totalCost = 0;
	$(".total-price").each(function() {
		totalCost += parseFloat($(this).text().replace("$", ""));
	})
	shippingCost = parseFloat($("#shippingCost").text().replace("$", ""));
	return (totalCost + shippingCost) > 2 ? (totalCost + shippingCost) : 0;
}