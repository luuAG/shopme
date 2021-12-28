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
