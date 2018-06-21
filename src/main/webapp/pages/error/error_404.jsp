<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page isErrorPage="true" %>

<main class="container">
    <section class="col-s-10 col-12">
        <ul class="error-paragraph">
            <li>Request from <c:out value="${pageContext.errorData.requestURI}" default="N/A"/> is failed</li>
            <li>Servlet name or type: <c:out value="${pageContext.errorData.servletName}" default="N/A"/></li>
            <li>Status code: <c:out value="${pageContext.errorData.statusCode}" default="N/A"/></li>
            <li>Exception: <c:out value="${pageContext.errorData.throwable}" default="N/A"/></li>
            <li>Message from exception: <c:out value="${pageContext.exception.message}" default="N/A"/></li>
            <li>Error message: <c:out value="${errorMessage}" default="Requested resource doesn't exist"/></li>
        </ul>
        <div class="custom-button">
            <a href="${pageContext.request.contextPath}/controller?command=back_from_error">Back</a>
        </div>
    </section>
</main>