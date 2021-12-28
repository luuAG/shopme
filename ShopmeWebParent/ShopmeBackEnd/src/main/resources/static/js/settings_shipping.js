function addLocation(index){
	
	html = `
		<form class="form-group row border align-items-center mt-2" 
			action=""
			method="post"
			id="form${index}"
			onsubmit="return getActionUrl(event, this);">
			<label class="col-sm-1 col-form-label">Location:</label>
			<div class="col-sm-2">
				<input id="location${index}" type="text" class="form-control" name="location" required/>
			</div>
			<label class="col-sm-1 col-form-label">Shipping cost:</label>
			<div class="col-sm-1">
				<input th:id="cost${index}" type="number" step="any" min="0" class="form-control" name="cost" required />
			</div>
			<label class="col-sm-2 col-form-label">Expected delivery time:</label>
			<div class="col-sm-1">
				<input th:id="time${index}" type="number" step="1" min="0" class="form-control" name="time" required />
			</div>
			<label class="col-sm-1 col-form-label">days</label>
			<div class="row col-sm-3 align-items-center" >
				<input type="submit" class="form-control button btn-primary" value="Save" style="max-width:80px;"/>
				<a href="" >
					<input type="button" class="form-control button btn-danger ml-3" value="Delete" style="max-width:80px;"/>
				</a>						
			</div>
		</form>
	`;
	
	$("#divContainer").append(html); 
	
}

function getActionUrl(e, form){
	e.preventDefault();

	action = $(form).attr("action");

	currentIndex = $(form).attr('id').match(/\d/g).join('');
	
	_location = $('#location' + currentIndex).val(); 

	if(action == ""){
		$(form).attr("action", moduleURL + "settings/save_shipping/"+_location);
	}
	//alert($(form).attr("action"));
	form.submit();

	
	return false;
}

function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
}