/**
 * Created by weifengxu on 17/7/8.
 */

var input_name = [];

$(function () {

    getDirs();


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
        var urls = [];
        for (var i = 0; i < input_name.length; i++) {
            var _name = input_name[i];
            var input_ck = $("input[name=" + _name + "]:checked");
            if (input_ck != null) {
                urls.push(input_ck.prev().attr('src'));
            }
        }

        if (urls.length == 0) {
            alert("空空如也！")

            return;
        }

        formDownload(urls);

    });
});


function getDirs() {
    $.ajax({
            type: 'POST',
            url: "/getDirs",
            data: {name: "getDir"},
            success: function (data) {
                if ('00' == data.returnCode) {
                    var str = '';
                    str += '<input  style="margin: 10px" type="radio" name="dir" >全部';
                    var dirs = data.data;
                    for (var j = 0; j < dirs.length; j++) {
                        var dir = dirs[j];
                        str += '<input style="margin: 10px" type="radio" url = "' + dir.tv_id + '"name="dir">' + dir.name;

                    }

                }

                $('#dir').html("");
                $('#dir').html(str);

                //默认选中第一个radio
                var first = $("#dir").find('input:first')
                first.attr('checked', 'checked');


            }
        }
    )
    ;
}

function search() {
    var input_ck = $('input[name=dir]:checked');
    $.ajax({
        type: 'POST',
        url: "/getWords",
        data: {
            word: $('#word').val(),
            tv_id: $(input_ck).attr('url')
        },
        success: function (data) {

            console.log(data)

            input_name = [];
            if ('00' == data.returnCode) {
                var str = '';
                var words = data.data;
                for (var j = 0; j < words.length; j++) {
                    //radio name
                    input_name.push('rd-' + j);

                    var oneWord = words[j];
                    str += ' <div class="s_head">' + oneWord.word + '</div>';
                    str += '<div >';
                    str += '<table>';
                    str += '<tr>';

                    var vs = JSON.parse(oneWord.videos);

                    for (var i = 0; i < vs.length; i++) {
                        if (i > 0 && i % 3 == 0) {
                            str += '</tr><tr>';
                        }
                        var video = vs[i];
                        var poster = video.path.substr(0, video.path.length - 4) + '.jpg';

                        str += '<td  width="20%">';
                        str += '<div class="of">';
                        str += '<video class="seek-video" id="video_show" playsinline="" poster="' + poster + '" preload="none" src="' + video.path + '" controls="controls" type width="99% " style="float: left"/>';
                        str += '<span style="float: left">匹配：' + video.type + '</span>'
                        str += '</div>';
                        str += '</td>';
                    }
                    str += '</tr>';
                    str += '</table>';
                    str += '<div/>';
                }

                $('#video_div').html("");
                $('#video_div').html(str);
                //对每个视频进行seek设置

                $('.seek-video').each(function (i, myVid) {


                    /*/usr/阿甘正传/57*08&57*11--沃尔特 那还是你吗Walter is that you .mp4*/
                    var mp4 = $(myVid).attr('src');
                    mp4 = mp4.substr(mp4.lastIndexOf('/') + 1);
                    mp4 = mp4.substr(0, mp4.indexOf('--'));
                    var times = mp4.split('&');
                    var startTime = getMs(times[0]) -1;
                    var endTime = getMs(times[1]) + 1;



                    myVid.addEventListener("timeupdate", timeupdate);

                    myVid.currentTime = startTime;

                    function timeupdate() {
                        //因为当前的格式是带毫秒的float类型的如：12.231233，所以把他转成String了便于后面分割取秒
                        var time = myVid.currentTime + "";
                        if (time >= endTime) {
                            myVid.pause();
                            myVid.currentTime = startTime;

                        }

                    }

                    $(myVid).attr('src', '/usr/阿甘正传.mp4');

                });

                //默认选中第一个radio
                $('table').each(function (i, e) {
                    var first = $(e).find('input:first')
                    first.attr('checked', 'checked');
                });
                // $('#alldownload_a').css('display', 'inline-block');
            } else {
                $('#video_div').html("<a style='font-size: 2em;'>加载失败！</a>");
            }


        }
    });
}

/**
 *
 * 时间转换成秒
 57*08或者 57*08.123
 */

function getMs(t) {
    var sencond = 0;

    var times = t.split(/[*.]/);
    var m = times[0] * 60;
    var s = times[1]*1;
    sencond = m + s;
    if (times.length == 3) {
        sencond = '' + sencond + '.' + times[2];
    }
    return sencond;
}

function formDownload(url) {
    var tmpForm = document.createElement("form");
    tmpForm.id = "form1";
    tmpForm.name = "form1";
    document.body.appendChild(tmpForm);

    var tmpInput = document.createElement("input");
// 设置input相应参数
    tmpInput.type = "text";
    tmpInput.name = "fileUrls";
    tmpInput.value = url;
// 将该输入框插入到 tmpform 中
    tmpForm.appendChild(tmpInput);

    tmpForm.action = "/common/download_zip";
    tmpForm.method = "Post";
    tmpForm.submit();

//从html从移除该form
    document.body.removeChild(tmpForm)

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
