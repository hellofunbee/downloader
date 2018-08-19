/**
 * Created by weifengxu on 17/7/11.
 */
/**
 * Created by weifengxu on 17/7/11.
 */

var result = {returnCode: '00', msg: ''};


var page = require('webpage').create(),
    system = require('system'),
    t, address;

//写入文件，用来测试。正式版本可以注释掉用来提高速度。
var fs = require("fs");

//读取命令行参数，也就是js文件路径。
if (system.args.length === 1) {
    result.returnCode = '01';
    result.msg = 'Usage: loadspeed.js <some URL>';
    console.log(JSON.stringify(result));
    phantom.exit();
}


page.settings.loadImages = false;  //为了提升加载速度，不加载图片
page.settings.resourceTimeout = 100000;//超过10秒放弃加载
//此处是用来设置截图的参数。不截图没啥用
page.viewportSize = {
    width: 1280,
    height: 800
};

/*//page的所要加载的资源在发起请求时，都可以回调该函数
page.onResourceRequested = function (requestData, networkRequest) {
console.log("----requestData-----",requestData);
console.log("******networkRequest******",networkRequest);
}
//page的所要加载的资源在加载过程中，每加载一个相关资源，都会在此先做出响应，它相当于http头部分,  其核心回调对象为response，可以在此获取本次请求的cookies、userAgent等
page.onResourceReceived = function(response) {
    console.log("response",JSON.stringify(response));
}*/

block_urls = ['baidu.com'];//为了提升速度，屏蔽一些需要时间长的。比如百度广告
page.onResourceRequested = function (requestData, request) {
    for (url in block_urls) {
        if (requestData.url.indexOf(block_urls[url]) !== -1) {
            request.abort();
            //console.log(requestData.url + " aborted");
            return;
        }
    }
}
address = system.args[1];
page.open(address, function (status) {
    if (status !== 'success') {
        result.returnCode = '01';
        result.msg = 'FAIL to load the address:' + address;
        console.log(JSON.stringify(result));
    } else {
        var ua = page.evaluate(
            //通过dom进行解析
            function () {
                var list = document.querySelectorAll('div._22yr2>img');
                var json = {};
                var imgs = [];
                for (var i = 0; i < list.length; i++) {
                    imgs.push(list[i].src);
                }
                var more = document.querySelectorAll("a._8imhp");
                if (more.length > 0 && more[0] != null) {
                    var link = more[0].href;
                    json.more = link;
                }

                json.imgs = imgs;
                return json;
            })
            ;
        // fs.write("testresult.html", ua, 'w');
        // console.log(page.content);
        result.returnCode = '00';
        result.msg = 'success';
        result.data = ua;
        // result.content = page.content;
        console.log(JSON.stringify(result));
    }
    phantom.exit();
});
