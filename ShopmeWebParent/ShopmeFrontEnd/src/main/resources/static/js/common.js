
function clearFilter() {
	window.location = "[[@{/}]]";
}
$(document).ready(function() {
	$("#logout-link").on("click", function(e) {
		e.preventDefault();
		document.logoutForm.submit();
	});
	
	customizeDropDownMenu();
})

function customizeDropDownMenu(){
	$(".navbar .dropdown").hover(
		function(){
			$(this).find(".dropdown-menu").first().stop(true,true).slideDown();
		},
		function() {
			$(this).find(".dropdown-menu").first().stop(true,true).slideUp();
		}
	);
	
	$(".dropdown > a").click(function(){
		location.href = this.href;
	});
}