<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Cart</title>
    <style>
        <%@include file="/css/style.css" %>
        <%@include file="/toastr/toastr.css" %>
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
    <p class="p-error">${requestScope.get('errorMessage')}</p>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.5.0/jquery.min.js"></script>
    <c:import url="../../_toastr.jsp"/>
    <c:if test="${not empty sessionScope.get('successMessage')}" >
        <c:import url="../../_toastrFuncSuccess.jsp"/>
    </c:if>

    <table width="100%">
        <%@include file="_cart_table_header.jsp" %>

        <c:set var="cart" value="${sessionScope.get('cart')}"/>
        <c:set var="customer" value="${sessionScope.get('customer')}"/>

        <c:forEach var="medicine" items="${sessionScope.get('medicineInCart')}">
            <c:set var="price" value="${medicine.priceForOne}"/>
            <c:set var="quantity" value="${medicine.quantity}"/>

            <tr>

                <td><c:out value="${medicine.medicine}"/></td>
                    <%-- Name --%>
                <td><c:out value="${medicine.dosage}"/></td>
                    <%-- Dosage --%>
                <td><c:out value="${medicine.indivisibleAmount}"/></td>
                    <%-- Indivisible_amount --%>
                <form id="changeInCart" action="${pageContext.request.contextPath}/customer/cabinet/cart"
                      method="post">
                    <td align="center">                                                 <%-- Quantity --%>
                        <c:if test="${price == 0}">
                            <input class="table_field_high" type="text" size="5" value="0" disabled
                                   title=<fmt:message key="label.account.cabinet.medInvalidForBuy"/>>
                        </c:if>
                        <c:if test="${price >0}">
                            <input name="amountForBuy" class="table_field_high" type="number" size="5"
                                   value="${medicine.quantity}" pattern="\d{1,3}" min="1" max="${medicine.amount}"
                                   title="<fmt:message key="label.medicine.amountForBuyTitle1"/> ${medicine.amount} <fmt:message key="label.medicine.amountForBuyTitle2"/>">

                        </c:if>
                    </td>
                    <td align="center">                                                 <%-- Price for one --%>
                        <c:if test="${price == 0}"> 0 <fmt:message key="label.rub"/> 0 <fmt:message key="label.kop"/></c:if>
                        <c:if test="${price > 0}"> ${medicine.rubForOne} <fmt:message key="label.rub"/>
                            ${medicine.coinForOne} <fmt:message key="label.kop"/></c:if>
                    </td>
                    <td>                                                                <%-- Price for quantity --%>
                            ${medicine.rubForQuantity} <fmt:message key="label.rub"/> ${medicine.coinForQuantity} <fmt:message key="label.kop"/>
                    </td>
                    <td align="center">                                                                <%-- Action --%>
                        <c:if test="${price == 0}">
                            <input type="submit" value=<fmt:message key="label.medicine.create.actionChange"/> disabled>
                        </c:if>
                        <c:if test="${price >0}">
                            <input type="submit" value=<fmt:message key="label.medicine.create.actionChange"/>>
                            <input type="hidden" name="frontCommand" value="changeQuantityInCart">
                            <input type="hidden" name="medicineId" value="${medicine.id}">
                        </c:if>
                    </td>
                </form>
                <td align="center">
                    <form action="${pageContext.request.contextPath}/customer/cabinet/cart" method="post">
                        <input type="submit" value=<fmt:message key="label.medicine.create.actionDelete"/>>
                        <input type="hidden" name="frontCommand" value="deleteFromCart">
                        <input type="hidden" name="medicineId" value="${medicine.id}">
                    </form>

                </td>

            </tr>
        </c:forEach>
        <tr class="total_row">
            <form id="buy" action="${pageContext.request.contextPath}/customer/cabinet/cart" method="post">
                <td colspan="3" align="right">
                    <c:choose>
                        <c:when test="${customer.balance >= 0}">
                            <p2><fmt:message key="label.account.balanceMessage"/>: ${customer.balanceRub} <fmt:message key="label.rub"/>
                                    ${customer.balanceCoin} <fmt:message key="label.kop"/></p2>
                        </c:when>
                        <c:otherwise>
                            <p2><fmt:message key="label.account.balanceMessage"/>: - ${customer.balanceRub*(-1)} <fmt:message key="label.rub"/>
                                    ${customer.balanceCoin*(-1)} <fmt:message key="label.kop"/>
                            </p2>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td colspan="3" align="right">
                    <p2><fmt:message key="label.account.cabinet.orderPrice"/>: ${cart.rub} <fmt:message key="label.rub"/>
                        ${cart.coin} <fmt:message key="label.kop"/></p2>
                </td>
                <td colspan="2" align="center">
                    <c:choose>
                        <c:when test="${customer.balance >= cart.orderPrice}">
                            <input type="submit" value=<fmt:message key="label.account.buttonBuy"/>>
                            <input type="hidden" name="frontCommand" value="buy">
                        </c:when>
                        <c:when test="${customer.balance >= 0}">
                            <input type="submit" value=<fmt:message key="label.account.buttonBuyInCredit"/>>
                            <input type="hidden" name="frontCommand" value="buyInCredit">
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="label.account.balanceDebtInfo"/>.
                        </c:otherwise>
                    </c:choose>
                </td>
            </form>
        </tr>
    </table>
    <br>


</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>
</body>
</html>
