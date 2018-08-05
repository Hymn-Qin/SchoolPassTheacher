(function (w) {
	var _root;
	_root.prototype = {
		shield: function(){
			return false;
		},
		var ws=null;
		function plusReady(){
			ws=plus.webview.currentWebview();
			// Android处理返回键
			plus.key.addEventListener('backbutton',function(){
				back();
			},false);
			compatibleAdjust();
		}
		if(w.plus){
			plusReady();
		}else{
			document.addEventListener('plusready',plusReady,false);
		}
	}
	
	document.addEventListener('touchstart',shield,false);//取消浏览器的所有事件，使得active的样式在手机上正常生效
	document.oncontextmenu=shield;//屏蔽选择函数
})(window);
