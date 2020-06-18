<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>On-line pharmacy. Medicine items</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="images/pharmacy_small.gif" rel="icon" type="image/gif"/>
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
        <%@include file="_medicine_list_header.jsp" %>

        <c:forEach var="medicine" items="${medicineList}">
            <tr>
                <td><c:out value="${medicine.name}"/></td>
                <td><c:out value="${medicine.dosage}"/></td>
                <td><c:out value="${medicine.exp_date}"/></td>
                <td>
                    <c:if test="${medicine.recipe_required == true}"> Да </c:if>
                    <c:if test="${medicine.recipe_required == false}"> Нет </c:if>
                </td>
                <td><c:out value="${medicine.pharm_form}"/></td>
                <td><c:out value="${medicine.indivisible_amount}"/></td>
                <td><c:out value="${medicine.rub}"/> руб. <c:out value="${medicine.coin}"/>коп.</td>
                <form id="addIntoCart" class="button_line" method="post"
                      action="${pageContext.request.contextPath}/customer/medicine/list">
                    <td>
                        <input name="amountForBuy" class="table_field_number" type="number" size="3"
                               value="1" min="1" max="${medicine.amount}"
                               title="Доступное количество ${medicine.amount} уп.">
                    </td>
                    <td align="center">
                        <c:if test="${medicine.customerNeedRecipe == false}">
                            <input type="submit" value="To cart" height="8">
                            <input type="hidden" name="medicine_id" value="${medicine.id}">
                            <input type="hidden" name="customerCommand" value="medicineForCart">
                        </c:if>
                        <c:if test="${medicine.customerNeedRecipe == true}">
                            <input type="submit" value=" Order recipe ">
                            <input type="hidden" name="customerCommand" value="requestRecipe">
                            <input type="hidden" name="medicine" value="${medicine.name}">
                            <input type="hidden" name="dosage" value="${medicine.dosage}">
                        </c:if>
                    </td>

                </form>

                    <%-- For realize in future --%>
                    <%--                <td>
                                        <form class="button_line" action="${pageContext.request.contextPath}/customer/medicine/list"
                                              method="post">
                                            <input type="submit" value="To favorite" height="8">
                                            <input type="hidden" name="customerCommand" value="medicineInrFavorite">
                                            <input type="hidden" name="medicine_id" value="${medicine.id}">
                                        </form>
                                    </td>--%>
                    <%--</div>--%>

            </tr>
        </c:forEach>
    </table>
</div>
<%--
<div id="right_customer">
    <c:import url="../_right_cart.jsp"/>
</div>--%>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
