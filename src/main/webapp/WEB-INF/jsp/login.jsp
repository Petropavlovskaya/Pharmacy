<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>On-line pharmacy: log in</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="../images/Pharmacy_small.gif" rel="icon" type="image/gif"/>
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
    <p class="p-red">${message}</p>

    <h3>
        <form action="${pageContext.request.contextPath}/login" method="post">
            <label for="fieldUser">Логин:</label><br>
            <input type="text" id="fieldUser" name="login"
                   required pattern="[A-Za-z]{1,}[0-9A-Za-z]{3,15}"
                   placeholder=" Логин"
                   title="Логин должен начинаться с латинской буквы и содержать буквы латинского алфавита или цифры. Длина от 4 до 15 символов."
                   value="Zoba"
            >
            <br><br>
            <label for="fieldPassword">Пароль:</label><br>
            <input type="password" id="fieldPassword" name="password"
                   required pattern="[0-9A-Za-z]{5,15}"
                   placeholder=" Пароль"
                   title="Пароль может состоять из цифр или букв латинского алфавита. Длина пароля от 5 до 15 символов."
                   value="eugen"
            >
            <br><br>
            <input type="submit" value="Log In">
        </form>
    </h3>

</div>

<div id="right">
    <jsp:include page="_right.jsp"></jsp:include>
</div>
<%--<div id="right">
    <p class="p-cen">
        Реклама и не только...<br/><br/>
        <a href="http://www.pogoda.by/33008" title="Погода в Бресте"> <img src="../images/pogoda.jpg"/></a><br/><br/>
        <a href="http://www.fitness-online.by/" title="Фитнес онлайн"> <img src="../images/fitnes.jpg"/></a><br/><br/>
    </p>
</div>--%>

<div id="footer">
    <jsp:include page="_footer.jsp"/>
</div>

</body>
</html>
