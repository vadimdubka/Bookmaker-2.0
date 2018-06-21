<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<main class="row container">
    <section class="section-center col-s-10">
        <div class="section-header"><h2><fmt:message key="header.manage.players"/></h2></div>
        <div class="section-content">
            <table class="events">
                <tr>
                    <th><fmt:message key="player.lname"/></th>
                    <th><fmt:message key="player.name"/></th>
                    <th><fmt:message key="player.mname"/></th>
                    <th><fmt:message key="player.birthdate"/></th>
                    <th><fmt:message key="balance"/></th>
                    <th><fmt:message key="status"/></th>
                    <th><fmt:message key="player.bet.limit"/></th>
                    <th><fmt:message key="player.withdrawal.limit"/></th>
                    <th><fmt:message key="player.this.month.withdrawal"/></th>
                </tr>
                <c:forEach var="player" items="${requestScope.players}">
                    <tr>
                        <td>${player.profile.lastName}</td>
                        <td>${player.profile.firstName}</td>
                        <td>${player.profile.middleName}</td>
                        <td>${player.profile.birthDate}</td>
                        <td>${player.account.balance}</td>
                        <td>${player.account.status.status}</td>
                        <td>${player.account.status.betLimit}</td>
                        <td>${player.account.status.withdrawalLimit}</td>
                        <td>${player.account.thisMonthWithdrawal}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </section>
</main>