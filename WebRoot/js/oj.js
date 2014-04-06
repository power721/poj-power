"use strict";

if ($) {
	$(document).ready(function() {

		$("td.Navigation").mouseover(function() {
			$(this).css("background-color", "#DAE6FF");
		});
		$("td.Navigation").mouseout(function() {
			$(this).css("background-color", "");
		});
	});
}

Date.prototype.format = function(format) {
	var o = {
		"M+" : this.getMonth() + 1, // month
		"d+" : this.getDate(), // day
		"h+" : this.getHours(), // hour
		"m+" : this.getMinutes(), // minute
		"s+" : this.getSeconds(), // second
		"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
		"S" : this.getMilliseconds()
	// millisecond
	};
	if (/(y+)/.test(format))
		format = format.replace(RegExp.$1, (this.getFullYear() + "")
				.substr(4 - RegExp.$1.length));
	for ( var k in o)
		if (new RegExp("(" + k + ")").test(format))
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
					: ("00" + o[k]).substr(("" + o[k]).length));
	return format;
};

// var Image1 = new Image(1024, 1);
// Image1.src = "images/bar.jpg";
var isIE = window.navigator.userAgent.indexOf("MSIE 6.0") >= 1
		|| window.navigator.userAgent.indexOf("MSIE 7.0") >= 1;

var extArray = new Array(".in", ".out", ".txt", ".doc", ".c", ".cc", ".java",
		".pas", ".asm", ".rar", ".zip", ".ppt", ".doc", ".gif", ".jpg", ".png",
		".bmp");
function LimitAttachData(form, file) {
	allowSubmit = false;
	if (!file)
		return;
	while (file.indexOf("\\") != -1)
		file = file.slice(file.indexOf("\\") + 1);
	ext = file.slice(file.indexOf(".")).toLowerCase();
	for (var i = 0; i < extArray.length; i++) {
		if (extArray[i] == ext) {
			allowSubmit = true;
			break;
		}
	}
	if (allowSubmit)
		form.submit();
	else
		alert("对不起，只能上传以下格式的文件:  " + (extArray.join("  ")) + "\n请重新选择符合条件的文件"
				+ "再上传.");
}

function checkQQ() {
	var qq = document.getElementById("qq").value;
	if (qq == 0)
		return true;
	var patrn = /^[1-9]\d{4,9}$/;
	if (patrn.exec(qq)) {
		qq = parseInt(qq, 10);
		if (qq > 10000 && qq < 2300000000) {
			return true;
		}
	}
	alert("请输入正确的QQ号码!或者不填写。");
	return false;
}

function tick() {
	var today;
	today = new Date();
	Clock.innerHTML = today.toLocaleString().replace(/(年|月)/g, "-").replace(
			/日/, "");
	window.setTimeout("tick()", 1000);
}
function RefreshImage() {
	var el = document.getElementById("ImageCode");
	el.src = el.src + '?';
}
function showtrick() {
	alert('谁说要验证码??!!\n看不清就看不清嘛');
}
function myprint() {

	//document
	//		.writeln("</br>&nbsp;&nbsp;<a href=../gongju/link.html><b><u><font color=red>OJ大全</font></u></b></a></br>&nbsp;&nbsp;<a href=../gongju><b><u><font color=red>资料下载</font></u></b></a></br>&nbsp;&nbsp;<a href=ACShare><b><u><font color=red>AC共享计划</font></u></b></a></br>&nbsp;&nbsp;<b>PowerOJ测试，如果遇到问题请联系<a href='mailto:power721@163.com'>管理员</a>。</b></br>");
}
function announcement() {
	document.writeln("----信息工程学院ACM集训队</br>");
}
function rejudge(str, probID) {
	if (confirm("Are you sure to rejudge?")) {
		window.location = "admin.rejudge?" + str + "=" + probID;
	}
}
function cfdel(id, r) {
	var s = (r == 0 ? "hide" : r == 1 ? "resume" : "del");
	if (confirm("Are you sure to " + s + " this message?"
			+ (r == 0 ? "\n注意旧版OJ不能隐藏！" : "\n删除功能不完善，尽量少用！"))) {
		window.location = s + "message?message_id=" + id;
	}
}
function sbdel(userID) {
	if (confirm("Are you sure to delete " + userID + " from source_browser?")) {
		window.location = "admin.sourcebrowser?op=del&user=" + userID;
	}
}
function sbadd(userID) {
	if (confirm("Are you sure to add this user to source_browser?")) {
		window.location = "admin.sourcebrowser?op=add&user=" + userID;
	}
}
// <a href='javascript:roledel("20094012","Administrator")'>Del</a>
function roledel(userID, role) {
	if (confirm("Are you sure to delete " + userID + " from " + role + "?")) {
		window.location = "admin.roles?op=del&user=" + userID + "&role=" + role;
	}
}
function pcdel(userID, id) {
	if (confirm("Are you sure to delete " + userID + " from this contest?")) {
		window.location = "admin.contestuser?op=del&user=" + userID
				+ "&contest_id=" + id;
	}
}
function pcadd(userID, id) {
	if (confirm("Are you sure to add this user to this contest?")) {
		window.location = "admin.contestuser?op=add&user=" + userID
				+ "&contest_id=" + id;
	}
}
function filedel(s, id) {
	if (confirm("Are you sure to delete this file?")) {
		window.location = "admin.upload?problem_id=" + s + "&op=del&idx=" + id;
	}
}

