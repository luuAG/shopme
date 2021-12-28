extraImagesCount = 1;
removedImagesCount = 0;


$(document).ready(function() {
	$("input[name='fileExtraImage']").each(function(index){
		$(this).change(function(){
			showExtraImageThumbnail(this, index);				
		});
	});
	extraImagesCount = $("input[name='fileExtraImage']").length;
});
function showExtraImageThumbnail(fileInput, index) {
	if(!isFileValid(fileInput)){
		return;
	}
	var file = fileInput.files[0];
	
	fileName = file.name;
	imageNameHiddenField = $("#imageName"+index);
	if (imageNameHiddenField.length){
		imageNameHiddenField.val(fileName);
	}
	
	var reader = new FileReader();
	reader.onload = function(e) {
		$("#extraThumbnail" + index).attr("src", e.target.result);
	};
	reader.readAsDataURL(file);
	
	if (extraImagesCount === 1){
		addExtraImageSection(index+1);
		extraImagesCount ++;
	}
	else {
		if (index - removedImagesCount == extraImagesCount-1){
			addExtraImageSection(index+1);
			extraImagesCount ++;
		}
	}

}

function addExtraImageSection(index){
	htmlForExtraImageSection = '<div class="col-4 border" id="divExtraImage'+index+'">'+
				'<div id="extraImageHeader'+index+'"><label>Extra image:</label></div>'+
				'<div class="text-center">'+
					'<img id="extraThumbnail'+index+'" src="'+defaultThumbnail+'" style="max-width:50%;"/>'+
				'</div>'+
				'<div class="m-2">'+
					'<input type="file" name="fileExtraImage"  accept="image/png, image/jpeg" '+
					' onchange="showExtraImageThumbnail(this, '+index+')"/>'+
				'</div>'+
			'</div>	';
	htmlLinkRemove = '<a class="btn fas fa-times-circle float-right icon-gray" href="javascript:removeExtraImage('+(index-1)+')"'+
						' title="Remove this image"></a>';
	
	$("#divExtraImages").append(htmlForExtraImageSection);
	$("#extraImageHeader" + (index-1)).append(htmlLinkRemove);
	
}

function removeExtraImage(index){
	$("#divExtraImage"+index).remove();
	extraImagesCount --;
	removedImagesCount ++;
}
