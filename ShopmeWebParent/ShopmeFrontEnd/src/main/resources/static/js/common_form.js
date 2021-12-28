$(document).ready(function() {
	$("#buttonCancel").on("click", function() {
		window.location = moduleURL;
	});

	$("#fileImage").change(
		function() {
			if(!isFileValid(this)){
				return;
			}
			showImageThumbnail(this);
	});
	
	$(document).on("input", "#confirmPassword", function(){
		password = $("#password").val();
		passwordNotification = $("#passwordNotification");
		if (password != $("#confirmPassword").val()){
			passwordNotification.addClass("text-danger");
			passwordNotification.text("Confirm password doesn't match!");
		}
		else {
			passwordNotification.text("Confirm password is matched!");
			passwordNotification.removeClass("text-danger");
			passwordNotification.addClass("text-success");
		}
	});
	$(document).on("input", "#password", function(){
		password = $("#password").val();
		passwordNotification = $("#passwordNotification");
		if (password != $("#confirmPassword").val()){
			passwordNotification.addClass("text-danger");
			passwordNotification.text("Confirm password doesn't match!");
		}
		else {
			passwordNotification.text("Confirm password is matched!");
			passwordNotification.removeClass("text-danger");
			passwordNotification.addClass("text-success");
		}
	});
});
function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
}
function isFileValid(fileInput){
	fileSize = fileInput.files[0].size;
	if (fileSize > 1048576) {
		fileInput.setCustomValidity("You must choose an image that less than 1MB");
		fileInput.reportValidity();
		return false;
	} else {
		fileInput.setCustomValidity("");
		return true;
	}
}
