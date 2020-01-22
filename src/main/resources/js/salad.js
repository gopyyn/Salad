if (!window.onerror) {
	//ajax is not resetting the active ajax count if there is error in the callback
	//listen to onError and reset the active if exist
	window.onerror = function(error) {
		if (window.jQuery.active) {
			--window.jQuery.active;
			console.log(error);
		}
	};
}
