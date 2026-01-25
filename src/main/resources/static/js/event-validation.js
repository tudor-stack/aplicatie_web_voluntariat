document.addEventListener("DOMContentLoaded", function() {
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");
    const dateHint = document.getElementById("dateHint");

    // 1. Calculăm data de azi + 2 zile
    let today = new Date();
    today.setDate(today.getDate() + 2);

    let minDateStr = today.toISOString().split('T')[0];

    // Verificăm dacă elementele există pe pagină înainte să le folosim (Safety check)
    if(startDateInput) {
        startDateInput.setAttribute("min", minDateStr);
        if(dateHint) dateHint.innerText = "Data minimă permisă: " + minDateStr;
    }

    function validateDates() {
        let startVal = startDateInput.value;
        let endVal = endDateInput.value;

        if (startVal) {
            endDateInput.setAttribute("min", startVal);
        }

        if (startVal && endVal && endVal < startVal) {
            alert("Data de final nu poate fi înaintea datei de start!");
            endDateInput.value = startVal;
        }
    }

    if(startDateInput && endDateInput) {
        startDateInput.addEventListener("change", validateDates);
        endDateInput.addEventListener("change", validateDates);
    }
});