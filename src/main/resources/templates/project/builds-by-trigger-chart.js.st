if ($('#builds-by-trigger').length) {

    Morris.Bar({
        element: 'builds-by-trigger',
        data: [

        <triggers: {trigger | { x: '<trigger.cause>', y: <trigger.count> \}} ;separator=",\n">
],
xkey: 'x',
    ykeys: ['y'],
    labels: ['Builds']
});
}
