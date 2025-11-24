// app.js - comportamiento UI ligero para Material Admin

document.addEventListener('DOMContentLoaded', function () {

    // Inicializa tooltips y popovers de MDB (si están presentes)
    try {
        if (typeof mdb !== 'undefined') {
            var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-mdb-toggle="tooltip"]'));
            tooltipTriggerList.map(function (el) { return new mdb.Tooltip(el); });

            var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-mdb-toggle="popover"]'));
            popoverTriggerList.map(function (el) { return new mdb.Popover(el); });
        }
    } catch (e) {
        // No fatal si MDB no está cargado
        console.debug('MDB init skipped', e);
    }

    // Confirmación para botones de borrar (forms con .needs-confirm)
    document.querySelectorAll('form.needs-confirm').forEach(function(form){
        form.addEventListener('submit', function(e){
            var msg = form.getAttribute('data-confirm') || '¿Confirmas esta acción?';
            if (!confirm(msg)) {
                e.preventDefault();
            }
        });
    });

    // Small helper: focus first input in modal forms (if any)
    document.querySelectorAll('.modal').forEach(function(modal){
        modal.addEventListener('shown.bs.modal', function () {
            var input = modal.querySelector('input, textarea, select');
            if (input) input.focus();
        });
    });

    // Optional: convierte cualquier input[type="date"] con placeholder yyyy-mm-dd
    document.querySelectorAll('input[data-date-autofill]').forEach(function(inp){
        if (!inp.value) {
            var d = new Date();
            var s = d.toISOString().split('T')[0];
            inp.value = s;
        }
    });

    // Helper: form submissions via AJAX (data-ajax="true")
    document.querySelectorAll('form[data-ajax="true"]').forEach(function(form){
        form.addEventListener('submit', function(e){
            e.preventDefault();
            var url = form.getAttribute('action') || window.location.href;
            var method = (form.getAttribute('method') || 'POST').toUpperCase();
            var formData = new FormData(form);
            var payload = {};
            formData.forEach(function(value, key){ payload[key] = value; });

            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                body: JSON.stringify(payload)
            })
                .then(function(resp){ return resp.json(); })
                .then(function(json){
                    if (json && json.message) alert(json.message);
                    // puedes actualizar la UI aquí o redireccionar
                })
                .catch(function(err){
                    console.error('AJAX form error', err);
                    alert('Error en la petición');
                });
        });
    });

}); // DOMContentLoaded
