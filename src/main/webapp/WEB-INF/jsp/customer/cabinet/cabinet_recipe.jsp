<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Recipe</title>
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
    <p class="p-red">${requestScope.get('message')} <br> </p>
    <c:if test="${empty sessionScope.get('recipe')}">
        <p3> You have not recipes</p3>
    </c:if>
    <c:if test="${!empty sessionScope.get('recipe')}">
        <table width="100%">
            <%@include file="_recipe_table_header.jsp" %>

            <c:forEach var="recipeItem" items="${sessionScope.get('recipe')}">
                <tr>

                    <td><c:out value="${recipeItem.medicine}"/></td>
                        <%-- Name --%>
                    <td><c:out value="${recipeItem.dosage}"/></td>
                        <%-- Dosage --%>
                    <td align="center">
                        <c:if test="${recipeItem.idMedicineInOrder == 0}">
                            <fmt:message key="label.no"/>
                        </c:if>
                            <%--                        <c:if test="${!empty medicine.idMedicineInOrder}">--%>
                        <c:if test="${recipeItem.idMedicineInOrder > 0}">
                            <fmt:message key="label.yes"/>
                        </c:if>
                    </td>
                    <td align="center"> <%-- Validity --%>
                        <c:if test="${! empty recipeItem.validity}">
                            <fmt:formatDate pattern="yyyy-MM-dd" value="${recipeItem.validity}"/>
                        </c:if>
                        <c:if test="${empty recipeItem.validity}">
                            -
                        </c:if>
                    </td>

                    <c:choose>
                        <c:when test="${recipeItem.idMedicineInOrder > 0}">
                            <form id="requestRecipe" action="${pageContext.request.contextPath}/customer/cabinet/recipe"
                                  method="post">
                                <td align="center">
                                    <input type="submit" value=<fmt:message key="label.medicine.buttonOrderRecipe"/>>
                                    <input type="hidden" name="customerCommand" value="requestRecipe">
                                    <input type="hidden" name="medicine" value="${recipeItem.medicine}">
                                    <input type="hidden" name="dosage" value="${recipeItem.dosage}">
                                </td>
                            </form>
                        </c:when>
                        <c:when test="${recipeItem.idMedicineInOrder == -1}">
                            <td><fmt:message key="label.recipe.refuseMessage"/></td>
                        </c:when>
                        <c:otherwise>
                            <jsp:useBean id="now" class="java.util.Date"/>
                            <c:if test="${recipeItem.validity < now}">
                                <form id="extendRecipe"
                                      action="${pageContext.request.contextPath}/customer/cabinet/recipe"
                                      method="post">
                                    <td align="center">
                                        <input type="submit" value=<fmt:message key="label.recipe.actionExtend"/>>
                                        <input type="hidden" name="customerCommand" value="extendRecipe">
                                        <input type="hidden" name="recipeId" value="${recipeItem.id}">
                                    </td>
                                </form>
                            </c:if>
                            <c:if test="${recipeItem.validity > now}">
                                <td><fmt:message key="label.recipe.validMessage"/></td>
                            </c:if>
                            <c:if test="${empty recipeItem.validity}">
                                <td><fmt:message key="label.recipe.extMessage"/></td>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                    <form id="deleteRecipe"
                          action="${pageContext.request.contextPath}/customer/cabinet/recipe" method="post">
                        <td align="center">
                            <input type="submit" value=<fmt:message key="label.medicine.create.actionDelete"/>>
                            <input type="hidden" name="customerCommand" value="deleteRecipe">
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
