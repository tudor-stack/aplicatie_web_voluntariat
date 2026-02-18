document.addEventListener("DOMContentLoaded", function() {

    // CONFIGURARE: Lista ID-urilor pe care le salvăm
    const formFieldsIds = ["title", "startDate", "endDate", "duration", "description", "category"];
    const STORAGE_KEY = "event_form_draft";

    // --- 1. RESTAURARE DATE (LOAD) ---
    function loadSavedData() {
        const savedDataJson = localStorage.getItem(STORAGE_KEY);
        if (savedDataJson) {
            const savedData = JSON.parse(savedDataJson);
            formFieldsIds.forEach(id => {
                const element = document.getElementById(id);
                if (element && savedData[id]) {
                    element.value = savedData[id];
                }
            });
        }
    }
    loadSavedData(); // Rulăm la pornire

    // --- 2. SALVARE DATE (SAVE) ---
    function saveToLocalStorage() {
        const currentData = {};
        formFieldsIds.forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                currentData[id] = element.value;
            }
        });
        localStorage.setItem(STORAGE_KEY, JSON.stringify(currentData));
    }

    // Ataşăm salvarea la orice modificare
    formFieldsIds.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.addEventListener("input", saveToLocalStorage);
            element.addEventListener("change", saveToLocalStorage);
        }
    });

    // --- 3. VALIDARE DATE CALENDARISTICE (LOGICA VECHE) ---
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");
    const dateHint = document.getElementById("dateHint");

    // Azi + 2 zile
    let today = new Date();
    today.setDate(today.getDate() + 2);
    let minDateStr = today.toISOString().split('T')[0];

    if(startDateInput) {
        startDateInput.setAttribute("min", minDateStr);
        if(dateHint) dateHint.innerText = "Data minimă permisă: " + minDateStr;
    }

    function validateDates() {
        let startVal = startDateInput.value;
        let endVal = endDateInput.value;

        if (startVal) {
            if(endDateInput) endDateInput.setAttribute("min", startVal);
            saveToLocalStorage(); // Salvăm și după validare
        }

        if (startVal && endVal && endVal < startVal) {
            alert("Data de final nu poate fi înaintea datei de start!");
            if(endDateInput) endDateInput.value = startVal;
            saveToLocalStorage(); // Salvăm corecția
        }
    }

    if(startDateInput && endDateInput) {
        startDateInput.addEventListener("change", validateDates);
        endDateInput.addEventListener("change", validateDates);
    }
});