if (!window.salad) {
    window.salad = true;
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
    $( document ).ready(function() {
        console.log( "salad script is ready!" );

        var send = XMLHttpRequest.prototype.send;
        var release = function(){ --XMLHttpRequest.active };
        var onloadend = function(){ setTimeout(release, 1) };
        XMLHttpRequest.active = 0;
        XMLHttpRequest.prototype.send = function() {
            ++XMLHttpRequest.active;
            this.addEventListener('loadend', onloadend, true);
            send.apply(this, arguments);
        };
    });
}
