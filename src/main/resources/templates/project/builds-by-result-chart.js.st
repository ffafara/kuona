if ($('#builds-by-result').length) {
    Morris.Donut({
        element : 'builds-by-result',
        data : [
        <outcomes: {outcome | { label: '<outcome>', value: <counts.(outcome)> \}} ;separator=",\n">
    ],
        formatter : function(x) {
            return x + "%"
        }
    });
}
