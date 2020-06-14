<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>n-line pharmacy: medicine</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="../images/Pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>

<body>

<div id="logo">
    <jsp:include page="_header.jsp"/>
</div>
<div id="line"></div>

<div id="menu">
    <c:import url="_menu.jsp"/>
</div>

<div id="center">
<%--    <c:if test="${not empty message}"><p class="p-red">${message}</p></c:if>--%>
    <table>
        <thead>
        <th>Наименование<%--Medicine name--%></th>
        <th>Неделимое <br>кол-во<%--Indivisible amount--%></th>
        <th>В наличии<%--Total amount--%></th>
        <th>Дозировка<%--Dosage--%></th>
        <th>Годен до<%--Expiration date--%></th>
        <th>Требуется <br>рецепт<%--Recipe required--%></th>
        <th>Цена<%--Price--%></th>
        <th>Форма <br>выпуска<%--Pharmacy form--%></th>
        </thead>

        <c:forEach var="medicine" items="${medicineList}">
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
            </tr>
        </c:forEach>
    </table>
</div>

<div id="right"><jsp:include page="_right.jsp"></jsp:include></div>

<div id="footer">
    <jsp:include page="_footer.jsp"/>
</div>
</body>
</html>

