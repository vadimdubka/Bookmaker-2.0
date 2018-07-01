    <%@ page contentType="text/html;charset=UTF-8" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <%@ taglib prefix="j" uri="http://bookmaker.com/functions" %>
        <%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
        <fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'default'}"/>
        <fmt:setBundle basename="textcontent.pagecontent"/>

        <!DOCTYPE html>
        <html lang="<fmt:message key="header.html.lang"/>">

        <head>
        <title>Bookmaker Online</title>
        <meta charset="UTF-8"/>
        <meta name="description" content="<fmt:message key="header.head.description"/>"/>
        <meta name="keywords" content="<fmt:message key="header.head.keywords"/>"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link rel="shortcut icon" type="image/png" href="<s:url value="/img/favicon.png"/>" />
        <link rel="stylesheet" type="text/css" href="<s:url value="/css/style.css"/>" />
        </head>

        <body>
        <header>
        <h1>
        <a href="<c:url value="/main_page"/>" ><fmt:message key="header.sitename"/></a>
        </h1>
        </header>

        <t:insertAttribute name="header"/>
        <t:insertAttribute name="body"/>
        <t:insertAttribute name="footer"/>

        </body>
        </html>