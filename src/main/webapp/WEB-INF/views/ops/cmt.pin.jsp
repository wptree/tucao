<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tucao.me/functions" prefix="f" %>
<div class="cmt convo row-fluid">
	<div class="f-l mr-5"><a class="img" title="${cmt.createdBy.name}"
		href="<c:url value="/profiles/${cmt.createdBy.id}" />" >
		<img src="${f:avatarUrl(cmt.createdBy.avatar, cmt.createdBy.gender)}" width=30 height=30 /></a></div>
	<div class="f-l lh-16" style="width: 157px;"><a href="<c:url value="/profiles/${cmt.createdBy.id}" />" >${cmt.createdBy.name}</a>&nbsp;
		<span class="c-666">${cmt.content}</span>&nbsp;
		<span>(<span class="timeago" title="<fmt:formatDate 
		value="${cmt.createdAt}" pattern="yyyy-MM-dd HH:mm:ss Z"/>"></span>)</span>
	</div>
</div>