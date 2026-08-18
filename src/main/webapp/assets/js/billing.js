/**
 * Sunrise Dental Clinic - Dynamic Billing & Strategy Calculator
 */
document.addEventListener('DOMContentLoaded', () => {
    const consultFeeInput = document.getElementById('consultationFee');
    const treatCostInput = document.getElementById('treatmentCost');
    const additionalInput = document.getElementById('additionalCharges');
    const discountTypeSelect = document.getElementById('discountType');
    const taxInput = document.getElementById('taxPercentage');

    const subtotalDisplay = document.getElementById('calcSubtotal');
    const discountDisplay = document.getElementById('calcDiscount');
    const taxDisplay = document.getElementById('calcTax');
    const totalDisplay = document.getElementById('calcTotal');

    function recalculateBill() {
        const consult = parseFloat(consultFeeInput ? consultFeeInput.value : 0) || 0;
        const treat = parseFloat(treatCostInput ? treatCostInput.value : 0) || 0;
        const additional = parseFloat(additionalInput ? additionalInput.value : 0) || 0;
        const taxRate = parseFloat(taxInput ? taxInput.value : 0) || 0;

        const subtotal = consult + treat + additional;

        // Determine discount rate based on selected strategy
        let discountPercent = 0;
        const discType = discountTypeSelect ? discountTypeSelect.value : 'Standard';

        if (discType.includes('SENIOR')) {
            discountPercent = 10.0;
        } else if (discType.includes('INSUR')) {
            discountPercent = 15.0;
        } else if (discType.includes('LOYAL')) {
            discountPercent = 5.0;
        } else {
            discountPercent = 0.0;
        }

        const discountAmount = Math.round((subtotal * (discountPercent / 100.0)) * 100) / 100;
        const taxableAmount = Math.max(0, subtotal - discountAmount);
        const taxAmount = Math.round((taxableAmount * (taxRate / 100.0)) * 100) / 100;
        const totalAmount = Math.round((taxableAmount + taxAmount) * 100) / 100;

        if (subtotalDisplay) subtotalDisplay.textContent = 'LKR ' + subtotal.toLocaleString('en-US', { minimumFractionDigits: 2 });
        if (discountDisplay) discountDisplay.textContent = '- LKR ' + discountAmount.toLocaleString('en-US', { minimumFractionDigits: 2 }) + ' (' + discountPercent + '%)';
        if (taxDisplay) taxDisplay.textContent = '+ LKR ' + taxAmount.toLocaleString('en-US', { minimumFractionDigits: 2 });
        if (totalDisplay) totalDisplay.textContent = 'LKR ' + totalAmount.toLocaleString('en-US', { minimumFractionDigits: 2 });
    }

    if (consultFeeInput) consultFeeInput.addEventListener('input', recalculateBill);
    if (treatCostInput) treatCostInput.addEventListener('input', recalculateBill);
    if (additionalInput) additionalInput.addEventListener('input', recalculateBill);
    if (discountTypeSelect) discountTypeSelect.addEventListener('change', recalculateBill);
    if (taxInput) taxInput.addEventListener('input', recalculateBill);

    // Initial calculation
    recalculateBill();
});
