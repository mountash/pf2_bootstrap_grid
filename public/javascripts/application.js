$(function(){
    initChangeableControls();
    addJSReload('.grid');
});

function initChangeableControls(){
    initActionControls();
    initDatePickers();
    initGridPopovers();
    initDropdows();
}

function initActionControls(){
    $(' .btnGridEditAction ').click(function(ev){
        ev.preventDefault();
        var link = $(this).attr('href');
        var parentGrid = $(this).parents('.grid');
        var gridId = parentGrid.attr('id');
        var reloadLink = parentGrid.attr('grid_reload_link');
        $.ajax({
           url: link
           ,method: 'GET'
           ,headers: { 'Accept': 'application/javascript' }
           ,data: null
           ,beforeSend:showLoading
           ,success: function(resp){
                appSettings.crossReload = function(){
                    reloadTheGrid(gridId, reloadLink);
                }
           }
           ,error: displayError
           ,complete: hideLoading
        });
    });
    $(' .btnGridDeleteAction ').click(function(ev){
        var action = $(this).attr('href');
        var id = $(this).attr('item_id');
        var name = $(this).attr('item_name');
        var parentGrid = $(this).parents('.grid');
        var gridId = parentGrid.attr('id');
        var reloadLink = parentGrid.attr('grid_reload_link');
        showConfirmDialog({
            title: 'Remove '+name+'?'
            ,message: name+' with id '+ id +' will be removed. <br>Are you sure?'
            ,buttons:[
                {
                    text: 'Delete'
                    ,type: 'danger'
                    ,action: function() {
                        appSettings.crossReload = function(){
                            reloadTheGrid(gridId, reloadLink);
                        };
                        $.ajax({
                            url: action
                            ,headers: { 'Accept': 'application/javascript' }
                            ,method: 'POST'
                            ,data: null
                            ,success: function(resp){
                             // nothing to do
                            }
                            ,error: displayError
                        });
                    }
                }
                ,{
                    text: 'Cancel'
                }
            ]
        });
        ev.preventDefault();
    });
}

function initDatePickers(){
    $(".dateInput").datepicker({
        format: "yyyy/mm/dd"
    }).on('changeDate', function(ev){
          $(ev.currentTarget).datepicker("hide")
        });
}

function initGridPopovers(){
    $('.grid-popover').popover();
}

function addJSReload(identity){
    $(identity).each(function(){
        var curGrid = $(this);
        var gridId = curGrid.attr('id');
        var mainContainer = curGrid.parents('.gridPanel');
        mainContainer.find('th').click(function(ev){
            ev.preventDefault();
            link = $(this).find('a').attr('href');
            reloadTheGrid(gridId, link);
        });
        mainContainer.find('ul.pagination li:not(.active)').click(function(ev){
            ev.preventDefault();
            link = $(this).find('a').attr('href');
            reloadTheGrid(gridId, link);
        });

        mainContainer.find('.grid-popover').click(function(ev){
            ev.preventDefault();
            var popo = $(this);
            var link = popo.attr("link");
            var maxPageAvailable = 1*popo.attr("maxPage");
            var inputField = popo.parent().find('input');
            inputField.focus();
            $(this).parent().find('button.btn-primary').click(function(ev){
                ev.preventDefault();
                var pageToGo = inputField.val();
                if(pageToGo != '' && $.isNumeric(pageToGo) && (1*pageToGo > 0)){
                    if(1*pageToGo > maxPageAvailable)
                          pageToGo = maxPageAvailable;
                    reloadTheGrid(gridId, link.replace('page=123','page='+(1*pageToGo-1)));
                }
                popo.popover('toggle');
            });
        });

        mainContainer.find(".queryForm button[type='submit']").click(function(ev){
            ev.preventDefault();
            var link = mainContainer.find(".queryForm").attr('action');
            var params = mainContainer.find(".queryForm").serialize();
            $.ajax({
               url: link
               ,method: 'POST'
               ,data: params
               ,beforeSend:showLoading
               ,success: function(resp){
                   $('#'+gridId).parents('.gridPanel').parent().hide();
                   $('#'+gridId).parents('.gridPanel').parent().html(resp);
                   initChangeableControls();
                   addJSReload('#'+gridId);
                   $('#'+gridId).parents('.gridPanel').parent().fadeIn();
               }
               ,error: function(resp, errtype, err){
                    if(err == 'Unauthorized'){
                       unAuthorized();
                    }else{
                       displayError(resp, errtype, err);
                    }
               }
               ,complete: hideLoading
            });
        });

        mainContainer.find('button.btnLoadMore').click(function(ev){
            ev.preventDefault();
            var btnLoadMore = $(this);
            btnLoadMore.attr('disabled','disabled');
            var link = mainContainer.find(".queryForm").attr('action');
            var nextPageHref = mainContainer.find('ul.pagination li.next a').attr('href');
            var params = nextPageHref.substr(nextPageHref.indexOf('?')+1);
            var pageToLoad = params.substr(params.indexOf('page=')+5);
            pageToLoad = pageToLoad.substring(0,pageToLoad.indexOf('&'));
            var prepParams = params.replace('page='+pageToLoad,'page={page_num}');
            var pageAtr = btnLoadMore.attr('page');
            if(pageAtr != undefined){
                pageToLoad = pageAtr;
            }
            var paramsToLoad = prepParams.replace('{page_num}',pageToLoad);

            $.ajax({
               url: link
               ,method: 'POST'
               ,data: paramsToLoad
               ,beforeSend:showLoading
               ,success: function(resp){
                   mainContainer.find('table tbody').append($($.parseHTML(resp)).find('table tbody').html());
                   btnLoadMore.attr('page', 1*pageToLoad+1);
                   btnLoadMore.removeAttr('disabled');
               }
               ,error: function(resp, errtype, err){
                    if(err == 'Unauthorized'){
                       unAuthorized();
                    }else{
                       displayError(resp, errtype, err);
                    }
               }
               ,complete: hideLoading
            });
        });

    });
}

function reloadTheGrid(gridId, link){
    $.ajax({
       url: link
       ,method: 'POST'
       ,data: null
       ,beforeSend:showLoading
       ,success: function(resp){
           $('#'+gridId).parents('.gridPanel').parent().hide();
           $('#'+gridId).parents('.gridPanel').parent().html(resp);
           initChangeableControls();
           addJSReload('#'+gridId);
           $('#'+gridId).parents('.gridPanel').parent().fadeIn();
       }
       ,error: function(resp, errtype, err){
            if(err == 'Unauthorized'){
               unAuthorized();
            }else{
               displayError(resp, errtype, err);
            }
       }
       ,complete: hideLoading
    });
}

function initDropdows(){
    $(".dropdown-form li a").click(function(ev){
        ev.preventDefault();
        var selText = $(this).text();
        var selValue= $(this).attr("value");
        $(this).parents('.btn-group').find('.dropdown-toggle').html(selText+' <span class="caret"></span>');
        $(this).parents('.form-group').find('input').val(selValue);
        $(this).parents('.btn-group').find('.dropdown-toggle').dropdown('toggle');
    });

    //setting default values if exist

    $(".dropdown-input").each(function(){
        var value = $(this).val();
        if(value.length > 0){
            $(this).parents('.form-group').find('.btn-group .dropdown-toggle').html($(this).parents('.form-group').find('.dropdown-form li a[value="'+value+'"]').text()+' <span class="caret"></span>');
        }
    });

}