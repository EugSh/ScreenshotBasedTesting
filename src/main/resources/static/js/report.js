import {sendRequest} from "./SendXMLRequest.js";
import {btnDeleteHandler, getBtnDelete} from "./ToolBar.js";
import {addClickListener, paramExtractorNameId, redirectBack} from "./Utils.js";

let roundId = document.querySelector(".main-content").getAttribute("data-roundId");
let testId = document.querySelector(".main-content").getAttribute("data-testId");

let btnBack = document.querySelector(".btn-left-top");
btnBack.addEventListener("click", () => redirectBack(getRedirectBackUrl()));

if (testId === null) {
    displayTestsReport();
} else {
    displayScreenshotsReport();
}

function displayTestsReport() {
    let btnDeleteReport = document.querySelector(".btn-right-top");
    btnDeleteReport.addEventListener("click", () => handlerDeleteReport("/api/report", "roundId=" + roundId));
    let items = document.querySelectorAll(".container-item");
    addClickListener(Array.from(items), "/api/report/test", btnDeleteHandler, getBtnDelete, deleteTestReportParamExtractor)
}

function displayScreenshotsReport() {
    let btnDeleteReport = document.querySelector(".btn-right-top");
    btnDeleteReport.addEventListener("click", () => handlerDeleteReport("/api/report/test", "roundId=" + roundId + "&testId=" + testId));
    let items = document.querySelectorAll(".container-item");
    addClickListener(Array.from(items), "/api/report/test/screenshot", btnDeleteHandler, getBtnDelete, deleteScreenshotReportParamExtractor)
}

function deleteTestReportParamExtractor(item) {
    return "roundId=" + roundId + "&" + paramExtractorNameId(item, "testId");
}

function deleteScreenshotReportParamExtractor(item) {
    return "roundId=" + roundId + "&testId=" + testId + "&" + paramExtractorNameId(item, "screenshotId");
}

function handlerDeleteReport(url, args) {
    sendRequest("delete", url, args, (request) => {
        alert("Removal completed successfully");
        redirectBack(getRedirectBackUrl());
    });
}

function getRedirectBackUrl() {
    return testId === null ?
        "/service?section=rounds" :
        "/service/report?roundId=" + roundId + "&testId=" + testId
}