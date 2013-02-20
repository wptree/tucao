<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>登录 吐槽 有图有真相</title>
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/bootstrap.css" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/tucao-base.css" />" />
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/tucao-theme.css" />" />
</head>
<body>
	<div class="container">
		<div class="row mt-30" >
			<div class="span8 offset2 mt-20">
				<div class="hero-unit board">
					<h1>吐槽</h1>
					<p>完成登录，开始各种吐槽.</p>
					<form:form cssClass="sign-in mt-30" modelAttribute="signInCredentialVo" method="post">
						<div class="row-fluid ">
							<div class="f-l"><input id="user-name" class="input-xlarge" name="signInName" type="text"
								placeholder="登陆邮箱" autocomplete="off" value="<c:out value="${signInCredentialVo.signInName}" />"/></div>
							<div class="f-l hint"><form:errors path="signInName" cssClass="alert alert-error" /></div>
						</div>
						<div class="row-fluid mt-10">
							<div class="f-l"><input id="user-pwd" class="input-xlarge" name="signInPassword" type="password"
								placeholder="密码" autocomplete="off" value="<c:out value="${signInCredentialVo.signInPassword}" />"></div>
							<div class="f-l hint"><form:errors path="signInPassword" cssClass="alert alert-error" /></div>
						</div>
						<div class="row-fluid mt-10">
							 <div class="span6">
							 	<button type="submit" class="btn btn-primary btn-large">登 录</button>
							 	<a class="btn btn-large ml-10" href="<c:url value="/" />">返回</a></div>
							 <div class="span6 ta-r"><a href="#">忘记密码？</a></div>
						</div>
					</form:form>
					<p><a href="<c:url value="/signup" />">还没有吐槽账号？立即注册！</a></p>
				</div>
			</div>
		</div>
	</div>
<script type="text/javascript" src="<c:url value="/resources/js/ga.js" />" ></script>
</body>
</html>