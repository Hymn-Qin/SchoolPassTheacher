var config = {
	url: {
		apiurl: "https://test.tch.weixiaobang.net/V6/"
	    //apiurl: "http://192.168.50.221:6061/V6/"
	},
	api: {
		userInfo: 'User/index',
		process: {
			list: 'Process/index',
			add: 'Process/add',
			delete: 'Process/delete',
			edit: 'Process/update',
			check: 'Process/getinfo',
			sort: 'Process/sort',
			confirm: 'Process/confirm'
		}
	},
	regExp: {
		pattern: new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]","g")  // 去除所有特殊符号
	}
}