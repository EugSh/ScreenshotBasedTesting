export function getInput(element, alert_msg) {
    let input = element.value.trim();
    if (input.match(/[ .*+?^${}()|[\]\\]/g)) {
        alert("Forbidden symbols - .*+?^${}()|[  ]\\");
        return undefined;
    }
    if (input === "") {
        alert(alert_msg);
        return;
    }
    return input;
}