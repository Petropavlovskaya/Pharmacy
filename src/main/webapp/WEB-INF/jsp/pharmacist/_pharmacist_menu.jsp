<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<ul id="menu-v">
    <li><a href="${pageContext.request.contextPath}/pharmacist/main"><br><fmt:message key="label.menu.main"/><br><br></a></li>
    <li><span><br><fmt:message key="label.menu.drugs"/><br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/pharmacist/medicine/list"><br>
                <fmt:message key="label.menu.medicine"/><br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/pharmacist/medicine/create"><br>
                <fmt:message key="label.menu.addMedicine"/><br><br></a></li>
            <li><a href="${pageContext.request.contextPath}/pharmacist/medicine/expired"><br>
                <fmt:message key="label.menu.expiredMedicine"/><br><br></a></li>
        </ul>
    </li>
    <li><span><br><fmt:message key="label.menu.account"/><br><br></span>
        <ul>
            <li><a href="${pageContext.request.contextPath}/pharmacist/cabinet/profile"><br><fmt:message key="label.menu.profile"/><br><br></a></li>
        </ul>
    </li>
    <li><a href="${pageContext.request.contextPath}/logout"><br><fmt:message key="label.menu.logout"/> (${accountLogin})<br><br></a></li>
</ul>
