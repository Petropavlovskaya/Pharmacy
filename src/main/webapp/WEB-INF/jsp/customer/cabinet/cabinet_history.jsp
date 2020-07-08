<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. History</title>
    <style>
        <%@include file="/css/style.css" %>
        <%@include file="/css/hiddenDiv.css" %>
    </style>
    <link href="${pageContext.request.contextPath}/images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>

<body>

<div id="logo">
    <c:import url="../../_header.jsp"/>
</div>
<div id="line"></div>
<div id="menu">
    <c:import url="../_customer_menu.jsp"/>
</div>


<div id="center_no_right">
    <div class="demo">
        <%--    <c:set var="order" value="${orders}"/>--%>
        <%--    <c:set var="customer" value="${customer}"/>--%>
        <c:if test="${empty orders}">
            <p3><fmt:message key="label.account.cabinet.noOrdersMessage"/></p3>
        </c:if>
        <c:if test="${!empty orders}">
            <%--        <c:set var=""--%>
            <c:forEach var="order" items="${orders}">
                <%--                Key = ${order.key}, value = ${order.value}--%>

                <input class="hide" id="${order.key.id}" type="checkbox">
                <label for="${order.key.id}"><fmt:message key="label.account.cabinet.orderDate"/>:
                    <fmt:formatDate pattern="yyyy-MM-dd" value="${order.key.orderDate}"/>.
                    <fmt:message key="label.account.cabinet.orderPrice"/>: <c:out value="${order.key.rub}"/>
                    <fmt:message key="label.rub"/>
                    <c:out value="${order.key.coin}"/> <fmt:message key="label.kop"/> </label>

                <div class="text">
                    <c:forEach var="orderItem" items="${order.value}">
                        <fmt:message key="label.tableHeader.medicineName"/>: <c:out value="${orderItem.medicine}"/>
                        (<fmt:message key="label.tableHeader.dosageInRecipe"/>: <c:out value="${orderItem.dosage}"/>,
                        <fmt:message key="label.medicine.amountInPack"/>: <c:out value="${orderItem.indivisibleAmount}"/>
                        <fmt:message key="label.psc"/>)<br>
                        <fmt:message key="label.account.cabinet.medItemBought"/>: <c:out value="${orderItem.quantity}"/>
                        <fmt:message key="label.account.cabinet.medItemPrice"/> <c:out value="${orderItem.rubForOne}"/>
                        <fmt:message key="label.rub"/> <c:out value="${orderItem.coinForOne}"/> <fmt:message key="label.kop"/> <br>
                        <fmt:message key="label.account.cabinet.medTotalPrice"/>: <c:out value="${orderItem.rubForQuantity}"/>
                        <fmt:message key="label.rub"/>
                        <c:out value="${orderItem.coinForQuantity}"/> <fmt:message key="label.kop"/> <br>
                        <br>

                    </c:forEach>
                </div>
                <br><br>
            </c:forEach>
        </c:if>
    </div>
</div>


<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
