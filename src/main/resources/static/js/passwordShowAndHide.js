console.log("Enter in  PasswordShowAndHide.js");


let password = document.getElementById("password");
let confirmPassword = document.getElementById("confirmPassword");
let currentPassword = document.getElementById("currentPassword");
let eyeIcon = document.getElementById("eyeIconForPassword");
let eyeIcon1 = document.getElementById("eyeIconForConfirmPassword");
let eyeIcon2 = document.getElementById("eyeIconForCurrentPassword");

eyeIcon.onclick = function () {
    if (password.type === "password") {
        password.type = "text";
        eyeIcon.classList.remove("fa-eye-slash");
        eyeIcon.classList.add("fa-eye");
    } else {
        password.type = "password";
        eyeIcon.classList.remove("fa-eye");
        eyeIcon.classList.add("fa-eye-slash");
    }
    console.log("clicked : "+ password.type);
}

eyeIcon1.onclick = function () {
    if (confirmPassword.type === "password") {
        confirmPassword.type = "text";
        eyeIcon1.classList.remove("fa-eye-slash");
        eyeIcon1.classList.add("fa-eye");
    } else {
        confirmPassword.type = "password";
        eyeIcon1.classList.remove("fa-eye");
        eyeIcon1.classList.add("fa-eye-slash");
    }
    console.log("clicked  : " + confirmPassword.type);
}
eyeIcon2.onclick = function () {
    if (currentPassword.type === "password") {
        currentPassword.type = "text";
        eyeIcon2.classList.remove("fa-eye-slash");
        eyeIcon2.classList.add("fa-eye");
    } else {
        currentPassword.type = "password";
        eyeIcon2.classList.remove("fa-eye");
        eyeIcon2.classList.add("fa-eye-slash");
    }
    console.log("clicked  : " + currentPassword.type);
}
