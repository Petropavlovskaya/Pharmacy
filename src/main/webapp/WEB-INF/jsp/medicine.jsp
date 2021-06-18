<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy: medicine</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="../images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>

<body>

<div id="logo">
    <jsp:include page="_header.jsp"/>
</div>
<div id="line"></div>

<div id="menu">
    <c:import url="_menu.jsp"/>
</div>

<div id="center_no_right">
<table width="100%">
    <thead>
    <th><fmt:message key="label.tableHeader.medicineName"/></th>
    <th><fmt:message key="label.tableHeader.indivisibleQuantity"/></th>
    <th><fmt:message key="label.tableHeader.available"/></th>
    <th><fmt:message key="label.tableHeader.dosage"/></th>
    <th><fmt:message key="label.tableHeader.exp"/></th>
    <th><fmt:message key="label.tableHeader.recipeRequired"/></th>
    <th><fmt:message key="label.tableHeader.price"/></th>
    <th><fmt:message key="label.tableHeader.releaseForm"/></th>
    </thead>

    <c:forEach var="medicine" items="${sessionScope.get('medicineList')}">
        <tr>
                <%--            <td><c:out value="${medItem.id}"/></td>--%>
            <td><c:out value="${medicine.name}"/></td>
            <td><c:out value="${medicine.indivisibleAmount}"/></td>
            <td><c:out value="${medicine.amount}"/></td>
            <td><c:out value="${medicine.dosage}"/></td>
            <td><c:out value="${medicine.expDate}"/></td>
            <td align="center">
                <c:if test="${medicine.recipeRequired == true}"> <fmt:message key="label.yes"/> </c:if>
                <c:if test="${medicine.recipeRequired == false}"> <fmt:message key="label.no"/> </c:if>
            </td>
            <td><c:out value="${medicine.price/100}"/></td>
            <td><c:out value="${medicine.pharmForm}"/></td>
        </tr>
    </c:forEach>
</table>
<br>

<c:import url="_commonPageNavigation.jsp"/>

<form id="getPage" method="post" action="${pageContext.request.contextPath}/medicine">
    <label for="records"><fmt:message key="label.pagination.perPageMessage"/>:</label>
    <select id="records" name="recordsPerPage" onchange="this.form.submit()">

        <c:if test="${sessionScope.get('recordsPerPage') == 5}">
            <option value="5" selected>5</option>
        </c:if>
        <c:if test="${sessionScope.get('recordsPerPage') != 5}">
            <option value="5">5</option>
        </c:if>

        <c:if test="${sessionScope.get('recordsPerPage') == 10}">
            <option value="10" selected>10</option>
        </c:if>
        <c:if test="${sessionScope.get('recordsPerPage') != 10}">
            <option value="10">10</option>
        </c:if>

        <c:if test="${sessionScope.get('recordsPerPage') == 15}">
            <option value="15" selected>15</option>
        </c:if>
        <c:if test="${sessionScope.get('recordsPerPage') != 15}">
            <option value="15">15</option>
        </c:if>

    </select>
</form>

</div>

<div id="footer">
    <jsp:include page="_footer.jsp"/>
</div>
</body>
</html>

