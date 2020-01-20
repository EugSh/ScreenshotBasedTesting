import {sendRequest} from "./SendXMLRequest.js";
import {createItem} from "./ElementCreator.js";
import {resultFromReportsDTO} from "./Utils.js";

export function btnDeleteHandler(element, url, params, ok_handler = removeItemHandler) {
    sendRequest("delete", url, params, (request) => ok_handler(request, element));
}

export function btnRunHandler(element, url, params, ok_handler = runItemHandler) {
    document.querySelector(".dots-container").classList.remove("__hidden");
    sendRequest("post", url, params, (request) => ok_handler(request, element));
}

export function btnChoosePrefHandler(element, url, params, ok_handler = choosePrefHandler) {
    sendRequest("post", url, params, (request) => ok_handler(request));
}

export function getBtnDelete(element) {
    return element.querySelector(".btn-delete-item");
}

export function getBtnRun(element) {
    return element.querySelector(".btn-run-test");
}

export function getBtnChoosePref(element) {
    return element.querySelector(".btn-choose-pref");
}

function removeItemHandler(request, element) {
    element.parentNode.parentNode.parentNode.removeChild(element.parentNode.parentNode);
}

function runItemHandler(request, element) {
    let json = JSON.parse(request.responseText);
    element.parentElement.parentElement.querySelector(".container-item-result").innerText = "Result: " + resultFromReportsDTO(json);
    document.querySelector(".dots-container").classList.add("__hidden");
}

function choosePrefHandler(request) {
    let response = request.responseText;
    let json = JSON.parse(response);
    let previousPref = document.querySelector(".screenshot-preference");
    let btnPref = previousPref.querySelector(".div-form-btn");
    if (!previousPref.classList.contains("__hidden")) {
        let prevPrefId = btnPref.value;
        const itemCreatorParam = new Map();
        itemCreatorParam.set("form-action", "/service/test/screenshot");
        itemCreatorParam.set("form-method", "get");
        itemCreatorParam.set("btn-name", "screenshotId");
        itemCreatorParam.set("btn-value", prevPrefId);
        itemCreatorParam.set("btn-text", "Screenshot: " + prevPrefId);
        itemCreatorParam.set("btn-run", true);
        itemCreatorParam.set("btn-preference", true);
        itemCreatorParam.set("btn-delete-url", "/api/screenshot");
        itemCreatorParam.set("btn-delete-param-extractor", paramExtractorTestIdScreenId);
        itemCreatorParam.set("btn-run-url", "/api/difference");
        itemCreatorParam.set("btn-run-param-extractor", paramExtractorTestIdScreenId);
        itemCreatorParam.set("btn-choose-pref-url", "/api/reference");
        itemCreatorParam.set("btn-choose-pref-param-extractor", paramExtractorTestIdScreenId);
        itemCreatorParam.set("form-addition", getElementInputTestId(json["testId"]));
        let item = createItem(itemCreatorParam);
        let containerDiv = document.querySelector(".thymeleafListItem");
        containerDiv.insertAdjacentElement('afterbegin', item);
    }
    previousPref.classList.remove("__hidden");
    btnPref.value = json["screenshotId"];
    btnPref.innerText = "The reference screenshot is " + json["screenshotId"];
    let item = document.querySelector(".container-item button[value='" + json["screenshotId"] + "']")
        .parentNode
        .parentNode
        .parentNode
        .parentNode;
    item.parentNode.removeChild(item);
}

function paramExtractorTestIdScreenId(item) {
    let screenshotId = item.querySelector("button[name='screenshotId']").value;
    let testId = document.querySelector(".main-content").getAttribute("data-testId");
    return "testId=" + testId + "&screenshotId=" + screenshotId;
}

function getElementInputTestId(testId) {
    let input = document.createElement("input");
    input.classList.add("__hidden");
    input.setAttribute("name", "testId");
    input.setAttribute("value", testId);
    return input;
}

