$(document).ready(function(){
	$(".linkMinus").on("click", function(e){
		e.preventDefault();
		productId = $(this).attr("pid");
		quantity = $("#quantity"+productId);
		if(quantity.val() > 1){
			quantity.val(quantity.val() - 1)
		}
	});
	$(".linkPlus").on("click", function(e){
		e.preventDefault();
		productId = $(this).attr("pid");
		quantity = $("#quantity"+productId);
		quantity.val(parseInt(quantity.val()) + 1)
		
	});
})