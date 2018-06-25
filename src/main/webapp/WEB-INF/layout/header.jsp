    <%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" session="true" language="java" %>

        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <%@ taglib prefix="j" uri="http://bookmaker.com/functions" %>
        <fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'default'}"/>
        <fmt:setBundle basename="textcontent.pagecontent"/>

        <!DOCTYPE html>
        <html lang="<fmt:message key="header.html.lang"/>">
        <head>
        <title>Bookmaker Online</title>
        <meta charset="UTF-8">
        <meta name="description" content="<fmt:message key="header.head.description"/>">
        <meta name="keywords" content="<fmt:message key="header.head.keywords"/>">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" type="image/png" href="<s:url value="/img/favicon.png"/>" >
        <link rel="stylesheet" type="text/css" href="<s:url value="/css/style.css"/>" >

        </head>
        <body>

        <header>
        <h1>
        <a href="<s:url value="/controller?command_type=goto_index"/>" ><fmt:message
            key="header.sitename"/></a>
        </h1>
        </header>

        <div class="lang-block border-test">
        <a class="rus" href="<c:url value="/change_locale?locale=ru_RU"/>" title="RU">rus</a>
        <a class="eng" href="<c:url value="/change_locale?locale=en_US"/>" title="EN">eng</a>
        </div>

        <nav>
        <ul class="nav-horizontal">
        <li><a href="<c:url value="/controller?command_type=goto_event_show_actual"/>" >
        <fmt:message key="header.topmenu.main"/></a></li>
        <li><a href="<c:url value="/controller?command_type=goto_event_show_result"/>" >
        <fmt:message key="header.topmenu.results"/></a></li>
        <li><a href="<c:url value="/controller?command_type=goto_rules"/>" >
        <fmt:message key="header.topmenu.rules"/></a></li>
        <li><a href="<c:url value="/controller?command_type=goto_support"/>" >
        <fmt:message key="header.topmenu.support"/></a></li>
        <li><a href="<c:url value="/controller?command_type=goto_actions"/>" >
        <fmt:message key="header.topmenu.actions"/></a></li>
        <li><a href="<c:url value="/controller?command_type=goto_news"/>" >
        <fmt:message key="header.topmenu.news"/></a></li>
        <li><a href="<c:url value="/controller?command_type=goto_aboutus"/>" >
        <fmt:message key="header.topmenu.aboutus"/></a></li>
        </ul>
        </nav>