function emb() {
	if (confirm("Are you sure to empty your mail box?")) {
		window.location = "emptymailbox";
	}
}
function ca() {
	var c = document.getElementById('cba').checked;
	for (var i = 1;; ++i) {
		var cb = document.getElementById('cb' + i);
		if (cb) {
			cb.checked = c;
		} else {
			break;
		}
	}
}
function ds() {
	for (var i = 1;; ++i) {
		var cb = document.getElementById('cb' + i);
		if (cb) {
			if (cb.checked) {
				if (confirm('Are you sure to delete selected mails?')) {
					document.delform.submit();
				}
				return;
			}
		} else {
			break;
		}
	}
	alert('No mails selected!');
}
var TOTTIME = 150;
var RATIO = 3.1415926 / 2 / TOTTIME;
var targeth = new Array();
function fold(t, time) {
	var cur = document.getElementById(t);
	if (time > 0) {
		var r = time / TOTTIME;
		var temp = Math.round(r * cur.offsetHeight);
		if (isIE) {
			cur.filters.alpha.opacity = 100 * r;
		} else {
			cur.style.opacity = r;
		}
		if (temp > 1) {
			cur.style.height = temp;
		} else {
			cur.style.visibility = "hidden";
			cur.style.height = 1;
			return;
		}
		var recur = "fold('" + t + "'," + (time - 10) + ")";
		setTimeout(recur, 10);
	} else {
		cur.style.visibility = 'hidden';
		cur.style.height = 1;
	}
}
function unfold(t, time, h) {
	var cur = document.getElementById(t);
	if (time > 0) {
		var r = 1 - time / TOTTIME;
		var temp = Math.round(r * h);
		if (temp > 0)
			cur.style.height = temp;
		if (isIE) {
			cur.filters.alpha.opacity = 100 * r;
		} else {
			cur.style.opacity = r;
		}
		var recur = "unfold('" + t + "'," + (time - 10) + "," + h + ")";
		setTimeout(recur, 10);
	} else {
		cur.style.height = "";
		if (isIE)
			cur.filters.alpha.opacity = 100;
		else
			cur.style.opacity = 1;
	}
}
function sh(tb) {
	var tmp_u = 'u' + tb, tmp_i = 'i' + tb;
	var ele_u = document.getElementById(tmp_u);
	if (ele_u.style.visibility != "hidden") {
		var temph = ele_u.offsetHeight;
		fold(tmp_u, TOTTIME);
		targeth[tmp_u] = temph;
		document.getElementById(tmp_i).src = 'images/op.gif';
	} else {
		document.getElementById(tmp_i).src = 'images/om.gif';
		ele_u.style.visibility = "";
		if (targeth[tmp_u] == null || targeth[tmp_u] == undefined) {
			ele_u.style.height = '100%';
			targeth[tmp_u] = ele_u.offsetHeight;
			ele_u.style.height = 1;
		}
		unfold(tmp_u, TOTTIME, targeth[tmp_u]);
	}
}
function s2(tb) {
	var tmp_u = 'u' + tb, tmp_i = 'i' + tb;
	if (document.getElementById(tmp_u).style.display != "none") {
		document.getElementById(tmp_u).style.display = "none";
		document.getElementById(tmp_i).src = 'images/op.gif';
	} else {
		document.getElementById(tmp_u).style.display = "";
		document.getElementById(tmp_i).src = 'images/om.gif';
	}
}

