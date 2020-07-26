<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>

<html>
<head>
    <title>On-line pharmacy: sign up</title>
    <style>
        <%@include file="/css/style.css" %>
    </style>
    <link href="${pageContext.request.contextPath}/images/pharmacy_small.gif" rel="icon" type="image/gif"/>
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

    <p><fmt:message key="label.signup.textRequiredField1"/><span class="p-red"> * </span><fmt:message key="label.signup.textRequiredField2"/></p>
    <h3>
        <form action="${pageContext.request.contextPath}/signup" method="post">
            <label for="fieldSurname"><fmt:message key="label.signup.fieldSurname"/>:<span
                    class="p-red">*</span></label><br>
            <input type="text" id="fieldSurname" name="accountSurname" value="${requestScope.get('surname')}"
                   required maxlength="30" title=
                   <fmt:message key="label.signup.fieldSurnameTitle"/>
            >
            <br>
            <label for="fieldName"><fmt:message key="label.signup.fieldName"/>:<span class="p-red">*</span></label><br>
            <input type="text" id="fieldName" name="accountName" value="${requestScope.get('name')}"
                   required maxlength="20" title=
                   <fmt:message key="label.signup.fieldNameTitle"/>
            >
            <br>
            <label for="fieldPatronymic"><fmt:message key="label.signup.fieldPatronymic"/>:</label><br>
            <input type="text" id="fieldPatronymic" name="accountPatronymic" maxlength="30" value="${requestScope.get('patronymic')}"
                   title=
                   <fmt:message key="label.signup.fieldPatronymicTitle"/>
            >
            <br>
            <label for="fieldPhoneNum"><fmt:message key="label.signup.fieldPhone"/>:</label><br>
            <input type="tel" id="fieldPhoneNum" name="accountPhone" value="${requestScope.get('phone')}"
                   placeholder="+375(XX)XXX-XX-XX" pattern="\+375\([1-9]{2}\)[1-9][0-9]{2}-[0-9]{2}-[0-9]{2}"
                   title="+375(XX)XXX-XX-XX"
            >
            <br>
            <label for="fieldUser"><fmt:message key="label.signup.fieldLogin"/>:<span class="p-red">*</span></label><br>
            <input type="text" id="fieldUser" name="login" required pattern="[A-Za-z]{1,}[0-9A-Za-z]{3,15}" value="${requestScope.get('login')}"
                   title=
                   <fmt:message key="label.signup.fieldLoginTitle"/>
            >
            <br>
            <label for="fieldPassword"><fmt:message key="label.signup.fieldPassword"/>:<span
                    class="p-red">*</span></label><br>
            <input type="password" id="fieldPassword" name="password" required pattern="[0-9A-Za-z]{5,15}"
                   title=
                   <fmt:message key="label.signup.fieldPasswordTitle"/>
            >
            <br>
            <label for="fieldPasswordConfirm"><fmt:message key="label.signup.fieldPasswordConfirm"/>:<span
                    class="p-red">*</span></label><br>
            <input type="password" id="fieldPasswordConfirm" name="passwordConfirm"
                   required pattern="[0-9A-Za-z]{5,15}"
                   title=
                   <fmt:message key="label.signup.fieldPasswordTitle"/>
            >
            <br><br>
            <input type="submit" value=<fmt:message key="label.signup.buttonSignup"/>>
        </form>
    </h3>


</div>

<div id="right">
    <jsp:include page="_right.jsp"/>
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
