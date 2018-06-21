<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<main class="row container">
    <section class="section-center col-s-7">
        <div class="section-header"><h2><fmt:message key="header.section.player.state"/></h2></div>
        <div class="section-content">
            <p><b>${sessionScope.user.role} ${sessionScope.user.email}</b></p>
            <div class="col-s-8 col-4">
                <table class="user-menu">
                    <tr>
                        <td class="name"><fmt:message key="player.status"/></td>
                        <td class="info">${sessionScope.player.account.status.status.status}</td>
                    </tr>
                    <tr>
                        <td class="name"><fmt:message key="bet.limit"/></td>
                        <td class="info">${sessionScope.player.account.status.betLimit}</td>
                    </tr>
                    <tr>
                        <td class="name"><fmt:message key="player.balance"/></td>
                        <td class="info">${sessionScope.player.account.balance}</td>
                    </tr>
                </table>
            </div>

            <table class="bets user-state">
                <tr>
                    <th colspan="5"><fmt:message key="bet"/></th>
                    <th colspan="3"><fmt:message key="event"/></th>
                </tr>
                <tr>
                    <th><fmt:message key="bet.status"/></th>
                    <th><fmt:message key="bet.date"/></th>
                    <th><fmt:message key="bet.amount"/></th>
                    <th><fmt:message key="bet.coefficient"/></th>
                    <th><fmt:message key="bet.outcome.type"/></th>
                    <th><fmt:message key="event.result"/></th>
                    <th><fmt:message key="event.date"/></th>
                    <th>№</th>
                </tr>
                <c:forEach var="bet" items="${requestScope.bet_list}">
                    <c:set var="status" value="${bet.status.status}"/>
                    <tr>
                        <c:choose>
                            <c:when test="${status == 'win'}">
                                <td class="bet-status bet-status-win" rowspan="3">${bet.status.status}</td>
                            </c:when>
                            <c:when test="${status == 'losing'}">
                                <td class="bet-status bet-status-losing" rowspan="3">${bet.status.status}</td>
                            </c:when>
                            <c:when test="${status == 'paid'}">
                                <td class="bet-status bet-status-paid" rowspan="3">${bet.status.status}</td>
                            </c:when>
                            <c:otherwise>
                                <td class="bet-status" rowspan="3">${bet.status.status}</td>
                            </c:otherwise>
                        </c:choose>
                        <td rowspan="3">${j:formatDateTime(bet.date, "yyyy.MM.dd HH:mm")}</td>
                        <td rowspan="3">${bet.amount}</td>
                        <td rowspan="3">${bet.coefficient}</td>
                        <td rowspan="3">${bet.outcomeType}</td>
                        <td colspan="3">${requestScope.sport_map[bet].name}
                            - ${requestScope.category_map[bet].name}</td>
                    </tr>
                    <tr>
                        <td colspan="3">${requestScope.event_map[bet].participant1}
                            - ${requestScope.event_map[bet].participant2}</td>
                    </tr>
                    <tr>
                        <td>${requestScope.event_map[bet].result1}
                            - ${requestScope.event_map[bet].result2}</td>
                        <c:set var="date" value="${requestScope.event_map[bet].date}"/>
                        <td>${j:formatDateTimeFromString(date, "MM.dd.yyyy HH:mm")}</td>
                        <td>${requestScope.event_map[bet].id}</td>
                    </tr>
                </c:forEach>
            </table>
            <c:if test="${requestScope.pagination.amountOfPages>0}">
                <div align="center">
                    <c:forEach var="i" begin="1" end="${requestScope.pagination.amountOfPages}">
                        <c:choose>
                            <c:when test="${i!=requestScope.pagination.currentPage}">
                                <a href="controller?command_type=goto_player_state&page_number=<c:out value="${i}"/>">
                                    <c:out value="${i}"/>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <c:out value="${i}"/>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </div>
            </c:if>
        </div>
    </section>
</main>