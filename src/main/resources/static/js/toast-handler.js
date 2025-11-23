$(document).ready(function () {

    const successToast = $("#successToast");
    const errorToast = $("#errorToast");

    const successMsg = successToast.data("msg");
    const errorMsg = errorToast.data("msg");

    if (successMsg) {
        $("#successMessage").text(successMsg);
        new bootstrap.Toast(successToast[0]).show();
    }

    if (errorMsg) {
        $("#errorMessage").text(errorMsg);
        new bootstrap.Toast(errorToast[0]).show();
    }

});
