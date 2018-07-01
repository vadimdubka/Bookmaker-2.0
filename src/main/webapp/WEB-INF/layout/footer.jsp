    <%@ page contentType="text/html;charset=UTF-8" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'default'}"/>
        <fmt:setBundle basename="textcontent.pagecontent"/>

        <footer>
        <div class="footer-social">
        <a href="mailto:vadimdubka@gmail.com" target="_blank">
        <img class="img-logo" src="<s:url value="/img/email.png"/>"
        alt="Email-logo" title="<fmt:message key="footer.email.title"/>">
        </a>
        <a href="https://github.com/vadimdubka/Bookmaker" target="_blank">
        <img class="img-logo" src="<s:url value="/img/github.png"/>"
        alt="GitHub-logo" title="<fmt:message key="footer.github.title"/>">
        </a>
        <a href="https://www.linkedin.com/in/dubatovka-vadim" target="_blank">
        <img class="img-logo" src="<s:url value="/img/linkedin.png"/>"
        alt="LinkedIn-logo" title="<fmt:message key="footer.linkedin.title"/>">
        </a>
        </div>
        <div class="copyright"><p><fmt:message key="footer.rights"/></p></div>
        </footer>
        <script>
        var errorMessage = "${requestScope.errorMessage}";
        var infoMessage = "${requestScope.infoMessage}";
        if (errorMessage) {
        alert(errorMessage);
        }
        if (infoMessage) {
        alert(infoMessage);
        }
        </script>