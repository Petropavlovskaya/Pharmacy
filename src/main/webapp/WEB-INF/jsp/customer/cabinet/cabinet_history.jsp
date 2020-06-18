<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
            <p3> You have not completed orders</p3>
        </c:if>
        <c:if test="${!empty orders}">
            <%--        <c:set var=""--%>
            <c:forEach var="order" items="${orders}">
                <%--                Key = ${order.key}, value = ${order.value}--%>

                <input class="hide" id="${order.key.id}" type="checkbox">
                <label for="${order.key.id}">Дата заказа: <fmt:formatDate pattern="yyyy-MM-dd" value="${order.key.order_date}"/>.
                    Сумма: <c:out value="${order.key.rub}"/> руб. <c:out value="${order.key.coin}"/> коп. </label>

                <div class="text">
                    <c:forEach var="orderItem" items="${order.value}">
                        Лекарство: <c:out value="${orderItem.medicine}"/> (дозировка: <c:out
                            value="${orderItem.dosage}"/>,
                        в упаковке: <c:out value="${orderItem.indivisible_amount}"/> шт.)<br>
                        Приобретено: <c:out value="${orderItem.quantity}"/> ед.
                            по цене <c:out value="${orderItem.rubForOne}"/> руб. <c:out value="${orderItem.coinForOne}"/> коп. <br>
                        Стоимость: <c:out value="${orderItem.rubForQuantity}"/> руб.
                        <c:out value="${orderItem.coinForQuantity}"/> коп. <br>
                        <br>

                    </c:forEach>
                </div><br><br>
            </c:forEach>
        </c:if>
    </div>
</div>


<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
