/**
 * Created by weifengxu on 17/7/8.
 */

var input_name = [];
var words = [];

$(function () {

    // getDirs();


    $('#word').bind('keypress', function (event) {
        if (event.keyCode == 13) {
            search()
        }
    });

    $('#allsub').on('click', function () {
        search()
    });


    //获取选中视频的地址 下载
    $('#alldownload_a').on('click', function () {

        if (words.length == 0) {
            alert("空空如也！")

            return;
        }

        var result = [];
        var count = $('#count').val()


        for (var i = 0; i < words.length; i++) {
            var oneWord = words[i];

            var ws = JSON.parse(oneWord.videos);
            var min = Math.min(ws.length, count);
            for (var j = 0; j < min; j++) {

                result.push(ws[j]);
            }

        }


        $.ajax({
            type: 'POST',
            url: "/yb/ybstreamCuts",
            data: {data: JSON.stringify(result)},
            success: function (data) {
            }
        })

    });
});



function search() {
    var input_ck = $('input[name=dir]:checked');
    $.ajax({
        type: 'POST',
        url: "/yb/getWords_batch",
        data: {
            word: $('#word').val(),
            path: $(input_ck).attr('url')
        },
        success: function (data) {
            console.log(data)
            input_name = [];
            if ('00' == data.returnCode) {
                var str = '';
                words = data.data;
                for (var j = 0; j < words.length; j++) {
                    //radio name
                    input_name.push('rd-' + j);

                    var oneWord = words[j];
                    str += ' <h4>' + oneWord.word + '</h4>';
                    str += '<div >';
                    str += '<table>';
                    str += '<tr>';

                    var vs = JSON.parse(oneWord.videos);

                    for (var i = 0; i < vs.length; i++) {
                        if (i > 0 && i % 2 == 0) {
                            str += '</tr><tr>';
                        }
                        var video = vs[i];
                        str += '<td  width="50%" style="border: solid">';
                        str += '<div >';
                        str += '<span  style="float: left;margin-left="16px" ;color: red">【' + i + '】</span>';
                        str += '<input  type="radio" id="' + j + '-' + i + '" name="rd-' + j + '" position="top">';
                        str += '<ul>'
                        str += '<li class="">' + video.word + '</li>';
                        // str += '<li><span style="font-size: 10px;color: #5e5e5e">' + video.name + '</span></li>';
                        str += '<li style=";color: red">匹配：' + video.type + '</li>';

                        str += '<button word="' + video.word + '" key="' + oneWord.word + '" s="' + video.s + '" e="' + video.e + '" name="' + video.name + '" style="float: right;margin: 4px">下载</button>';
                        str += '</ul>'
                        str += '</div>';
                        str += '</td>';
                    }
                    str += '</tr>';
                    str += '</table>';
                    str += '<div/>';
                }

                $('#video_div').html("");
                $('#video_div').html(str);

                //默认选中第一个radio
                $('table').each(function (i, e) {
                    var first = $(e).find('input:first')
                    first.attr('checked', 'checked');
                });

                $('table button').on('click', function () {
                    var s = $(this).attr('s');
                    var e = $(this).attr('e');
                    var name = $(this).attr('name');
                    var key = $(this).attr('key');
                    var word = $(this).attr('word');
                    cutStream(s, e, name, key, word);
                    console.log("s:" + s + "\ne:" + e + "\nname:" + name)
                });


                $('#alldownload_a').css('display', 'inline-block');
                $('#count').css('display', 'inline-block');
            } else {
                $('#video_div').html("<a style='font-size: 2em;'>加载失败！</a>");
            }


        }
    });
}

//以val最大取值90为例
function getColor(val) {
    val = 100 - val;
    var one = (255 + 255) / 66;//（255+255）除以最大取值的三分之二
    var r = 0, g = 0, b = 0;
    if (val < 30)//第一个三等分
    {
        r = one * val;
        g = 255;
    }
    else if (val >= 33 && val < 66)//第二个三等分
    {
        r = 255;
        g = 255 - ((val - 33) * one);//val减最大取值的三分之一
    }
    else {
        r = 255;
    }//最后一个三等分
    return 'rgb(' + Math.round(r) + ',' + Math.round(g) + ',' + Math.round(b) + ')';
}



function cutStream(s, e, name, key, word) {
    $.ajax({
        type: 'POST',
        url: "/yb/ybstreamCut",
        data: {
            s: s,
            e: e,
            name: name,
            key: key,
            word: word,
        },
        success: function (data) {

            console.log(data);

        }
    })
}


// document.domain = "caibaojian.com";
function setIframeHeight(iframe) {
    if (iframe) {
        var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
        if (iframeWin.document.body) {
            iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
        }
    }
};
