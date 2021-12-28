detailsCount=1;
$(document).ready(function(){
	detailsCount = $("div[id^='divDetail']").length;
});

function addDetailSection(){
	htmlDetailSection = `
		<div id="divDetail${detailsCount}" class="form-inline row justify-content-start m-3 ml-5 ">
				<label class="col-sm-1 col-form-label">Name:</label> 
				<input type="text" class="form-control col-sm-2" name="detailName" required maxlength="255" /> 
				<label class="col-sm-1 col-form-label">Value:</label> 
				<input type="text" class="form-control" name="detailValue" required maxlength="255" style="min-width:50%;"/> 
				<a class="text-decoration-none fas fa-times-circle fa-2x icon-gray ml-5" 
					href="javascript:removeDetail(${detailsCount})" "title="Remove this property"></a>
			</div>
	`;
	$("#detailsSection").append(htmlDetailSection);
	detailsCount ++;
};

function removeDetail(index){
	$('#divDetail'+index).remove();
	
}
