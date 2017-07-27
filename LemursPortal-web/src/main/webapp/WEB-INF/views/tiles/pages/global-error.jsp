<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:url value="/resources" var="resourcesPath"/>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Error !</title>
</head>
<body>
    <div class="container">
        <div class="clear"></div>
    	<div class="row">
            <div class="login-page" align="center">
              <h3>Erreur ! (Ceci est une page d'erreur personalisée)</h3>
              <c:url value="/" var="homePageUrl"/>
              <a href="${homePageUrl}">Retourner à la page d'accueil</a>
              </div>
            </div>
        </div>
    </div>
</body>
</html>