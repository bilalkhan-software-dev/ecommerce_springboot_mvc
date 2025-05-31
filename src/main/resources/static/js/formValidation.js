$(function() {
    // Add custom validation methods
    $.validator.addMethod("lettersOnly", function(value, element) {
        return this.optional(element) || /^[a-zA-Z\s]+$/.test(value);
    });

    $.validator.addMethod("digitsOnly", function(value, element) {
        return this.optional(element) || /^\d+$/.test(value);
    });

    $.validator.addMethod("lettersAndDigits", function(value, element) {
        return this.optional(element) || /^[a-zA-Z0-9\s\-.,]+$/.test(value);
    });

    $.validator.addMethod("noSpace", function(value, element) {
        return this.optional(element) || !/\s/.test(value);
    });

    $.validator.addMethod("fileSizeNotGreaterThan4Mb", function(value, element) {
        if (element.files && element.files[0]) {
            const fileSize = element.files[0].size / 1024 / 1024; // Convert to MB
            return fileSize <= 4;
        }
        return true;
    });

    $.validator.addMethod("validMobileNumber", function(value, element) {
        return this.optional(element) || /^[0-9]{11}$/.test(value);
    });


   const $resetPassword = $("#resetPasswordForm");
    $resetPassword.validate({
       rules:{
           password :{
               required:true,
               minlength : 6,
               noSpace :true
           },
           confirmPassword :{
               equalTo : "#password",
               minlength:6,
               noSpace : true
           }
       },
        messages:{
           password :{
               required : "This field is required",
               minlength : "Password must be 6 length characters",
               noSpace : "Space not Allowed"
           },
            confirmPassword :{
               equalTo : "Must be same as password" ,
                minlength : "Password must be 6 length character",
                noSpace : "Space Not Allowed"
            }
        }
    })


    const $orderPlaceForm = $("#orderPlaceForm");
    $orderPlaceForm.validate({
        rules: {
            firstName: {
                required: true,
                lettersOnly: true,
                minlength: 2
            },
            lastName: {
                required: true,
                lettersOnly: true,
                minlength: 2
            },
            email: {
                required: true,
                email: true
            },
            mobileNumber: {
                required: true,
                validMobileNumber: true
            },
            address: {
                required: true,
                minlength: 5
            },
            city: {
                required: true,
                lettersOnly: true
            },
            state: {
                required: true,
                lettersOnly: true
            },
            pinCode: {
                required: true,
                digitsOnly: true,
                minlength: 4,
                maxlength: 10
            },
            paymentMethod: {
                required: true
            }
        },
        messages: {
            firstName: {
                required: "Please enter your first name",
                lettersOnly: "Only alphabetic characters are allowed",
                minlength: "Name must be at least 2 characters"
            },
            lastName: {
                required: "Please enter your last name",
                lettersOnly: "Only alphabetic characters are allowed",
                minlength: "Name must be at least 2 characters"
            },
            email: {
                required: "Please enter your email",
                email: "Please enter a valid email address"
            },
            mobileNumber: {
                required: "Please enter your mobile number",
                validMobileNumber: "Please enter a valid mobile number"
            },
            address: {
                required: "Please enter your address",
                minlength: "Address must be at least 5 characters"
            },
            city: {
                required: "Please enter your city",
                lettersOnly: "Only alphabetic characters are allowed"
            },
            state: {
                required: "Please enter your state",
                lettersOnly: "Only alphabetic characters are allowed"
            },
            pinCode: {
                required: "Please enter your pin code",
                digitsOnly: "Only numbers are allowed",
                minlength: "Pin code must be at least 4 digits",
                maxlength: "Pin code cannot exceed 10 digits"
            },
            paymentMethod: {
                required: "Please select a payment method"
            }
        }
    })

    const $userRegistrationForm = $("#userRegistrationForm") ;
    $userRegistrationForm.validate({
        rules: {
            fullName: {
                required: true,
                lettersOnly: true,
                minlength: 2
            },
            email: {
                required: true,
                email: true,
                noSpace: true
            },
            password: {
                required: true,
                minlength: 6,
                noSpace: true
            },
            confirmPassword: {
                required: true,
                equalTo: "#password",
                noSpace: true
            },
            mobileNumber: {
                required: true,
                validMobileNumber: true
            },
            address: {
                required: true,
                lettersAndDigits: true,
                minlength: 5
            },
            city: {
                required: true,
                lettersOnly: true
            },
            state: {
                required: true,
                lettersOnly: true
            },
            pinCode: {
                required: true,
                digitsOnly: true,
                minlength: 4,
                maxlength: 10
            },
            file: {
                required: true,
                fileSizeNotGreaterThan4Mb: true,
                accept: "image/*"
            }
        },
        messages: {
            fullName: {
                required: "Please enter your full name",
                lettersOnly: "Only alphabetic characters are allowed",
                minlength: "Name must be at least 2 characters"
            },
            email: {
                required: "Please enter your email",
                email: "Please enter a valid email address",
                noSpace: "Spaces are not allowed"
            },
            password: {
                required: "Please enter a password",
                minlength: "Password must be at least 6 characters",
                noSpace: "Spaces are not allowed"
            },
            confirmPassword: {
                required: "Please confirm your password",
                equalTo: "Passwords do not match",
                noSpace: "Spaces are not allowed"
            },
            mobileNumber: {
                required: "Please enter your mobile number",
                validMobileNumber: "Please enter a valid 11-digit number"
            },
            address: {
                required: "Please enter your address",
                lettersAndDigits: "Only letters, numbers, and basic punctuation are allowed",
                minlength: "Address must be at least 5 characters"
            },
            city: {
                required: "Please enter your city",
                lettersOnly: "Only alphabetic characters are allowed"
            },
            state: {
                required: "Please enter your state",
                lettersOnly: "Only alphabetic characters are allowed"
            },
            pinCode: {
                required: "Please enter your pin code",
                digitsOnly: "Only numbers are allowed",
                minlength: "Pin code must be at least 4 digits",
                maxlength: "Pin code cannot exceed 10 digits"
            },
            file: {
                required: "Please upload a profile image",
                fileSizeNotGreaterThan4Mb: "Image size must be less than 4MB",
                accept: "Only image files are allowed"
            }
        }
    })


})