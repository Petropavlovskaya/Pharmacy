<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Medicine items</title>
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
    <p class="p-red">${requestScope.get('message')}</p>
    <table width="100%">
        <%@include file="_medicine_list_header.jsp" %>

        <c:forEach var="medicine" items="${sessionScope.get('medicineList')}">
            <tr>
                <td><c:out value="${medicine.name}"/></td>
                <td><c:out value="${medicine.dosage}"/></td>
                <td><c:out value="${medicine.expDate}"/></td>
                <td align="center">
                    <c:if test="${medicine.recipeRequired == true}"> <fmt:message key="label.yes"/> </c:if>
                    <c:if test="${medicine.recipeRequired == false}"> <fmt:message key="label.no"/> </c:if>
                </td>
                <td><c:out value="${medicine.pharmForm}"/></td>
                <td><c:out value="${medicine.indivisibleAmount}"/></td>
                <td><c:out value="${medicine.rub}"/> <fmt:message key="label.rub"/> <c:out value="${medicine.coin}"/>
                    <fmt:message key="label.kop"/></td>
                <form id="addIntoCart" class="button_line" method="post"
                      action="${pageContext.request.contextPath}/customer/medicine/list">
                    <td>
                        <input name="amountForBuy" class="table_field_number" type="number" size="3"
                               value="1" min="1" max="${medicine.amount}"
                               title="<fmt:message key="label.medicine.amountForBuyTitle1"/> ${medicine.amount} <fmt:message key="label.medicine.amountForBuyTitle2"/>">
                    </td>
                    <td align="center">
                        <c:if test="${medicine.customerNeedRecipe == false}">
                            <input type="submit" value=
                                <fmt:message key="label.medicine.buttonToCart"/> height="8">
                            <input type="hidden" name="medicineId" value="${medicine.id}">
                            <input type="hidden" name="customerCommand" value="medicineForCart">
                        </c:if>
                        <c:if test="${medicine.customerNeedRecipe == true}">
                            <input type="submit" value=<fmt:message key="label.medicine.buttonOrderRecipe"/>>
                            <input type="hidden" name="customerCommand" value="requestRecipe">
                            <input type="hidden" name="medicine" value="${medicine.name}">
                            <input type="hidden" name="dosage" value="${medicine.dosage}">
                        </c:if>
                    </td>

                </form>
            </tr>
        </c:forEach>
    </table>
    <br>

    <c:import url="../../_accountPageNavigation.jsp"/>
    <c:import url="../../_accountRecordsPerPage.jsp"/>


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
