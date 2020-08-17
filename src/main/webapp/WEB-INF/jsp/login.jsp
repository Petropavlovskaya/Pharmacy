<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy: log in</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="../images/pharmacy_small.gif" rel="icon" type="image/gif"/>
</head>

<body>

<div id="logo">
    <jsp:include page="_header.jsp"/>
</div>
<div id="line"></div>

<div id="menu">
    <c:import url="_menu.jsp"/>
</div>

<div id="center">
    <p class="p-error">${requestScope.get('errorMessage')}</p>

    <h3>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <label for="fieldUser"><fmt:message key="label.login.loginField"/>:</label><br>
            <input type="text" id="fieldUser" name="login"
                   required pattern="[A-Za-z]{1,}[0-9A-Za-z]{3,15}"
                   title=<fmt:message key="label.login.loginFieldTitle"/>
            >
            <br><br>
            <label for="fieldPassword"><fmt:message key="label.login.passwordField"/>:</label><br>
            <input type="password" id="fieldPassword" name="password"
                   required pattern="[0-9A-Za-z]{5,15}"
                   title=<fmt:message key="label.login.passwordFieldTitle"/>
            >
            <br><br>
            <input type="submit" value=<fmt:message key="label.login.buttonLogin"/>>
        </form>
    </h3>

</div>

<div id="right">
    <jsp:include page="_right.jsp"/>
</div>

<div id="footer">
    <jsp:include page="_footer.jsp"/>
</div>

</body>
</html>
