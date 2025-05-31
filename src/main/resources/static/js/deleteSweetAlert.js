// const baseUrl = "http://localhost:8081";
const baseUrl = window.location.origin;

async function deleteCategory(id) {
    Swal.fire({
        title: 'Are you sure?',
        text: 'You won’t be able to revert this!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'No, cancel!',
        customClass: {
            confirmButton: 'btn-confirm',
            cancelButton: 'btn-cancel'
        },
        buttonsStyling: false  // Important to allow custom classes
    }).then((result) => {
        if (result.isConfirmed) {
            const url = `${baseUrl}/admin/deleteCategoryById/${id}`;
            window.location.replace(url) ;
            console.log("Category Deleted in Url  "+ url);
        }
    });
}

async function deleteProduct(id) {
    Swal.fire({
        title: 'Are you sure?',
        text: 'You won’t be able to revert this!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'No, cancel!',
        customClass: {
            confirmButton: 'btn-confirm',
            cancelButton: 'btn-cancel'
        },
        buttonsStyling: false  // Important to allow custom classes
    }).then((result) => {
        if (result.isConfirmed) {
            const url = `${baseUrl}/admin/deleteProductById/${id}`;
            window.location.replace(url) ;
            console.log("Product Deleted in Url "+ url);
        }
    });
}

async function updateStatus(id, status) {
    Swal.fire({
        title: 'Are you sure?',
        text: 'You won’t be able to revert this!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Just,do it!',
        cancelButtonText: 'No, cancel!',
        customClass: {
            confirmButton: 'btn-confirm',
            cancelButton: 'btn-cancel'
        },
        buttonsStyling: false  // Important to allow custom classes
    }).then((result) => {
        if (result.isConfirmed) {
            const url = `${baseUrl}/admin/updateAccountStatus?id=${id}&status=${status}`;
            window.location.replace(url) ;
            console.log("Product Deleted in Url "+ url);
        }
    });
}

async function updateOrderStatus(oid,status) {
    Swal.fire({
        title: 'Are you sure?',
        text: 'You won’t be able to revert this!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes,I want to do it!',
        cancelButtonText: 'No, cancel!',
        customClass: {
            confirmButton: 'btn-confirm',
            cancelButton: 'btn-cancel'
        },
        buttonsStyling: false  // Important to allow custom classes
    }).then((result) => {
        if (result.isConfirmed) {
            const url = `${baseUrl}/user/updateOrderStatus?oid=${oid}&status=${status}`;
            window.location.replace(url) ;
            console.log("Product Deleted in Url "+ url);
        }
    });
}


