<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

    <p>
        <fmt:message key="label.right.advertising"/> <br /><br />
        <a href="http://www.pogoda.by/" title=<fmt:message key="label.right.weather"/>> <img src="${pageContext.request.contextPath}/images/pogoda.jpg" /></a><br /><br />
        <a href="http://minzdrav.gov.by/ru/" title=<fmt:message key="label.right.minzdrav"/>> <img src="${pageContext.request.contextPath}/images/minzdrav.jpg" /></a><br /><br />
    </p>