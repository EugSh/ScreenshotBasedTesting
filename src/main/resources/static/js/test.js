import {defaultHandler, sendRequest} from "./SendXMLRequest.js";
import {redirectBack, addClickListener, paramExtractorNameId} from "./Utils.js";
import {
    btnChoosePrefHandler,
    btnDeleteHandler,
    btnRunHandler,
    getBtnChoosePref,
    getBtnDelete,
    getBtnRun
} from "./ToolBar.js";

let testId = document.querySelector(".main-content").getAttribute("data-testId");
let btnDeleteTest = document.querySelector(".btn-right-top");
btnDeleteTest.addEventListener("click", handlerDeleteTest);
loadPreference();

let items = document.querySelectorAll(".container-item");
let itemsArray = Array.from(items);
addClickListener(itemsArray, "/api/screenshot", btnDeleteHandler, getBtnDelete, extractTestIdScreenshotId);
addClickListener(itemsArray, "/api/difference", btnRunHandler, getBtnRun, extractTestIdScreenshotId);
addClickListener(itemsArray, "/api/reference", btnChoosePrefHandler, getBtnChoosePref, extractTestIdScreenshotId);

function loadPreference() {
    sendRequest("get", "/api/reference", "testId=" + testId, handlerHasPreference, defaultHandler, handlerNotFoundPreference);
}

function extractTestIdScreenshotId(item) {
    return "testId=" + testId + "&" + paramExtractorNameId(item, "screenshotId");
}

function handlerHasPreference(request) {
    let pref = document.querySelector(".screenshot-preference");
    let json = JSON.parse(request.responseText);
    pref.querySelector(".div-form-btn").value = json["screenshotId"];
    pref.querySelector(".div-form-btn").innerHTML = "The reference screenshot is "+ json["screenshotId"];
    pref.classList.remove("__hidden");
}

function handlerNotFoundPreference() {
    alert("Dont forget set preference screenshot");
}

function handlerDeleteTest() {
    sendRequest("delete", "/api/test", "testId=" + testId, (request) => {
        alert("Removal completed successfully");
        redirectBack("/service?section=tests");
    });
}