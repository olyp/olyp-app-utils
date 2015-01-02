(function (GLOBAL) {
    function olypHttpFactory(csrfToken) {
        return function http(method, path, body) {
            var deferred = when.defer();

            var xhr = new XMLHttpRequest();
            xhr.open(method, path, true);
            xhr.setRequestHeader("X-CSRF-Token", csrfToken);
            xhr.onload = function () {
                var status = xhr.status;
                if (status >= 200 && status < 300) {
                    var contentType = xhr.getResponseHeader("Content-Type");
                    if (contentType && contentType.indexOf("application/json") === 0) {
                        deferred.resolve(JSON.parse(xhr.responseText));
                    } else {
                        deferred.resolve(xhr.responseText);
                    }
                } else {
                    deferred.reject({status: status, body: xhr.responseText, xhr: xhr});
                }
            };
            if (body) {
                xhr.send(JSON.stringify(body));
            } else {
                xhr.send();
            }

            return deferred.promise;
        }
    }

    GLOBAL.OLYP_HTTP_FACTORY = olypHttpFactory;
}(this));
