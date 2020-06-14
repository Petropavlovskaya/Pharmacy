<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>On-line pharmacy. Favourite</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="images/Pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>

<div id="logo">
    <c:import url="../../_header.jsp"/>
</div>
<div id="line"></div>
<div id="menu">
    <c:import url="../_customer_menu.jsp"/>
</div>
<div id="center_customer">
    <table>
        <%@include file="_medicine_list_header.jsp"%>

        <c:forEach var="medicine" items="${medicineCartList}">
            <tr>
                    <%--            <td><c:out value="${medItem.id}"/></td>--%>
                <td><c:out value="${medicine.name}"/></td>
                <td><c:out value="${medicine.indivisible_amount}"/></td>
                <td><c:out value="${medicine.amount}"/></td>
                <td><c:out value="${medicine.dosage}"/></td>
                <td><c:out value="${medicine.exp_date}"/></td>
                <td>
                    <c:if test="${medicine.recipe_required == true}"> Да </c:if>
                    <c:if test="${medicine.recipe_required == false}"> Нет </c:if>
                </td>
                <td><c:out value="${medicine.price/100}"/></td>
                <td><c:out value="${medicine.pharm_form}"/></td>
                <td><input name="amountForBuy" class="table_field_high" type="text" form="addIntoCart" size="7"
                           pattern="\d{1,3}" min="1" max="100" title="Количество от 1 до 100"></td>
                <td>
                    <form id="addIntoCart" class="button_line"
                          action="${pageContext.request.contextPath}/pharmacist/medicine/favourite"
                          method="post">
                        <input type="submit" value="To cart">
                        <input type="hidden" name="customerCommand" value="medicineForCart">
                        <input type="hidden" name="medicine_id" value="${medicine.id}">
                    </form>
                    <br>
                    <form class="button_line" action="${pageContext.request.contextPath}/pharmacist/medicine/favourite"
                          method="post">
                        <input type="submit" value="Out of favorite">
                        <input type="hidden" name="customerCommand" value="medicineOutOfFavorite">
                        <input type="hidden" name="medicine_id" value="${medicine.id}">
                    </form>
                </td>
                    <%--</div>--%>

            </tr>
        </c:forEach>
    </table>
</div>
<div id="right_customer">
    <c:import url="../_right_cart.jsp"/>
</div>
<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>
</body>
</html>
