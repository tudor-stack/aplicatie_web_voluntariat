document.addEventListener("DOMContentLoaded", function() {
    
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");

    // Dacă nu suntem pe pagina de editare, ne oprim
    if (!startDateInput) return;

    // 1. Memorăm data originală (cea sfântă)
    const originalDate = startDateInput.value;

    // 2. Calculăm limita (Azi + 2 zile)
    let limitDate = new Date();
    limitDate.setDate(limitDate.getDate() + 2);
    const minDateStr = limitDate.toISOString().split('T')[0];

    // Funcție: Verifică dacă data curentă din input este validă conform regulii noi
    function isCurrentDateValid() {
        return startDateInput.value >= minDateStr;
    }

    // --- LOGICA MAGICĂ ---

    // A. La încărcare:
    // Dacă data originală este DEJA validă (e peste 1 lună), punem restricția permanent.
    // Dacă e "mâine" (invalidă pe regula nouă), NU o punem, ca să nu blocheze formularul.
    if (originalDate >= minDateStr) {
        startDateInput.setAttribute("min", minDateStr);
    }

    // B. Când utilizatorul dă CLICK/FOCUS să schimbe data:
    // Activăm restricția vizuală (facem zilele gri)
    startDateInput.addEventListener("focus", function() {
        this.setAttribute("min", minDateStr);
    });

    // C. Când utilizatorul PLEACĂ de pe câmp (Blur):
    startDateInput.addEventListener("blur", function() {
        // Dacă utilizatorul nu a schimbat data (sau a pus-o la loc pe cea veche)
        if (this.value === originalDate) {
            // SCOATEM restricția ca să nu țipe formularul la Submit
            // (Doar dacă originala era sub limită. Dacă era validă, o lăsăm).
            if (originalDate < minDateStr) {
                this.removeAttribute("min");
            }
        } else {
            // Dacă a ales o dată NOUĂ, păstrăm restricția, 
            // ca să fim siguri că noua dată e corectă.
            this.setAttribute("min", minDateStr);
        }
    });

    // D. Validare extra la schimbare
    startDateInput.addEventListener("input", function() {
        // Dacă a ales o dată nouă și e invalidă (a scris-o manual, nu din picker)
        if (this.value !== originalDate && this.value < minDateStr) {
            this.setCustomValidity("Trebuie să alegi o dată cu minim 2 zile în viitor.");
        } else {
            this.setCustomValidity(""); // E curat
        }
        
        // Sincronizare cu data de final
        if(endDateInput) {
            endDateInput.setAttribute("min", this.value);
            if(endDateInput.value < this.value) {
                endDateInput.value = this.value;
            }
        }
    });
});