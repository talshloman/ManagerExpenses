$( "#home" ).ready(function()
{
    $("#resetOption").hide();
});

$("#goToLogin").click(function()
{
    $("#usernameLogin").val("");
    $("#passwordLogin").val("");
    $.mobile.changePage( "#login" );
});

$("#goToRegister").click(function()
{
    $("#answerFirstName").text("");
    $("#firstName").val("");
    $("#answerLastName").text("");
    $("#lastName").val("");
    $("#answerUsername").text("");
    $("#usernameRegister").val("");
    $("#password").val("");
    $("#answerConfirmPassword").text("");
    $("#ConfirmPassword").val("");
    $("#phone").val("");
    $.mobile.changePage( "#register" );
});

$("#goToResetPassword").click(function()
{
    if($("#resetOption").is(":visible") == true)
    {
        $("#resetOption").hide();
    }
    else
    {
        $("#usernameRest").val("");
        $("#resetOption").show();
    }
});

$("#buttonResetPassword").click(function()
{
    var username;
    var result;

    username = $("#usernameRest").val();
    result = window.action.checkUsernameInDB(username);
    if(result == true)
    {
        window.action.sendCodeToUser(username);
        $("#popupResetPassword").html("The code sent");
        $("#popupResetPassword").popup("open");
        setTimeout(function(){$.mobile.changePage( "#resetPassword" );}, 1000);
    }
    else
    {
        $("#popupResetPassword").html("Username not exist");
        $("#popupResetPassword").popup("open");
        setTimeout(function(){$("#popupResetPassword").popup("close");}, 1000);
    }
});

$("#buttonSavePassword").click(function()
{
    var code;
    var newPassword;
    var result;

    code = $("#code").val();
    newPassword = $("#newPassword").val();
    result = window.action.savePassword(newPassword, code);
    if(result == true)
    {
         $("#popupSavePassword").html("You saved the password");
         $("#popupSavePassword").popup("open");
         setTimeout(function(){
                                $("#resetOption").hide();
                                $.mobile.changePage( "#home" );}, 1000);
    }
    else
    {
        $("#popupSavePassword").html("Your inputs are not match");
        $("#popupSavePassword").popup("open");
        setTimeout(function(){$("#popupSavePassword").popup("close");}, 1000);
    }
});

$("#buttonLogin").click(function()
{
    var username;
    var password;
    var result;

    username = $("#usernameLogin").val();
    password = $("#passwordLogin").val();
    result = window.action.login(username, password);
    if(result == true)
    {
        result = window.action.isFirstTimeLogin();
        if(result == true)
        {
            $.mobile.changePage( "#setting" );
        }
        else
        {
                $("#SearchOption").hide();
                $.mobile.changePage( "#main" );
        }
    }
    else
    {
        $("#popupLogin").html("Your inputs are invalid");
        $("#popupLogin").popup("open");
        setTimeout(function(){$("#popupLogin").popup("close");}, 1000);
    }
});

$("#backToHome").click(function()
{
    $.mobile.changePage( "#home" );
});

$("#buttonRegister").click(function()
{
    var username;
    var firstname;
    var lastname;
    var password;
    var phone;
    var userCreated;

    username = $("#usernameRegister").val();
    firstname = $("#firstName").val();
    lastname = $("#lastName").val();
    password = $("#password").val();
    phone = $("#phone").val();
    userCreated = window.action.addUser(username, firstname, lastname, password, phone);

    if(userCreated == true)
    {
        $("#popupRegister").html("You ready to use the application");
        $("#popupRegister").popup("open");
        setTimeout(function(){$.mobile.changePage( "#home" );}, 1000);
    }
});

$("#main").on("pagebeforecreate",function()
{
    $("#tbodyMain").empty();
    addRowsToTable();
});

$("#SearchButton").click(function()
{
    if($("#SearchOption").is(":visible") == true)
    {
        $("#SearchOption").hide();
    }
    else
    {
        $("#formDate").val("");
        $("#untilDate").val("");
        $("#SearchOption").show();
    }
});

$( "#main" ).ready(function()
{
    $("#SearchOption").hide();
});

