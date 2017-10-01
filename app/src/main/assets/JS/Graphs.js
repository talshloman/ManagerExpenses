$(document).on("pagebeforecreate","#jqplot_test",function()
{
    var i;
    var month;
    var today;
    var monthArray;
    var allValuesJson;

    today = new Date();
    month = getListAllMonth();
    monthArray = [today];
    for(i = 1; i < 6; i++)
    {
        monthArray.unshift(addMonths(new Date(), -i));
    }
    allValuesJson = window.action.getValuesMonths(JSON.stringify(monthArray));
    var allValuesParse = JSON.parse(allValuesJson);
    var ctx = document.getElementById("myChart").getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: {

            labels: [month[monthArray[0].getMonth()],
                     month[monthArray[1].getMonth()],
                     month[monthArray[2].getMonth()],
                     month[monthArray[3].getMonth()],
                     month[monthArray[4].getMonth()],
                     month[monthArray[5].getMonth()]],
            datasets: [{
                label: '# Clean the graph',
                data: [allValuesParse[0], allValuesParse[1], allValuesParse[2], allValuesParse[3], allValuesParse[4], allValuesParse[5]],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)'
                ],
                borderColor: [
                    'rgba(255,99,132,1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero:true
                    }
                }]
            }
        }
    });
});

function addMonths(date, months)
{
    date.setMonth(date.getMonth() + months);
    return date;
}

function getListAllMonth()
{
    var allMonth = new Array();
    allMonth[0] = "January";
    allMonth[1] = "February";
    allMonth[2] = "March";
    allMonth[3] = "April";
    allMonth[4] = "May";
    allMonth[5] = "June";
    allMonth[6] = "July";
    allMonth[7] = "August";
    allMonth[8] = "September";
    allMonth[9] = "October";
    allMonth[10] = "November";
    allMonth[11] = "December";

    return allMonth;
}

$(document).on("pagebeforecreate","#Pie_Chart",function(){
    var values;
    var ctx;
    var myChart;
    var allValuesParse;
    var allOptions;

    allOptions = ["food", "home", "pleasures", "shopping", "another"];
    values = window.action.getValuesExpensesOfOptionChoice();
    allValuesParse = JSON.parse(values);
    ctx = document.getElementById("chart-area").getContext("2d");
    myChart = new Chart(ctx, {
        type: 'pie',
            data: {
                datasets: [{
                data: [allValuesParse[0], allValuesParse[1], allValuesParse[2], allValuesParse[3], allValuesParse[4]],
                backgroundColor: [
                                    window.chartColors.red,
                                    window.chartColors.orange,
                                    window.chartColors.yellow,
                                    window.chartColors.green,
                                    window.chartColors.blue,],

                                    label: 'Dataset 1'}],
                                    labels: [
                                             allOptions[0],
                                             allOptions[1],
                                             allOptions[2],
                                             allOptions[3],
                                             allOptions[4]
                                             ]
                                           },
                                           options: {
                                               responsive: true
                                           }
                                       });
});