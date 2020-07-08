<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<ul id="menu-v">
    <li><a href="${pageContext.request.contextPath}/main"><br><fmt:message key="label.menu.main"/> <br><br></a></li>
    <li><a href="${pageContext.request.contextPath}/medicine"><br><fmt:message key="label.menu.medicine"/><br><br></a></li>

    <c:choose>
        <c:when test="${empty accountLogin}">
            <li><a href="${pageContext.request.contextPath}/signup"><br><fmt:message key="label.menu.signup"/><br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/login"><br><fmt:message key="label.menu.login"/><br><br></a></li>
        </c:when>
        <c:otherwise>
            <li><a href="${pageContext.request.contextPath}/logout"><br><fmt:message key="label.menu.logout"/><br><br></a></li>
        </c:otherwise>
    </c:choose>


</ul>