function addRowsToTable()
{
    var allItems;
    var allItemsParse;
    var coin;

    allItems = window.action.getAllExpensesInCurrentMonth();
    if (allItems != null)
    {
        coin = window.action.getCoin();
        allItemsParse = JSON.parse(allItems);

        for(var i=0;i<allItemsParse.length;i++)
        {
            drawRow(allItemsParse[i], coin);
        }
    }
}

function drawRow(rowData, coin)
{
    var table;
    var row;

    table = $("#tbodyMain");
    row = $('<tr id = '+rowData.id+' data-role="collapsible" data-collapsed-icon=false data-expanded-icon=false>').html('<h1>'+rowData.ExpenseAmount+coin+' - '+rowData.Category+'</h1><p>Description: '+
    rowData.Description+'</p><p>Category: '+rowData.Category+'</p><p>Date: '+rowData.Date+
    '</p><a href="" id="buttonDeleteItem'+rowData.id+'" class="ui-btn ui-shadow ui-corner-all" >Remove</a>'+
    '<a href=""id="buttonUpdateItem'+rowData.id+'" class="ui-btn ui-shadow ui-corner-all" >Update</a>');
    table.append(row);
    $("#buttonDeleteItem"+rowData.id).click(function(){
        RemoveRow(rowData.id);
        return false;
     });
    $("#buttonUpdateItem"+rowData.id).click(function(){
        UpdateRow(rowData);
        return false;
    });
}

function RemoveRow(itemId)
{
    window.action.deleteItemFromItems(itemId);
    $('table#tableMain tr#'+itemId).remove();
}

function UpdateRow(item)
{
    var formUpdate;
    var addButton;

    $("#updateId").val(item.id);
    $("#updateDescription").val(item.Description);
    $("#updateSumAmount").val(parseInt(item.ExpenseAmount));
    $("#updateCategory").val(item.Category);
    $("#updateDate").val(item.Date);

    formUpdate = $("#UpdateExpenseForm");
    if($("#UpdateExpenseForm a").length < 1)
    {
        addButton = $('<a href=""id="buttonUpdateExpense'+item.id+'" class="ui-btn ui-shadow ui-corner-all" >Save</a>');
        formUpdate.append(addButton);
    }

    $("#buttonUpdateExpense"+item.id).click(function()
    {
        var descriptionForUpdate;
        var sumAmountForUpdate;
        var categoryForUpdate;
        var dateForUpdate;
        var idForUpdate;
        var updateItem;

        descriptionForUpdate = $("#updateDescription").val();
        sumAmountForUpdate = $("#updateSumAmount").val();
        categoryForUpdate = $("#updateCategory").val();
        dateForUpdate = $("#updateDate").val();
        idForUpdate = $("#updateId").val();
        updateItem = window.action.updateItem(idForUpdate, descriptionForUpdate, sumAmountForUpdate, categoryForUpdate, dateForUpdate);

        if(updateItem == true)
        {
            $("#popupUpdateExpense").html('Your expense updated.');
            $("#popupUpdateExpense").popup("open");
            setTimeout(function()
            {
                $("#tbodyMain").empty();
                addRowsToTable();
                $("#tableMain").trigger('create');
                $("#SearchOption").hide();
                $.mobile.changePage( "#main" );}, 1000);
        }
        else
        {
            $("#popupUpdateExpense").html('Your expense not updated.');
            $("#popupUpdateExpense").popup("open");
            setTimeout(function(){$("#popupUpdateExpense").popup("close");}, 1000);
        }
    });

    $.mobile.changePage( "#updateExpense" );
}

$("#gobackToMainFromUpdateExpense").click(function()
{
    $("#SearchOption").hide();
    $.mobile.changePage( "#main" );
});

$("#goToAddExpense").click(function(event)
{
    $("#description").val("");
    $("#sumAmount").val("");
    $('#Choosehere').prop('disabled', false);
    $("#category").val("Choosehere").change();
    $('#Choosehere').prop('disabled', true);
    $("#date").val("");
    $.mobile.changePage( "#addExpense" );
});

$("#returnToMainWithAllExpenses").click(function()
{
    $("#tbodyMain").empty();
    addAllExpensesRowsToTable();
    $("#tableMain").trigger('create');
    $("#SearchOption").hide();
    $.mobile.changePage( "#main" );
});

