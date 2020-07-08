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
    <c:import url="../_pharmacist_menu.jsp"/>
</div>
<div id="center_no_right">
    <c:if test="${!empty sessionScope.get('message')}">
        <p class="p-red">${sessionScope.get('message')}</p><br>
    </c:if>

    <table>
        <%@include file="_pharmacist_medicine_table_header.jsp" %>


        <%--        Next form view if item was selected to edit--%>
        <c:if test="${!empty requestScope.get('editMedicine')}">

            <form action="${pageContext.request.contextPath}/pharmacist/medicine/list" method="post">
                <tr class="insert_row">
                    <td><textarea name="medicineName" class="table_field_high"
                                  required pattern="[А-ЯЁ]{1}[а-яё\s-){1,19}"> ${editMedicine.name}</textarea></td>
                    <td><input name="indivisibleAmount" class="table_field_number" type="number"
                               value="${editMedicine.indivisibleAmount}"
                               required pattern="\d{1,3}" min="1" title=<fmt:message key="label.medicine.create.indivisibleTitle"/>></td>
                    <td><input name="amount" class="table_field_number" type="number" value="${editMedicine.amount}"
                               required pattern="\d{1,4}" min="1" title=<fmt:message key="label.medicine.create.amountTitle"/>></td>
                    <td><input name="dosage" class="table_field_number" type="text" value="${editMedicine.dosage}"
                               required></td>
                    <td><input name="expDate" class="table_field_high" type="date" value="${editMedicine.expDate}"
                               min="${sessionScope.get('minDate')}"
                               placeholder=<fmt:message key="label.medicine.create.datePlaceholder"/> required></td>
                    <c:choose>
                        <c:when test="${editMedicine.recipeRequired == true}">
                            <td><input name="recipeRequired" class="table_field_high" type="checkbox" checked></td>
                        </c:when>
                        <c:otherwise>
                            <td><input name="recipeRequired" class="table_field_high" type="checkbox"></td>
                        </c:otherwise>
                    </c:choose>
                    <td><input name="priceRub" type="number" value="${editMedicine.rub}" class="table_field_money"
                               required pattern="\d{0,3}" min="0" align="right" title=<fmt:message key="label.medicine.create.priceRub"/>>
                        руб.
                        <input name="priceKop" type="number" value="${editMedicine.coin}" class="table_field_money"
                               required pattern="\d{0,2}" min="0" align="right" title=<fmt:message key="label.medicine.create.priceKop"/>>
                        коп.
                    </td>
                    <td><textarea name="pharmForm" class="table_field_high" type="text"
                                  required>${editMedicine.pharmForm}</textarea></td>
                    <td><input type="submit" value=<fmt:message key="label.medicine.create.actionSave"/>></td>
                </tr>
                <input type="hidden" name="medicineCommand" value="setChanges"/>
                <input type="hidden" name="accountId" value="${accountId}">
                <input type="hidden" name="medicineId" value="${editMedicine.id}">
            </form>
        </c:if>

        <c:forEach var="medicine" items="${medicineList}">
            <tr>
                <td><c:out value="${medicine.name}"/></td>
                <td><c:out value="${medicine.indivisibleAmount}"/></td>
                <td><c:out value="${medicine.amount}"/></td>
                <td><c:out value="${medicine.dosage}"/></td>
                <td><c:out value="${medicine.expDate}"/></td>
                <td>
                    <c:if test="${medicine.recipeRequired == true}"> <fmt:message key="label.yes"/> </c:if>
                    <c:if test="${medicine.recipeRequired == false}"> <fmt:message key="label.no"/> </c:if>
                </td>
                <td><c:out value="${medicine.price/100}"/></td>
                <td><c:out value="${medicine.pharmForm}"/></td>
                <td>
                    <form class="button_line" action="${pageContext.request.contextPath}/pharmacist/medicine/list"
                          method="post">
                        <input type="submit" value=<fmt:message key="label.medicine.create.actionChange"/>>
                        <input type="hidden" name="medicineCommand" value="medicineForEdit">
                        <input type="hidden" name="medicineId" value="${medicine.id}">
                        <input type="hidden" name="accountLogin" value="${accountLogin}">
                    </form>
                    <form class="button_line" action="${pageContext.request.contextPath}/pharmacist/medicine/list"
                          method="post">
                        <input type="submit" value=<fmt:message key="label.medicine.create.actionDelete"/>>
                        <input type="hidden" name="medicineCommand" value="medicineForDelete">
                        <input type="hidden" name="medicineId" value="${medicine.id}">
                        <input type="hidden" name="accountLogin" value="${accountLogin}">
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>

    <c:import url="../../_accountPageNavigation.jsp"/>
    <c:import url="../../_accountRecordsPerPage.jsp"/>

</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
