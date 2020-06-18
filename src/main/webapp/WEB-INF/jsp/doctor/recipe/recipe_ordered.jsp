<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title>On-line pharmacy. Recipe orders</title>
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
    <c:import url="../_doctor_menu.jsp"/>
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
                    <form id="extendRecipe" action="${pageContext.request.contextPath}/doctor/recipe/ordered"
                          method="post">
                        <td><c:out value="${recipeItem.customerFio}"/></td>
                        <td><c:out value="${recipeItem.medicine}"/></td>
                            <%-- Name --%>
                        <td><c:out value="${recipeItem.dosage}"/></td>
                            <%-- Dosage --%>
                        <td align="center"> <%-- Validity --%>
                            <input name="validity" class="table_field_high" type="date" size="9"
                                   placeholder="ГГГГ-ММ-ДД" >
                        </td>
                        <td align="center">
                            <input type="submit" value=" Extend recipe ">
                            <input type="hidden" name="customerCommand" value="extendRecipe">
                            <input type="hidden" name="recipeId" value="${recipeItem.id}">
                        </td>
                    </form>
                    <form id="deleteRecipe" action="${pageContext.request.contextPath}/doctor/recipe/ordered"
                          method="post">
                        <td align="center">
                            <input type="submit" value=" Refuse ">
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
