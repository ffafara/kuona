function config($stateProvider, $urlRouterProvider, $ocLazyLoadProvider) {
    $urlRouterProvider.otherwise("/index/main");

    $ocLazyLoadProvider.config({
        // Set to true if you want to see what and when is dynamically loaded
        debug: false
    });

    $stateProvider
        .state('index', {
            abstract: true,
            url: "/index",
            templateUrl: "views/common/content.html",
        })
        .state('index.main', {
            url: "/main",
            templateUrl: "views/main.html",
            data: {pageTitle: 'Example view'}
        })
        .state('index.minor', {
            url: "/minor",
            templateUrl: "views/minor.html",
            data: {pageTitle: 'Example view'}
        })
        .state('index.dashboard', {
            url: "/dashboard",
            templateUrl: "views/dashboard.html",
            data: {pageTitle: 'Dashboard'}
        })
        .state('index.new-project', {
            url: "/new-project",
            templateUrl: "views/admin/project.html",
            data: {pageTitle: "New Project"}
        })
        .state('index.gonogo-dashboard', {
            url: "/gonogo",
            templateUrl: "views/metric-dashboard.html",
            data: {pageTitle: "Go/NoGo"}
        });
}

kuona
    .config(config)
    .run(function ($rootScope, $state) {
        $rootScope.$state = $state;
    });
