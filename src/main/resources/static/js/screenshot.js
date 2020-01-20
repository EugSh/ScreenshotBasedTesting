import {defaultHandler, sendRequest} from "./SendXMLRequest.js";
import {redirectBack} from "./Utils.js";


let roundId = document.querySelector(".main-content").getAttribute("data-roundId");
let testId = document.querySelector(".main-content").getAttribute("data-testId");
let screenshotId = document.querySelector(".main-content").getAttribute("data-screenshotId");


if (roundId !== null) {
    let btnDeleteTest = document.querySelector(".btn-right-top");
    btnDeleteTest.addEventListener("click", handlerDeleteScreenReport);
    sendRequest("get",
        "/api/report/test/screenshot", "roundId=" + roundId + "&testId=" + testId + "&screenshotId=" + screenshotId,
        (r) => displayScreenReport(r));
} else {
    let btnDeleteTest = document.querySelector(".btn-right-top");
    btnDeleteTest.addEventListener("click", handlerDeleteScreenshot);
    sendRequest("get",
        "/api/reference",
        "testId=" + testId,
        (r) => displayScreen(r, "reference", "Reference screenshot"),
        defaultHandler,
        handlerNotFoundReference);
    sendRequest("get",
        "/api/difference",
        "testId=" + testId + "&screenshotId=" + screenshotId,
        (r) => displayScreen(r, "difference", "Difference screenshot"),
        defaultHandler,
        handlerNotFoundDifference);
    sendRequest("get",
        "/api/screenshot",
        "testId=" + testId + "&screenshotId=" + screenshotId,
        (r) => displayScreen(r, "screenshot", "Screenshot"));
}

function displayScreen(request, id, title) {
    let image = document.getElementById(id);
    let json = JSON.parse(request.responseText);
    setImage(image, json["image"], title);
}

function setImage(image, imageObj, title) {
    if (imageObj["imageType"].toLowerCase() === "png") {
        image.src = "data:image/png;base64," + imageObj["imageBytes"];
    } else {
        image.src = "data:image/jpeg;base64," + imageObj["imageBytes"];
    }
    let itemName = image.parentElement.querySelector(".container-item-name");
    itemName.textContent = title;
    image.parentElement.classList.remove("__hidden");
}


function displayScreenReport(request) {
    let imageDifference = document.getElementById("difference");
    let imageReference = document.getElementById("screenshot");
    let json = JSON.parse(request.responseText);
    setImage(imageReference, json["expected"], "Expected screenshot");
    setImage(imageDifference, json["actual"], "Actual screenshot");

}

function handlerDeleteScreenshot() {
    sendRequest("delete", "/api/screenshot", "testId=" + testId + "&screenshotId=" + screenshotId, (request) => {
        alert("Removal completed successfully");
        let url = "/service/test?testId=" + testId;
        redirectBack(url);
    });
}

function handlerDeleteScreenReport() {
    sendRequest("delete",
        "/api/report/test/screenshot",
        "roundId=" + roundId + "&testId=" + testId + "&screenshotId=" + screenshotId,
        (request) => {
            alert("Removal completed successfully");
            redirectBack("/service/report/test?roundId=" + roundId + "&testId=" + testId);
        });
}

function handlerNotFoundReference(request) {
}

function handlerNotFoundDifference(request) {
}