function init() {
	if (document.all) {
		topbar.style.position = "absolute";
	}
}
function check() {
	topbar.style.top = document.documentElement.scrollTop;
}
var g_myBodyInstance = (document.documentElement ? document.documentElement
		: window);
g_myBodyInstance.onscroll = check;
var onit = true;
var num = 0;
function moveup(iteam, top, txt, rec) {
	temp = eval(iteam);
	tempat = eval(top);
	temptxt = eval(txt);
	temprec = eval(rec);
	at = parseInt(temp.style.top);
	temprec.style.display = "";
	if (num > 27) {
		temptxt.style.display = "";
	}
	if (at > (tempat - 28) && onit) {
		num++;
		temp.style.top = at - 1;
		Stop = setTimeout("moveup(temp,tempat,temptxt,temprec)", 10);
	} else {
		return;
	}
}
function movedown(iteam, top, txt, rec) {
	temp = eval(iteam);
	temptxt = eval(txt);
	temprec = eval(rec);
	clearTimeout(Stop);
	temp.style.top = top;
	num = 0;
	temptxt.style.display = "none";
	temprec.style.display = "none";
}
function ontxt(iteam, top, txt, rec) {
	temp = eval(iteam);
	temptxt = eval(txt);
	temprec = eval(rec);
	if (onit) {
		temp.style.top = top - 28;
		temptxt.style.display = "";
		temprec.style.display = "";
	}
}
function movereset(over) {
	if (over == 1) {
		onit = false;
	} else {
		onit = true;
	}
}
function table_n_ie(num, tot1, tot2, href01) {
	var i;
	var allvalues = 0;
	for (i = 0; i < num; i++) {
		allvalues += sa[0][i];
	}
	document
			.write("<br><p align=center><font size=5 color=blue>Statistics</font></p>");
	document
			.write("<table class=a borderColor=#ffffff border=1 width=80% align=center>");
	document
			.write("<tr><td width=80%>Total Submissions</td><td align=right><a href="
					+ href01 + ">" + allvalues + "</a></td></tr>");
	document.write("<tr><td>Users (Submitted)</td><td align=right>" + tot1
			+ "</td></tr>");
	document.write("<tr><td>Users (Solved)</td><td align=right>" + tot2
			+ "</td></tr>");
	for (i = 0; i < num; i++) {
		document.write("<tr><td>" + sa[1][i] + "</td><td align=right>"
				+ "<a href=" + sa[2][i] + ">" + sa[0][i] + "</a></td></tr>");
	}
	document.write("</table>");
}

