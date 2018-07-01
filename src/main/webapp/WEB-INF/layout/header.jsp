    <%@ page contentType="text/html;charset=UTF-8" %>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
        <fmt:setLocale value="${sessionScope.locale != null ? sessionScope.locale : 'default'}"/>
        <fmt:setBundle basename="textcontent.pagecontent"/>

        <div class="lang-block border-test">
        <a class="rus" href="<c:url value="/change_locale?locale=ru_RU"/>" title="RU">rus</a>
        <a class="eng" href="<c:url value="/change_locale?locale=en_US"/>" title="EN">eng</a>
        </div>

        <nav>
        <ul class="nav-horizontal">
        <li><a href="<c:url value="/event_show_actual"/>" >
        <fmt:message key="header.topmenu.main"/></a></li>
        <li><a href="<c:url value="/event_show_result"/>" >
        <fmt:message key="header.topmenu.results"/></a></li>
        <li><a href="<c:url value="/main_page"/>" >
        <fmt:message key="header.topmenu.rules"/></a></li>
        <li><a href="<c:url value="/main_page"/>" >
        <fmt:message key="header.topmenu.support"/></a></li>
        <li><a href="<c:url value="/main_page"/>" >
        <fmt:message key="header.topmenu.actions"/></a></li>
        <li><a href="<c:url value="/main_page"/>" >
        <fmt:message key="header.topmenu.news"/></a></li>
        <li><a href="<c:url value="/main_page"/>" >
        <fmt:message key="header.topmenu.aboutus"/></a></li>
        </ul>
        </nav>