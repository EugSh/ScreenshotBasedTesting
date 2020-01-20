/**
 *
 * @param {Map}map
 */
import {addClickListener} from "./Utils.js";
import {
    btnChoosePrefHandler,
    btnDeleteHandler,
    btnRunHandler,
    getBtnChoosePref,
    getBtnDelete,
    getBtnRun
} from "./ToolBar.js";

export function createItem(map) {
    let item = document.createElement("div");
    item.classList.add("container-item");
    let itemName = document.createElement("div");
    itemName.classList.add("container-item-name");
    let divForm = document.createElement("div");
    divForm.classList.add("div-form");
    let form = document.createElement("form");
    form.action = map.get("form-action");
    form.method = map.get("form-method");
    form.enctype = "multipart/form-data";
    if (map.get("form-addition")) {
        form.appendChild(map.get("form-addition"));
    }
    let btn = document.createElement("button");
    btn.classList.add("div-form-btn");
    btn.name = map.get("btn-name");
    btn.value = map.get("btn-value");
    btn.type = "submit";
    btn.innerText = map.get("btn-text");
    let divResult = document.createElement("div");
    divResult.classList.add("container-item-result");
    divResult.innerText = "Result: " + map.get("result");
    let toolBar = document.createElement("div");
    toolBar.classList.add("container-item-toolbar");
    let btnDelete = document.createElement("div");
    btnDelete.classList.add("btn-delete-item", "btn-help");
    btnDelete.setAttribute("data-title", "Delete.");

    if (map.get('btn-preference')) {
        let btnPreference = document.createElement("div");
        btnPreference.classList.add("btn-choose-pref", "btn-help");
        btnPreference.setAttribute("data-title", "Choose preference.");
        toolBar.appendChild(btnPreference);
    }
    if (map.get("btn-run")) {
        let btnDelete = document.createElement("div");
        btnDelete.classList.add("btn-run-test", "btn-help");
        btnDelete.setAttribute("data-title", "Run.");
        toolBar.appendChild(btnDelete);
    }
    toolBar.appendChild(btnDelete);
    let divClear = document.createElement("div");
    divClear.classList.add("float-clear");
    form.appendChild(btn);
    divForm.appendChild(form);
    itemName.appendChild(divForm);
    item.append(itemName);
    item.appendChild(divResult);
    item.appendChild(toolBar);
    item.appendChild(divClear);
    addListener(item, map);
    return item;
}

function addListener(item, map) {
    addClickListener([item],
        map.get("btn-delete-url"),
        btnDeleteHandler,
        getBtnDelete,
        map.get("btn-delete-param-extractor"));
    if (map.get('btn-preference')) {
        addClickListener([item],
            map.get("btn-choose-pref-url"),
            btnChoosePrefHandler,
            getBtnChoosePref,
            map.get("btn-choose-pref-param-extractor"));
    }
    if (map.get("btn-run")) {
        addClickListener([item],
            map.get("btn-run-url"),
            btnRunHandler,
            getBtnRun,
            map.get("btn-run-param-extractor"));
    }
}