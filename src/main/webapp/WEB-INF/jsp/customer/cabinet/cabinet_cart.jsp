<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>On-line pharmacy. Cart</title>
    <style>
        <%@include file="/css/style.css" %>
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
    <table>
        <%@include file="_cart_table_header.jsp" %>

        <c:set var="cart" value="${cart}"/>
        <c:set var="customer" value="${customer}"/>

        <c:forEach var="medicine" items="${medicineInCart}">
            <c:set var="price" value="${medicine.priceForOne}"/>
            <c:set var="quantity" value="${medicine.quantity}"/>

            <tr class="insert_row">

                <td><c:out value="${medicine.medicine}"/></td>
                    <%-- Name --%>
                <td><c:out value="${medicine.dosage}"/></td>
                    <%-- Dosage --%>
                <td><c:out value="${medicine.indivisible_amount}"/></td>
                    <%-- Indivisible_amount --%>
                <form id="changeInCart" action="${pageContext.request.contextPath}/customer/cabinet/cart"
                      method="post">
                    <td align="center">                                                 <%-- Quantity --%>
                        <c:if test="${price == 0}">
                            <input class="table_field_high" type="text" size="5" value="0" disabled
                                   title="Данный товар не существует либо у Вас нет рецепта для его приобретения">
                        </c:if>
                        <c:if test="${price >0}">
                            <input form="changeInCart" name="amountForBuy" class="table_field_high" type="number"
                                   size="5"
                                   value="${quantity}" pattern="\d{1,3}" min="1" max="${medicine.amount}"
                                   title="Доступное количество ${medicine.amount} уп.">
                        </c:if>
                    </td>
                    <td align="center">                                                 <%-- Price for one --%>
                        <c:if test="${price == 0}"> 0 руб. 0 коп.</c:if>
                        <c:if test="${price > 0}"> ${medicine.rubForOne} руб. ${medicine.coinForOne} коп.</c:if>
                    </td>
                    <td>                                                                <%-- Price for quantity --%>
                            ${medicine.rubForQuantity} руб. ${medicine.coinForQuantity} коп.
                    </td>
                    <td align="center">                                                                <%-- Action --%>
                        <c:if test="${price == 0}">
                            <input type="submit" value="Change" disabled>
                        </c:if>
                        <c:if test="${price >0}">
                            <input type="submit" value=" Change ">
                            <input type="hidden" name="customerCommand" value="changeQuantityInCart">
                            <input type="hidden" name="medicine_id" value="${medicine.id}">
                        </c:if>
                    </td>
                </form>
                <td align="center">
                    <form action="${pageContext.request.contextPath}/customer/cabinet/cart" method="post">
                        <input type="submit" value=" Delete ">
                        <input type="hidden" name="customerCommand" value="deleteFromCart">
                        <input type="hidden" name="medicine_id" value="${medicine.id}">
                    </form>

                </td>

            </tr>
        </c:forEach>
        <tr class="total_row">
            <form id="buy" action="${pageContext.request.contextPath}/customer/cabinet/cart" method="post">
                <td colspan="3" align="right">
                    <c:choose>
                        <c:when test="${customer.balance >= 0}">
                            <p2>Ваш баланс составляет: ${customer.balanceRub} руб. ${customer.balanceCoin} коп.</p2>
                        </c:when>
                        <c:otherwise>
                            <p2>Ваш баланс составляет: - ${customer.balanceRub*(-1)} руб. ${customer.balanceCoin*(-1)}
                                коп.
                            </p2>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td colspan="3" align="right">
                    <p2>Общая сумма заказа: ${cart.rub} руб. ${cart.coin} коп.</p2>
                </td>
                <td colspan="2" align="center">
                    <c:choose>
                        <c:when test="${customer.balance >= cart.order_price}">
                            <input type="submit" value=" Buy ">
                            <input type="hidden" name="customerCommand" value="buy">
                        </c:when>
                        <c:when test="${customer.balance >= 0}">
                            <input type="submit" value=" Buy in credit ">
                            <input type="hidden" name="customerCommand" value="buyInCredit">
                        </c:when>
                        <c:otherwise>
                            You have to pay off<br>the dept.
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
