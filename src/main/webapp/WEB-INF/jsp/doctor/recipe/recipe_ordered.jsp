<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Recipe orders</title>
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

    <c:if test="${empty sessionScope.get('recipe')}">
        <h2> <fmt:message key="label.recipe.noOrders"/> </h2>
    </c:if>
    <c:if test="${!empty sessionScope.get('recipe')}">
        <table width="100%">
            <%@include file="_recipe_table_header.jsp" %>

            <c:forEach var="recipeItem" items="${recipe}">

                <tr>
                    <form id="extendRecipe" action="${pageContext.request.contextPath}/doctor/recipe/ordered"
                          method="post">
                        <td><c:out value="${recipeItem.customerFio}"/></td>
                        <td><c:out value="${recipeItem.medicine}"/></td>
                            <%-- Name --%>
                        <td><c:out value="${recipeItem.dosage}"/></td>
                            <%-- Dosage --%>
                        <td align="center"> <%-- Validity --%>
                            <input name="validity" class="table_field_high" type="date" size="15" required
                                   value="${sessionScope.get('maxDate')}"
                                   min="${sessionScope.get('minDate')}" max="${sessionScope.get('maxDate')}"
                                   placeholder=<fmt:message key="label.medicine.create.datePlaceholder"/> >
                        </td>
                        <td align="center">
                            <input type="submit" value=<fmt:message key="label.recipe.actionExtend"/>>
                            <input type="hidden" name="frontCommand" value="extendRecipe">
                            <input type="hidden" name="recipeId" value="${recipeItem.id}">
                        </td>
                    </form>
                    <form id="deleteRecipe" action="${pageContext.request.contextPath}/doctor/recipe/ordered"
                          method="post">
                        <td align="center">
                            <input type="submit" value=<fmt:message key="label.recipe.actionRefuse"/>>
                            <input type="hidden" name="frontCommand" value="refuseRecipe">
                            <input type="hidden" name="recipeId" value="${recipeItem.id}">
                        </td>
                    </form>

                </tr>
            </c:forEach>
        </table>

    </c:if>
</div>


<div id="footer">
    <c:import url="../../_footer.jsp"/>
</div>

</body>
</html>
