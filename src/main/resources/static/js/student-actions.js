$(document).ready(function () {

    $('#deleteModal').on('show.bs.modal', function (event) {
        let id = $(event.relatedTarget).data('id');
        let page = $(event.relatedTarget).data('page');

        $(this).find('#deleteForm').attr('action', '/students/' + id + '/delete?page=' + page);
    });

    $(".viewBtn").click(function () {
        let id = $(this).data("id");

        $.ajax({
            url: "/students/" + id + "/details",
            type: "GET",
            success: function (s) {
                $("#vId").text(s.id);
                $("#vName").text(s.name);
                $("#vGender").text(s.gender);
                $("#vDob").text(s.dob);
                $("#vEmail").text(s.email);
                $("#vMobile").text(s.mobile);
                $("#vPhone").text(s.phone);

                new bootstrap.Modal(document.getElementById('viewModal')).show();
            }
        });
    });

});
