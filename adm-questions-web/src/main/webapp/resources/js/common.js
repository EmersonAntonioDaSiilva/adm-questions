function onlyDigits(event) {
	var code, digits = "0123456789";

	if (document.all) {
		code = event.keyCode;
	} else if (event.which) {
		code = event.which;
	} else {
		code = null;
	}

	if (code == 8 || code == 49 || code == null || digits.indexOf(String.fromCharCode(code)) >= 0) {
		return true;
	}

	return false;
}

function filterAfterEnter(selector) {
	if ((event.keyCode ? event.keyCode : event.which) == 13) {
		PF(selector).filter();
		event.stopPropagation();
		event.preventDefault();
	}
}
