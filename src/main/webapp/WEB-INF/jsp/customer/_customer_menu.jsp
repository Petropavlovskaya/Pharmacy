<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<ul id="menu-v">
    <li><a href="${pageContext.request.contextPath}/customer/main"><br><fmt:message key="label.menu.main"/><br><br></a></li>
    <li><span><br><fmt:message key="label.menu.drugs"/><br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/customer/medicine/list"><br><fmt:message key="label.menu.medicine"/><br><br></a>
            </li>
            <li><a href="${pageContext.request.contextPath}/customer/medicine/favorite"><br><fmt:message key="label.menu.favorite"/><br><br></a></li>
        </ul>
    </li>

    <li><span><br><fmt:message key="label.menu.account"/><br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/customer/cabinet/profile"><br><fmt:message key="label.menu.profile"/><br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/customer/cabinet/cart"><br><fmt:message key="label.menu.cart"/><br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/customer/cabinet/history"><br><fmt:message key="label.menu.ordersHistory"/><br><br></a>
            </li>
            <li><a href="${pageContext.request.contextPath}/customer/cabinet/recipe"><br><fmt:message key="label.menu.recipe"/><br><br></a></li>
        </ul>
    </li>

    <li><a href="${pageContext.request.contextPath}/logout"><br><fmt:message key="label.menu.logout"/> (${accountLogin})<br><br></a></li>

</ul>
