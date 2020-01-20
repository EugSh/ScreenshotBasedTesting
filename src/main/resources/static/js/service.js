import {getInput} from "./GetCorrectInput.js";
import {sendRequest} from "./SendXMLRequest.js";
import {createItem} from "./ElementCreator.js";
import {resultFromReportsDTO} from "./Utils.js";
import {disableWaiter, enableWaiter} from "./Utils.js";

let section = document.querySelector(".main-content").getAttribute("data-section");

let btnRunRound = document.querySelector(".btn-run-round .div-form-btn");
btnRunRound.addEventListener("click", runRoundHandler);

let btnCreateTest = document.querySelector(".btn-create-test .div-form-btn");
btnCreateTest.addEventListener("click", creatTestHandler);

let btnRounds = document.querySelector(".btn-left-top");
btnRounds.addEventListener("click", () => rounds());

let btnTests = document.querySelector(".btn-right-top");
btnTests.addEventListener("click", () => tests());

if (section === "rounds") {
    rounds();
} else if (section === "tests") {
    tests();
} else {
    alert("There is no section " + section);
}

function rounds() {
    clearContainer();
    document.querySelector(".div-form.btn-run-round")
        .classList
        .remove("__hidden");
    document.querySelector(".div-form.btn-create-test")
        .classList
        .add("__hidden");
    displayReports();
}

function tests() {
    clearContainer();
    document.querySelector(".div-form.btn-run-round")
        .classList
        .add("__hidden");
    document.querySelector(".div-form.btn-create-test")
        .classList
        .remove("__hidden");
    displayTests();
}

function clearContainer() {
    let container = document.querySelector(".container");
    while (container.firstChild) {
        container.removeChild(container.firstChild);
    }
}


/**
 *
 */
function runRoundHandler() {
    let roundId = getInput(document.querySelector(".btn-run-round .div-form-input"),
        "Enter round id.");
    if (roundId === undefined) {
        return;
    }

    enableWaiter();
    sendRequest("post",
        "/api/round/run",
        "roundId=" + roundId,
        (request) => responseHandlerRunRound(request, getDefaultItemParamForRound()));
}

function getDefaultItemParamForRound() {
    const itemCreatorParam = new Map();
    itemCreatorParam.set("form-action", "/service/report");
    itemCreatorParam.set("form-method", "get");
    itemCreatorParam.set("btn-name", "roundId");
    itemCreatorParam.set("btn-delete-url", "/api/report");
    return itemCreatorParam;
}

function creatTestHandler() {
    let testId = getInput(document.querySelector(".btn-create-test .div-form-input"),
        "Enter test id.");
    if (testId==="") {
        return;
    }

    sendRequest("post",
        "/api/test",
        "testId=" + testId,
        (request) => responseHandlerAddTest(request, getDefaultItemParamForTest()));
}

function getDefaultItemParamForTest() {
    const itemCreatorParam = new Map();
    itemCreatorParam.set("form-action", "/service/test");
    itemCreatorParam.set("form-method", "get");
    itemCreatorParam.set("btn-name", "testId");
    itemCreatorParam.set("btn-run", true);
    itemCreatorParam.set("btn-delete-url", "/api/test");
    itemCreatorParam.set("btn-delete-param-extractor", (e) => paramExtractorNameId(e, "testId"));
    itemCreatorParam.set("btn-run-url", "/api/test/run");
    itemCreatorParam.set("btn-run-param-extractor", (e) => paramExtractorNameId(e, "testId"));
    itemCreatorParam.set("btn-run-response-handler", (r, e) => {
        let json = JSON.parse(r.innerText);
        e.querySelector(".container-item-result").innerText = "Result: " + resultFromReportsDTO(json);
    });
    return itemCreatorParam;
}



function responseHandlerRunRound(request, itemParam) {
    let reportDTO = JSON.parse(request.responseText);
    addItemReport(reportDTO, itemParam, document.querySelector(".container"));
    disableWaiter();
}

function addItemReport(reportDTO, itemParam, container) {
    itemParam.set("btn-value", reportDTO["id"]);
    itemParam.set("btn-text", "Round: " + reportDTO["id"]);
    itemParam.set("result", resultFromReportsDTO(reportDTO));
    itemParam.set("btn-delete-param-extractor", (e) => "roundId=" + reportDTO["id"]);
    let item = createItem(itemParam);
    container.insertAdjacentElement('afterbegin', item);
}



/**
 *
 * @param {XMLHttpRequest}request
 * @param {Map}itemParam
 */
function responseHandlerAddTest(request, itemParam) {
    let testCase = JSON.parse(request.responseText);
    console.log(testCase);
    itemParam.set("btn-run-response-handler", (r, e) => {
        let json = JSON.parse(r.innerText);
        console.log(json);
        e.querySelector(".container-item-result").innerText = "Result: " + json["differenceStatus"];
    });
    let reportDTO = {
        id: testCase["testId"],
        status: testCase["differenceStatus"],
        okCount: "0",
        failedCount: "0"
    };
    addItemTest(reportDTO, itemParam, document.querySelector(".container"));

}

function addItemTest(testCase, itemParam, container) {
    itemParam.set("btn-value", testCase["id"]);
    itemParam.set("btn-text", "Test: " + testCase["id"]);
    itemParam.set("result", resultFromReportsDTO(testCase));
    itemParam.set("btn-delete-param-extractor", (e) => "testId=" + testCase["id"]);
    itemParam.set("btn-run-param-extractor", (e) => "testId=" + testCase["id"]);
    let item = createItem(itemParam);
    container.insertAdjacentElement('afterbegin', item);
}

function displayReports() {
    sendRequest("get",
        "/api/reports",
        "",
        displayReportsHandler)
}

function displayTests() {
    sendRequest("get",
        "/api/tests",
        "",
        displayTestsHandler)
}

function displayReportsHandler(request) {
    let response = request.responseText;
    let json = JSON.parse(response);
    for (let i = 0; i < json.length; i++) {
        let report = json[i];
        let itemParam = getDefaultItemParamForRound();
        itemParam.set("btn-delete-param-extractor", (e) => "roundId=" + report["id"]);
        addItemReport(report, itemParam, document.querySelector(".container"));
    }
}

function displayTestsHandler(request) {
    let response = request.responseText;
    let json = JSON.parse(response);
    for (let i = 0; i < json.length; i++) {
        let testCase = json[i];
        let itemParam = getDefaultItemParamForTest();
        itemParam.set("btn-delete-param-extractor", (e) => "testId=" + testCase["testId"]);
        itemParam.set("btn-run-param-extractor", (e) => "testId=" + testCase["testId"]);
        addItemTest(testCase, itemParam, document.querySelector(".container"));
    }
}

