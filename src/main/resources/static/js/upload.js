import {getInput} from "./GetCorrectInput.js";
import {defaultHandler} from "./SendXMLRequest.js";

let input = document.querySelector(".div-form-input-file");

let label = input.nextElementSibling;
let preview = document.getElementById("preview");
let bytes = undefined;
let width = 0;
let height = 0;
let btnUpload = document.getElementById("upload");
let inputScreenId = document.getElementById("screenshotId");
let testId = document.querySelector(".main-content").getAttribute("data-testId");


btnUpload.addEventListener("click", (e) => {
    let file = document.getElementById("screenshot").files[0];
    if (file === '' || width === 0 || height === 0) {
        alert("Select screenshot");
        return;
    }
    let screenshotId = getInput(inputScreenId, "Enter screenshot Id");
    if (screenshotId === "") {
        return;
    }
    sendScreenshot(screenshotId, file);

});
input.addEventListener('change', function (e) {
    let fileName = "";
    if (this.files.length > 1) {
        alert("Select one screenshot");
        input.value = "";
        return;
    }
    fileName = e.target.value.split('\\').pop();
    label.textContent = fileName;
    var reader = new FileReader();
    reader.onload = function (e) {
        let img = new Image();
        img.src = e.target.result;
        img.onload = function () {
            width = img.width;
            height = img.height;
        };
        preview.setAttribute('src', e.target.result);
    };
    reader.readAsDataURL(this.files[0]);
});

function handlerAddScreen(request) {
    let json = JSON.parse(request.responseText);
    alert("Screen was add. Test id - " + json["testId"] + " and screenshot id - " + json["screenshotId"]);
    width = 0;
    height = 0;
    bytes = undefined;
    input.value = "";
    preview.src = "";
}

function sendScreenshot(screenshotId, file) {
    var Request = new XMLHttpRequest();
    if (!Request) {
        return;
    }
    Request.onreadystatechange = function () {
        if (Request.readyState === 4) {
            switch (Request.status) {
                case 200:
                    handlerAddScreen(Request);
                    break;
                case 400:
                    defaultHandler(Request);
                    break;
                case 404:
                    defaultHandler(Request);
                    break;
                default:
                    alert(Request.responseText);
            }

        }
    };
    let formData = new FormData();
    formData.append("testId", testId);
    formData.append("screenshotId", screenshotId);
    formData.append("screenshot", file);
    formData.append("height", height);
    formData.append("width", width);

    Request.open("post", "/api/screenshot", true);
    Request.send(formData);

}
