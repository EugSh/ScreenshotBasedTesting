export function sendRequest(r_method, r_path, r_args, ok_handler = defaultHandler, bad_req_handler = defaultHandler, not_found_handler = defaultHandler) {
    var Request = new XMLHttpRequest();
    if (!Request) {
        return;
    }
    Request.onreadystatechange = function () {
        if (Request.readyState === 4) {
            switch (Request.status) {
                case 200:
                    ok_handler(Request);
                    break;
                case 400:
                    bad_req_handler(Request);
                    break;
                case 404:
                    not_found_handler(Request);
                    break;
                default:
                    alert(Request.responseText);
            }

        }
    };

    if ((r_method.toLowerCase() === "get" || r_method.toLowerCase() === "delete") && r_args.length > 0)
        r_path += "?" + r_args;

    Request.open(r_method, r_path, true);

    if (r_method.toLowerCase() === "post") {
            Request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        Request.send(r_args);
    } else {
        Request.send(null);
    }
}

export function defaultHandler(request) {
    alert(request.responseText);
}