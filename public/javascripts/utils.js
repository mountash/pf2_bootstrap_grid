//defining appSettings object
var appSettings = new Object();

//defining .center() function
(function($){
    $.fn.extend({
        center: function () {
            return this.each(function() {
                var top = ($(window).height() - $(this).outerHeight()) / 2;
                var left = ($(window).width() - $(this).outerWidth()) / 2;
                $(this).css({position:'fixed', margin:0, top: (top > 0 ? top : 0)+'px', left: (left > 0 ? left : 0)+'px'});
            });
        }
    });
})(jQuery);

// loading indicator functions
var loadingsNum = 0;

function showLoading(){
    if(!$("#loadingContainer").length){
          $("body").append("<div id=\"loadingContainer\"><img src=\""+iconsDir+"loading.gif\">Loading...</div>");
    }
    $("#loadingContainer").center();
    $("#loadingContainer").show();
    loadingsNum++;
}

function hideLoading(){
    loadingsNum--;
    if(loadingsNum <= 0){
        $("#loadingContainer").hide();
        $("#loadingContainer img").stop();
        loadingsNum = 0;
    }
}

function displayError(resp, errtype, err){
    showInfoDialog({
        messageType: 'danger'
        ,title: 'ERROR!'
        ,message: '<p>Request Status:' + resp.statusText + '</p><p>Error Type:'+errtype+'</p><p>Message:'+err.message+'</p>'
        ,autoClose: false
    });
}

function showInfoDialog(conf){
    if(conf.CCid == undefined)
        conf.CCid = null;
    if(conf.removeCCid == undefined)
        conf.removeCCid = true;
    if(conf.messageType == undefined)
        conf.messageType = 'info';
    if(conf.autoClose == undefined)
        conf.autoClose = true;
    if(conf.closeTimeout == undefined)
        conf.closeTimeout = 3000;
    if(conf.title == undefined)
            conf.title = '';
    if(conf.message == undefined)
            conf.message = '';
    var word = 'Hey!';
    if(conf.messageType == 'warning' || conf.messageType == 'danger')
        word = 'Ooops!';

    if(!$( "#infoDialogContainer" ).length)
        $("body").append('<div id="infoDialogContainer" class="alert fade in text-center"></div>');

    $("#infoDialogContainer").removeClass('alert-info').removeClass('alert-warning').removeClass('alert-danger').removeClass('alert-success').addClass('alert-'+conf.messageType);
    $("#infoDialogContainer").hide();
    $("#infoDialogContainer").center();

    if(conf.CCid == null){
        $("#infoDialogContainer").html('<button type="button" class="close" data-dismiss="alert" aria-hidden="true">×</button>'
            +'<h4>'+word+' '+conf.title+'</h4>'
            +'<p>'+conf.message+'</p>');

        $("#infoDialogContainer button").click(function(){
            $( "#infoDialogContainer" ).hide("scale",{},300);
        });
    }else{
        $("#infoDialogContainer").html($(conf.CCid).html());
        if(conf.removeCCid)
            $(conf.CCid).empty();
    }

    $( "#infoDialogContainer" ).show("scale",{},500);

    if(conf.autoClose){
        setTimeout(function(){
            $( "#infoDialogContainer" ).hide("scale",{},300);
        }, conf.closeTimeout);
    }
}

function showConfirmDialog(conf){
    if(conf.title == undefined)
          conf.title = '';
    if(conf.message == undefined)
         conf.message = '';
    if(conf.buttons == undefined)
             conf.buttons = [];

    if(!$( "#confirmDialogContainer" ).length)
        $("body").append('<div id="confirmDialogContainer" class="alert alert-warning fade in text-center"></div>');

    $("#confirmDialogContainer").hide();
        $("#confirmDialogContainer").center();

    var innerContent ='<button type="button" id="confirmDialogContainer_btnHide" class="close" data-dismiss="alert" aria-hidden="true">×</button>'
                    +'<h4>'+conf.title+'</h4>'
                    +'<p>'+conf.message+'</p>'
                    +'<p>';

    $.each(conf.buttons, function( index, value ) {
        var curButton = value;
        if(curButton.type == undefined)
            curButton.type = 'default';

        innerContent +='<button id="confirmDialogContainer_btn_'+index+'" type="button" class="btn btn-'+curButton.type+'">'+curButton.text+'</button>';
    });
     innerContent +='</p>';

     $("#confirmDialogContainer").html(innerContent);


     $("#confirmDialogContainer_btnHide").click(function(){
         $( "#confirmDialogContainer" ).hide("scale",{},300);
     });

     $.each(conf.buttons, function( index, value ) {
        var curButton = value;
        if(curButton.action == undefined)
            curButton.action = function(){ $( "#confirmDialogContainer" ).hide("scale",{},300);};

        $("#confirmDialogContainer_btn_"+index).click(function(){
            curButton.action();
            $( "#confirmDialogContainer" ).hide("scale",{},300);
        });
     });

     $( "#confirmDialogContainer" ).show("scale",{},500);
}