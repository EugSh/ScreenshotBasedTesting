export function redirectBack(url) {
    window.location.assign(url);
}

export function paramExtractorNameId(item, name) {
    let id = item.querySelector("button[name='" + name + "']").value;
    return name + "=" + id;
}

/**
 *
 * @param {Array}items
 * @param {String}url
 * @param {function}handler
 * @param {function}bntExtractor
 * @param {function}paramExtractor
 */
export function addClickListener(items, url, handler, bntExtractor, paramExtractor) {
    items.forEach(function (item) {
        let param = paramExtractor(item);
        let btn = bntExtractor(item);
        btn.addEventListener("click", () => handler(bntExtractor(item), url, param));
    });
}

export function resultFromReportsDTO(json) {
    return json["status"] + ". p/f -- " + json["okCount"] + "/" + json["failedCount"];
}