function validateRegister() {

    var ERR_EMPTY_FIELD = "Заполните поле",
        ERR_EMAIL_PATTERN = "Email должен содержать . и @, например: 'ivanov.ivan@gmail.com'",
        ERR_PASSWORD_NOT_MATCH = "Пароли не совпадают",
        ERR_PASSWORD_PATTERN = "Пароль должен состоять только из латинских букв и цифр, знаков '_', '-', содержать не менее 8 символов, в т.ч. 1 заглавную букву, 1 строчную букву, 1 цифру",
        ERR_BDATE_MORE = "Максимальный возраст - 120 лет",
        ERR_BDATE_LESS = "Вам нет 18 лет",
        ERR_NAME_PATTERN = "Поле должно состоять только из латинских букв";

    var htmlEl = document.documentElement;
    var locale = htmlEl.getAttribute("lang");
    if (locale == "en") {
        ERR_EMPTY_FIELD = "Fill the field";
        ERR_EMAIL_PATTERN = "Email must contain . and @, eg: 'ivanov.ivan@gmail.com'";
        ERR_PASSWORD_NOT_MATCH = "Passwords do not match";
        ERR_PASSWORD_PATTERN = "Password should contain only latin letters and digits, symbols '_', '-', contain not less than 8 symbols, including 1 uppercase letter, 1 lowercase letter, 1 digit";
        ERR_BDATE_MORE = "The maximum age is 120 years";
        ERR_BDATE_LESS = "You are under 18 years old";
        ERR_NAME_PATTERN = "The field must consist only of Latin letters";
    }

    var result = true;

    var PATTERN_PASSWORD = "^(?=\\w*[0-9])(?=\\w*[a-z])(?=\\w*[A-Z])(?=\\S+$)[\\w_-]{8,}$",
        PATTERN_NAME = "^[A-Za-z]{1,70}$";

    var errEmail = document.getElementById("err-email"),
        errPwd1 = document.getElementById("err-pwd1"),
        errPwd2 = document.getElementById("err-pwd2"),
        errBdate = document.getElementById("err-bdate"),
        errFname = document.getElementById("err-fname"),
        errMname = document.getElementById("err-mname"),
        errLname = document.getElementById("err-lname");

    var form = document.registerForm,
        email = form.email.value,
        pwd1 = form.password.value,
        pwd2 = form.password_again.value,
        bdate = form.birthdate.value,
        fname = form.fname.value,
        mname = form.mname.value,
        lname = form.lname.value;

    errEmail.innerHTML = "";
    errPwd1.innerHTML = "";
    errPwd2.innerHTML = "";
    errBdate.innerHTML = "";
    errFname.innerHTML = "";
    errMname.innerHTML = "";
    errLname.innerHTML = "";

    if (!email) {
        errEmail.innerHTML = ERR_EMPTY_FIELD;
        result = false;
    }
    if (email && !(~email.indexOf(".") && ~email.indexOf("@"))) {
        errEmail.innerHTML = ERR_EMAIL_PATTERN;
        result = false;
    }
    if (!pwd1) {
        errPwd1.innerHTML = ERR_EMPTY_FIELD;
        result = false;
    }
    if (!pwd2) {
        errPwd2.innerHTML = ERR_EMPTY_FIELD;
        result = false;
    }
    if (pwd1 && !~pwd1.search(PATTERN_PASSWORD)) {
        errPwd1.innerHTML = ERR_PASSWORD_PATTERN;
        form.password.value = "";
        form.password_again.value = "";
        result = false;
    }
    if (pwd1 && pwd2 && pwd1 !== pwd2) {
        errPwd1.innerHTML = ERR_PASSWORD_NOT_MATCH;
        errPwd2.innerHTML = ERR_PASSWORD_NOT_MATCH;
        form.password.value = "";
        form.password_again.value = "";
        result = false;
    }
    if (!bdate) {
        errBdate.innerHTML = ERR_EMPTY_FIELD;
        result = false;
    }
    if (bdate) {
        bdate = new Date(bdate);
        var year = bdate.getFullYear(),
            month = bdate.getMonth(),
            day = bdate.getDate(),
            curDate = new Date(),
            curYear = curDate.getFullYear(),
            curMonth = curDate.getMonth(),
            curDay = curDate.getDate(),
            delYear = curYear - year,
            delMonth = curMonth - month,
            delDay = curDay - day;

        if (!(delYear > 18
            || delYear == 18 && delMonth > 0
            || delYear == 18 && delMonth == 0 && delDay >= 0)) {
            errBdate.innerHTML = ERR_BDATE_LESS;
            result = false;
        }

        if (delYear > 120) {
            errBdate.innerHTML = ERR_BDATE_MORE;
            result = false;
        }
    }

    if (!fname) {
        errFname.innerHTML = ERR_EMPTY_FIELD;
        result = false;
    }
    if (fname && !~fname.search(PATTERN_NAME)) {
        errFname.innerHTML = ERR_NAME_PATTERN;
        result = false;
    }

    if (!mname) {
        errMname.innerHTML = ERR_EMPTY_FIELD;
        result = false;
    }
    if (mname && !~mname.search(PATTERN_NAME)) {
        errMname.innerHTML = ERR_NAME_PATTERN;
        result = false;
    }

    if (!lname) {
        errLname.innerHTML = ERR_EMPTY_FIELD;
        result = false;
    }
    if (lname && !~lname.search(PATTERN_NAME)) {
        errLname.innerHTML = ERR_NAME_PATTERN;
        result = false;
    }

    return result;
}