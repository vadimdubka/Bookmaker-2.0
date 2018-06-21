<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<main class="row container">
    <section class="section-center col-s-10">
        <div class="section-header"><h2><fmt:message key="header.make.bet"/></h2></div>
        <div class="section-content">
            <table class="events">
                <tr>
                    <th><fmt:message key="event.category"/></th>
                    <th><fmt:message key="event.id"/></th>
                    <th><fmt:message key="event.date"/></th>
                    <th><fmt:message key="event.name"/></th>
                    <th><fmt:message key="event.outcome.type"/></th>
                    <th><fmt:message key="event.outcome.coefficient"/></th>
                </tr>
                <tr>
                    <td>${requestScope.sportCategory.name} - ${requestScope.category.name}</td>
                    <td>${requestScope.event.id}</td>
                    <jsp:useBean id="event" scope="request" class="com.dubatovka.app.entity.Event"/>
                    <td>${j:formatDateTime(event.date, "dd.MM.yyyy HH:mm")}</td>
                    <td>${requestScope.event.participant1} - ${requestScope.event.participant2}</td>
                    <td>${requestScope.outcome.type.type}</td>
                    <td>${requestScope.outcome.coefficient}</td>
                </tr>
            </table>
            <div class="div-align-center">
                <form class="make-bet-form" action="controller" method="post">
                    <input type="hidden" name="command_type" value="make_bet"/>
                    <input type="hidden" name="event_id" value="${requestScope.event.id}"/>
                    <input type="hidden" name="outcome_type" value="${requestScope.outcome.type.type}"/>
                    <input type="hidden" name="outcome_coefficient" value="${requestScope.outcome.coefficient}"/>
                    <input type="number" name="bet_amount" value=""
                           title="<fmt:message key="input.title.set.bet.amount"/>"
                           min="0.01" max="999.99" step="0.01" required/><br>
                    <input type="submit" value="<fmt:message key="input.value.make.bet"/>"/>
                </form>
            </div>
        </div>
    </section>
</main>