function table(num, table_left, table_top, all_width, all_height, table_title,
		unit, radius, l_width, tot1, tot2, href01) {

	if (!(window.navigator.userAgent.indexOf("MSIE 6.0") >= 1
			|| window.navigator.userAgent.indexOf("MSIE 7.0") >= 1 || window.navigator.userAgent
			.indexOf("MSIE 8.0") >= 1)) {
		table_n_ie(num, tot1, tot2, href01);
		return;
	}
	var allvalues = 0;
	var color = new Array();
	var bg_color = new Array(num);
	var pie = new Array(num);
	color[0] = "#19ff19";
	color[1] = "#ff8c19";
	color[2] = "#ff1919";
	color[3] = "#ffff19";
	color[4] = "#1919ff";
	color[5] = "#fc0000";
	color[6] = "#3cc000";
	color[7] = "#ff19ff";
	color[8] = "#993300";
	color[9] = "#f60000";
	for (i = 0, j = 0; i < num; i++, j++) {
		bg_color[i] = color[j];
		if (j == color.length) {
			j = -1;
		}
	}
	for (var i = 0; i < num; i++) {
		allvalues += sa[0][i];
	}
	var k = 0;
	for (i = 0; i < num - 1; i++) {
		pie[i] = parseInt((sa[0][i]) / allvalues * 10000) / 10000;
		k += pie[i];
	}
	pie[num - 1] = 1 - k;
	document
			.writeln("<v:shapetype id='Cake_3D' coordsize='21600,21600' o:spt='95' adj='11796480,5400' path='al10800,10800@0@0@2@14,10800,10800,10800,10800@3@15xe'></v:shapetype>");
	document
			.writeln("<v:shapetype id='3dtxt' coordsize='21600,21600' o:spt='136' adj='10800' path='m@7,l@8,m@5,21600l@6,21600e'>");
	document
			.writeln("<v:path textpathok='t' o:connecttype='custom' o:connectlocs='@9,0;@10,10800;@11,21600;@12,10800' o:connectangles='270,180,90,0'/>");
	document.writeln("<v:textpath on='t' fitshape='t'/>");
	document.writeln("<o:lock v:ext='edit' text='t' shapetype='t'/>");
	document.writeln("</v:shapetype>");
	document.writeln("<v:group ID='table' style='position:absolute;left:"
			+ table_left + "px;top:" + table_top + "px;WIDTH:" + l_width
			+ "px;HEIGHT:" + all_height + "px;' coordsize = '21000,11500'>");
	document
			.writeln("<v:Rect style='position:relative;left:500;top:200;width:20000;height:800'filled='false' stroked='false'>");
	document.writeln("<v:TextBox inset='0pt,0pt,0pt,0pt'>");
	document
			.writeln("<table width='100%' border='0' align='center' cellspacing='0'>");
	document.writeln("<tr>");
	document
			.writeln("<td align='center' valign='middle'><div style='font-size:15pt; font-family:Arial, Helvetica, sans-serif;'><B>"
					+ table_title + "</B></div></td>");
	document.writeln("</tr>");
	document.writeln("</table>");
	document.writeln("</v:TextBox>");
	document.writeln("</v:Rect> ");
	var height0 = 7000 / 11;
	document
			.writeln("<v:rect id='back' style='position:relative;left:500;top:1000;width:20000; height:"
					+ ((num + 3) * height0 + 3400)
					+ ";' onmouseover='movereset(1)' onmouseout='movereset(0)' fillcolor='#9cf' strokecolor='#888888'>");
	document
			.writeln("<v:fill rotate='t' angle='-45' focus='100%' type='gradient'/>");
	document.writeln("</v:rect>");
	document
			.writeln("<v:rect id='back' style='position:relative;left:800;top:4100;width:18000; height:"
					+ ((num + 3) * height0 + 0)
					+ ";' fillcolor='#9cf' stroked='t' strokecolor='#0099ff'>");
	document
			.writeln("<v:fill rotate='t' angle='-175' focus='100%' type='gradient'/>");
	document
			.writeln("<v:shadow on='t' type='single' color='silver' offset='3pt,3pt'/>");
	document.writeln("</v:rect>");
	document.writeln("<a class=s0 style='cursor:hand;' href='" + href01 + "'>");
	document
			.writeln("<v:Rect id='recrec11' style='position:relative;left:1300;top:4200;width:17000;height:500' fillcolor='#000000' stroked='f' strokecolor='#000000'>");
	document
			.writeln("<v:TextBox inset='8pt,4pt,3pt,3pt' style='font-size:11pt; font-family:Arial, Helvetica, sans-serif;'><div><div class=sd1>Total Submissions:</div><div class=sd2>"
					+ allvalues + unit + "</div></div></v:TextBox>");
	document.writeln("</v:Rect></a>");
	document
			.writeln("<v:Rect style='position:relative;left:1300;top:4700;width:17000;height:500' fillcolor='#000000' stroked='f' strokecolor='#000000'>");
	document
			.writeln("<v:TextBox inset='8pt,4pt,3pt,3pt' style='font-size:11pt; font-family:Arial, Helvetica, sans-serif;'><div align='left'><font color='#ffffff'><B><div class=sd1>Users (Submitted):</div><div class=sd2>"
					+ tot1 + unit + "</div></B></font></div></v:TextBox>");
	document.writeln("</v:Rect> ");
	document
			.writeln("<v:Rect style='position:relative;left:1300;top:5200;width:17000;height:500' fillcolor='#000000' stroked='f' strokecolor='#000000'>");
	document
			.writeln("<v:TextBox inset='8pt,4pt,3pt,3pt' style='font-size:11pt; font-family:Arial, Helvetica, sans-serif;'><div align='left'><font color='#ffffff'><B><div class=sd1>Users (Solved):</div><div class=sd2>"
					+ tot2 + unit + "</div></B></font></div></v:TextBox>");
	document.writeln("</v:Rect> ");
	for (i = 0; i < num; i++) {
		document
				.writeln("<a class=s style='cursor:hand;' onmouseover='moveup(cake"
						+ i
						+ ","
						+ (table_top + radius / 14)
						+ ",txt"
						+ i
						+ ",rec"
						+ i
						+ ")'; onmouseout='movedown(cake"
						+ i
						+ ","
						+ (table_top + radius / 14)
						+ ",txt"
						+ i
						+ ",rec"
						+ i + ");' href=" + sa[2][i] + ">");
		document
				.writeln("<v:Rect id='rec"
						+ i
						+ "' style='position:relative;left:1100;top:"
						+ Math.round((i + 3) * height0 + 3850)
						+ ";width:17000;height:600;display:none' fillcolor='#efefef' strokecolor='"
						+ bg_color[i] + "'>");
		document
				.writeln("<v:fill opacity='.6' color2='fill darken(118)' o:opacity2='.6' rotate='t' method='linear sigma' focus='100%' type='gradient'/>");
		document.writeln("</v:Rect>");
		document.writeln("<v:Rect style='position:relative;left:1300;top:"
				+ Math.round((i + 3) * height0 + 3900)
				+ ";width:1300;height:500' fillcolor='" + bg_color[i]
				+ "' stroked='f'/>");
		document.writeln("<v:Rect style='position:relative;left:3100;top:"
				+ Math.round((i + 3) * height0 + 3900)
				+ ";width:14400;height:500' filled='f' stroked='f'>");
		document
				.writeln("<v:TextBox inset='0pt,5pt,0pt,0pt' style='font-size:10pt; font-family:Arial, Helvetica, sans-serif; cursor:hand;'><div><div style='position:absolute; left:0px'>"
						+ sa[1][i]
						+ ":</div><div style='position:absolute; right:0px'><b>"
						+ sa[0][i] + unit + "</b></div></div></v:TextBox>");
		document.writeln("</v:Rect>");
		document.writeln("</a>");
	}
	document.writeln("</v:group>");
	var k1 = 180;
	var k4 = 10;
	for (i = 0; i < num; i++) {
		k2 = 360 * pie[i] / 2;
		k3 = k1 + k2;
		if (k3 >= 360) {
			k3 = k3 - 360;
		}
		kkk = (-11796480 * pie[i] + 5898240);
		k5 = 3.1414926 * 2 * (180 - (k3 - 180)) / 360;
		R = radius / 2;
		txt_x = table_left + radius / 8 - 30 + R + R * Math.sin(k5) * 0.7;
		txt_y = table_top + radius / 14 - 39 + R + R * Math.cos(k5) * 0.7 * 0.5;
		titlestr = "Result     : " + sa[1][i] + "&#13;&#10;Number     : "
				+ sa[0][i] + unit + "&#13;&#10;Percentage : "
				+ Math.round(pie[i] * 10000) / 100 + "%&nbsp;&nbsp;";
		document.writeln("<div style='cursor:hand;'>");
		document.writeln("<v:shape id='cake" + i + "' type='#Cake_3D' title='"
				+ titlestr + "'");
		document.writeln("style='position:absolute;left:"
				+ (table_left + radius / 8) + "px;top:"
				+ (table_top + radius / 14) + "px;WIDTH:" + radius
				+ "px;HEIGHT:" + radius + "px;rotation:" + k3 + ";z-index:"
				+ k4 + "'");
		document.writeln("adj='" + kkk + ",0' fillcolor='" + bg_color[i]
				+ "' onmouseover='moveup(cake" + i + ","
				+ (table_top + radius / 14) + ",txt" + i + ",rec" + i
				+ ")'; onmouseout='movedown(cake" + i + ","
				+ (table_top + radius / 14) + ",txt" + i + ",rec" + i + ");'>");
		document
				.writeln("<v:fill opacity='60293f' color2='fill lighten(120)' o:opacity2='60293f' rotate='t' angle='-135' method='linear sigma' focus='100%' type='gradient'/>");
		document
				.writeln("<o:extrusion v:ext='view' on='t' backdepth='25' rotationangle='60' viewpoint='0,0'viewpointorigin='0,0' skewamt='0' lightposition='-50000,-50000' lightposition2='50000'/>");
		document.writeln("</v:shape>");
		document
				.writeln("<v:shape id='txt"
						+ i
						+ "' type='#3dtxt' style='position:absolute;left:"
						+ txt_x
						+ "px;top:"
						+ txt_y
						+ "px;z-index:20;display:none;width:50; height:10;' fillcolor='black'");
		document.writeln("onmouseover='ontxt(cake" + i + ","
				+ (table_top + radius / 14) + ",txt" + i + ",rec" + i + ")'>");
		document
				.writeln("<v:textpath style=\"font-family:Arial; v-text-kern:t\" trim='true' string='"
						+ Math.round(pie[i] * 10000) / 100 + "%'/>");
		document.writeln("</v:shape>");
		document.writeln("</div>");
		k1 = k1 + k2 * 2;
		if (k1 >= 360) {
			k1 = k1 - 360;
		}
		if (k1 > 180) {
			k4 = k4 + 1;
		} else {
			k4 = k4 - 1;
		}
	}
}

