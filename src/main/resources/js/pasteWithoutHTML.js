var editableDiv = document.getElementById('document');
function handlepaste (e) {
    var types, pastedData, savedContent;
    if (e && e.clipboardData && e.clipboardData.types && e.clipboardData.getData) {
        types = e.clipboardData.types;
        if (((types instanceof DOMStringList) && types.contains("text/html")) || (types.indexOf && types.indexOf('text/html') !== -1)) {
            pastedData = e.clipboardData.getData('text/html');
            processPaste(editableDiv, pastedData);
            e.stopPropagation();
            e.preventDefault();
            return false;
        }
    }
    savedContent = document.createDocumentFragment();
    while(editableDiv.childNodes.length > 0) {
        savedContent.appendChild(editableDiv.childNodes[0]);
    }
    waitForPastedData(editableDiv, savedContent);
    return true;
}
function waitForPastedData (elem, savedContent) {
    if (elem.childNodes && elem.childNodes.length > 0) {
        var pastedData = elem.innerHTML;
        elem.innerHTML = "";
        elem.appendChild(savedContent);
        processPaste(elem, pastedData);
    }
    else {
        setTimeout(function () {
            waitForPastedData(elem, savedContent)
        }, 20);
    }
}
function processPaste (elem, pastedData) {
    alert(pastedData);
    elem.focus();
}
if (editableDiv.addEventListener) {
    editableDiv.addEventListener('paste', handlepaste, false);
}
else {
    editableDiv.attachEvent('onpaste', handlepaste);
}