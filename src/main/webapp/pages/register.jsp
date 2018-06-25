<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="j" uri="http://bookmaker.com/functions" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'default'}"/>
<fmt:setBundle basename="textcontent.pagecontent"/>

<main class="row container">
    <section class="section-center col-s-10 col-6">
        <div class="section-header"><h2><fmt:message key="register.header"/></h2></div>
        <div class="section-content">
            <noscript><p class="error-message">${requestScope.errorMessage}</p></noscript>
            <form onsubmit="return validateRegister()" id="register-form" class="register-form" name="registerForm"
                  action="controller" method="post">
                <input type="hidden" name="command_type" value="register"/>
                <div class="input-block">
                    <label class="required" for="email-input"><fmt:message key="register.email"/></label>
                    <span id="err-email" class="err-msg"></span>
                    <input id="email-input" type="email" name="email" value="${email_input}"
                           pattern="^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$"
                           title="<fmt:message key="register.email.title"/>"
                           maxlength="320" required/>
                </div>
                <div class="input-block">
                    <label class="required" for="password-input"><fmt:message key="register.password"/></label>
                    <span id="err-pwd1" class="err-msg"></span>
                    <input id="password-input" type="password" name="password" value=""
                           pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[\w_-]{8,}$"
                           title="<fmt:message key="register.password.title"/>"
                           required/>
                </div>
                <div class="input-block">
                    <label class="required" for="password-input-again"><fmt:message key="register.passwordagain"/></label>
                    <span id="err-pwd2" class="err-msg"></span>
                    <input id="password-input-again" type="password" name="password_again" value=""
                           pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[\w_-]{8,}$"
                           title="<fmt:message key="register.password.title"/>"
                           required/>
                </div>
                <div class="input-block">
                    <label for="lname-input"><fmt:message key="register.lname"/></label>
                    <span id="err-lname" class="err-msg"></span>
                    <input id="lname-input" type="text" name="lname" value="${lname_input}" pattern="[A-Za-z]{1,70}"
                           title="<fmt:message key="register.lname.title"/>" required/>
                </div>
                <div class="input-block">
                    <label for="fname-input"><fmt:message key="register.fname"/></label>
                    <span id="err-fname" class="err-msg"></span>
                    <input id="fname-input" type="text" name="fname" value="${fname_input}" pattern="[A-Za-z]{1,70}"
                           title="<fmt:message key="register.fname.title"/>" required/>
                </div>
                <div class="input-block">
                    <label for="mname-input"><fmt:message key="register.mname"/></label>
                    <span id="err-mname" class="err-msg"></span>
                    <input id="mname-input" type="text" name="mname" value="${mname_input}" pattern="[A-Za-z]{1,70}"
                           title="<fmt:message key="register.mname.title"/>" required/>
                </div>
                <div class="input-block">
                    <label class="required" for="birthdate-input"><fmt:message key="register.birthdate"/></label>
                    <span id="err-bdate" class="err-msg"></span>
                    <input id="birthdate-input" type="date" name="birthdate" value="${birthdate_input}"
                           min="1900-01-01" title="<fmt:message key="register.birthdate.title"/>" required/>
                </div>
                <div class="input-block">
                    <input type="submit" value="<fmt:message key="register.submit"/>"/>
                </div>
            </form>
            <div class="custom-link">
                <a href="/controller?command_type=goto_index">
                    <fmt:message key="register.back"/>
                </a>
            </div>
        </div>
    </section>
</main>
<script type="text/javascript" src="/resources/js/validate-form.js"></script>


