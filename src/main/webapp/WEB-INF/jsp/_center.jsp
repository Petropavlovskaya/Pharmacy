<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

   <c:choose>
        <c:when test="${empty accountLogin}">
            <h3 align="center"><fmt:message key="label.main.placeOrder"/>
                <br>
                <a href="${pageContext.request.contextPath}/login"><fmt:message key="label.main.Login"/></a>
                <fmt:message key="label.main.or"/>
                <a href="${pageContext.request.contextPath}/signup"><fmt:message key="label.main.register"/></a>
            </h3>
        </c:when>
    </c:choose>

    <br><br>
    <h2> <fmt:message key="label.main.slogan"/>
    </h2>