function addAllExpensesRowsToTable()
{
    var allItems;
    var allItemsParse;
    var coin;
    allItems = window.action.getAllExpenses();
    if (allItems != null)
    {
        allItemsParse = JSON.parse(allItems);
        coin = window.action.getCoin();
        for(var i=0;i<allItemsParse.length;i++)
        {
            drawRow(allItemsParse[i], coin);
        }
    }
}

$("#returnToMainWithThisMonthExpenses").click(function()
{
    $("#tbodyMain").empty();
    addRowsToTable();
    $("#tableMain").trigger('create');
    $("#SearchOption").hide();
    $.mobile.changePage( "#main" );
});

$("#goToSetting").click(function()
{
    var setting;
    var settingAfterParsing;

    setting = window.action.getSettingOfUser();
    if(setting != null)
    {
        settingAfterParsing = JSON.parse(setting);
        $("#dailyEcpense").val(settingAfterParsing.limitDaily);
        $("#weeklyExpense").val(settingAfterParsing.limitWeekly);
        $("#monthlyExpense").val(settingAfterParsing.limitMonthly);
    }
    else
    {
            $("#dailyEcpense").val("");
            $("#weeklyExpense").val("");
            $("#monthlyExpense").val("");
    }

    $.mobile.changePage( "#setting" );
});

$("#goToReport").click(function()
{
    $.mobile.changePage( "#report" );
});

$("#goTologout").click(function()
{
    window.action.logout();
});

$("#gobackToMainFromAddExpense").click(function()
{
    $("#SearchOption").hide();
    $.mobile.changePage( "#main" );
});

$("#buttonAddExpense").click(function()
{
    var description;
    var sumAmount;
    var category;
    var date;
    var expenseCreated;

    date = $("#date").val();
    category = $("#category").val();
    sumAmount = $("#sumAmount").val();
    description = $("#description").val();
    expenseCreated = window.action.addExpenseToDb(description, sumAmount, category, date);
    if(expenseCreated != null)
    {
        if(expenseCreated.localeCompare("moveOn") != 0)
        {
            var coin = window.action.getCoin();
            drawRow(JSON.parse(expenseCreated), coin);
            $("#tableMain").trigger('create');
            $("#popupAddExpense").html('Your expense added.');
            $("#popupAddExpense").popup("open");
            $("#SearchOption").hide();
            setTimeout(function(){$.mobile.changePage( "#main" );}, 1000);
        }
        else
        {
            $("#popupAddExpense").html('Your expense added.');
            $("#popupAddExpense").popup("open");
            $("#SearchOption").hide();
            setTimeout(function(){$.mobile.changePage( "#main" );}, 1000);
        }
    }
    else
    {
        $("#popupAddExpense").html('Your expense not added.');
        $("#popupAddExpense").popup("open");
        setTimeout(function(){$("#popupAddExpense").popup("close");}, 1000);
    }
});

$("#gobackToMainFromSetting").click(function()
{
    $("#SearchOption").hide();
    $.mobile.changePage( "#main" );
});

$("#buttonSaveSetting").click(function()
{
    var daily;
    var weekly;
    var monthly;
    var result;

    daily = $("#dailyEcpense").val();
    weekly = $("#weeklyExpense").val();
    monthly = $("#monthlyExpense").val();
    result = window.action.saveSettingOfUser(daily, weekly, monthly);
    if(result == true)
    {
        $("#popupSettingSuccess").popup("open");
        setTimeout(function(){$("#SearchOption").hide();
                              $.mobile.changePage( "#main" );}, 1000);
    }
    else
    {
        $("#popupSettingFail").popup("open");
        setTimeout(function(){$("#popupSettingFail").popup("close");}, 1000);
    }
});

$('#usernameRegister').blur(function()
{
    var usernameToCheck;
    var result;

    usernameToCheck = $("#usernameRegister").val();
    result = window.action.checkUsernameInDB(usernameToCheck);
    if (result == true)
    {
        $("#answerUsername").text("The name Exist");
        $("#answerUsername").css("color", "red");
    }
    else
    {
        $("#answerUsername").text("The name is not Exist");
        $("#answerUsername").css("color", "green");
    }
});

