$(document).ready(function(){
	$("#addButton").on("click", function(e){
		addToCart();
	})

});

function addToCart(){
	quantity = $("#quantity"+productId).val();
	url = contextPath + "cart/add/"+productId+"/"+quantity;
	console.log(parseInt(quantity) > parseInt($("#productAmount").text()));
	if (parseInt(quantity) > parseInt($("#productAmount").text())){
		$("#modalTitle").text("Notification");
		$("#modalBody").text("Check the available amount again!");
		$("#modalDialog").modal();
		return;
	}
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response){
		$("#modalOk").click(function(){
			location.reload();
		})
		$("#modalTitle").text("Notification");
		$("#modalBody").text(response);
		$("#modalDialog").modal();
	}).fail(function(){
		$("#modalTitle").text("Error");
		$("#modalBody").text("Cannot add product into cart! An error occurs.");
		$("#modalDialog").modal();
	});
	
}