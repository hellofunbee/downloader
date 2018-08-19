/**
 * Created by weifengxu on 17/7/8.
 */

$(function () {
    $('#picsub').on('click', function () {
        $.ajax({
            type: 'POST',
            url: "/picurl",
            data: {"url": $('#url').val()},
            success: function (data) {
                if ('00' == data.returnCode) {
                    $('#img_show').attr('src', data.url);
                    $('#download_a').css('display', 'block');
                    $('#download_a').attr('href', data.url);
                } else {

                }
            }
        })
    });
    $('#videosub').on('click', function () {
        $.ajax({
            type: 'POST',
            url: "/videourl",
            data: {"url": $('#url').val()},
            success: function (data) {
                if ('00' == data.returnCode && data.url != '') {
                    var str = '<video id="video_show" playsinline="" src="' + data.url + '" controls="controls" type width="50%"/>';
                    $('#video_div').html(str);
                    $('#download_a').css('display', 'inline-block');
                    $('#download_a').attr('href', data.url);
                } else {

                }
            }
        })
    });

    $('#allsub').on('click', function () {
        $.ajax({
            type: 'POST',
            url: "/allurl",
            data: {"url": $('#url').val()},
            success: function (data) {
                if ('00' == data.returnCode) {
                    var str = '';
                    for (var i = 0; i < data.data.length; i++) {
                        str += '<img class="img-all" src="' + data.data[i] + '">';
                    }

                    $('#img_show').html(str);
                    $('#alldownload_a').css('display', 'inline-block');
                } else {
                    $('#img_show').html("<a style='font-size: 2em;'>加载失败！</a>");
                }
            }
        })
    });

    $('#alldownload_a').on('click', function () {
        $('#temp').html('');
        $('#img_show').find('img').each(function (i, item) {
            var a = document.createElement('a');
            a.setAttribute('download', '');
            a.href = $(item).attr('src');
            console.log(a)
            // document.getElementById('temp').appendChild(a);
            a.click();
        });
    });
});

// document.domain = "caibaojian.com";
function setIframeHeight(iframe) {
    if (iframe) {
        var iframeWin = iframe.contentWindow || iframe.contentDocument.parentWindow;
        if (iframeWin.document.body) {
            iframe.height = iframeWin.document.documentElement.scrollHeight || iframeWin.document.body.scrollHeight;
        }
    }
};
