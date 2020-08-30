<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Medicine expired items</title>
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

        <c:forEach var="medicine" items="${sessionScope.get('expiredMedicineList')}">
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
                </td>
                <td>
                    <form class="button_line" action="${pageContext.request.contextPath}/pharmacist/medicine/expired"
                          method="post">
                        <input type="submit" value=<fmt:message key="label.medicine.create.actionDelete"/>>
                        <input type="hidden" name="frontCommand" value="medicineForDelete">
                        <input type="hidden" name="medicineId" value="${medicine.id}">
                        <input type="hidden" name="accountLogin" value="${sessionScope.get('accountLogin')}">
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>

    <c:import url="../../_accountPageNavigation.jsp"/>
    <c:import url="_expiredRecordsPerPage.jsp"/>

</div>

<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
