<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="j" uri="http://bookmaker.com/functions" %>
<fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'default'}"/>
<fmt:setBundle basename="textcontent.pagecontent"/>
<%@ page isErrorPage="true" %>

<%--TODO настроить показ страниц с ошибками--%>
<main class="container">
    <h3>Error page</h3><%--TODO delete--%>
    <section class="col-s-10 col-12">
        <ul class="error-paragraph">
            <li>Request from <c:out value="${pageContext.errorData.requestURI}" default="N/A"/> is failed</li>
            <li>Servlet name or type: <c:out value="${pageContext.errorData.servletName}" default="N/A"/></li>
            <li>Status code: <c:out value="${pageContext.errorData.statusCode}" default="N/A"/></li>
            <li>Exception: <c:out value="${pageContext.errorData.throwable}" default="N/A"/></li>
            <li>Message from exception: <c:out value="${pageContext.exception.message}" default="N/A"/></li>
            <li>Error message: <c:out value="${errorMessage}" default="Invalid request"/></li>
        </ul>
        <div class="custom-button">
            <a href="<c:url value="/main_page"/>">Back</a>
        </div>
    </section>
</main>