var countdown = 6;
function setRefresh(newCountDown) {
	countdown = newCountDown;
	setTimeout("refresh()", 10000);
}
function refresh() {
	if (countdown <= 0) {
		window.location = window.location;
	} else {
		countdown -= 1;
		setTimeout("refresh()", 10000);
	}
}
function KeyDown(event) {
	event = event ? event : (window.event ? window.event : null);
	if ((event.ctrlKey) && ((event.keyCode == 92) || (event.keyCode == 220))) {
		window.location = window.location;
	}
}

function set_size(s) {
	document.getElementById('Problem').style.fontSize = s + "px";

}
function sub_size() {
	document.getElementById('Problem').style.fontSize = "1.6em";

}
function add_size() {
	document.getElementById('Problem').style.fontSize = "1.8em";

}

function copyCode(obj) {
	var rng = document.body.createTextRange();
	rng.moveToElementText(obj);
	rng.select();
	rng.execCommand("Copy");
}

function saveCode(obj, filename) {
	var winname = window.open('', '_blank', 'top=10000');
	winname.document.open('text/html', 'replace');
	winname.document.writeln(obj.value);
	winname.document.execCommand('saveas', '', filename);
	winname.close();
}

function toHTML(s) {
	s = s.split("&").join("&amp;");
	s = s.split("<").join("&lt;");
	return s.split(">").join("&gt;");
}

