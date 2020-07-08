<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<ul id="menu-v">
    <li><a href="${pageContext.request.contextPath}/doctor/main"><br><fmt:message key="label.menu.main"/><br><br></a>
    </li>

    <li><span><br><fmt:message key="label.menu.drugs"/><br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/doctor/medicine/list"><br>
                <fmt:message key="label.menu.medicine"/><br><br></a></li>
        </ul>
    </li>
    <li><span><br><fmt:message key="label.menu.recipe"/><br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/doctor/recipe/create"><br><fmt:message
                    key="label.menu.createRecipe"/><br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/doctor/recipe/ordered"><br><fmt:message
                    key="label.menu.recipeOrders"/><br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/doctor/recipe/history"><br><fmt:message
                    key="label.menu.recipeHistory"/><br><br></a></li>
        </ul>
    </li>

    <li><span><br><fmt:message key="label.menu.account"/><br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/doctor/cabinet/profile"><br><fmt:message
                    key="label.menu.profile"/><br><br></a></li>
        </ul>
    </li>

    <li><a href="${pageContext.request.contextPath}/logout"><br><fmt:message key="label.menu.logout"/> (${accountLogin})<br><br></a>
    </li>

</ul>
