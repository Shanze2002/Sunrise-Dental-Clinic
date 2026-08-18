/**
 * Sunrise Dental Clinic - Appointment Booking & Dynamic Slot Checking
 */
document.addEventListener('DOMContentLoaded', () => {
    const doctorSelect = document.getElementById('doctorSelect');
    const dateInput = document.getElementById('appointmentDate');
    const slotContainer = document.getElementById('timeSlotGrid');
    const timeInput = document.getElementById('appointmentTime');
    const docFeeDisplay = document.getElementById('docFeeDisplay');
    const treatmentSelect = document.getElementById('treatmentSelect');
    const treatCostDisplay = document.getElementById('treatCostDisplay');
    const totalEstDisplay = document.getElementById('totalEstDisplay');

    // Standard clinic morning & afternoon slots
    const standardSlots = [
        "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00",
        "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00"
    ];

    function calculateEstimatedTotal() {
        let docFee = 0;
        let treatCost = 0;

        if (doctorSelect && doctorSelect.selectedIndex > 0) {
            const selectedOpt = doctorSelect.options[doctorSelect.selectedIndex];
            docFee = parseFloat(selectedOpt.getAttribute('data-fee')) || 0;
        }

        if (treatmentSelect && treatmentSelect.selectedIndex > 0) {
            const selectedOpt = treatmentSelect.options[treatmentSelect.selectedIndex];
            treatCost = parseFloat(selectedOpt.getAttribute('data-cost')) || 0;
        }

        if (docFeeDisplay) docFeeDisplay.textContent = 'LKR ' + docFee.toLocaleString('en-US', { minimumFractionDigits: 2 });
        if (treatCostDisplay) treatCostDisplay.textContent = 'LKR ' + treatCost.toLocaleString('en-US', { minimumFractionDigits: 2 });
        if (totalEstDisplay) totalEstDisplay.textContent = 'LKR ' + (docFee + treatCost).toLocaleString('en-US', { minimumFractionDigits: 2 });
    }

    if (doctorSelect) doctorSelect.addEventListener('change', () => {
        calculateEstimatedTotal();
        checkDoctorSlots();
    });

    if (treatmentSelect) treatmentSelect.addEventListener('change', calculateEstimatedTotal);
    if (dateInput) dateInput.addEventListener('change', checkDoctorSlots);

    function checkDoctorSlots() {
        if (!doctorSelect || !dateInput || !slotContainer) return;

        const doctorId = doctorSelect.value;
        const date = dateInput.value;

        if (!doctorId || !date) {
            slotContainer.innerHTML = '<p class="text-muted" style="font-size:0.85rem;">Please select a doctor and date to view available slots.</p>';
            return;
        }

        slotContainer.innerHTML = '<p class="text-muted" style="font-size:0.85rem;">Checking slot availability with clinic server...</p>';

        const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
        const apiUrl = contextPath + '/api/doctors/availability?doctorId=' + doctorId + '&date=' + date;

        fetch(apiUrl)
            .then(res => res.json())
            .then(json => {
                if (json.success && json.data) {
                    renderSlots(json.data.bookedSlots || []);
                } else {
                    renderSlots([]);
                }
            })
            .catch(err => {
                console.warn('Slot check fallback:', err);
                renderSlots([]);
            });
    }

    function renderSlots(bookedSlots) {
        if (!slotContainer) return;
        slotContainer.innerHTML = '';

        const selectedTimeVal = timeInput ? timeInput.value : '';

        standardSlots.forEach(slot => {
            const btn = document.createElement('button');
            btn.type = 'button';
            btn.className = 'slot-btn';
            btn.textContent = slot;

            const isBooked = bookedSlots.includes(slot);
            if (isBooked) {
                btn.disabled = true;
                btn.title = 'Slot already booked for this doctor';
            } else {
                if (selectedTimeVal.startsWith(slot)) {
                    btn.classList.add('selected');
                }

                btn.addEventListener('click', () => {
                    document.querySelectorAll('.slot-btn').forEach(b => b.classList.remove('selected'));
                    btn.classList.add('selected');
                    if (timeInput) {
                        timeInput.value = slot + ':00';
                    }
                });
            }
            slotContainer.appendChild(btn);
        });
    }

    // Initial check if values are preset
    if (doctorSelect && doctorSelect.value && dateInput && dateInput.value) {
        calculateEstimatedTotal();
        checkDoctorSlots();
    }
});
