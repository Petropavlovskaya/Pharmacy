<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy. Page not found ((</title>
    <style>
        <%@include file="/css/style.css" %>
        body {
            background: url("${pageContext.request.contextPath}/images/nofound.gif") no-repeat fixed center center;
            left: 20%;
            background-size: contain;
            background-clip: border-box;
        }
    </style>
    <link href="${pageContext.request.contextPath}/images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>
<body>

<div class="div-left">
    <br>
    <br>
    <p class="p-big-left"><fmt:message key="label.404.sorry"/>, <br><fmt:message key="label.404.pageNotFound"/></p>
    <br>
    <br>
    <p class="p-error">${requestScope.get('errorMessage')}</p>
    <br>
    <c:choose>
        <c:when test="${empty accountRole}">
            <a class="link-back" href="${pageContext.request.contextPath}/main"><fmt:message
                    key="label.404.goBack"/></a>
        </c:when>
        <c:otherwise>
            <a class="link-back" href="${pageContext.request.contextPath}/${accountRole}/main"><fmt:message
                    key="label.404.goBack"/></a>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>