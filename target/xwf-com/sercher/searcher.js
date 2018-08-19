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
                console.log(data)
                if ('00' == data.returnCode) {
                    var str = '';
                    str += '<input  style="margin: 10px" type="radio" name="dir" >全部';
                    var dirs = data.data;
                    for (var j = 0; j < dirs.length; j++) {
                        var dir = dirs[j];
                        str += '<input style="margin: 10px" type="radio" url = "' + dir.path + '"name="dir">' + dir.name;

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
            path: $(input_ck).attr('url')
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
                    str += ' <h4>' + oneWord.word + '</h4>';
                    str += '<div >';
                    str += '<table>';
                    str += '<tr>';

                    var vs = JSON.parse(oneWord.videos);

                    for (var i = 0; i < vs.length; i++) {
                        if (i > 0 && i % 5 == 0) {
                            str += '</tr><tr>';
                        }
                        var video = vs[i];
                        var poster = video.path.replace(".mp4", ".jpg")
                        str += '<td  width="20%">';
                        str += '<div >';
                        str += '<video id="video_show" playsinline="" poster="' + poster + '" preload="none" src="' + video.path + '" controls="controls" type width="99% " style="float: left"/>';
                        str += '<input type="radio" id="' + j + '-' + i + '" name="rd-' + j + '" position="top">';
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

                //默认选中第一个radio
                $('table').each(function (i, e) {
                    var first = $(e).find('input:first')
                    first.attr('checked', 'checked');
                });


                $('#alldownload_a').css('display', 'inline-block');
            } else {
                $('#video_div').html("<a style='font-size: 2em;'>加载失败！</a>");
            }


        }
    });
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

    tmpForm.action = "/download_zip";
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
