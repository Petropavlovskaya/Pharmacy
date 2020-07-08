<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>

<fmt:setLocale value="${sessionScope.get('lang')}" />
<c:set value="${sessionScope.get('lang')}" var="locale"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>Title</title>
    <script type="text/javascript">
    var images = document.getElementsByTagName('img');
    var locale = document.getElementsByName("locale");
    function show () {
        if (locale.toString() === "ru") {
            images[0].style.visibility = "hidden";
            images[1].style.visibility = "visible";
            images[2].style.visibility = "visible";
        }

        if (locale.toString() === "en") {
            images[0].style.visibility = "visible";
            images[1].style.visibility = "hidden";
            images[2].style.visibility = "visible";
        }

        if (locale.toString() === "pl") {
            images[0].style.visibility = "visible";
            images[1].style.visibility = "visible";
            images[2].style.visibility = "hidden";
        }
    }

 </script>
</head>
<body>
<%--<c:set var="${lang}" value="locale"/>--%>


<div>
    <a href="?locale=ru" onclick="show();">
        <img class="img-rightRU" src="${pageContext.request.contextPath}/images/RU.gif" alt="RU"></a>
    <a href="?locale=en" onclick="show();">
        <img class="img-rightEN" src="${pageContext.request.contextPath}/images/EN.gif" alt="EN"></a>
    <a href="?locale=pl" onclick="show();">
        <img class="img-rightPL" src="${pageContext.request.contextPath}/images/PL.gif" alt="PL"></a>
</div>
<div>
    <br>
    <br>
    <c:out value="${locale}"/>          <br>
    <fmt:message key="label.main"/>     <br>
    <fmt:message key="label.menu.medicine"/> <br>
</div>
</body>
</html>
