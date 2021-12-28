dropdownBrand = $("#brand");
dropdownCategory = $("#category")

$(document).ready(function() {
	$("#description").richText();
	//
	newMode = true;
	if ($("#categoryId").val() > 0){
		newMode = false;
	}
	if (newMode){
		getCorrespodingCategories();
	}
	//
	dropdownBrand.change(function() {
		dropdownCategory.empty();
		getCorrespodingCategories();
	})
	
});

function getCorrespodingCategories() {
	brandId = dropdownBrand.val();
	url = brandModuleURL + "/" + brandId + "/categories";
	$.get(url, function(responseJson) {
		$.each(responseJson, function(index, category) {
			$("<option>").val(category.id).text(category.name).appendTo(dropdownCategory);
		})
	});
}

function checkProductUnique(form) {
	
	productName = $("#name").val();
	productId = $("#id").val();
	csrfValue = $("input[name='_csrf']").val();
	params = { id: productId, name: productName, _csrf: csrfValue };

	$.post(checkUniqueURL, params, function(response) {
		if (response === "OK") {
			form.submit();
		}
		else {
			document.getElementById("modalBody").innerHTML = 'Duplicate product name: ' + productName + ' !!';
			$("#modalDialog").modal();
		}


	}).fail(function() {
		alert('failed:' + url);
	});

	return false;
}