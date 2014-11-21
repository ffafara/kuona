if ($("#vcs-user-activity").length) {
    var dataSet = [
        { label: "User Commit Activity", data: [

            <activity: {c | [ <i>, <c.count> ]} ;separator=",\n">

       ],
//            color: "#E8E800"
            }
    ];
    var ticks = [
        <activity: {c | [ <i>, "<c.username>" ]} ;separator=",\n">
    ];

    var options = {
        series: {
            bars: {
                show: true
            }
        },
        bars: {
            align: "center",
            barWidth: 0.5,
            horizontal: false,
            fillColor: { colors: [
                { opacity: 0.1 },
                { opacity: 1}
            ] },
            lineWidth: 1
        },
        xaxis: {
            axisLabel: "Userss",
            axisLabelUseCanvas: true,
//            tickColor: "#5E5E5E",
            ticks: ticks,
            rotateTicks: 135,
//            color: "black"
            axisLabelPadding: 3
        },
        yaxis: {
            axisLabel: "Users",
            axisLabelUseCanvas: true,
            axisLabelPadding: 3,
//            tickColor: "#5E5E5E",
//            color: "black"
        },
        legend: {
            noColumns: 0,
//            labelBoxBorderColor: "#858585",
            position: "ne"
        },
        grid: {
            hoverable: true,
            borderWidth: 2,
//            backgroundColor: { colors: ["#171717", "#4F4F4F"] }
        },
        colors: [$chrt_second]
    };
    $.plot($("#vcs-user-activity"), dataSet, options);
}
;