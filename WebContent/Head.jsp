<div style="padding:20px">
	<div name="logo">
		<a href="index.html"><img src="img/logo.png" style="width:125px; height:60px; float:left"/></a>
	</div>
	<div name="searchbar" style="padding-top:20px; margin-left:130px">
		<form action="Search?" id="searchform" method="get">
			<%
				out.print("<input type='text' id='query' name='query' style='width:500px; height:25px; font-size:20px' value='"+ request.getSession().getAttribute("Query")+"' />");
			%>
			<input type="submit" id="search" value="" style="height:25px; width:25px; background:url(img/searchbutton.png) no-repeat;border:none;"/>
			
			<input>
			<input>
		</form>
	</div>

	<div name="tabbar" style="margin-top:30px; padding-bottom:10px; border-bottom-style:solid; border-bottom-width:2px; border-bottom-color:#c6c6c8">
		<div style="margin-left:130px;float:left;border-bottom-style:solid;border-bottom-width:2px; padding-bottom:10px"><span style="font-family:'Arial'"><a>Actors</a></span></div>
		<div style="margin-left:200px"><span style="font-family:'Arial';color:#c6c6c8"><a>Movies</a></span></div>
	</div>
</div>