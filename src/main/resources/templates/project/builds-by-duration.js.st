if ($('#builds-by-duration').length) {

    Morris.Bar({
        element: 'builds-by-duration',
        data: [

        <bands: {band | { x: '<band.name>', y: <band.value> \}} ;separator=",\n">
        ],
        xkey: 'x',
        ykeys: ['y'],
        labels: ['Builds']
    });
}
