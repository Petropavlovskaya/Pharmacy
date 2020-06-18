<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
    <c:if test="${empty recipe}">
        <p3> You have not recipes</p3>
    </c:if>
    <c:if test="${!empty recipe}">
        <table>
            <%@include file="_recipe_table_header.jsp" %>

            <c:forEach var="recipeItem" items="${recipe}">

                <tr>

                    <td><c:out value="${recipeItem.medicine}"/></td>
                        <%-- Name --%>
                    <td><c:out value="${recipeItem.dosage}"/></td>
                        <%-- Dosage --%>
                    <td align="center">
                        <c:if test="${recipeItem.id_medicine_in_order == 0}">
                            Нет
                        </c:if>
                            <%--                        <c:if test="${!empty medicine.id_medicine_in_order}">--%>
                        <c:if test="${recipeItem.id_medicine_in_order > 0}">
                            Да
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
                        <c:when test="${recipeItem.id_medicine_in_order > 0}">
                            <form id="requestRecipe" action="${pageContext.request.contextPath}/customer/cabinet/recipe"
                                  method="post">
                                <td align="center">
                                    <input type="submit" value=" Make an order ">
                                    <input type="hidden" name="customerCommand" value="requestRecipe">
                                    <input type="hidden" name="medicine" value="${recipeItem.medicine}">
                                    <input type="hidden" name="dosage" value="${recipeItem.dosage}">
                                </td>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <jsp:useBean id="now" class="java.util.Date"/>
                            <c:if test="${recipeItem.validity < now}">
                                <form id="extendRecipe"
                                      action="${pageContext.request.contextPath}/customer/cabinet/recipe"
                                      method="post">
                                    <td align="center">
                                        <input type="submit" value=" Make an extend ">
                                        <input type="hidden" name="customerCommand" value="extendRecipe">
                                        <input type="hidden" name="recipe_id" value="${recipeItem.id}">
                                    </td>
                                </form>
                            </c:if>
                            <c:if test="${recipeItem.validity > now}">
                                <td>Рецепт<br>действителен.</td>
                            </c:if>
                            <c:if test="${empty recipeItem.validity}">
                                <td>Рецепт на<br>продлении.</td>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                    <form id="deleteRecipe"
                          action="${pageContext.request.contextPath}/customer/cabinet/recipe" method="post">
                        <td align="center">
                            <input type="submit" value=" Delete ">
                            <input type="hidden" name="customerCommand" value="deleteRecipe">
                            <input type="hidden" name="recipe_id" value="${recipeItem.id}">
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
