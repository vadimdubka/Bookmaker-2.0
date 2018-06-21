<%@ page contentType="text/html;charset=UTF-8" %>
<main class="row container">

    <c:choose>
        <c:when test="${sessionScope.user == null}">
            <%@include file="jspf/user_login.jspf" %>
        </c:when>
        <c:otherwise>
            <%@include file="jspf/user_menu.jspf" %>
        </c:otherwise>
    </c:choose>

    <%@include file="jspf/sport_category.jspf" %>

    <c:set var="event_goto_type" value="${sessionScope.event_command_type}" scope="page"/>
    <c:choose>
        <c:when test="${requestScope.event_set!=null && event_goto_type!=null}">
            <c:choose>
                <c:when test="${event_goto_type == 'show_actual'}">
                    <%@include file="jspf/events/events_actual.jspf" %>
                </c:when>
                <c:when test="${event_goto_type == 'show_result'}">
                    <%@include file="jspf/events/events_results.jspf" %>
                </c:when>
                <c:when test="${event_goto_type == 'manage_event'}">
                    <%@include file="jspf/events/events_manage_event.jspf" %>
                </c:when>
                <c:when test="${event_goto_type == 'manage_outcome'}">
                    <%@include file="jspf/events/events_manage_outcome.jspf" %>
                </c:when>
                <c:when test="${event_goto_type == 'manage_result'}">
                    <%@include file="jspf/events/events_manage_result.jspf" %>
                </c:when>
                <c:when test="${event_goto_type == 'manage_failed'}">
                    <%@include file="jspf/events/events_failed.jspf" %>
                </c:when>
                <c:when test="${event_goto_type == 'show_to_pay'}">
                    <%@include file="jspf/events/events_to_pay.jspf" %>
                </c:when>
                <c:otherwise>
                    <%@include file="jspf/events/events_actual.jspf" %>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <section class="section-promo col-s-12 col-6 col-float-right">
                <div class="section-header"><h2><fmt:message key="header.section.promo"/></h2></div>
                <img class="img-choose-sport"
                     src="${pageContext.request.contextPath}/resources/img/choose-sport.jpg"
                     alt="Choose-sport-logo"
                     title="Choose your sport">
            </section>
        </c:otherwise>
    </c:choose>
</main>