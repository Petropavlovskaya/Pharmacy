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
    <link href="images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>

<div id="logo">
    <c:import url="../../_header.jsp"/>
</div>
<div id="line"></div>
<div id="menu">
    <c:import url="../_doctor_menu.jsp"/>
</div>
<div id="center_no_right">

    <table width="100%">
        <%@include file="_doctor_medicine_table_header.jsp" %>

        <c:forEach var="medicine" items="${sessionScope.get('medicineList')}">
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
            </tr>
        </c:forEach>
    </table>
    <br>

    <c:import url="../../_accountPageNavigation.jsp"/>
    <c:import url="../../_accountRecordsPerPage.jsp"/>

</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
