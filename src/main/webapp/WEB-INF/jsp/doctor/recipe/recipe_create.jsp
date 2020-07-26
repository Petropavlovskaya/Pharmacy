<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Create new recipe</title>
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
    <c:import url="../_doctor_menu.jsp"/>
</div>


<div id="center_no_right">
    <p class="p-error">${requestScope.get('errorMessage')}</p>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/3.5.0/jquery.min.js"></script>
    <c:import url="../../_toastr.jsp"/>
    <c:if test="${not empty sessionScope.get('successMessage')}" >
        <c:import url="../../_toastrFuncSuccess.jsp"/>
    </c:if>

    <table width="100%">
        <%@include file="_new_recipe_table_header.jsp" %>

        <form action="${pageContext.request.contextPath}/doctor/recipe/create" method="post">
            <tr class="insert_row">

                <td><input list="customer_select" name="customer" size="50" required>
                    <datalist id="customer_select" onchange="">
                        <c:forEach var="customer" items="${sessionScope.get('activeCustomers')}">
                            <option>${customer.value}; ${customer.key}</option>
                        </c:forEach>
                    </datalist>
                </td>

                <td><input list="medicine_select" name="medicine" size="30" required>
                    <datalist id="medicine_select">
                        <c:forEach var="medicine" items="${sessionScope.get('availableMedicine')}">
                            <option> ${medicine.name}; ${medicine.dosage}</option>
                        </c:forEach>
                    </datalist>
                </td>

                <td><input name="expDate" class="table_field_high" type="date" size="15"
                           value="${sessionScope.get('maxDate')}"
                           min="${sessionScope.get('minDate')}" max="${sessionScope.get('maxDate')}"
                           placeholder=<fmt:message key="label.medicine.create.datePlaceholder"/>
                           required></td>

                <td align="center">
                    <input type="submit" value=<fmt:message key="label.recipe.actionIssue"/>>
                    <input type="hidden" name="frontCommand" value="createRecipe">
                </td>
            </tr>
        </form>

    </table>


</div>


<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>


</body>
</html>
