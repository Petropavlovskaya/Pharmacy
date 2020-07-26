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
    <c:import url="../_pharmacist_menu.jsp"/>
</div>
<div id="center_no_right">
    <p class="p-error">${requestScope.get('errorMessage')}</p>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.5.0/jquery.min.js"></script>
    <c:import url="../../_toastr.jsp"/>
    <c:if test="${not empty sessionScope.get('successMessage')}" >
        <c:import url="../../_toastrFuncSuccess.jsp"/>
    </c:if>

    <table width="100%">
        <%@include file="_pharmacist_medicine_table_header.jsp" %>

        <form action="${pageContext.request.contextPath}/pharmacist/medicine/create" method="post">
            <tr class="insert_row">
                <td><textarea name="medicineName" class="table_field_high"
                              required pattern="(([A-ZА-Я][a-zа-я]{1,10})[-'\\s]*)+?(([a-zа-я]{1,10})[-'\\s]*)+?">${requestScope.get('medicineName')}</textarea></td>
                <td><input name="indivisibleAmount" class="table_field_number" type="number" size="6" value="${requestScope.get('indivisibleAmount')}"
                           required pattern="\d{1,3}" min="1" title=<fmt:message key="label.medicine.create.indivisibleTitle"/>></td>
                <td><input name="amount" class="table_field_number" type="number" value="${requestScope.get('amount')}"
                           required pattern="\d{1,4}" min="1" title=<fmt:message key="label.medicine.create.amountTitle"/>></td>
                <td><input name="dosage" class="table_field_number" type="text" required value="${requestScope.get('dosage')}"></td>
                <td><input name="expDate" class="table_field_high" type="date" size="9" placeholder=<fmt:message key="label.medicine.create.datePlaceholder"/>
                        min="${sessionScope.get('minDate')}" required value="${requestScope.get('expDate')}"></td>
                <td align="center"><input name="recipeRequired" class="table_field_high" type="checkbox"
                                          <c:if test="${requestScope.get('recipeRequired') == true}">checked</c:if> ></td>
                <td><input name="priceRub" type="number" class="table_field_money" value="${requestScope.get('priceRub')}"
                           required pattern="\d{0,3}" min="0" align="right" title=<fmt:message key="label.medicine.create.priceRub"/>> руб.<br>
                    <input name="priceKop" type="number" class="table_field_money" value="${requestScope.get('priceKop')}"
                           required pattern="\d{0,2}" min="0" align="right" title=<fmt:message key="label.medicine.create.priceKop"/>>
                    коп.
                </td>
                <td><textarea name="pharmForm" class="table_field_high" type="text" size="5" required>${requestScope.get('pharmForm')}</textarea></td>
                <td align="center"><input type="submit" value=<fmt:message key="label.medicine.create.actionCreate"/>></td>
                <td align="center"><input name="clear" type="reset" value=<fmt:message key="label.medicine.create.actionClear"/>></td>
                <input type="hidden" name="frontCommand" value="create">
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
