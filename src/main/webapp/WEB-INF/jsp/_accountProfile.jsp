<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setLocale value="${sessionScope.get('lang')}"/>
<fmt:setBundle basename="messages"/>


<h3><fmt:message key="label.account.cabinet.changePersonalData"/>:</h3>
<p><fmt:message key="label.signup.textRequiredField1"/><span class="p-red"> * </span><fmt:message key="label.signup.textRequiredField2"/></p>
<c:set var="account" value="${requestScope.get('account')}"/>
<h3>
    <form action="${pageContext.request.contextPath}/${sessionScope.get('accountRole')}/cabinet/profile" method="post">
        <label for="fieldSurname"><fmt:message key="label.signup.fieldSurname"/>:<span
                class="p-red">*</span></label><br>
        <input type="text" id="fieldSurname" name="accountSurname" value="${account.surname}"
               required maxlength="30" title=
               <fmt:message key="label.signup.fieldSurnameTitle"/>
        >
        <br>
        <label for="fieldName"><fmt:message key="label.signup.fieldName"/>:<span class="p-red">*</span></label><br>
        <input type="text" id="fieldName" name="accountName" value="${account.name}"
               required maxlength="20" title=
               <fmt:message key="label.signup.fieldNameTitle"/>
        >
        <br>
        <label for="fieldPatronymic"><fmt:message key="label.signup.fieldPatronymic"/>:</label><br>
        <input type="text" id="fieldPatronymic" name="accountPatronymic" maxlength="30"
               value="${account.patronymic}"
               title=
               <fmt:message key="label.signup.fieldPatronymicTitle"/>
        >
        <br>
        <label for="fieldPhoneNum"><fmt:message key="label.signup.fieldPhone"/>:</label><br>
        <input type="tel" id="fieldPhoneNum" name="accountPhone" value="${account.phoneNumber}"
               placeholder="+375(XX)XXX-XX-XX" pattern="\+375\([1-9]{2}\)[1-9][0-9]{2}-[0-9]{2}-[0-9]{2}"
               title="+375(XX)XXX-XX-XX"
        >
        <br><br>
        <input type="submit" value=<fmt:message key="label.account.actionChange"/>>
        <input type="hidden" name="frontCommand" value="changeAccountData">
    </form>

    <br><br>

    <form action="${pageContext.request.contextPath}/${sessionScope.get('accountRole')}/cabinet/profile" method="post">
        <label for="fieldOldPassword"><fmt:message key="label.account.fieldOldPassword"/>:<span
                class="p-red">*</span></label><br>
        <input type="password" id="fieldOldPassword" name="oldPassword"
               required pattern="[0-9A-Za-z]{5,15}"
               title= <fmt:message key="label.signup.fieldPasswordTitle"/>
        >
        <br>
        <label for="fieldNewPassword"><fmt:message key="label.account.fieldNewPassword"/>:<span class="p-red">*</span></label><br>
        <input type="password" id="fieldNewPassword" name="newPassword"
               required pattern="[0-9A-Za-z]{5,15}"
               title= <fmt:message key="label.signup.fieldPasswordTitle"/>
        >
        <br>
        <label for="fieldNewPasswordConfirm"><fmt:message key="label.account.fieldNewPasswordConfirm"/>:<span class="p-red">*</span></label><br>
        <input type="password" id="fieldNewPasswordConfirm" name="newPasswordConfirm"
               required pattern="[0-9A-Za-z]{5,15}"
               title= <fmt:message key="label.signup.fieldPasswordTitle"/>
        >
        <br><br>
        <input type="submit" value=<fmt:message key="label.account.actionChange"/>>
        <input type="hidden" name="frontCommand" value="changeAccountPassword">
    </form>

</h3>
