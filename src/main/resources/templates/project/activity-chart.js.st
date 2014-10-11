if ($('#build-activity-chart').length) {
    var day_data = [
       <buildcounts: {bc | { elapsed: "<bc.("elapsed")>", value: <bc.("value")> \}} ;separator=",\n">
    ];

    Morris.Line({
        element : 'build-activity-chart',
        data : day_data,
        xkey : 'elapsed',
        ykeys : ['value'],
        labels : ['value'],
        parseTime : false
    });
}
