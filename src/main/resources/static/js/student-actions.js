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
            success: function (student) {
                $("#vId").text(student.id);
                $("#vName").text(student.name);
                $("#vGender").text(student.gender);
                $("#vDob").text(student.dob);
                $("#vEmail").text(student.email);
                $("#vMobile").text(student.mobile);
                $("#vPhone").text(student.phone);

                new bootstrap.Modal(document.getElementById('viewModal')).show();
            }
        });
    });

});