// copy code and this can support firefox
function newcopyCode(txt) {
	// support ie
	if (window.clipboardData) {
		window.clipboardData.clearData();
		window.clipboardData.setData("Text", txt);
	} else
	// opera
	if (navigator.userAgent.indexOf("Opera") != -1) {
		window.location = txt;
	} else
	// firefox
	if (window.netscape) {
		// xpcom
		try {
			netscape.security.PrivilegeManager
					.enablePrivilege("UniversalXPConnect");
		} catch (errors) {
			alert("被浏览器拒绝！\n请你在浏览器地址栏中输入'about:config'并且回车\n然后将'signed.applets.codebase_principal.support'设置为true\n如果你想使用这个复制功能的话，那么最好这么设置^_^|||");
		}
		var clip = Components.classes['@mozilla.org/widget/clipboard;1']
				.createInstance(Components.interfaces.nsIClipboard);
		if (!clip)
			return;
		var trans = Components.classes['@mozilla.org/widget/transferable;1']
				.createInstance(Components.interfaces.nsITransferable);
		if (!trans)
			return;
		trans.addDataFlavor("text/unicode");
		// var str = new Object();
		// var len = new Object();
		// var str =
		// Components.classes['@mozilla.org/supports-string;1'].createInstance(Components.interfaces.nsISupportsString);
		var copytext = txt;
		str.data = copytext;
		trans.setTransferData("text/unicode", str, copytext.length * 2);
		var clipid = Components.interfaces.nsIClipboard;
		if (!clip)
			return false;
		clip.setData(trans, null, clipid.kGlobalClipboard);
	}
	// alert("Copy Successful!");
}
function renderCode(s) {
	s = s.split("\r\n").join("\n");

	comment = {
		color : "green",
		bold : false,
		italic : false,
		name : "comment"
	};
	precompiler = {
		color : "blue",
		bold : false,
		italic : false,
		name : "pre"
	};
	operator = {
		color : "#FF00FF",
		bold : true,
		italic : false,
		name : "operator"
	};
	stringLiteral = {
		color : "green",
		bold : false,
		italic : false,
		name : "string"
	};
	charLiteral = {
		color : "green",
		bold : false,
		italic : false,
		name : "char"
	};
	intLiteral = {
		color : "#CC3300",
		bold : false,
		italic : false,
		name : "int"
	};
	floatLiteral = {
		color : "#CC3300",
		bold : false,
		italic : false,
		name : "float"
	};
	boolLiteral = {
		color : "#CC3300",
		bold : false,
		italic : false,
		name : "bool"
	};
	types = {
		color : "blue",
		bold : true,
		italic : false,
		name : "type"
	};
	flowControl = {
		color : "#005f5F",
		bold : true,
		italic : false,
		name : "flow"
	};
	keyword = {
		color : "#0000FF",
		bold : true,
		italic : false,
		name : "keyword"
	};

	keys = new Array();
	keys.push({
		style : comment,
		start : /\s*\/\*[\s\S]*?\*\//mg
	});
	keys.push({
		style : comment,
		start : /\s*\/\//mg,
		end : /\n/mg,
		neglect : /\\|\?\?\//mg
	});
	keys.push({
		style : precompiler,
		start : /\s*?^\s*(?:#|\?\?=|%:)/mg,
		end : /\n/m,
		neglect : /\\[\s\S]|\?\?\/[\s\S]/m
	});
	keys.push({
		style : stringLiteral,
		start : /\s*(?:\bL)?"/mg,
		end : /"/m,
		neglect : /\\[\s\S]|\?\?\/[\s\S]/m
	});
	keys.push({
		style : charLiteral,
		start : /\s*(?:\bL)?'/mg,
		end : /'/m,
		neglect : /\\[\s\S]|\?\?\/[\s\S]/m
	});
	keys
			.push({
				style : floatLiteral,
				start : /\s*(?:(?:\b\d+\.\d*|\.\d+)(?:E[\+\-]?\d+)?|\b\d+E[\+\-]?\d+)[FL]?\b|\s*\b\d+\./mgi
			});
	keys.push({
		style : intLiteral,
		start : /\s*\b(?:0[0-7]*|[1-9]\d*|0x[\dA-F]+)(?:UL?|LU?)?\b/mgi
	});
	keys.push({
		style : boolLiteral,
		start : /\s*\b(?:true|false)\b/mg
	});
	keys
			.push({
				style : types,
				start : /\s*\b(?:bool|char|double|float|int|Integer|long|short|signed|unsigned|void|wchar_t|__int64)\b/mg
			});
	keys
			.push({
				style : flowControl,
				start : /\s*\b(?:break|begin|case|catch|continue|default|do|end|else|for|goto|if|return|switch|throw|try|while)\b/mg
			});
	keys
			.push({
				style : keyword,
				start : /\s*\b(?:add|asm|auto|class|const_cast|const|delete|dynamic_cast|enum|explicit|export|extern|friend|inline|invoke|main|mov|mutable|namespace|new|operator|program|private|protected|public|register|reinterpret_cast|sizeof|static_cast|static|struct|template|this|typedef|typeid|typename|union|using|var|virtual|volatile|and_eq|and|bitand|bitor|compl|not_eq|not|or_eq|or|xor_eq|xor|__asm|__fastcall|__based|__cdecl|__pascal|__inline|__multiple_inheritance|__single_inheritance|__virtual_inheritance)\b/mg
			});
	keys.push({
		style : operator,
		start : /\s*[\{\}\[\]\(\)<>%:;\.\?\*\+\-\^&\|~!=,\\]+|\s*\//mg
	});

	for (var i = 0; i != keys.length; ++i) {
		keys[i].before = "";
		if (keys[i].style.bold)
			keys[i].before += "<b>";
		if (keys[i].style.italic)
			keys[i].before += "<i>";
		keys[i].before += "<font color=\"" + keys[i].style.color + "\">";
		keys[i].after = "</font>";
		if (keys[i].style.italic)
			keys[i].after += "</i>";
		if (keys[i].style.bold)
			keys[i].after += "</b>";
	}

	var keyString = "";
	var match = 0;
	var strResult = "";

	strResult = "";

	var previousMatch = -1;
	for (i = 0; i != keys.length; ++i)
		keys[i].startPos = -1;

	for (var position = 0; position != s.length; position = keys[match].endPos) {
		for (i = 0; i != keys.length; ++i) {
			if (keys[i].startPos < position) {
				// update needed
				keys[i].start.lastIndex = position;
				var result = keys[i].start.exec(s);
				if (result != null) {
					keys[i].startPos = result.index;
					keys[i].endPos = keys[i].start.lastIndex;
				} else
					keys[i].startPos = keys[i].endPos = s.length;
			}
		}
		match = 0;
		for (i = 1; i < keys.length; ++i)
			// find first matching key
			if (keys[i].startPos < keys[match].startPos)
				match = i;
		if (keys[match].end != undefined) {
			// end must be found
			var end = new RegExp(keys[match].end.source + "|"
					+ keys[match].neglect.source, "mg");
			end.lastIndex = keys[match].endPos;
			while (keys[match].endPos != s.length) {
				result = end.exec(s);
				if (result != null) {
					if (result[0].search(keys[match].end) == 0) {
						keys[match].endPos = end.lastIndex;
						break;
					}
				} else
					keys[match].endPos = s.length;
			}
		}
		var before = s.substring(position, keys[match].startPos);
		keyString = s.substring(keys[match].startPos, keys[match].endPos);
		var output = "";
		if ((before == "") && (match == previousMatch))
			output += toHTML(keyString);
		else {
			if (previousMatch != -1)
				output += keys[previousMatch].after;
			output += toHTML(before);
			if (keyString != "")
				output += keys[match].before + toHTML(keyString);
		}
		previousMatch = match;
		strResult += output;
	}
	if (keyString != "")
		strResult += keys[match].after;

	return strResult;
}

var flag = false;
function DrawImage(ImgD) {
	var image = new Image();
	image.src = ImgD.src;
	if (image.width > 0 && image.height > 0) {
		flag = true;
		if (image.width / image.height >= 340 / 380) {
			if (image.width > 340) {
				ImgD.width = 340;
				ImgD.height = (image.height * 340) / image.width;
			} else {
				ImgD.width = image.width;
				ImgD.height = image.height;
			}
		} else {
			if (image.height > 380) {
				ImgD.height = 380;
				ImgD.width = (image.width * 380) / image.height;
			} else {
				ImgD.width = image.width;
				ImgD.height = image.height;
			}
		}
	}
}
