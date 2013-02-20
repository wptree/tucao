<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!--ul id="side-nav" class="dis-n">
	<li class="about"><a class ="btn-info" href="" target="_blank" title="关于作者">关于作者&nbsp;&nbsp;&nbsp;<i class="icon-user icon-white"></i></a></li>
	<li class="code"><a class="btn-primary" href="" target="_blank" title="获取源代码">获取源码&nbsp;&nbsp;&nbsp;<i class="icon-file icon-white"></i></a></li>
	<li class="vote"><a class="btn-success" href="" target="_blank" title="大赛专帖">大赛专帖&nbsp;&nbsp;&nbsp;<i class="icon-fire icon-white"></i></a></li>
	<li class="vote"><a class="btn-inverse" href="" target="_blank" title="投票支持">投票支持&nbsp;&nbsp;&nbsp;<i class="icon-ok icon-white"></i></a></li>
</ul-->
<script type="text/javascript">
	$(function() {
		/*$('#side-nav a').stop().animate({
			'marginLeft' : '-85px'
		}, 1000);*/
		$('#side-nav').css('top', Math.max(100, ($(window).height()-$('#side-nav').height())/2));
		$(window).resize(function(){
			$('#side-nav').css('top', Math.max(100, ($(window).height()-$('#side-nav').height())/2));
		});
		$('#side-nav a').css('marginLeft', '-85px');
		$('#side-nav').show();
		$('#side-nav > li').hover(function() {
			$('a', $(this)).stop().animate({
				'marginLeft' : '-2px'
			}, 200);
		}, function() {
			$('a', $(this)).stop().animate({
				'marginLeft' : '-85px'
			}, 200);
		});
	});
</script>