$('#ConfirmPassword').blur(function()
{
    var password;
    var confirmPassword;

    password = $("#password").val();
    confirmPassword = $("#ConfirmPassword").val();
    if(confirmPassword.localeCompare(password) != 0) {
        $("#answerConfirmPassword").text("Not much to password");
        $("#answerConfirmPassword").css("color", "red");
    } else {
      $("#answerConfirmPassword").text("");
    }
});

$("#gobackToMainFromReport").click(function()
{
    $("#SearchOption").hide();
    $.mobile.changePage( "#main" );
});

$('#phone').blur(function()
{
    var phone;

    phone = $("#phone").val();
    if(phone.length == 10)
    {
        $("#answerPhone").text("");
    }
    else
    {
        $("#answerPhone").text("Must be 9 digits");
        $("#answerPhone").css("color", "red");
    }
});

$('#password').blur(function()
{
    var phone;

    password = $("#password").val();
    if(password.length == 6)
    {
        $("#answerPassword").text("");
    }
    else
    {
        $("#answerPassword").text("Minimum lenght 6");
        $("#answerPassword").css("color", "red");

    }
});

$("#goToConstantExpense").click(function()
{
    $.mobile.changePage( "#constantExpense" );
});

$("#goToAddConstantExpense").click(function()
{
    $("#descriptionConstant").val("");
    $("#sumAmountConstant").val("");
    $('#Choosehere').prop('disabled', false);
    $("#categoryConstant").val("Choosehere").change();
    $('#Choosehere').prop('disabled', true);
    $("#dateConstant").val("");
    $.mobile.changePage( "#addConstantExpense" );
});

$("#gobackToConstantExpensesFromAddConstantExpense").click(function()
{
    $.mobile.changePage( "#constantExpense" );
});

$("#constantExpense").on("pagebeforecreate",function()
{
    $("#tbodyConstantMain").empty();
    addRowsToConstantTable();
});

function addRowsToConstantTable()
{
    var allItems;
    var allItemsParse;
    var coin;

    allItems = window.action.getAllConstantExpenses();
    if (allItems != null)
    {
        coin = window.action.getCoin();
        allItemsParse = JSON.parse(allItems);

        for(var i=0;i<allItemsParse.length;i++)
        {
            drawConstantRow(allItemsParse[i], coin);
        }
    }
}

function drawConstantRow(rowData, coin)
{
    var table;
    var row;

    table = $("#tbodyConstantMain");
    row = $('<tr id = '+rowData.id+' data-role="collapsible" data-collapsed-icon=false data-expanded-icon=false>').html('<h1>'+rowData.ExpenseAmount+coin+' - '+rowData.Category+'</h1><p>Description: '+
    rowData.Description+'</p><p>Category: '+rowData.Category+'</p><a href="" id="buttonDeleteItem'+rowData.id+'" class="ui-btn ui-shadow ui-corner-all" >Remove</a>');
    table.append(row);
    $("#buttonDeleteItem"+rowData.id).click(function(){
    RemoveConstantRow(rowData.id);
    return false;
    });

    table.append(row);
}

function RemoveConstantRow(itemId)
{
    window.action.deleteItemFromConstansItems(itemId);
    $('table#tableConstantMain tr#'+itemId).remove();
}

$("#buttonAddConstantExpense").click(function()
{
    var description;
    var sumAmount;
    var category;
    var expenseCreated;

    category = $("#categoryConstant").val();
    sumAmount = $("#sumAmountConstant").val();
    description = $("#descriptionConstant").val();
    expenseCreated = window.action.addConstantExpenseToDb(description, sumAmount, category);
    if(expenseCreated != null)
    {
        var coin = window.action.getCoin();
        drawConstantRow(JSON.parse(expenseCreated), coin);
        $("#tbodyConstantMain").trigger('create');
        $("#popupAddConstantExpense").html('Your constant expense added.');
        $("#popupAddConstantExpense").popup("open");
        setTimeout(function(){$.mobile.changePage( "#constantExpense" );}, 1000);
    }
    else
    {
        $("#popupAddConstantExpense").html('Your constant expense not added.');
        $("#popupAddConstantExpense").popup("open");
        setTimeout(function(){$("#popupAddConstantExpense").popup("close");}, 1000);
    }
});