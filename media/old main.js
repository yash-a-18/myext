import { AxiomPatientTracker } from "./main copy.js";

(function () {
    const htmlContent = AxiomPatientTracker()
    // Do something with the result
    console.log(htmlContent)
    // Get the div with id `appp`
    const appDiv = document.getElementById('app');

    // Insert the generated HTML content into the div
    if (appDiv) {
        appDiv.innerHTML = htmlContent;
    } else {
        console.error("Div with id 'app' not found!");
    }
}());
