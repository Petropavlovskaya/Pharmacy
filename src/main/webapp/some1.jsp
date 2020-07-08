<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>

<fmt:setLocale value="${sessionScope.get('lang')}"/>
<c:set value="${sessionScope.get('lang')}" var="locale"/>
<fmt:setBundle basename="messages"/>
<c:set value="ru" var="ru"/>
<c:set value="en" var="en"/>
<c:set value="pl" var="pl"/>

<html>
<head>
    <title>Title</title>
    <style>
        label {
            width: 40px;
            height: 120px;
            display: block;
            position: relative;
        }

        input[type="radio"] + a {
            position: absolute;
            width: 100%;
            height: 100%;
            cursor: pointer;
        }

        input.ru[type="radio"] + a {
            left: 0;
            top: 0;
            /*background: url(images/RU.gif) no-repeat;*/
        }
        input.en[type="radio"] + a {
            left: 0;
            top: 40px;
            /*background: url(images/EN.gif) no-repeat;*/
        }
        input.pl-en[type="radio"] + a {
            left: 0;
            top: 40px;
            /*background: url(images/PL.gif) no-repeat;*/
        }
        input.pl-ru[type="radio"] + a {
            left: 0;
            top: 0;
            /*background: url(images/PL.gif) no-repeat;*/
        }
    </style>
</head>
<body>
<%--<c:set var="${lang}" value="locale"/>--%>


<div class="language">

    <label>

        <c:if test="${locale != ru}">
            <input class="ru" type="radio" name="locale"><a href="?locale=ru">
            <img class="img-rightRU" src="${pageContext.request.contextPath}/images/RU.gif" alt="RU">
        </a>
        </c:if>
        <c:if test="${locale == ru}">
            <input class="pl-ru" type="radio" name="locale"><a href="?locale=pl">
            <img class="img-rightPL" src="${pageContext.request.contextPath}/images/PL.gif" alt="PL"></a>
            </a>
        </c:if>

        <c:if test="${locale != en}">
            <input class="en" type="radio" name="locale"><a href="?locale=en">
            <img class="img-rightEN" src="${pageContext.request.contextPath}/images/EN.gif" alt="EN"></a>
        </a>
        </c:if>

        <c:if test="${locale == en}">
            <input class="pl-en" type="radio" name="locale"><a href="?locale=pl">
            <img class="img-rightPL" src="${pageContext.request.contextPath}/images/PL.gif" alt="PL"></a>
        </a>
        </c:if>


    </label>

<%--    <label>

        <c:if test="${locale != ru}">
            <input class="ru" type="radio" name="locale"><a href="?locale=ru"></a>
        </c:if>

        <c:if test="${locale != en}">
            <input class="en" type="radio" name="locale"><a href="?locale=en"></a>
        </c:if>

        <c:if test="${locale == en}">
            <input class="pl-en" type="radio" name="locale"><a href="?locale=pl"></a>
        </c:if>

        <c:if test="${locale == ru}">
            <input class="pl-ru" type="radio" name="locale"><a href="?locale=pl"></a>
        </c:if>
    </label> --%>





    <%--
        <label><input type="checkbox" value="1" name="k"><span></span></label>

        <input type="radio" name="drink" value="rad1"> Пиво<Br>
        <input type="radio" name="drink" value="rad2"> Чай<Br>
        <input type="radio" name="drink" value="rad3"> Кофе


        <a href="?locale=ru" onclick="show();">
            <img class="img-rightRU" src="${pageContext.request.contextPath}/images/RU.gif" alt="RU"></a>
        <a href="?locale=en" onclick="show();">
            <img class="img-rightEN" src="${pageContext.request.contextPath}/images/EN.gif" alt="EN"></a>
        <a href="?locale=pl" onclick="show();">
            <img class="img-rightPL" src="${pageContext.request.contextPath}/images/PL.gif" alt="PL"></a>--%>
</div>
<div>
    <br>
    <br>
    <c:out value="${locale}"/> <br>
    <fmt:message key="label.main"/> <br>
    <fmt:message key="label.menu.medicine"/> <br>
</div>
</body>
</html>
