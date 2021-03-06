<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>

<link href="<c:url value="/resources/css/login.css"/>" rel="stylesheet" media="screen" />
<title>Login Page</title>
</head>
<body onload='document.loginForm.login.focus();'>

	<div id="login-box">

		<h3>Login with Username and Password</h3>

		<c:if test="${not empty error}">
			<div class="error">${error}</div>
		</c:if>
		<c:if test="${not empty message}">
			<div class="msg">${message}</div>
		</c:if>

		<form name='loginForm' action="<c:url value='/authenticate' />" method='POST'>

			<table>
				<tr>
					<td>User:</td>
					<td><input type='text' name='login' value=''/></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type='password' name='password' /></td>
				</tr>
				<tr>
					<td colspan='2'><input name="submit" type="submit"
						value="submit" /></td>
				</tr>
			</table>

			<input type="hidden" name="${_csrf.parameterName}"
				value="${_csrf.token}" />
		</form>
	</div>
</body>
</html>