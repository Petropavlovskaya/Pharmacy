<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Add new item</title>
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
    <c:import url="../_pharmacist_menu.jsp"/>
</div>
<div id="center_no_right">
    <c:if test="${not empty message}">
        <p class="p-red">${message}<br></p>
    </c:if>

    <table>
        <%@include file="_pharmacist_medicine_table_header.jsp" %>

        <form action="${pageContext.request.contextPath}/pharmacist/medicine/create" method="post">
            <tr class="insert_row">
                <td><textarea name="medicineName" class="table_field_high" required
                              required pattern="[А-ЯЁ]{1}[а-яё\s-){1,19}"></textarea></td>
                <td><input name="indivisibleAmount" class="table_field_number" type="number" size="6"
                           required pattern="\d{1,3}" min="1" title=<fmt:message key="label.medicine.create.indivisibleTitle"/>></td>
                <td><input name="amount" class="table_field_number" type="number"
                           required pattern="\d{1,4}" min="1" title=<fmt:message key="label.medicine.create.amountTitle"/>></td>
                <td><input name="dosage" class="table_field_number" type="text" required></td>
                <td><input name="expDate" class="table_field_high" type="date" size="9" placeholder=<fmt:message key="label.medicine.create.datePlaceholder"/>
                        min="${sessionScope.get('minDate')}" required></td>
                <td align="center"><input name="recipeRequired" class="table_field_high" type="checkbox"></td>
                <td><input name="priceRub" type="number" class="table_field_money"
                           required pattern="\d{0,3}" min="0" align="right" title=<fmt:message key="label.medicine.create.priceRub"/>> руб.<br>
                    <input name="priceKop" type="number" class="table_field_money"
                           required pattern="\d{0,2}" min="0" align="right" title=<fmt:message key="label.medicine.create.priceKop"/>>
                    коп.
                </td>
                <td><textarea name="pharmForm" class="table_field_high" type="text" size="5" required></textarea></td>
                <td align="center"><input type="submit" value=<fmt:message key="label.medicine.create.actionCreate"/>></td>
                <td align="center"><input name="clear" type="reset" value=<fmt:message key="label.medicine.create.actionClear"/>></td>
                <input type="hidden" name="medicineCommand" value="create">
                <input type="hidden" name="accountId" value="${accountId}">

            </tr>
            <br><br>
        </form>

    </table>
</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
